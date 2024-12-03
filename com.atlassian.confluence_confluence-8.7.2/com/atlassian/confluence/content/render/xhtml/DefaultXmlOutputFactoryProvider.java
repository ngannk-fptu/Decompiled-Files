/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;

public class DefaultXmlOutputFactoryProvider
implements XmlOutputFactoryProvider {
    private final XmlOutputFactory xmlOutputFactory;
    private final XmlOutputFactory xmlFragmentOuputFactory;

    public DefaultXmlOutputFactoryProvider(XmlOutputFactory xmlOutputFactory, XmlOutputFactory xmlFragmentOuputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlFragmentOuputFactory = xmlFragmentOuputFactory;
    }

    @Override
    public XmlOutputFactory getXmlOutputFactory() {
        return this.xmlOutputFactory;
    }

    @Override
    public XmlOutputFactory getXmlFragmentOutputFactory() {
        return this.xmlFragmentOuputFactory;
    }
}

