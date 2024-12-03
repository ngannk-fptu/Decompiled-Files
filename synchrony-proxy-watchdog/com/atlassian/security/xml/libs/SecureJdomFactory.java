/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.input.SAXBuilder
 */
package com.atlassian.security.xml.libs;

import com.atlassian.security.xml.SecureXmlParserFactory;
import org.jdom.input.SAXBuilder;
import org.xml.sax.XMLReader;

public class SecureJdomFactory {
    private SecureJdomFactory() {
    }

    public static SAXBuilder newSaxBuilder() {
        return new SAXBuilder(){

            protected XMLReader createParser() {
                return SecureXmlParserFactory.newNamespaceAwareXmlReader();
            }
        };
    }
}

