/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.beanvalidation;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.hibernate.HibernateException;

public enum ValidationMode {
    AUTO("auto"),
    CALLBACK("callback"),
    NONE("none"),
    DDL("ddl");

    private final String externalForm;

    private ValidationMode(String externalForm) {
        this.externalForm = externalForm;
    }

    public static Set<ValidationMode> getModes(Object modeProperty) {
        HashSet<ValidationMode> modes = new HashSet<ValidationMode>(3);
        if (modeProperty == null) {
            modes.add(AUTO);
        } else {
            String[] modesInString;
            for (String modeInString : modesInString = modeProperty.toString().split(",")) {
                modes.add(ValidationMode.getMode(modeInString));
            }
        }
        if (modes.size() > 1 && (modes.contains((Object)AUTO) || modes.contains((Object)NONE))) {
            throw new HibernateException("Incompatible validation modes mixed: " + ValidationMode.loggable(modes));
        }
        return modes;
    }

    private static ValidationMode getMode(String modeProperty) {
        if (modeProperty == null || modeProperty.length() == 0) {
            return AUTO;
        }
        try {
            return ValidationMode.valueOf(modeProperty.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            throw new HibernateException("Unknown validation mode in javax.persistence.validation.mode: " + modeProperty);
        }
    }

    public static String loggable(Set<ValidationMode> modes) {
        if (modes == null || modes.isEmpty()) {
            return "[<empty>]";
        }
        StringBuilder buffer = new StringBuilder("[");
        String sep = "";
        for (ValidationMode mode : modes) {
            buffer.append(sep).append(mode.externalForm);
            sep = ", ";
        }
        return buffer.append("]").toString();
    }
}

