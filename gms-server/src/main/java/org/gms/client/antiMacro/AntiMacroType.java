package org.gms.client.antiMacro;

public enum AntiMacroType {
    Undefined(0),
    Item(1),
    AdminSkill(2);

    int value;
    AntiMacroType(int i) {
        value = i;
    }
}
