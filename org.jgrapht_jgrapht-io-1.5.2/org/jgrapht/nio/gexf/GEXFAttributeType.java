/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio.gexf;

public enum GEXFAttributeType {
    BOOLEAN("boolean"),
    INTEGER("integer"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    STRING("string"),
    LISTSTRING("liststring"),
    ANYURI("anyURI");

    private String name;

    private GEXFAttributeType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static GEXFAttributeType create(String value) {
        switch (value) {
            case "boolean": {
                return BOOLEAN;
            }
            case "integer": {
                return INTEGER;
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
            case "liststring": {
                return LISTSTRING;
            }
            case "anyURI": {
                return ANYURI;
            }
        }
        throw new IllegalArgumentException("Type " + value + " is unknown");
    }
}

