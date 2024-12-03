/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio;

public enum AttributeType {
    NULL("null"),
    BOOLEAN("boolean"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("string"),
    HTML("html"),
    UNKNOWN("unknown"),
    IDENTIFIER("identifier");

    private String name;

    private AttributeType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static AttributeType create(String value) {
        switch (value) {
            case "null": {
                return NULL;
            }
            case "boolean": {
                return BOOLEAN;
            }
            case "int": {
                return INT;
            }
            case "long": {
                return LONG;
            }
            case "float": {
                return FLOAT;
            }
            case "double": {
                return DOUBLE;
            }
            case "string": {
                return STRING;
            }
            case "html": {
                return HTML;
            }
            case "unknown": {
                return UNKNOWN;
            }
            case "identifier": {
                return IDENTIFIER;
            }
        }
        throw new IllegalArgumentException("Type " + value + " is unknown");
    }
}

