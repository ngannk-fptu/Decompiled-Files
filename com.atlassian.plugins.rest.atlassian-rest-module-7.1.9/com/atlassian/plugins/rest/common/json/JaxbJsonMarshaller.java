/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 */
package com.atlassian.plugins.rest.common.json;

import javax.xml.bind.JAXBException;

public interface JaxbJsonMarshaller {
    public String marshal(Object var1);

    @Deprecated
    public String marshal(Object var1, Class ... var2) throws JAXBException;
}

