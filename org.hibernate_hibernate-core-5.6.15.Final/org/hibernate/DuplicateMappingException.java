/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.MappingException;

public class DuplicateMappingException
extends MappingException {
    private final String name;
    private final String type;

    public DuplicateMappingException(Type type, String name) {
        this(type.text, name);
    }

    @Deprecated
    public DuplicateMappingException(String type, String name) {
        this("Duplicate " + type + " mapping " + name, type, name);
    }

    public DuplicateMappingException(String customMessage, Type type, String name) {
        this(customMessage, type.name(), name);
    }

    @Deprecated
    public DuplicateMappingException(String customMessage, String type, String name) {
        super(customMessage);
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public static enum Type {
        ENTITY("entity"),
        COLLECTION("collection"),
        TABLE("table"),
        PROPERTY("property"),
        COLUMN("column"),
        COLUMN_BINDING("column-binding"),
        NAMED_ENTITY_GRAPH("NamedEntityGraph"),
        QUERY("query"),
        RESULT_SET_MAPPING("ResultSetMapping"),
        PROCEDURE("NamedStoredProcedureQuery");

        private final String text;

        private Type(String text) {
            this.text = text;
        }
    }
}

