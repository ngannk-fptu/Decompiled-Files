/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types;

import com.google.template.soy.data.SoyValue;

public interface SoyType {
    public Kind getKind();

    public boolean isAssignableFrom(SoyType var1);

    public boolean isInstance(SoyValue var1);

    public static enum Kind {
        ANY,
        UNKNOWN,
        ERROR,
        NULL,
        BOOL,
        INT,
        FLOAT,
        STRING,
        HTML,
        ATTRIBUTES,
        JS,
        CSS,
        URI,
        LIST,
        RECORD,
        MAP,
        OBJECT,
        ENUM,
        UNION,
        SPECIALIZED;

    }
}

