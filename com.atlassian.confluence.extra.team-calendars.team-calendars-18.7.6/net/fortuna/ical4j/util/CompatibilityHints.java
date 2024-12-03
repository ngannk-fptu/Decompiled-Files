/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fortuna.ical4j.util.Configurator;

public final class CompatibilityHints {
    public static final String KEY_RELAXED_UNFOLDING = "ical4j.unfolding.relaxed";
    public static final String KEY_RELAXED_PARSING = "ical4j.parsing.relaxed";
    public static final String KEY_RELAXED_VALIDATION = "ical4j.validation.relaxed";
    public static final String KEY_OUTLOOK_COMPATIBILITY = "ical4j.compatibility.outlook";
    public static final String KEY_NOTES_COMPATIBILITY = "ical4j.compatibility.notes";
    public static final String KEY_VCARD_COMPATIBILITY = "ical4j.compatibility.vcard";
    private static final Map<String, Boolean> HINTS = new ConcurrentHashMap<String, Boolean>();

    private CompatibilityHints() {
    }

    public static void setHintEnabled(String key, boolean enabled) {
        HINTS.put(key, enabled);
    }

    public static void clearHintEnabled(String key) {
        HINTS.remove(key);
    }

    public static boolean isHintEnabled(String key) {
        if (HINTS.get(key) != null) {
            return HINTS.get(key);
        }
        return "true".equals(Configurator.getProperty(key));
    }

    static {
        CompatibilityHints.setHintEnabled(KEY_RELAXED_UNFOLDING, "true".equals(Configurator.getProperty(KEY_RELAXED_UNFOLDING).orElse("false")));
        CompatibilityHints.setHintEnabled(KEY_RELAXED_PARSING, "true".equals(Configurator.getProperty(KEY_RELAXED_PARSING).orElse("false")));
        CompatibilityHints.setHintEnabled(KEY_RELAXED_VALIDATION, "true".equals(Configurator.getProperty(KEY_RELAXED_VALIDATION).orElse("false")));
        CompatibilityHints.setHintEnabled(KEY_OUTLOOK_COMPATIBILITY, "true".equals(Configurator.getProperty(KEY_OUTLOOK_COMPATIBILITY).orElse("false")));
        CompatibilityHints.setHintEnabled(KEY_NOTES_COMPATIBILITY, "true".equals(Configurator.getProperty(KEY_NOTES_COMPATIBILITY).orElse("false")));
    }
}

