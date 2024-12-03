/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.security.xml.libs;

import com.atlassian.security.xml.SecureXmlParserFactory;
import org.dom4j.io.SAXReader;

public class SecureDom4jFactory {
    private SecureDom4jFactory() {
    }

    public static SAXReader newSaxReader() {
        SAXReader saxReader = new SAXReader(SecureXmlParserFactory.newXmlReader());
        return saxReader;
    }
}

