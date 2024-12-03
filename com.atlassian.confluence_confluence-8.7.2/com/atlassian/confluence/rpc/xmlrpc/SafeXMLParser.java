/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 */
package com.atlassian.confluence.rpc.xmlrpc;

import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.IOException;
import java.util.Locale;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

@Deprecated
public class SafeXMLParser
implements Parser {
    private static final String DELEGATE_CLASS_NAME = "org.apache.xerces.parsers.SAXParser";
    private final Parser delegate;

    public SafeXMLParser() {
        try {
            XMLReader parser = (XMLReader)Class.forName(DELEGATE_CLASS_NAME).newInstance();
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setEntityResolver(SecureXmlParserFactory.emptyEntityResolver());
            this.delegate = new XMLReaderAdapter(parser);
        }
        catch (ReflectiveOperationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void parse(InputSource inputSource) throws SAXException, IOException {
        this.delegate.parse(inputSource);
    }

    @Override
    public void parse(String s) throws SAXException, IOException {
        this.delegate.parse(s);
    }

    @Override
    public void setDocumentHandler(DocumentHandler documentHandler) {
        this.delegate.setDocumentHandler(documentHandler);
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        throw new UnsupportedOperationException("You are not allowed to override the entity resolver.");
    }

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.delegate.setDTDHandler(dtdHandler);
    }

    @Override
    public void setLocale(Locale locale) throws SAXException {
        this.delegate.setLocale(locale);
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.delegate.setErrorHandler(errorHandler);
    }
}

