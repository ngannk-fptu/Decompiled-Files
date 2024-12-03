/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value=XmlAccessType.FIELD)
public class CountItem {
    private final int count;
    private final String cssClass;

    public CountItem(int count, String cssClass) {
        this.count = count;
        this.cssClass = cssClass;
    }

    public int getCount() {
        return this.count;
    }

    public String getCssClass() {
        return this.cssClass;
    }
}

