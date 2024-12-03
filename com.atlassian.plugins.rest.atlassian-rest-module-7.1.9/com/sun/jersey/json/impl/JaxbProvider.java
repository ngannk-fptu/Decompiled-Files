/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;

public interface JaxbProvider {
    public String getJaxbContextClassName();

    public Class<? extends JaxbXmlDocumentStructure> getDocumentStructureClass();
}

