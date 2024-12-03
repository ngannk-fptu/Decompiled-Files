/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.metatype.annotations;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AttributeType {
    STRING("String"),
    LONG("Long"),
    INTEGER("Integer"),
    SHORT("Short"),
    CHARACTER("Character"),
    BYTE("Byte"),
    DOUBLE("Double"),
    FLOAT("Float"),
    BOOLEAN("Boolean"),
    PASSWORD("Password");

    private final String value;

    private AttributeType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

