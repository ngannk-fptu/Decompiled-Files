/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

public enum GenerationTiming {
    NEVER{

        @Override
        public boolean includesInsert() {
            return false;
        }

        @Override
        public boolean includesUpdate() {
            return false;
        }
    }
    ,
    INSERT{

        @Override
        public boolean includesInsert() {
            return true;
        }

        @Override
        public boolean includesUpdate() {
            return false;
        }
    }
    ,
    ALWAYS{

        @Override
        public boolean includesInsert() {
            return true;
        }

        @Override
        public boolean includesUpdate() {
            return true;
        }
    };


    public abstract boolean includesInsert();

    public abstract boolean includesUpdate();

    public static GenerationTiming parseFromName(String name) {
        if ("insert".equalsIgnoreCase(name)) {
            return INSERT;
        }
        if ("always".equalsIgnoreCase(name)) {
            return ALWAYS;
        }
        return NEVER;
    }
}

