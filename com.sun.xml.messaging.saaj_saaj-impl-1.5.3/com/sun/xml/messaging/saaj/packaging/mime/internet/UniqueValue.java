/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

class UniqueValue {
    private static int part = 0;

    UniqueValue() {
    }

    public static String getUniqueBoundaryValue() {
        StringBuilder s = new StringBuilder();
        s.append("----=_Part_").append(part++).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
        return s.toString();
    }
}

