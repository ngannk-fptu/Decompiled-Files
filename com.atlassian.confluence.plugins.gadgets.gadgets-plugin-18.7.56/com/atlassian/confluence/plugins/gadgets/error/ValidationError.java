/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.error;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValidationError {
    @XmlElement
    private String field;
    @XmlElement
    private String error;

    public ValidationError() {
    }

    public ValidationError(String field, String error) {
        this.field = field;
        this.error = error;
    }
}

