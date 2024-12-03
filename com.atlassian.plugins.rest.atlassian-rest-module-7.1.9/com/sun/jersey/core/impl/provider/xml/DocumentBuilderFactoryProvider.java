/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.impl.provider.xml.ThreadLocalSingletonContextProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryProvider
extends ThreadLocalSingletonContextProvider<DocumentBuilderFactory> {
    private static final Logger LOGGER = Logger.getLogger(DocumentBuilderFactoryProvider.class.getName());
    private final boolean disableXmlSecurity;

    public DocumentBuilderFactoryProvider(@Context FeaturesAndProperties fps) {
        super(DocumentBuilderFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }

    @Override
    protected DocumentBuilderFactory getInstance() {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        if (!this.disableXmlSecurity) {
            f.setExpandEntityReferences(false);
        }
        return f;
    }
}

