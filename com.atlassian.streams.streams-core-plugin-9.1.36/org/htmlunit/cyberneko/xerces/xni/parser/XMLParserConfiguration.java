/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import java.io.IOException;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;

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

    public void setInputSource(XMLInputSource var1) throws XMLConfigurationException, IOException;

    public boolean parse(boolean var1) throws XNIException, IOException;

    public void cleanup();
}

