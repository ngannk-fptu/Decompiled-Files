/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.extra.masterdetail.rest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceErrorBean {
    @XmlAttribute
    private int errorCode;
    @XmlAttribute
    private String errorType;
    @XmlElement
    private Object errorData;
    @XmlElement
    private String errorMessage;

    private ResourceErrorBean() {
    }

    public ResourceErrorBean(int errorCode, @Nonnull String errorType, @Nullable Object errorData, @Nonnull String errorMessage) {
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorData = errorData;
        this.errorMessage = errorMessage;
    }
}

