/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jdom.JDOMException;
import org.jdom.adapters.AbstractDOMAdapter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class OracleV2DOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: OracleV2DOMAdapter.java,v $ $Revision: 1.19 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    @Override
    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class<?> parserClass = Class.forName("oracle.xml.parser.v2.DOMParser");
            Object parser = parserClass.newInstance();
            Method parse = parserClass.getMethod("parse", InputSource.class);
            parse.invoke(parser, new InputSource(in));
            Method getDocument = parserClass.getMethod("getDocument", null);
            Document doc = (Document)getDocument.invoke(parser, null);
            return doc;
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException)targetException;
                throw new JDOMException("Error on line " + parseException.getLineNumber() + " of XML document: " + parseException.getMessage(), parseException);
            }
            if (targetException instanceof IOException) {
                IOException ioException = (IOException)targetException;
                throw ioException;
            }
            throw new JDOMException(targetException.getMessage(), targetException);
        }
        catch (Exception e) {
            throw new JDOMException(e.getClass().getName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Document createDocument() throws JDOMException {
        try {
            return (Document)Class.forName("oracle.xml.parser.v2.XMLDocument").newInstance();
        }
        catch (Exception e) {
            throw new JDOMException(e.getClass().getName() + ": " + e.getMessage() + " when creating document", e);
        }
    }
}

