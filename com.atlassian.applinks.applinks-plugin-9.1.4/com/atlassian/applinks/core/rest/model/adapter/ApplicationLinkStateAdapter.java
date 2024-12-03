/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.applinks.core.rest.model.adapter;

import com.atlassian.applinks.core.rest.model.ApplicationLinkState;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ApplicationLinkStateAdapter
extends XmlAdapter<String, ApplicationLinkState> {
    public String marshal(ApplicationLinkState applicationStatus) throws Exception {
        return applicationStatus == null ? null : applicationStatus.name();
    }

    public ApplicationLinkState unmarshal(String s) throws Exception {
        return s == null ? null : ApplicationLinkState.valueOf(s);
    }
}

