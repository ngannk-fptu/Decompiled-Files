/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.security.SecureRandom;

public class RandomSingleton {
    private static final SecureRandom INSTANCE = new SecureRandom();

    public static SecureRandom getInstance() {
        return INSTANCE;
    }
}

