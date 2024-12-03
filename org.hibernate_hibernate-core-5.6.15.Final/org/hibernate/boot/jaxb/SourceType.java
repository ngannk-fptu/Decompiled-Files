/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb;

public enum SourceType {
    RESOURCE("resource"),
    FILE("file"),
    INPUT_STREAM("input stream"),
    URL("URL"),
    STRING("string"),
    DOM("xml"),
    JAR("jar"),
    ANNOTATION("annotation"),
    OTHER("other");

    private final String legacyTypeText;

    private SourceType(String legacyTypeText) {
        this.legacyTypeText = legacyTypeText;
    }

    public String getLegacyTypeText() {
        return this.legacyTypeText;
    }
}

