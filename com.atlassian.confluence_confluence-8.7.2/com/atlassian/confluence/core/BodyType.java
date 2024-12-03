/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.api.model.content.ContentRepresentation;
import java.io.Serializable;

public class BodyType
implements Serializable {
    private final int id;
    private static final int ID_UNKNOWN = -1;
    private static final int ID_WIKI = 0;
    private static final int ID_RAW = 1;
    private static final int ID_XHTML = 2;
    public static final BodyType UNKNOWN = new BodyType(-1);
    public static final BodyType WIKI = new BodyType(0);
    public static final BodyType RAW = new BodyType(1);
    public static final BodyType XHTML = new BodyType(2);

    public BodyType(int id) {
        this.id = id;
    }

    public int toInt() {
        return this.id;
    }

    public static BodyType fromInt(int id) {
        switch (id) {
            case 0: {
                return WIKI;
            }
            case 1: {
                return RAW;
            }
            case 2: {
                return XHTML;
            }
        }
        throw new RuntimeException("Unable to determine BodyType for id: " + id);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("BodyType:");
        switch (this.id) {
            case 0: {
                builder.append("WIKI");
                break;
            }
            case 1: {
                builder.append("RAW");
                break;
            }
            case 2: {
                builder.append("XHTML");
                break;
            }
            default: {
                builder.append("id=").append(this.id);
            }
        }
        return builder.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BodyType)) {
            return false;
        }
        BodyType other = (BodyType)obj;
        return this.id == other.id;
    }

    public int hashCode() {
        return 13 * (this.id + 1);
    }

    public ContentRepresentation toContentRepresentation() {
        switch (this.toInt()) {
            case 1: {
                return ContentRepresentation.RAW;
            }
            case 0: {
                return ContentRepresentation.WIKI;
            }
            case 2: {
                return ContentRepresentation.STORAGE;
            }
        }
        throw new IllegalStateException("Unknown body type id : " + this.toInt());
    }
}

