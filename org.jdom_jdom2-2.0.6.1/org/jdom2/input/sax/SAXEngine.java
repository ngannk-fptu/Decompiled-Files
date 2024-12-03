/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.JDOMFactory;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

public interface SAXEngine {
    public JDOMFactory getJDOMFactory();

    public boolean isValidating();

    public ErrorHandler getErrorHandler();

    public EntityResolver getEntityResolver();

    public DTDHandler getDTDHandler();

    public boolean getIgnoringElementContentWhitespace();

    public boolean getIgnoringBoundaryWhitespace();

    public boolean getExpandEntities();

    public Document build(InputSource var1) throws JDOMException, IOException;

    public Document build(InputStream var1) throws JDOMException, IOException;

    public Document build(File var1) throws JDOMException, IOException;

    public Document build(URL var1) throws JDOMException, IOException;

    public Document build(InputStream var1, String var2) throws JDOMException, IOException;

    public Document build(Reader var1) throws JDOMException, IOException;

    public Document build(Reader var1, String var2) throws JDOMException, IOException;

    public Document build(String var1) throws JDOMException, IOException;
}

