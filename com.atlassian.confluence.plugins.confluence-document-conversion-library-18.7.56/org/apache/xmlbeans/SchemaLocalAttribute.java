/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaAnnotated;
import org.apache.xmlbeans.SchemaField;

public interface SchemaLocalAttribute
extends SchemaField,
SchemaAnnotated {
    public static final int PROHIBITED = 1;
    public static final int OPTIONAL = 2;
    public static final int REQUIRED = 3;

    public int getUse();
}

