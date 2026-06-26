package org.gms.client.antiMacro;

import org.gms.client.Character;
import org.gms.client.autoban.AutobanFactory;
import org.gms.net.server.Server;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 测谎业务逻辑：协调 CaptchaService、处罚、通知发起者等。
 */
public class AntiMacroService {
    private final CaptchaService captcha;

    ConcurrentHashMap<Integer, ScheduledFuture<?>> timeoutRecords = new ConcurrentHashMap<>();

    public AntiMacroService() {
        this.captcha = new CaptchaService();
    }

    /**
     * 发起测谎 + 启动超时检测
     *
     * @param sender 发起者角色
     * @param target 目标角色
     * @param type 测谎类型 (1=道具, 2=技能GM)
     * @param onSuccess 成功回调
     */
    public void sendAntiMacro(Character sender, Character target, AntiMacroType type, Runnable onSuccess) {
        if (!target.isBattle()) {
            sender.sendPacket(AntiMacroPackets.PlayerNotBattle());
            return;
        }
        
        if (type == AntiMacroType.Item) {
            // 校验冷却 避免不停骚扰同一个角色
            if (captcha.isOnCooldown(sender.getId(), target.getId())) {
                sender.sendPacket(AntiMacroPackets.AlreadyTested());
                return;
            }
        }

        if (captcha.hasPending(target.getId())) {
            sender.sendPacket(AntiMacroPackets.CurrentlyTesting());
            return;
        }

        CaptchaService.CaptchaResult captchaResult = captcha.createCaptcha(target.getId(), type, sender.getId());
        if (captchaResult == null) {
            sender.sendPacket(PacketCreator.enableActions());
            return;
        }

        target.sendPacket(AntiMacroPackets.ShowAntiMacroCaptcha(captchaResult.imageBytes));

        if (onSuccess != null) {
            onSuccess.run();
        }

        if (type == AntiMacroType.Item) {
            captcha.setCooldown(sender.getId(), target.getId());
            target.sendPacket(AntiMacroPackets.LieDetectorUsed(sender.getName()));
        }

        // 启动超时检测（60秒）
        scheduleTimeout(sender.getWorld(), target.getId(), sender.getId(), target.getName(), sender.getId(), type);
    }

    /**
     * 处理答案提交
     */
    public void handleAnswer(Character chr, String answer) {
        CaptchaService.VerifyResult result = captcha.verifyAnswer(chr.getId(), answer);
        if (result == null) {
            return;
        }

        // 目前直接进入处罚流程
        penalize(chr.getWorld(), result.antiMacroType, result.passed, result.sourceId, chr.getId(), chr.getName());
    }

    /**
     * 测谎超时处理
     */
    private void scheduleTimeout(int worldId, int targetId, int senderId, String targetName, int reporterId, AntiMacroType type) {
        ScheduledFuture<?> future = TimerManager.getInstance().schedule(() -> {
            captcha.cleanup(targetId);
            penalize(worldId, type, false, reporterId, targetId, targetName);
        }, 60_000);
        timeoutRecords.put(targetId, future);
    }

    /**
     * 测谎处罚逻辑
     */
    private void penalize(int worldId, AntiMacroType type, boolean passed, int reporterId, int victimId, String victimName) {
        Character target = Server.getInstance().getWorld(worldId).getPlayerStorage()
            .getCharacterById(victimId);
        if (target == null) {
            return;
        }

        if (!passed) {
            // 处罚：未通过测谎
            target.sendPacket(AntiMacroPackets.SuspectedMacro(victimName));
            target.sendPacket(AntiMacroPackets.SanctionDialog(type));

            // 计数、封禁由 autoban 管理
            if (target.getAutoBanManager() != null) {
                target.getAutoBanManager().addPoint(AutobanFactory.ANTI_MACRO, "未通过测谎");
            }

            // 通知发起者（如果在线）
            Character sender = Server.getInstance().getWorld(worldId).getPlayerStorage()
                .getCharacterById(reporterId);
            if (sender != null) {
                sender.sendPacket(AntiMacroPackets.TargetFailedReward());
                sender.gainMeso(7000, true, false, true);
            }
        } else {
            // 通过测谎
            target.sendPacket(AntiMacroPackets.PassedLieDetector(target.getName()));
            target.sendPacket(AntiMacroPackets.PassDialog(type));

            if (type == AntiMacroType.Item) {
                target.gainMeso(5000, true, false, true);
            }
        }

        ScheduledFuture<?> future = timeoutRecords.remove(victimId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
