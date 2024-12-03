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
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
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
    private ResourceErrorType errorType;
    @XmlElement
    private Object errorData;
    @XmlElement
    private String errorMessage;

    private ResourceErrorBean() {
    }

    public ResourceErrorBean(int errorCode, @Nonnull ResourceErrorType errorType, @Nullable Object errorData, @Nonnull String errorMessage) {
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorData = errorData;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public ResourceErrorType getErrorType() {
        return this.errorType;
    }

    public Object getErrorData() {
        return this.errorData;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}

