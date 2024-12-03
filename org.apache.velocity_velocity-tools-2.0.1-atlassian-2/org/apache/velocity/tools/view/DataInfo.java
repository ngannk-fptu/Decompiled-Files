/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view;

import org.apache.velocity.tools.view.ToolInfo;

@Deprecated
public class DataInfo
implements ToolInfo {
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_BOOLEAN = "boolean";
    private static final int TYPE_ID_STRING = 0;
    private static final int TYPE_ID_NUMBER = 1;
    private static final int TYPE_ID_BOOLEAN = 2;
    private String key = null;
    private int type_id = 0;
    private Object data = null;

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type_id = TYPE_BOOLEAN.equalsIgnoreCase(type) ? 2 : (TYPE_NUMBER.equalsIgnoreCase(type) ? 1 : 0);
    }

    public void setValue(String value) {
        this.data = this.type_id == 2 ? Boolean.valueOf(value) : (this.type_id == 1 ? (value.indexOf(46) >= 0 ? (Number)new Double(value) : (Number)new Integer(value)) : value);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getClassname() {
        return this.data != null ? this.data.getClass().getName() : null;
    }

    @Override
    public Object getInstance(Object initData) {
        return this.data;
    }
}

