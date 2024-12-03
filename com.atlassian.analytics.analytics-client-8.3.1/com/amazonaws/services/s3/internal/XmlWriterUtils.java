/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.services.s3.internal.XmlWriter;

public final class XmlWriterUtils {
    private XmlWriterUtils() {
    }

    public static void addIfNotNull(XmlWriter writer, String tagName, String tagValue) {
        if (tagValue != null) {
            writer.start(tagName).value(tagValue).end();
        }
    }
}

