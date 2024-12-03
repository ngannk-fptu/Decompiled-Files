/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.impl.provider.xml.SecureSAXParserFactory;
import com.sun.jersey.core.impl.provider.xml.ThreadLocalSingletonContextProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;

public class SAXParserContextProvider
extends ThreadLocalSingletonContextProvider<SAXParserFactory> {
    private final boolean disableXmlSecurity;

    public SAXParserContextProvider(@Context FeaturesAndProperties fps) {
        super(SAXParserFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }

    @Override
    protected SAXParserFactory getInstance() {
        SAXParserFactory f = SAXParserFactory.newInstance();
        f.setNamespaceAware(true);
        if (!this.disableXmlSecurity) {
            f = new SecureSAXParserFactory(f);
        }
        return f;
    }
}

