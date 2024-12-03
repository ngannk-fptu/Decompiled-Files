/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.aalto.AsyncByteBufferFeeder
 *  com.fasterxml.aalto.AsyncXMLInputFactory
 *  com.fasterxml.aalto.AsyncXMLStreamReader
 *  com.fasterxml.aalto.evt.EventAllocatorImpl
 *  com.fasterxml.aalto.stax.InputFactoryImpl
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.AbstractDecoder
 *  org.springframework.core.codec.DecodingException
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferLimitException
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MimeType
 *  org.springframework.util.MimeTypeUtils
 *  org.springframework.util.xml.StaxUtils
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.codec.xml;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;

public class XmlEventDecoder
extends AbstractDecoder<XMLEvent> {
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private static final boolean aaltoPresent = ClassUtils.isPresent((String)"com.fasterxml.aalto.AsyncXMLStreamReader", (ClassLoader)XmlEventDecoder.class.getClassLoader());
    boolean useAalto = aaltoPresent;
    private int maxInMemorySize = 262144;

    public XmlEventDecoder() {
        super(new MimeType[]{MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, new MediaType("application", "*+xml")});
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public Flux<XMLEvent> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (this.useAalto) {
            AaltoDataBufferToXmlEvent mapper = new AaltoDataBufferToXmlEvent(this.maxInMemorySize);
            return Flux.from(input).flatMapIterable((Function)mapper).doFinally(signalType -> mapper.endOfInput());
        }
        return DataBufferUtils.join(input, (int)this.maxInMemorySize).flatMapIterable(buffer -> {
            try {
                InputStream is = buffer.asInputStream();
                XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
                ArrayList result = new ArrayList();
                eventReader.forEachRemaining(event -> result.add((XMLEvent)event));
                ArrayList arrayList = result;
                return arrayList;
            }
            catch (XMLStreamException ex) {
                throw new DecodingException(ex.getMessage(), (Throwable)ex);
            }
            finally {
                DataBufferUtils.release((DataBuffer)buffer);
            }
        });
    }

    private static class AaltoDataBufferToXmlEvent
    implements Function<DataBuffer, List<? extends XMLEvent>> {
        private static final AsyncXMLInputFactory inputFactory = (AsyncXMLInputFactory)StaxUtils.createDefensiveInputFactory(InputFactoryImpl::new);
        private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader = inputFactory.createAsyncForByteBuffer();
        private final XMLEventAllocator eventAllocator = EventAllocatorImpl.getDefaultInstance();
        private final int maxInMemorySize;
        private int byteCount;
        private int elementDepth;

        public AaltoDataBufferToXmlEvent(int maxInMemorySize) {
            this.maxInMemorySize = maxInMemorySize;
        }

        @Override
        public List<? extends XMLEvent> apply(DataBuffer dataBuffer) {
            try {
                this.increaseByteCount(dataBuffer);
                ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).feedInput(dataBuffer.asByteBuffer());
                ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
                while (this.streamReader.next() != 257) {
                    XMLEvent event = this.eventAllocator.allocate((XMLStreamReader)this.streamReader);
                    events.add(event);
                    if (event.isEndDocument()) break;
                    this.checkDepthAndResetByteCount(event);
                }
                if (this.maxInMemorySize > 0 && this.byteCount > this.maxInMemorySize) {
                    this.raiseLimitException();
                }
                ArrayList<XMLEvent> arrayList = events;
                return arrayList;
            }
            catch (XMLStreamException ex) {
                throw new DecodingException(ex.getMessage(), (Throwable)ex);
            }
            finally {
                DataBufferUtils.release((DataBuffer)dataBuffer);
            }
        }

        private void increaseByteCount(DataBuffer dataBuffer) {
            if (this.maxInMemorySize > 0) {
                if (dataBuffer.readableByteCount() > Integer.MAX_VALUE - this.byteCount) {
                    this.raiseLimitException();
                } else {
                    this.byteCount += dataBuffer.readableByteCount();
                }
            }
        }

        private void checkDepthAndResetByteCount(XMLEvent event) {
            if (this.maxInMemorySize > 0) {
                if (event.isStartElement()) {
                    this.byteCount = this.elementDepth == 1 ? 0 : this.byteCount;
                    ++this.elementDepth;
                } else if (event.isEndElement()) {
                    --this.elementDepth;
                    this.byteCount = this.elementDepth == 1 ? 0 : this.byteCount;
                }
            }
        }

        private void raiseLimitException() {
            throw new DataBufferLimitException("Exceeded limit on max bytes per XML top-level node: " + this.maxInMemorySize);
        }

        public void endOfInput() {
            ((AsyncByteBufferFeeder)this.streamReader.getInputFeeder()).endOfInput();
        }
    }
}

