package com.example.utils;

import net.serenitybdd.screenplay.targets.Target;

public class HealLocator {
    public static Target resolve(Target original) {
        // For v2.4.7 use this prefix format:
        return Target.the(original.getName())
            .locatedBy("heal:enabled:" + original.getCssOrXPathSelector());
    }
}
