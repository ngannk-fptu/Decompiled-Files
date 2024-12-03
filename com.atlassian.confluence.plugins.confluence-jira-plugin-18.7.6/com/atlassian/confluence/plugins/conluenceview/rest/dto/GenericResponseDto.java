/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.conluenceview.rest.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement
public class GenericResponseDto
implements Serializable {
    private int status;
    private String errorMessage;

    protected GenericResponseDto(Builder builder) {
        this.status = builder.status;
        this.errorMessage = builder.errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Builder {
        protected int status = 200;
        protected String errorMessage = "";

        public Builder withStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public GenericResponseDto build() {
            return new GenericResponseDto(this);
        }
    }
}

