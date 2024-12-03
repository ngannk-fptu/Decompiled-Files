/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public interface XMLPullParserConfiguration
extends XMLParserConfiguration {
    public void setInputSource(XMLInputSource var1) throws XMLConfigurationException, IOException;

    public boolean parse(boolean var1) throws XNIException, IOException;

    public void cleanup();
}

