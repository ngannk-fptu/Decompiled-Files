/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.util;

import java.util.UUID;

public class IDGenerator {
    public static String generateID(String prefix) {
        String id = UUID.randomUUID().toString();
        if (prefix != null) {
            return prefix + id;
        }
        return "G" + id;
    }
}

