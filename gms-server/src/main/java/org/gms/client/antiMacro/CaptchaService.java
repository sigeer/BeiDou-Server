package org.gms.client.antiMacro;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

public class CaptchaService {
    private final ConcurrentHashMap<String, Instant> cooldowns = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, CaptchaInfo> pending = new ConcurrentHashMap<>();
    private static final int IMAGE_WIDTH = 190;
    private static final int IMAGE_HEIGHT = 48;
    private static final int CODE_LENGTH = 5;
    private static final long EXPIRY_SECONDS = 60;
    private static final long COOLDOWN_HOURS = 1;

    public boolean isOnCooldown(int sourceId, int targetId) {
        String key = sourceId + ":" + targetId;
        Instant until = cooldowns.get(key);
        return until != null && Instant.now().isBefore(until);
    }

    public void setCooldown(int sourceId, int targetId) {
        String key = sourceId + ":" + targetId;
        cooldowns.put(key, Instant.now().plus(COOLDOWN_HOURS, ChronoUnit.HOURS));
    }

    public CaptchaResult createCaptcha(int targetCharacterId, AntiMacroType antiMacroType, int sourceId) {
        String code = generateRandomCode();

        CaptchaInfo info = new CaptchaInfo();
        info.expectedAnswer = code;
        info.expiresAt = Instant.now().plus(EXPIRY_SECONDS, ChronoUnit.SECONDS);
        info.antiMacroType = antiMacroType;
        info.sourceId = sourceId;

        CaptchaInfo existing = pending.putIfAbsent(targetCharacterId, info);
        if (existing != null) {
            return null; // 该角色已有待验证的测谎
        }

        byte[] jpegBytes = generateCaptchaImage(code);

        CaptchaResult result = new CaptchaResult();
        result.imageBytes = jpegBytes;
        return result;
    }

    public boolean hasPending(int targetCharacterId) {
        return pending.containsKey(targetCharacterId);
    }

    public VerifyResult verifyAnswer(int targetCharacterId, String userAnswer) {
        CaptchaInfo info = pending.remove(targetCharacterId);
        if (info == null) {
            return null;
        }

        VerifyResult result = new VerifyResult();
        result.passed = Instant.now().isBefore(info.expiresAt)
                && (userAnswer != null && userAnswer.trim().equalsIgnoreCase(info.expectedAnswer));
        result.antiMacroType = info.antiMacroType;
        result.sourceId = info.sourceId;
        return result;
    }

    public void cleanup(int targetCharacterId) {
        pending.remove(targetCharacterId);
    }

    private static String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return code.toString();
    }

    private static byte[] generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景
        g2d.setColor(new Color(240, 240, 245));
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 干扰线条
        for (int i = 0; i < 6; i++) {
            g2d.setColor(new Color(
                    ThreadLocalRandom.current().nextInt(40) + 180,
                    ThreadLocalRandom.current().nextInt(40) + 180,
                    ThreadLocalRandom.current().nextInt(40) + 180));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(
                    ThreadLocalRandom.current().nextInt(IMAGE_WIDTH), ThreadLocalRandom.current().nextInt(IMAGE_HEIGHT),
                    ThreadLocalRandom.current().nextInt(IMAGE_WIDTH), ThreadLocalRandom.current().nextInt(IMAGE_HEIGHT));
        }

        // 干扰噪点
        for (int i = 0; i < 80; i++) {
            g2d.setColor(new Color(
                    ThreadLocalRandom.current().nextInt(100) + 100,
                    ThreadLocalRandom.current().nextInt(100) + 100,
                    ThreadLocalRandom.current().nextInt(100) + 100));
            g2d.fillRect(ThreadLocalRandom.current().nextInt(IMAGE_WIDTH), ThreadLocalRandom.current().nextInt(IMAGE_HEIGHT), 1, 1);
        }

        // 逐个字符绘制，旋转随机角度
        Font font = new Font("Arial", Font.BOLD | Font.ITALIC, 22);
        g2d.setFont(font);

        for (int i = 0; i < code.length(); i++) {
            String charStr = String.valueOf(code.charAt(i));
            int angle = ThreadLocalRandom.current().nextInt(41) - 20; // -20 到 20
            float x = (float) (IMAGE_WIDTH / (code.length() + 1)) * (i + 0.3f);
            int y = ThreadLocalRandom.current().nextInt(10) + 35; // 35 到 45

            g2d.setColor(new Color(
                    ThreadLocalRandom.current().nextInt(70) + 30,
                    ThreadLocalRandom.current().nextInt(70) + 30,
                    ThreadLocalRandom.current().nextInt(80) + 60));

            AffineTransform origTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(angle), x, y);
            g2d.drawString(charStr, x, y);
            g2d.setTransform(origTransform);
        }

        g2d.dispose();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }

    public static class CaptchaInfo {
        public String expectedAnswer = "";
        public Instant expiresAt;
        public AntiMacroType antiMacroType;
        public int sourceId;
    }

    public static class CaptchaResult {
        public byte[] imageBytes = new byte[0];
    }

    public static class VerifyResult {
        public boolean passed;
        public AntiMacroType antiMacroType;
        public int sourceId;
    }
}
