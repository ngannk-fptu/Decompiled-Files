/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import java.net.URI;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RequiredURIAdapter
extends XmlAdapter<String, URI> {
    public URI unmarshal(String v) throws Exception {
        return new URI(v);
    }

    public String marshal(URI v) throws Exception {
        return v.toString();
    }
}

