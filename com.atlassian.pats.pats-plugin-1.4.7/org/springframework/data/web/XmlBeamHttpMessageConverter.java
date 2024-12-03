/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.AbstractHttpMessageConverter
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.http.converter.HttpMessageNotWritableException
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.xmlbeam.XBProjector
 *  org.xmlbeam.XBProjector$Flags
 *  org.xmlbeam.config.DefaultXMLFactoriesConfig
 *  org.xmlbeam.config.XMLFactoriesConfig
 */
package org.springframework.data.web;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.web.ProjectedPayload;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.xml.sax.SAXParseException;
import org.xmlbeam.XBProjector;
import org.xmlbeam.config.DefaultXMLFactoriesConfig;
import org.xmlbeam.config.XMLFactoriesConfig;

public class XmlBeamHttpMessageConverter
extends AbstractHttpMessageConverter<Object> {
    private final XBProjector projectionFactory;
    private final Map<Class<?>, Boolean> supportedTypesCache = new ConcurrentReferenceHashMap();

    public XmlBeamHttpMessageConverter() {
        this(new XBProjector((XMLFactoriesConfig)new DefaultXMLFactoriesConfig(){
            private static final long serialVersionUID = -1324345769124477493L;

            public DocumentBuilderFactory createDocumentBuilderFactory() {
                DocumentBuilderFactory factory = super.createDocumentBuilderFactory();
                factory.setAttribute("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setAttribute("http://xml.org/sax/features/external-general-entities", false);
                return factory;
            }
        }, new XBProjector.Flags[0]));
    }

    public XmlBeamHttpMessageConverter(XBProjector projector) {
        super(new MediaType[]{MediaType.APPLICATION_XML, MediaType.parseMediaType((String)"application/*+xml")});
        Assert.notNull((Object)projector, (String)"XBProjector must not be null!");
        this.projectionFactory = projector;
    }

    protected boolean supports(Class<?> type) {
        Class rawType = ResolvableType.forType(type).resolve(Object.class);
        Boolean result = this.supportedTypesCache.get(rawType);
        if (result != null) {
            return result;
        }
        result = rawType.isInterface() && AnnotationUtils.findAnnotation((Class)rawType, ProjectedPayload.class) != null;
        this.supportedTypesCache.put(rawType, result);
        return result;
    }

    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            return this.projectionFactory.io().stream(inputMessage.getBody()).read(clazz);
        }
        catch (RuntimeException o_O) {
            Throwable cause = o_O.getCause();
            if (SAXParseException.class.isInstance(cause)) {
                throw new HttpMessageNotReadableException("Cannot read input message!", cause, inputMessage);
            }
            throw o_O;
        }
    }

    protected void writeInternal(Object t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    }
}

