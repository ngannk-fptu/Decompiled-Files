/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.jws.HandlerChain
 *  javax.jws.WebService
 *  javax.jws.soap.SOAPMessageHandlers
 *  javax.xml.ws.Provider
 *  javax.xml.ws.Service
 */
package com.sun.xml.ws.util;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.server.AsyncProvider;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.handler.HandlerChainsModel;
import com.sun.xml.ws.model.ReflectAnnotationReader;
import com.sun.xml.ws.server.EndpointFactory;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.HandlerAnnotationInfo;
import com.sun.xml.ws.util.MrJarUtil;
import com.sun.xml.ws.util.UtilException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPMessageHandlers;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

public class HandlerAnnotationProcessor {
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.util");

    public static HandlerAnnotationInfo buildHandlerInfo(@NotNull Class<?> clazz, QName serviceName, QName portName, WSBinding binding) {
        HandlerChain handlerChain;
        MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(clazz, binding);
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if ((handlerChain = metadataReader.getAnnotation(HandlerChain.class, clazz)) == null) {
            if ((clazz = HandlerAnnotationProcessor.getSEI(clazz, metadataReader)) != null) {
                handlerChain = metadataReader.getAnnotation(HandlerChain.class, clazz);
            }
            if (handlerChain == null) {
                return null;
            }
        }
        if (clazz.getAnnotation(SOAPMessageHandlers.class) != null) {
            throw new UtilException("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
        }
        InputStream iStream = HandlerAnnotationProcessor.getFileAsStream(clazz, handlerChain);
        XMLStreamReader reader = XMLStreamReaderFactory.create(null, iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        HandlerAnnotationInfo handlerAnnInfo = HandlerChainsModel.parseHandlerFile(reader, clazz.getClassLoader(), serviceName, portName, binding);
        try {
            reader.close();
            iStream.close();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        return handlerAnnInfo;
    }

    public static HandlerChainsModel buildHandlerChainsModel(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        HandlerChain handlerChain = clazz.getAnnotation(HandlerChain.class);
        if (handlerChain == null) {
            return null;
        }
        InputStream iStream = HandlerAnnotationProcessor.getFileAsStream(clazz, handlerChain);
        XMLStreamReader reader = XMLStreamReaderFactory.create(null, iStream, true);
        XMLStreamReaderUtil.nextElementContent(reader);
        HandlerChainsModel handlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(clazz, reader);
        try {
            reader.close();
            iStream.close();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new UtilException(e.getMessage(), new Object[0]);
        }
        return handlerChainsModel;
    }

    static Class getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new UtilException("util.handler.class.not.found", className);
        }
    }

    static Class getSEI(Class<?> clazz, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        if (Provider.class.isAssignableFrom(clazz) || AsyncProvider.class.isAssignableFrom(clazz)) {
            return null;
        }
        if (Service.class.isAssignableFrom(clazz)) {
            return null;
        }
        WebService webService = metadataReader.getAnnotation(WebService.class, clazz);
        if (webService == null) {
            throw new UtilException("util.handler.no.webservice.annotation", clazz.getCanonicalName());
        }
        String ei = webService.endpointInterface();
        if (ei.length() > 0) {
            clazz = HandlerAnnotationProcessor.getClass(webService.endpointInterface());
            WebService ws = metadataReader.getAnnotation(WebService.class, clazz);
            if (ws == null) {
                throw new UtilException("util.handler.endpoint.interface.no.webservice", webService.endpointInterface());
            }
            return clazz;
        }
        return null;
    }

    static InputStream getFileAsStream(Class clazz, HandlerChain chain) {
        return MrJarUtil.getResourceAsStream(clazz, chain.file());
    }
}

