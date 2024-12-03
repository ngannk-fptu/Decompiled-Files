/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import com.sun.jersey.server.wadl.generators.resourcedoc.model.AnnotationDocType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="paramDoc", propOrder={})
public class ParamDocType {
    private String paramName;
    private String commentText;
    @XmlElementWrapper(name="annotationDocs")
    protected List<AnnotationDocType> annotationDoc;
    @XmlAnyElement(lax=true)
    private List<Object> any;

    public ParamDocType() {
    }

    public ParamDocType(String paramName, String commentText) {
        this.paramName = paramName;
        this.commentText = commentText;
    }

    public List<AnnotationDocType> getAnnotationDocs() {
        if (this.annotationDoc == null) {
            this.annotationDoc = new ArrayList<AnnotationDocType>();
        }
        return this.annotationDoc;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }

    public String getCommentText() {
        return this.commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}

