/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.Digester
 */
package com.atlassian.security.xml.libs;

import com.atlassian.security.xml.SecureXmlParserFactory;
import org.apache.commons.digester.Digester;

public class SecureDigesterFactory {
    private SecureDigesterFactory() {
    }

    public static Digester newDigester() {
        Digester digester = new Digester(SecureXmlParserFactory.newXmlReader());
        return digester;
    }
}

