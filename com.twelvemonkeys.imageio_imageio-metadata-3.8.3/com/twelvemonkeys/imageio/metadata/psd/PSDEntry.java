/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.imageio.metadata.psd;

import com.twelvemonkeys.imageio.metadata.AbstractEntry;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import com.twelvemonkeys.lang.StringUtil;
import java.lang.reflect.Field;

class PSDEntry
extends AbstractEntry {
    private final String name;

    public PSDEntry(int n, String string, Object object) {
        super(n, object);
        this.name = StringUtil.isEmpty((String)string) ? null : string;
    }

    @Override
    protected String getNativeIdentifier() {
        return String.format("0x%04x", (Integer)this.getIdentifier());
    }

    @Override
    public String getFieldName() {
        Class[] classArray;
        block2: for (Class clazz : classArray = new Class[]{this.getPSDClass()}) {
            Field[] fieldArray;
            for (Field field : fieldArray = clazz.getDeclaredFields()) {
                try {
                    if (field.getType() != Integer.TYPE || !field.getName().startsWith("RES_")) continue;
                    field.setAccessible(true);
                    if (!field.get(null).equals(this.getIdentifier())) continue;
                    String string = StringUtil.lispToCamel((String)field.getName().substring(4).replace("_", "-").toLowerCase(), (boolean)true);
                    return this.name != null ? string + ": " + this.name : string;
                }
                catch (IllegalAccessException illegalAccessException) {
                    continue block2;
                }
            }
        }
        return this.name;
    }

    private Class<?> getPSDClass() {
        try {
            return Class.forName("com.twelvemonkeys.imageio.plugins.psd.PSD");
        }
        catch (ClassNotFoundException classNotFoundException) {
            return PSD.class;
        }
    }
}

