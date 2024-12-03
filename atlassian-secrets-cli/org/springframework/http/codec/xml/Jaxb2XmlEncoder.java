/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.MarshalException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.xml;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.codec.xml.JaxbContextContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

public class Jaxb2XmlEncoder
extends AbstractSingleValueEncoder<Object> {
    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

    public Jaxb2XmlEncoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (super.canEncode(elementType, mimeType)) {
            Class<?> outputClass = elementType.resolve(Object.class);
            return outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class);
        }
        return false;
    }

    @Override
    protected Flux<DataBuffer> encode(Object value, DataBufferFactory dataBufferFactory, ResolvableType type, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        try {
            DataBuffer buffer = dataBufferFactory.allocateBuffer(1024);
            OutputStream outputStream = buffer.asOutputStream();
            Class<?> clazz = ClassUtils.getUserClass(value);
            Marshaller marshaller = this.jaxbContexts.createMarshaller(clazz);
            marshaller.setProperty("jaxb.encoding", (Object)StandardCharsets.UTF_8.name());
            marshaller.marshal(value, outputStream);
            return Flux.just((Object)buffer);
        }
        catch (MarshalException ex) {
            return Flux.error((Throwable)new EncodingException("Could not marshal " + value.getClass() + " to XML", ex));
        }
        catch (JAXBException ex) {
            return Flux.error((Throwable)new CodecException("Invalid JAXB configuration", ex));
        }
    }
}

