/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.rest;

import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.rest.ModificationBean;

public class XMLModificationBean
extends ModificationBean {
    private XMLModification xmlModification;

    public XMLModification getXmlModification() {
        return this.xmlModification;
    }

    public void setXmlModification(XMLModification xmlModification) {
        this.xmlModification = xmlModification;
    }
}

