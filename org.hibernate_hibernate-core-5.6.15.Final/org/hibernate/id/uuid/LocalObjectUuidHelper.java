/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.uuid;

import java.util.UUID;

public class LocalObjectUuidHelper {
    private LocalObjectUuidHelper() {
    }

    public static String generateLocalObjectUuid() {
        return UUID.randomUUID().toString();
    }
}

