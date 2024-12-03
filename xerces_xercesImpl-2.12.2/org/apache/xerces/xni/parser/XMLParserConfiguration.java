/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;

public interface XMLParserConfiguration
extends XMLComponentManager {
    public void parse(XMLInputSource var1) throws XNIException, IOException;

    public void addRecognizedFeatures(String[] var1);

    public void setFeature(String var1, boolean var2) throws XMLConfigurationException;

    @Override
    public boolean getFeature(String var1) throws XMLConfigurationException;

    public void addRecognizedProperties(String[] var1);

    public void setProperty(String var1, Object var2) throws XMLConfigurationException;

    @Override
    public Object getProperty(String var1) throws XMLConfigurationException;

    public void setErrorHandler(XMLErrorHandler var1);

    public XMLErrorHandler getErrorHandler();

    public void setDocumentHandler(XMLDocumentHandler var1);

    public XMLDocumentHandler getDocumentHandler();

    public void setDTDHandler(XMLDTDHandler var1);

    public XMLDTDHandler getDTDHandler();

    public void setDTDContentModelHandler(XMLDTDContentModelHandler var1);

    public XMLDTDContentModelHandler getDTDContentModelHandler();

    public void setEntityResolver(XMLEntityResolver var1);

    public XMLEntityResolver getEntityResolver();

    public void setLocale(Locale var1) throws XNIException;

    public Locale getLocale();
}

