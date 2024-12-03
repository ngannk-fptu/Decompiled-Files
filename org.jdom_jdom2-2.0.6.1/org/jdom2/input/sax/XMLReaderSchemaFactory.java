/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.jdom2.input.sax.AbstractReaderSchemaFactory;

public class XMLReaderSchemaFactory
extends AbstractReaderSchemaFactory {
    public XMLReaderSchemaFactory(Schema schema) {
        super(SAXParserFactory.newInstance(), schema);
    }

    public XMLReaderSchemaFactory(String factoryClassName, ClassLoader classloader, Schema schema) {
        super(SAXParserFactory.newInstance(factoryClassName, classloader), schema);
    }
}

