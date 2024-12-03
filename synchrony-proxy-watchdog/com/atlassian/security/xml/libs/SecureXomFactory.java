/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  nu.xom.Builder
 */
package com.atlassian.security.xml.libs;

import com.atlassian.security.xml.SecureXmlParserFactory;
import nu.xom.Builder;

public final class SecureXomFactory {
    private SecureXomFactory() {
    }

    public static Builder newBuilder() {
        return new Builder(SecureXmlParserFactory.newXmlReader());
    }
}

