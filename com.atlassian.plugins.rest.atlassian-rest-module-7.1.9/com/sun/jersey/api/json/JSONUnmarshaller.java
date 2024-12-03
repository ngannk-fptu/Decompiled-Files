/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 */
package com.sun.jersey.api.json;

import java.io.InputStream;
import java.io.Reader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

public interface JSONUnmarshaller {
    public <T> T unmarshalFromJSON(InputStream var1, Class<T> var2) throws JAXBException;

    public <T> T unmarshalFromJSON(Reader var1, Class<T> var2) throws JAXBException;

    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(InputStream var1, Class<T> var2) throws JAXBException;

    public <T> JAXBElement<T> unmarshalJAXBElementFromJSON(Reader var1, Class<T> var2) throws JAXBException;
}

