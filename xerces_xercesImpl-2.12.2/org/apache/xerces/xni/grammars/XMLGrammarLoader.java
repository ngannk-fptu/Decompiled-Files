/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.grammars;

import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;

public interface XMLGrammarLoader {
    public String[] getRecognizedFeatures();

    public boolean getFeature(String var1) throws XMLConfigurationException;

    public void setFeature(String var1, boolean var2) throws XMLConfigurationException;

    public String[] getRecognizedProperties();

    public Object getProperty(String var1) throws XMLConfigurationException;

    public void setProperty(String var1, Object var2) throws XMLConfigurationException;

    public void setLocale(Locale var1);

    public Locale getLocale();

    public void setErrorHandler(XMLErrorHandler var1);

    public XMLErrorHandler getErrorHandler();

    public void setEntityResolver(XMLEntityResolver var1);

    public XMLEntityResolver getEntityResolver();

    public Grammar loadGrammar(XMLInputSource var1) throws IOException, XNIException;
}

