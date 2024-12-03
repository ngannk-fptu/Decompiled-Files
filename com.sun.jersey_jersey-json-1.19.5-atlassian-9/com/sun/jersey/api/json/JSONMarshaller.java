/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.PropertyException
 */
package com.sun.jersey.api.json;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

public interface JSONMarshaller {
    public static final String FORMATTED = "com.sun.jersey.api.json.JSONMarshaller.formatted";

    public void marshallToJSON(Object var1, OutputStream var2) throws JAXBException;

    public void marshallToJSON(Object var1, Writer var2) throws JAXBException;

    public void setProperty(String var1, Object var2) throws PropertyException;
}

