/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ApplicationLinkState;
import com.atlassian.applinks.core.rest.model.adapter.ApplicationLinkStateAdapter;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="applicationLinkState")
public class ApplicationLinkStateEntity {
    @XmlJavaTypeAdapter(value=ApplicationLinkStateAdapter.class)
    private ApplicationLinkState appLinkState;

    private ApplicationLinkStateEntity() {
    }

    public ApplicationLinkStateEntity(ApplicationLinkState appLinkState) {
        this.appLinkState = appLinkState;
    }

    public ApplicationLinkState getAppLinkState() {
        return this.appLinkState;
    }
}

