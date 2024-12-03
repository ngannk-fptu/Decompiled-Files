/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Deprecated
@XmlRootElement(name="error")
public class ErrorListEntity {
    @XmlAttribute
    private int status;
    @XmlElement(name="message")
    private List<String> errors;
    @XmlElement(name="fields")
    private List<String> fields;

    public ErrorListEntity() {
    }

    public ErrorListEntity(int status, String ... errors) {
        this(status, Arrays.asList(errors));
    }

    public ErrorListEntity(int status, List<String> errors, List<String> fields) {
        this(status, errors);
        this.fields = fields;
    }

    public ErrorListEntity(int status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public List<String> getFields() {
        return this.fields;
    }
}

