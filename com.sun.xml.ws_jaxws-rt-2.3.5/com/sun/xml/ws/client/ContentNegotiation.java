/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.client;

public enum ContentNegotiation {
    none,
    pessimistic,
    optimistic;

    public static final String PROPERTY = "com.sun.xml.ws.client.ContentNegotiation";

    public static ContentNegotiation obtainFromSystemProperty() {
        try {
            String value = System.getProperty(PROPERTY);
            if (value == null) {
                return none;
            }
            return ContentNegotiation.valueOf(value);
        }
        catch (Exception e) {
            return none;
        }
    }
}

