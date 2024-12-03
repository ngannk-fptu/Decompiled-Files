/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import java.net.URI;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OptionalURIAdapter
extends XmlAdapter<String, URI> {
    public URI unmarshal(String v) throws Exception {
        return v == null ? null : new URI(v);
    }

    public String marshal(URI v) throws Exception {
        return v == null ? null : v.toString();
    }
}

