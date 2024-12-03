/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONUnmarshaller;
import com.sun.jersey.json.impl.JSONHelper;
import com.sun.jersey.json.impl.Stax2JsonFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class BaseJSONUnmarshaller
implements JSONUnmarshaller,
JSONConfigurated {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    protected final Unmarshaller jaxbUnmarshaller;
    private final JAXBContext jaxbContext;
    protected final JSONConfiguration jsonConfig;

    public BaseJSONUnmarshaller(JAXBContext jaxbContext, JSONConfiguration jsonConfig) throws JAXBException {
        this(jaxbContext.createUnmarshaller(), jaxbContext, jsonConfig);
    }

    public BaseJSONUnmarshaller(Unmarshaller jaxbUnmarshaller, JAXBContext jaxbContext, JSONConfiguration jsonConfig) {
        this.jaxbUnmarshaller = jaxbUnmarshaller;
        this.jaxbContext = jaxbContext;
        this.jsonConfig = jsonConfig;
    }

    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfig;
    }

    @Override
    public <T> T unmarshalFromJSON(InputStream inputStream, Class<T> expectedType) throws JAXBException {
        return this.unmarshalFromJSON(new InputStreamReader(inputStream, UTF8), expectedType);
    }

    @Override
    public <T> T unmarshalFromJSON(Reader reader, Class<T> expectedType) throws JAXBException {
        if (this.jsonConfig.isRootUnwrapping() || !expectedType.isAnnotationPresent(XmlRootElement.class)) {
            return (T)this.unmarshalJAXBElementFromJSON(reader, expectedType).getValue();
        }
        return (T)this.jaxbUnmarshaller.unmarshal(this.createXmlStreamReader(reader, expectedType));
    }

    @Override
    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(InputStream inputStream, Class<T> declaredType) throws JAXBException {
        return this.unmarshalJAXBElementFromJSON(new InputStreamReader(inputStream, UTF8), declaredType);
    }

    @Override
    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(Reader reader, Class<T> declaredType) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(this.createXmlStreamReader(reader, declaredType), declaredType);
    }

    private XMLStreamReader createXmlStreamReader(Reader reader, Class expectedType) throws JAXBException {
        try {
            return Stax2JsonFactory.createReader(reader, this.jsonConfig, this.jsonConfig.isRootUnwrapping() ? JSONHelper.getRootElementName(expectedType) : null, expectedType, this.jaxbContext);
        }
        catch (XMLStreamException ex) {
            throw new UnmarshalException("Error creating JSON-based XMLStreamReader", (Throwable)ex);
        }
    }
}

