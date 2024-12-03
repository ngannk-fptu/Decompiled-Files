/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.xml;

import com.sun.jersey.core.impl.provider.xml.ThreadLocalSingletonContextProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.Context;
import javax.xml.transform.TransformerFactory;

public class TransformerFactoryProvider
extends ThreadLocalSingletonContextProvider<TransformerFactory> {
    private final boolean disableXmlSecurity;

    public TransformerFactoryProvider(@Context FeaturesAndProperties fps) {
        super(TransformerFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }

    @Override
    protected TransformerFactory getInstance() {
        TransformerFactory f = TransformerFactory.newInstance();
        if (!this.disableXmlSecurity) {
            // empty if block
        }
        return f;
    }
}

