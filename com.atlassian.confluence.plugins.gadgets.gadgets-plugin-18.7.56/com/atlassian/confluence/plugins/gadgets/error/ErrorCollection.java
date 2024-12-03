/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.error;

import com.atlassian.confluence.plugins.gadgets.error.ValidationError;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorCollection {
    @XmlElement
    private Collection<String> errorMessages = new ArrayList<String>();
    @XmlElement
    private Collection<ValidationError> errors = new ArrayList<ValidationError>();

    public ErrorCollection() {
    }

    public ErrorCollection(Collection<String> errorMessages, Collection<ValidationError> errors) {
        this.errorMessages = errorMessages;
        this.errors = errors;
    }
}

