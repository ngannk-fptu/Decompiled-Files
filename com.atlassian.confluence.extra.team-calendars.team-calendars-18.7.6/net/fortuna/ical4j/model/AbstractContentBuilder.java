/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.CompatibilityHints;

public abstract class AbstractContentBuilder {
    protected boolean isExperimentalName(String name) {
        return name.startsWith("X-") && name.length() > "X-".length();
    }

    protected boolean allowIllegalNames() {
        return CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed");
    }
}

