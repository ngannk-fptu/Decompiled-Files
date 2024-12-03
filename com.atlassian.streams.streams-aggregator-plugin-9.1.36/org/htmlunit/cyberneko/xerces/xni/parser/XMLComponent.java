/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;

public interface XMLComponent {
    public void reset(XMLComponentManager var1) throws XMLConfigurationException;

    public String[] getRecognizedFeatures();

    public void setFeature(String var1, boolean var2) throws XMLConfigurationException;

    public String[] getRecognizedProperties();

    public void setProperty(String var1, Object var2) throws XMLConfigurationException;

    public Boolean getFeatureDefault(String var1);

    public Object getPropertyDefault(String var1);
}

