/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.internal.ReleasableInputStream;
import com.amazonaws.internal.SdkFilterInputStream;
import com.amazonaws.services.s3.internal.eventstreaming.Message;
import com.amazonaws.services.s3.internal.eventstreaming.MessageDecoder;
import com.amazonaws.services.s3.model.SelectObjectContentEvent;
import com.amazonaws.services.s3.model.SelectObjectContentEventException;
import com.amazonaws.services.s3.model.SelectObjectContentEventVisitor;
import com.amazonaws.services.s3.model.SelectRecordsInputStream;
import com.amazonaws.services.s3.model.transform.SelectObjectContentEventUnmarshaller;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.ValidationUtils;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

@NotThreadSafe
public class SelectObjectContentEventStream
implements Closeable {
    private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[0]);
    private final SdkFilterInputStream inputStream;
    private boolean readOptionChosen = false;

    public SelectObjectContentEventStream(SdkFilterInputStream inputStream) {
        this.inputStream = ValidationUtils.assertNotNull(inputStream, "inputStream");
    }

    public List<SelectObjectContentEvent> getAllEvents() throws SelectObjectContentEventException {
        ArrayList<SelectObjectContentEvent> events = new ArrayList<SelectObjectContentEvent>();
        Iterator<SelectObjectContentEvent> eventsIterator = this.getEventsIterator();
        while (eventsIterator.hasNext()) {
            events.add(eventsIterator.next());
        }
        return events;
    }

    public void visitAllEvents(SelectObjectContentEventVisitor visitor) throws SelectObjectContentEventException {
        Iterator<SelectObjectContentEvent> eventsIterator = this.getEventsIterator();
        while (eventsIterator.hasNext()) {
            eventsIterator.next().visit(visitor);
        }
    }

    public Iterator<SelectObjectContentEvent> getEventsIterator() throws SelectObjectContentEventException {
        this.readOptionChosen();
        return new SelectEventIterator();
    }

    public SelectRecordsInputStream getRecordsInputStream() throws SelectObjectContentEventException {
        return this.getRecordsInputStream(new SelectObjectContentEventVisitor(){});
    }

    public SelectRecordsInputStream getRecordsInputStream(SelectObjectContentEventVisitor listener) throws SelectObjectContentEventException {
        SequenceInputStream recordInputStream = new SequenceInputStream(new EventStreamEnumeration(this.getEventsIterator(), listener));
        recordInputStream = ReleasableInputStream.wrap(recordInputStream).disableClose();
        return new SelectRecordsInputStream(recordInputStream, this.inputStream);
    }

    public void abort() {
        this.inputStream.abort();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    private void readOptionChosen() {
        if (this.readOptionChosen) {
            throw new IllegalStateException("Reading of the select event stream was already started by another method.");
        }
        this.readOptionChosen = true;
    }

    private abstract class LazyLoadedIterator<T>
    implements Iterator<T> {
        private final Queue<T> next = new ArrayDeque<T>();
        private boolean isDone = false;

        private LazyLoadedIterator() {
        }

        @Override
        public boolean hasNext() {
            this.advanceIfNeeded();
            return !this.isDone;
        }

        @Override
        public T next() {
            this.advanceIfNeeded();
            if (this.isDone) {
                throw new NoSuchElementException();
            }
            return this.next.poll();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advanceIfNeeded() {
            if (!this.isDone && this.next.isEmpty()) {
                try {
                    this.next.addAll(this.getNext());
                    this.isDone = this.next.isEmpty();
                }
                catch (IOException e) {
                    throw new SelectObjectContentEventException("Failed to read S3 select event.", e);
                }
            }
        }

        protected abstract Collection<? extends T> getNext() throws IOException;
    }

    private class EventStreamEnumeration
    extends LazyLoadedIterator<InputStream>
    implements Enumeration<InputStream> {
        private final Iterator<SelectObjectContentEvent> selectEventIterator;
        private final SelectObjectContentEventVisitor additionalVisitor;
        private boolean initialized = false;

        private EventStreamEnumeration(Iterator<SelectObjectContentEvent> selectEventIterator, SelectObjectContentEventVisitor additionalVisitor) {
            this.selectEventIterator = selectEventIterator;
            this.additionalVisitor = additionalVisitor;
        }

        @Override
        protected Collection<? extends InputStream> getNext() {
            if (!this.initialized) {
                this.initialized = true;
                return Collections.singleton(EMPTY_INPUT_STREAM);
            }
            final ArrayList result = new ArrayList();
            while (this.selectEventIterator.hasNext()) {
                SelectObjectContentEvent event = this.selectEventIterator.next();
                event.visit(this.additionalVisitor);
                event.visit(new SelectObjectContentEventVisitor(){

                    @Override
                    public void visit(SelectObjectContentEvent.RecordsEvent event) {
                        ByteBuffer records = event.getPayload();
                        if (records != null) {
                            result.add(new ByteArrayInputStream(BinaryUtils.copyBytesFrom(records)));
                        }
                    }
                });
                if (result.isEmpty()) continue;
                return result;
            }
            return result;
        }

        @Override
        public boolean hasMoreElements() {
            return super.hasNext();
        }

        @Override
        public InputStream nextElement() {
            return (InputStream)super.next();
        }
    }

    private class SelectEventIterator
    extends LazyLoadedIterator<SelectObjectContentEvent> {
        private final MessageDecoder decoder = new MessageDecoder();

        private SelectEventIterator() {
        }

        @Override
        protected Collection<SelectObjectContentEvent> getNext() throws IOException {
            ArrayList<SelectObjectContentEvent> next = new ArrayList<SelectObjectContentEvent>();
            byte[] payload = new byte[256];
            while (next.isEmpty()) {
                int read = SelectObjectContentEventStream.this.inputStream.read(payload);
                if (read == -1) {
                    if (!this.decoder.hasPendingContent()) break;
                    throw new SelectObjectContentEventException("Service stream ended before an event could be entirely decoded.");
                }
                List<Message> messages = this.decoder.feed(payload, 0, read);
                for (Message message : messages) {
                    next.add(SelectObjectContentEventUnmarshaller.unmarshalMessage(message));
                }
            }
            return next;
        }
    }
}

