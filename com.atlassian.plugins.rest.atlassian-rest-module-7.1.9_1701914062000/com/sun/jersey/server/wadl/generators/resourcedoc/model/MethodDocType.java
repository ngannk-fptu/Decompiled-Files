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

import com.sun.jersey.server.wadl.generators.resourcedoc.model.ParamDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.RequestDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResponseDocType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="methodDoc", propOrder={})
public class MethodDocType {
    private String methodName;
    protected String commentText;
    private String returnDoc;
    private String returnTypeExample;
    private RequestDocType requestDoc;
    private ResponseDocType responseDoc;
    @XmlElementWrapper(name="paramDocs")
    protected List<ParamDocType> paramDoc;
    @XmlAnyElement(lax=true)
    private List<Object> any;

    public String getCommentText() {
        return this.commentText;
    }

    public void setCommentText(String value) {
        this.commentText = value;
    }

    public List<ParamDocType> getParamDocs() {
        if (this.paramDoc == null) {
            this.paramDoc = new ArrayList<ParamDocType>();
        }
        return this.paramDoc;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnDoc() {
        return this.returnDoc;
    }

    public void setReturnDoc(String returnDoc) {
        this.returnDoc = returnDoc;
    }

    public String getReturnTypeExample() {
        return this.returnTypeExample;
    }

    public void setReturnTypeExample(String returnTypeExample) {
        this.returnTypeExample = returnTypeExample;
    }

    public RequestDocType getRequestDoc() {
        return this.requestDoc;
    }

    public void setRequestDoc(RequestDocType requestDoc) {
        this.requestDoc = requestDoc;
    }

    public ResponseDocType getResponseDoc() {
        return this.responseDoc;
    }

    public void setResponseDoc(ResponseDocType responseDoc) {
        this.responseDoc = responseDoc;
    }
}

