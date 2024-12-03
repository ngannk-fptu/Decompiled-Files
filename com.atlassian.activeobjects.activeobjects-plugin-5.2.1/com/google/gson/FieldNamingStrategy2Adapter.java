/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.FieldNamingStrategy2;
import com.google.gson.internal.$Gson$Preconditions;

final class FieldNamingStrategy2Adapter
implements FieldNamingStrategy2 {
    private final FieldNamingStrategy adaptee;

    FieldNamingStrategy2Adapter(FieldNamingStrategy adaptee) {
        this.adaptee = $Gson$Preconditions.checkNotNull(adaptee);
    }

    public String translateName(FieldAttributes f) {
        return this.adaptee.translateName(f.getFieldObject());
    }
}

