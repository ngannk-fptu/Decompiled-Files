/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.spi;

public interface DuplicationStrategy {
    public boolean areMatch(Object var1, Object var2);

    public Action getAction();

    public static enum Action {
        ERROR,
        KEEP_ORIGINAL,
        REPLACE_ORIGINAL;

    }
}

