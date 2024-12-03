/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.core.rest.model.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.osgi.framework.Version;

public class VersionAdapter
extends XmlAdapter<String, Version> {
    public Version unmarshal(String v) throws Exception {
        return new Version(v);
    }

    public String marshal(Version v) throws Exception {
        return v.toString();
    }
}

