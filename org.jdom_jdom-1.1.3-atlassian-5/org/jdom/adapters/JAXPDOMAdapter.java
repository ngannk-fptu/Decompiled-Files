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
import org.jdom.input.BuilderErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;

public class JAXPDOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: JAXPDOMAdapter.java,v $ $Revision: 1.13 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    @Override
    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class.forName("javax.xml.transform.Transformer");
            Class<?> factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method setValidating = factoryClass.getMethod("setValidating", Boolean.TYPE);
            setValidating.invoke(factory, new Boolean(validate));
            Method setNamespaceAware = factoryClass.getMethod("setNamespaceAware", Boolean.TYPE);
            setNamespaceAware.invoke(factory, Boolean.TRUE);
            Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
            Object jaxpParser = newDocBuilder.invoke(factory, null);
            Class<?> parserClass = jaxpParser.getClass();
            Method setErrorHandler = parserClass.getMethod("setErrorHandler", ErrorHandler.class);
            setErrorHandler.invoke(jaxpParser, new BuilderErrorHandler());
            Method parse = parserClass.getMethod("parse", InputStream.class);
            Document domDoc = (Document)parse.invoke(jaxpParser, in);
            return domDoc;
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof IOException) {
                throw (IOException)targetException;
            }
            throw new JDOMException(targetException.getMessage(), targetException);
        }
        catch (Exception e) {
            throw new JDOMException("Reflection failed while parsing a document with JAXP", e);
        }
    }

    @Override
    public Document createDocument() throws JDOMException {
        try {
            Class.forName("javax.xml.transform.Transformer");
            Class<?> factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
            Object jaxpParser = newDocBuilder.invoke(factory, null);
            Class<?> parserClass = jaxpParser.getClass();
            Method newDoc = parserClass.getMethod("newDocument", null);
            Document domDoc = (Document)newDoc.invoke(jaxpParser, null);
            return domDoc;
        }
        catch (Exception e) {
            throw new JDOMException("Reflection failed while creating new JAXP document", e);
        }
    }
}

