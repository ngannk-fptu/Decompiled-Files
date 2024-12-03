/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import java.util.Locale;

public enum Case {
    UPPER{

        @Override
        public String apply(String s) {
            return Case.nullSafe(s).toUpperCase(Locale.ENGLISH);
        }
    }
    ,
    LOWER{

        @Override
        public String apply(String s) {
            return Case.nullSafe(s).toLowerCase(Locale.ENGLISH);
        }
    };


    public abstract String apply(String var1);

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}

