/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.license.rest.model;

import javax.xml.bind.annotation.XmlElement;

public class UserCountResourceModel {
    @XmlElement
    private final Integer count;

    public UserCountResourceModel(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return this.count;
    }
}

