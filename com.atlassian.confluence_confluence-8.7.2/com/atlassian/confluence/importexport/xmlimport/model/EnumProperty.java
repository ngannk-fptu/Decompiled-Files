/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.model;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedProperty;

@Deprecated
public class EnumProperty
extends ImportedProperty {
    private final String packageName;
    private final String className;
    private final String value;

    public EnumProperty(String name, String packageName, String className, String value) {
        super(name);
        this.packageName = packageName;
        this.className = className;
        this.value = value;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return super.toString() + this.packageName + "." + this.className + "[" + this.value + "]";
    }

    public Object getEnumValue() throws ClassNotFoundException {
        Class<?> clazz;
        Class<?> enumClass = clazz = Class.forName(this.packageName + "." + this.className);
        return Enum.valueOf(enumClass, this.value);
    }
}

