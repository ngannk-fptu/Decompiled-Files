/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package org.springframework.http.codec.xml;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.springframework.util.Assert;

final class JaxbContextContainer {
    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap(64);

    JaxbContextContainer() {
    }

    public Marshaller createMarshaller(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = this.getJaxbContext(clazz);
        return jaxbContext.createMarshaller();
    }

    public Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = this.getJaxbContext(clazz);
        return jaxbContext.createUnmarshaller();
    }

    private JAXBContext getJaxbContext(Class<?> clazz) throws JAXBException {
        Assert.notNull(clazz, "Class must not be null");
        JAXBContext jaxbContext = (JAXBContext)this.jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance((Class[])new Class[]{clazz});
            this.jaxbContexts.putIfAbsent(clazz, jaxbContext);
        }
        return jaxbContext;
    }
}

