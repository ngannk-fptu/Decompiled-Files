/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import com.sun.jersey.server.wadl.generators.resourcedoc.model.NamedValueType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="annotationDoc", propOrder={})
public class AnnotationDocType {
    private String annotationTypeName;
    @XmlElementWrapper(name="attributes")
    protected List<NamedValueType> attribute;

    public List<NamedValueType> getAttributeDocs() {
        if (this.attribute == null) {
            this.attribute = new ArrayList<NamedValueType>();
        }
        return this.attribute;
    }

    public boolean hasAttributeDocs() {
        return this.attribute != null && !this.attribute.isEmpty();
    }

    public String getAnnotationTypeName() {
        return this.annotationTypeName;
    }

    public void setAnnotationTypeName(String annotationTypeName) {
        this.annotationTypeName = annotationTypeName;
    }
}

