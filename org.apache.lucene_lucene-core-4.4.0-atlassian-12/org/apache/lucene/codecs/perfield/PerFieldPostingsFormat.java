/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.perfield;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.TermsConsumer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.IOUtils;

public abstract class PerFieldPostingsFormat
extends PostingsFormat {
    public static final String PER_FIELD_NAME = "PerField40";
    public static final String PER_FIELD_FORMAT_KEY = PerFieldPostingsFormat.class.getSimpleName() + ".format";
    public static final String PER_FIELD_SUFFIX_KEY = PerFieldPostingsFormat.class.getSimpleName() + ".suffix";

    public PerFieldPostingsFormat() {
        super(PER_FIELD_NAME);
    }

    @Override
    public final FieldsConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
        return new FieldsWriter(state);
    }

    static String getSuffix(String formatName, String suffix) {
        return formatName + "_" + suffix;
    }

    static String getFullSegmentSuffix(String fieldName, String outerSegmentSuffix, String segmentSuffix) {
        if (outerSegmentSuffix.length() == 0) {
            return segmentSuffix;
        }
        throw new IllegalStateException("cannot embed PerFieldPostingsFormat inside itself (field \"" + fieldName + "\" returned PerFieldPostingsFormat)");
    }

    @Override
    public final FieldsProducer fieldsProducer(SegmentReadState state) throws IOException {
        return new FieldsReader(state);
    }

    public abstract PostingsFormat getPostingsFormatForField(String var1);

    private class FieldsReader
    extends FieldsProducer {
        private final Map<String, FieldsProducer> fields = new TreeMap<String, FieldsProducer>();
        private final Map<String, FieldsProducer> formats = new HashMap<String, FieldsProducer>();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FieldsReader(SegmentReadState readState) throws IOException {
            boolean success = false;
            try {
                for (FieldInfo fi : readState.fieldInfos) {
                    if (!fi.isIndexed()) continue;
                    String fieldName = fi.name;
                    String formatName = fi.getAttribute(PER_FIELD_FORMAT_KEY);
                    if (formatName == null) continue;
                    String suffix = fi.getAttribute(PER_FIELD_SUFFIX_KEY);
                    assert (suffix != null);
                    PostingsFormat format = PostingsFormat.forName(formatName);
                    String segmentSuffix = PerFieldPostingsFormat.getSuffix(formatName, suffix);
                    if (!this.formats.containsKey(segmentSuffix)) {
                        this.formats.put(segmentSuffix, format.fieldsProducer(new SegmentReadState(readState, segmentSuffix)));
                    }
                    this.fields.put(fieldName, this.formats.get(segmentSuffix));
                }
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.formats.values());
                }
            }
        }

        @Override
        public Iterator<String> iterator() {
            return Collections.unmodifiableSet(this.fields.keySet()).iterator();
        }

        @Override
        public Terms terms(String field) throws IOException {
            FieldsProducer fieldsProducer = this.fields.get(field);
            return fieldsProducer == null ? null : fieldsProducer.terms(field);
        }

        @Override
        public int size() {
            return this.fields.size();
        }

        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
    }

    private class FieldsWriter
    extends FieldsConsumer {
        private final Map<PostingsFormat, FieldsConsumerAndSuffix> formats = new HashMap<PostingsFormat, FieldsConsumerAndSuffix>();
        private final Map<String, Integer> suffixes = new HashMap<String, Integer>();
        private final SegmentWriteState segmentWriteState;

        public FieldsWriter(SegmentWriteState state) {
            this.segmentWriteState = state;
        }

        @Override
        public TermsConsumer addField(FieldInfo field) throws IOException {
            Integer suffix;
            PostingsFormat format = PerFieldPostingsFormat.this.getPostingsFormatForField(field.name);
            if (format == null) {
                throw new IllegalStateException("invalid null PostingsFormat for field=\"" + field.name + "\"");
            }
            String formatName = format.getName();
            String previousValue = field.putAttribute(PER_FIELD_FORMAT_KEY, formatName);
            assert (previousValue == null);
            FieldsConsumerAndSuffix consumer = this.formats.get(format);
            if (consumer == null) {
                suffix = this.suffixes.get(formatName);
                suffix = suffix == null ? Integer.valueOf(0) : Integer.valueOf(suffix + 1);
                this.suffixes.put(formatName, suffix);
                String segmentSuffix = PerFieldPostingsFormat.getFullSegmentSuffix(field.name, this.segmentWriteState.segmentSuffix, PerFieldPostingsFormat.getSuffix(formatName, Integer.toString(suffix)));
                consumer = new FieldsConsumerAndSuffix();
                consumer.consumer = format.fieldsConsumer(new SegmentWriteState(this.segmentWriteState, segmentSuffix));
                consumer.suffix = suffix;
                this.formats.put(format, consumer);
            } else {
                assert (this.suffixes.containsKey(formatName));
                suffix = consumer.suffix;
            }
            previousValue = field.putAttribute(PER_FIELD_SUFFIX_KEY, Integer.toString(suffix));
            assert (previousValue == null);
            return consumer.consumer.addField(field);
        }

        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
    }

    static class FieldsConsumerAndSuffix
    implements Closeable {
        FieldsConsumer consumer;
        int suffix;

        FieldsConsumerAndSuffix() {
        }

        @Override
        public void close() throws IOException {
            this.consumer.close();
        }
    }
}

