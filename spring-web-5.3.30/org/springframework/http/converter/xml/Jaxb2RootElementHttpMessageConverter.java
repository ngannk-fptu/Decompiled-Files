/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.MarshalException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.PropertyException
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.http.converter.xml;

import java.io.StringReader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.xml.AbstractJaxb2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Jaxb2RootElementHttpMessageConverter
extends AbstractJaxb2HttpMessageConverter<Object> {
    private boolean supportDtd = false;
    private boolean processExternalEntities = false;
    private static final EntityResolver NO_OP_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new StringReader(""));

    public void setSupportDtd(boolean supportDtd) {
        this.supportDtd = supportDtd;
    }

    public boolean isSupportDtd() {
        return this.supportDtd;
    }

    public void setProcessExternalEntities(boolean processExternalEntities) {
        this.processExternalEntities = processExternalEntities;
        if (processExternalEntities) {
            this.supportDtd = true;
        }
    }

    public boolean isProcessExternalEntities() {
        return this.processExternalEntities;
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return (clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class)) && this.canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null && this.canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws Exception {
        try {
            source = this.processSource(source);
            Unmarshaller unmarshaller = this.createUnmarshaller(clazz);
            if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                return unmarshaller.unmarshal(source);
            }
            JAXBElement jaxbElement = unmarshaller.unmarshal(source, clazz);
            return jaxbElement.getValue();
        }
        catch (NullPointerException ex) {
            if (!this.isSupportDtd()) {
                throw new IllegalStateException("NPE while unmarshalling. This can happen due to the presence of DTD declarations which are disabled.", ex);
            }
            throw ex;
        }
        catch (UnmarshalException ex) {
            throw ex;
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
        }
    }

    protected Source processSource(Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            InputSource inputSource = new InputSource(streamSource.getInputStream());
            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !this.isSupportDtd());
                String featureName = "http://xml.org/sax/features/external-general-entities";
                xmlReader.setFeature(featureName, this.isProcessExternalEntities());
                if (!this.isProcessExternalEntities()) {
                    xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
                }
                return new SAXSource(xmlReader, inputSource);
            }
            catch (SAXException ex) {
                this.logger.warn((Object)"Processing of external entities could not be disabled", (Throwable)ex);
                return source;
            }
        }
        return source;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        try {
            Class clazz = ClassUtils.getUserClass((Object)o);
            Marshaller marshaller = this.createMarshaller(clazz);
            this.setCharset(headers.getContentType(), marshaller);
            marshaller.marshal(o, result);
        }
        catch (MarshalException ex) {
            throw ex;
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
        }
    }

    private void setCharset(@Nullable MediaType contentType, Marshaller marshaller) throws PropertyException {
        if (contentType != null && contentType.getCharset() != null) {
            marshaller.setProperty("jaxb.encoding", (Object)contentType.getCharset().name());
        }
    }
}

