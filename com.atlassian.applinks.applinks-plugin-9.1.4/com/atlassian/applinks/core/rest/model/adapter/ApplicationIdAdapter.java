/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import com.atlassian.applinks.api.ApplicationId;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ApplicationIdAdapter
extends XmlAdapter<String, ApplicationId> {
    public ApplicationId unmarshal(String v) throws Exception {
        return new ApplicationId(v);
    }

    public String marshal(ApplicationId v) throws Exception {
        return v.toString();
    }
}

