/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import com.sun.jersey.server.wadl.generators.resourcedoc.model.RepresentationDocType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="requestDoc", propOrder={})
public class RequestDocType {
    private RepresentationDocType representationDoc;

    public RepresentationDocType getRepresentationDoc() {
        return this.representationDoc;
    }

    public void setRepresentationDoc(RepresentationDocType representationDoc) {
        this.representationDoc = representationDoc;
    }
}

