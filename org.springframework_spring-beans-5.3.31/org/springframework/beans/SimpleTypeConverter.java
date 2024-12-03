/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.beans.TypeConverterDelegate;
import org.springframework.beans.TypeConverterSupport;

public class SimpleTypeConverter
extends TypeConverterSupport {
    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        this.registerDefaultEditors();
    }
}

