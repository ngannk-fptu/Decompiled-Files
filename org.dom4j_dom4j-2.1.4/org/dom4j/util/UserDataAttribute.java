/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.util;

import org.dom4j.QName;
import org.dom4j.tree.DefaultAttribute;

public class UserDataAttribute
extends DefaultAttribute {
    private Object data;

    public UserDataAttribute(QName qname) {
        super(qname);
    }

    public UserDataAttribute(QName qname, String text) {
        super(qname, text);
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }
}

