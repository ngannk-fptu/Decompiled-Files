/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.MarshalException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  org.apache.commons.logging.Log
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.AbstractSingleValueEncoder
 *  org.springframework.core.codec.CodecException
 *  org.springframework.core.codec.EncodingException
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MimeType
 *  org.springframework.util.MimeTypeUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.xml;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.logging.Log;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.xml.JaxbContextContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Jaxb2XmlEncoder
extends AbstractSingleValueEncoder<Object> {
    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();
    private Function<Marshaller, Marshaller> marshallerProcessor = Function.identity();

    public Jaxb2XmlEncoder() {
        super(new MimeType[]{MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, new MediaType("application", "*+xml")});
    }

    public void setMarshallerProcessor(Function<Marshaller, Marshaller> processor) {
        this.marshallerProcessor = this.marshallerProcessor.andThen(processor);
    }

    public Function<Marshaller, Marshaller> getMarshallerProcessor() {
        return this.marshallerProcessor;
    }

    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (super.canEncode(elementType, mimeType)) {
            Class outputClass = elementType.toClass();
            return outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class);
        }
        return false;
    }

    protected Flux<DataBuffer> encode(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Mono.fromCallable(() -> this.encodeValue(value, bufferFactory, valueType, mimeType, hints)).flux();
    }

    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug((Log)this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue((Object)value, (traceOn == false ? 1 : 0) != 0);
                return Hints.getLogPrefix((Map)hints) + "Encoding [" + formatted + "]";
            });
        }
        boolean release = true;
        DataBuffer buffer = bufferFactory.allocateBuffer(1024);
        try {
            OutputStream outputStream = buffer.asOutputStream();
            Class clazz = ClassUtils.getUserClass((Object)value);
            Marshaller marshaller = this.initMarshaller(clazz);
            marshaller.marshal(value, outputStream);
            release = false;
            DataBuffer dataBuffer = buffer;
            return dataBuffer;
        }
        catch (MarshalException ex) {
            throw new EncodingException("Could not marshal " + value.getClass() + " to XML", (Throwable)ex);
        }
        catch (JAXBException ex) {
            throw new CodecException("Invalid JAXB configuration", (Throwable)ex);
        }
        finally {
            if (release) {
                DataBufferUtils.release((DataBuffer)buffer);
            }
        }
    }

    private Marshaller initMarshaller(Class<?> clazz) throws CodecException, JAXBException {
        Marshaller marshaller = this.jaxbContexts.createMarshaller(clazz);
        marshaller.setProperty("jaxb.encoding", (Object)StandardCharsets.UTF_8.name());
        marshaller = this.marshallerProcessor.apply(marshaller);
        return marshaller;
    }
}

