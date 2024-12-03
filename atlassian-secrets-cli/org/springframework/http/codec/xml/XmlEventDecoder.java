/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.aalto.AsyncByteBufferFeeder
 *  com.fasterxml.aalto.AsyncXMLInputFactory
 *  com.fasterxml.aalto.AsyncXMLStreamReader
 *  com.fasterxml.aalto.evt.EventAllocatorImpl
 *  com.fasterxml.aalto.stax.InputFactoryImpl
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.xml;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class XmlEventDecoder
extends AbstractDecoder<XMLEvent> {
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private static final boolean aaltoPresent = ClassUtils.isPresent("com.fasterxml.aalto.AsyncXMLStreamReader", XmlEventDecoder.class.getClassLoader());
    boolean useAalto = aaltoPresent;

    public XmlEventDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
    }

    @Override
    public Flux<XMLEvent> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux flux = Flux.from(inputStream);
        if (this.useAalto) {
            AaltoDataBufferToXmlEvent aaltoMapper = new AaltoDataBufferToXmlEvent();
            return flux.flatMap((Function)aaltoMapper).doFinally(signalType -> aaltoMapper.endOfInput());
        }
        Mono<DataBuffer> singleBuffer = DataBufferUtils.join((Publisher<DataBuffer>)flux);
        return singleBuffer.flatMapMany(dataBuffer -> {
            try {
                InputStream is = dataBuffer.asInputStream();
                XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
                return Flux.fromIterable(() -> eventReader).doFinally(t -> DataBufferUtils.release(dataBuffer));
            }
            catch (XMLStreamException ex) {
                return Mono.error((Throwable)ex);
            }
        });
    }

    private static class AaltoDataBufferToXmlEvent
    implements Function<DataBuffer, Publisher<? extends XMLEvent>> {
        private static final AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader = inputFactory.createAsyncForByteBuffer();
        private final XMLEventAllocator eventAllocator = EventAllocatorImpl.getDefaultInstance();

        private AaltoDataBufferToXmlEvent() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Publisher<? extends XMLEvent> apply(DataBuffer dataBuffer) {
            try {
                ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).feedInput(dataBuffer.asByteBuffer());
                ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
                while (this.streamReader.next() != 257) {
                    XMLEvent event = this.eventAllocator.allocate((XMLStreamReader)this.streamReader);
                    events.add(event);
                    if (!event.isEndDocument()) continue;
                    break;
                }
                Flux flux = Flux.fromIterable(events);
                return flux;
            }
            catch (XMLStreamException ex) {
                Mono mono = Mono.error((Throwable)ex);
                return mono;
            }
            finally {
                DataBufferUtils.release(dataBuffer);
            }
        }

        public void endOfInput() {
            ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).endOfInput();
        }
    }
}

