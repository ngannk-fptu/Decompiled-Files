/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import com.sun.jersey.server.wadl.generators.resourcedoc.model.ClassDocType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="resourceDoc", propOrder={"classDoc"})
@XmlRootElement(name="resourceDoc")
public class ResourceDocType {
    @XmlElementWrapper(name="classDocs")
    protected List<ClassDocType> classDoc;

    public List<ClassDocType> getDocs() {
        if (this.classDoc == null) {
            this.classDoc = new ArrayList<ClassDocType>();
        }
        return this.classDoc;
    }
}

