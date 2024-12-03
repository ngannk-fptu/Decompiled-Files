/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.config.metro.dev;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public interface FeatureReader<T extends WebServiceFeature> {
    public static final QName ENABLED_ATTRIBUTE_NAME = new QName("enabled");

    public T parse(XMLEventReader var1) throws WebServiceException;
}

