/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.application.TypeId
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import com.atlassian.applinks.spi.application.TypeId;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TypeIdAdapter
extends XmlAdapter<String, TypeId> {
    public TypeId unmarshal(String v) throws Exception {
        return new TypeId(v);
    }

    public String marshal(TypeId v) throws Exception {
        return v.get();
    }
}

