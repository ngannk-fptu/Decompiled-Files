/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.core.rest.model.adapter;

import java.net.URI;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang3.StringUtils;

public class RequiredBaseURIAdapter
extends XmlAdapter<String, URI> {
    public URI unmarshal(String v) throws Exception {
        return new URI(StringUtils.stripEnd((String)v, (String)"/"));
    }

    public String marshal(URI v) throws Exception {
        return v.toString();
    }
}

