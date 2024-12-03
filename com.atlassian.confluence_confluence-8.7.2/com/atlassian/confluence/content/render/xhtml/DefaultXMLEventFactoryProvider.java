/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import javax.xml.stream.XMLEventFactory;

public class DefaultXMLEventFactoryProvider
implements XMLEventFactoryProvider {
    private XMLEventFactory xmlEventFactory;

    public DefaultXMLEventFactoryProvider(XMLEventFactory xmlEventFactory) {
        this.xmlEventFactory = xmlEventFactory;
    }

    @Override
    public XMLEventFactory getXmlEventFactory() {
        return this.xmlEventFactory;
    }
}

