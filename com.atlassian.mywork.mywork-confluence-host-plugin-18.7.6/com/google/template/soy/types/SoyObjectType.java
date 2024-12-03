/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types;

import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.types.SoyType;

public interface SoyObjectType
extends SoyType {
    public String getName();

    public String getNameForBackend(SoyBackendKind var1);

    public SoyType getFieldType(String var1);

    public String getFieldAccessor(String var1, SoyBackendKind var2);

    public String getFieldImport(String var1, SoyBackendKind var2);
}

