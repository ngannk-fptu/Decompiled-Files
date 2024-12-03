/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.impl.provider.xml.ThreadLocalSingletonContextProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.Context;
import javax.xml.stream.XMLInputFactory;

public class XMLStreamReaderContextProvider
extends ThreadLocalSingletonContextProvider<XMLInputFactory> {
    private final boolean disableXmlSecurity;

    public XMLStreamReaderContextProvider(@Context FeaturesAndProperties fps) {
        super(XMLInputFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }

    @Override
    protected XMLInputFactory getInstance() {
        XMLInputFactory f = XMLInputFactory.newInstance();
        if (!this.disableXmlSecurity) {
            f.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        }
        return f;
    }
}

