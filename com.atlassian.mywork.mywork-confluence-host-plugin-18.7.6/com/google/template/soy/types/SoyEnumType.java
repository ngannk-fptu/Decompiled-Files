/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.types;

import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.types.SoyType;
import javax.annotation.Nullable;

public interface SoyEnumType
extends SoyType {
    public String getName();

    public String getNameForBackend(SoyBackendKind var1);

    @Nullable
    public Integer getValue(String var1);
}

