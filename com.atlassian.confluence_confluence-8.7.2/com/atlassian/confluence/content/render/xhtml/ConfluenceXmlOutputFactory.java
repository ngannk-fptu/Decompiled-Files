/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConfluenceXMLEventWriter;
import com.atlassian.confluence.content.render.xhtml.DelegateXmlOutputFactory;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

public class ConfluenceXmlOutputFactory
extends DelegateXmlOutputFactory {
    public ConfluenceXmlOutputFactory(XMLOutputFactory xmlOutputFactory) {
        super(xmlOutputFactory);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return new ConfluenceXMLEventWriter(super.createXMLEventWriter(writer), writer);
    }

    public static ConfluenceXmlOutputFactory create() {
        ConfluenceXmlOutputFactory xmlOutputFactory = new ConfluenceXmlOutputFactory(XMLOutputFactory.newInstance());
        xmlOutputFactory.setProperty("com.ctc.wstx.addSpaceAfterEmptyElem", true);
        return xmlOutputFactory;
    }

    public static ConfluenceXmlOutputFactory createFragmentXmlOutputFactory() {
        ConfluenceXmlOutputFactory outputFactory = ConfluenceXmlOutputFactory.create();
        outputFactory.setProperty("com.ctc.wstx.outputValidateStructure", Boolean.FALSE);
        outputFactory.setProperty("com.ctc.wstx.automaticEndElements", Boolean.FALSE);
        return outputFactory;
    }
}

