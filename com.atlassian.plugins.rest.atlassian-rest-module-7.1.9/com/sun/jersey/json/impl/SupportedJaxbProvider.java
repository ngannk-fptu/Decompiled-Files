/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.JaxbJdkXmlStructure;
import com.sun.jersey.json.impl.JaxbProvider;
import com.sun.jersey.json.impl.JaxbRiXmlStructure;
import com.sun.jersey.json.impl.MoxyXmlStructure;

public enum SupportedJaxbProvider implements JaxbProvider
{
    JAXB_RI("com.sun.xml.bind.v2.runtime.JAXBContextImpl", JaxbRiXmlStructure.class),
    MOXY("org.eclipse.persistence.jaxb.JAXBContext", MoxyXmlStructure.class),
    JAXB_JDK("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl", JaxbJdkXmlStructure.class);

    private final String jaxbContextClassName;
    private final Class<? extends DefaultJaxbXmlDocumentStructure> documentStructureClass;

    private SupportedJaxbProvider(String jaxbContextClassName, Class<? extends DefaultJaxbXmlDocumentStructure> documentStructureClass) {
        this.jaxbContextClassName = jaxbContextClassName;
        this.documentStructureClass = documentStructureClass;
    }

    public Class<? extends DefaultJaxbXmlDocumentStructure> getDocumentStructureClass() {
        return this.documentStructureClass;
    }

    @Override
    public String getJaxbContextClassName() {
        return this.jaxbContextClassName;
    }
}

