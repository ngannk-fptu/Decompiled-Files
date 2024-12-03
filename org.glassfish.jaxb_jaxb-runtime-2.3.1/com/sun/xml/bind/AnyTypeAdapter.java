/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.sun.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class AnyTypeAdapter
extends XmlAdapter<Object, Object> {
    public Object unmarshal(Object v) {
        return v;
    }

    public Object marshal(Object v) {
        return v;
    }
}

