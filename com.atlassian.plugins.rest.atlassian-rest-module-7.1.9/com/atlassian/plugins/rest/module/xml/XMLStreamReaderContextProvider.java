/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.xml;

import com.sun.jersey.core.impl.provider.xml.ThreadLocalSingletonContextProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.ws.rs.core.Context;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

public class XMLStreamReaderContextProvider
extends ThreadLocalSingletonContextProvider<XMLInputFactory> {
    private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[0]);
    private final boolean disableXmlSecurity;

    public XMLStreamReaderContextProvider(@Context FeaturesAndProperties fps) {
        super(XMLInputFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }

    @Override
    protected XMLInputFactory getInstance() {
        XMLInputFactory f = XMLInputFactory.newInstance();
        if (!this.disableXmlSecurity) {
            f.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
            f.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
            f.setXMLResolver(new XMLResolver(){

                @Override
                public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
                    return EMPTY_INPUT_STREAM;
                }
            });
        }
        return f;
    }
}

