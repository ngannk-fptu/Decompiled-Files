/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

public class XNull
extends XNodeSet {
    static final long serialVersionUID = -6841683711458983005L;

    @Override
    public int getType() {
        return -1;
    }

    @Override
    public String getTypeString() {
        return "#CLASS_NULL";
    }

    @Override
    public double num() {
        return 0.0;
    }

    @Override
    public boolean bool() {
        return false;
    }

    @Override
    public String str() {
        return "";
    }

    @Override
    public int rtf(XPathContext support) {
        return -1;
    }

    @Override
    public boolean equals(XObject obj2) {
        return obj2.getType() == -1;
    }
}

