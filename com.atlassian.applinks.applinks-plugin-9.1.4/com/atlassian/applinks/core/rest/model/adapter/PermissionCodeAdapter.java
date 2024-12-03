/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import com.atlassian.applinks.core.rest.permission.PermissionCode;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PermissionCodeAdapter
extends XmlAdapter<String, PermissionCode> {
    public PermissionCode unmarshal(String v) throws Exception {
        return PermissionCode.valueOf(v);
    }

    public String marshal(PermissionCode v) throws Exception {
        return v.name();
    }
}

