/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;

public interface XMLComponentManager {
    public boolean getFeature(String var1) throws XMLConfigurationException;

    public Object getProperty(String var1) throws XMLConfigurationException;
}

