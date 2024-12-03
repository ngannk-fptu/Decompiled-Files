/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSchema
 *  javax.xml.bind.annotation.XmlType
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.xml.JaxbContextContainer;
import org.springframework.http.codec.xml.XmlEventDecoder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Jaxb2XmlDecoder
extends AbstractDecoder<Object> {
    private static final String JAXB_DEFAULT_ANNOTATION_VALUE = "##default";
    private final XmlEventDecoder xmlEventDecoder = new XmlEventDecoder();
    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

    public Jaxb2XmlDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> outputClass = elementType.getRawClass();
        return outputClass != null && (outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class)) && super.canDecode(elementType, mimeType);
    }

    @Override
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Class<?> outputClass = elementType.getRawClass();
        Assert.state(outputClass != null, "Unresolvable output class");
        Flux<XMLEvent> xmlEventFlux = this.xmlEventDecoder.decode(inputStream, ResolvableType.forClass(XMLEvent.class), mimeType, hints);
        QName typeName = this.toQName(outputClass);
        Flux<List<XMLEvent>> splitEvents = this.split(xmlEventFlux, typeName);
        return splitEvents.map(events -> this.unmarshal((List<XMLEvent>)events, outputClass));
    }

    @Override
    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return this.decode(inputStream, elementType, mimeType, hints).singleOrEmpty();
    }

    private Object unmarshal(List<XMLEvent> events, Class<?> outputClass) {
        try {
            Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
            XMLEventReader eventReader = StaxUtils.createXMLEventReader(events);
            if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
                return unmarshaller.unmarshal(eventReader);
            }
            JAXBElement jaxbElement = unmarshaller.unmarshal(eventReader, outputClass);
            return jaxbElement.getValue();
        }
        catch (UnmarshalException ex) {
            throw new DecodingException("Could not unmarshal XML to " + outputClass, ex);
        }
        catch (JAXBException ex) {
            throw new CodecException("Invalid JAXB configuration", ex);
        }
    }

    QName toQName(Class<?> outputClass) {
        String namespaceUri;
        String localPart;
        XmlRootElement annotation;
        if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
            annotation = outputClass.getAnnotation(XmlRootElement.class);
            localPart = annotation.name();
            namespaceUri = annotation.namespace();
        } else if (outputClass.isAnnotationPresent(XmlType.class)) {
            annotation = outputClass.getAnnotation(XmlType.class);
            localPart = annotation.name();
            namespaceUri = annotation.namespace();
        } else {
            throw new IllegalArgumentException("Output class [" + outputClass.getName() + "] is neither annotated with @XmlRootElement nor @XmlType");
        }
        if (JAXB_DEFAULT_ANNOTATION_VALUE.equals(localPart)) {
            localPart = ClassUtils.getShortNameAsProperty(outputClass);
        }
        if (JAXB_DEFAULT_ANNOTATION_VALUE.equals(namespaceUri)) {
            Package outputClassPackage = outputClass.getPackage();
            if (outputClassPackage != null && outputClassPackage.isAnnotationPresent(XmlSchema.class)) {
                XmlSchema annotation2 = outputClassPackage.getAnnotation(XmlSchema.class);
                namespaceUri = annotation2.namespace();
            } else {
                namespaceUri = "";
            }
        }
        return new QName(namespaceUri, localPart);
    }

    Flux<List<XMLEvent>> split(Flux<XMLEvent> xmlEventFlux, QName desiredName) {
        return xmlEventFlux.flatMap((Function)new SplitFunction(desiredName));
    }

    private static class SplitFunction
    implements Function<XMLEvent, Publisher<? extends List<XMLEvent>>> {
        private final QName desiredName;
        @Nullable
        private List<XMLEvent> events;
        private int elementDepth = 0;
        private int barrier = Integer.MAX_VALUE;

        public SplitFunction(QName desiredName) {
            this.desiredName = desiredName;
        }

        @Override
        public Publisher<? extends List<XMLEvent>> apply(XMLEvent event) {
            if (event.isStartElement()) {
                QName startElementName;
                if (this.barrier == Integer.MAX_VALUE && this.desiredName.equals(startElementName = event.asStartElement().getName())) {
                    this.events = new ArrayList<XMLEvent>();
                    this.barrier = this.elementDepth;
                }
                ++this.elementDepth;
            }
            if (this.elementDepth > this.barrier) {
                Assert.state(this.events != null, "No XMLEvent List");
                this.events.add(event);
            }
            if (event.isEndElement()) {
                --this.elementDepth;
                if (this.elementDepth == this.barrier) {
                    this.barrier = Integer.MAX_VALUE;
                    Assert.state(this.events != null, "No XMLEvent List");
                    return Mono.just(this.events);
                }
            }
            return Mono.empty();
        }
    }
}

