/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.perfield;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public abstract class PerFieldDocValuesFormat
extends DocValuesFormat {
    public static final String PER_FIELD_NAME = "PerFieldDV40";
    public static final String PER_FIELD_FORMAT_KEY = PerFieldDocValuesFormat.class.getSimpleName() + ".format";
    public static final String PER_FIELD_SUFFIX_KEY = PerFieldDocValuesFormat.class.getSimpleName() + ".suffix";

    public PerFieldDocValuesFormat() {
        super(PER_FIELD_NAME);
    }

    @Override
    public final DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
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
    public final DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
        return new FieldsReader(state);
    }

    public abstract DocValuesFormat getDocValuesFormatForField(String var1);

    private class FieldsReader
    extends DocValuesProducer {
        private final Map<String, DocValuesProducer> fields = new TreeMap<String, DocValuesProducer>();
        private final Map<String, DocValuesProducer> formats = new HashMap<String, DocValuesProducer>();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public FieldsReader(SegmentReadState readState) throws IOException {
            boolean success = false;
            try {
                for (FieldInfo fi : readState.fieldInfos) {
                    if (!fi.hasDocValues()) continue;
                    String fieldName = fi.name;
                    String formatName = fi.getAttribute(PER_FIELD_FORMAT_KEY);
                    if (formatName == null) continue;
                    String suffix = fi.getAttribute(PER_FIELD_SUFFIX_KEY);
                    assert (suffix != null);
                    DocValuesFormat format = DocValuesFormat.forName(formatName);
                    String segmentSuffix = PerFieldDocValuesFormat.getSuffix(formatName, suffix);
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

        private FieldsReader(FieldsReader other) {
            IdentityHashMap<DocValuesProducer, DocValuesProducer> oldToNew = new IdentityHashMap<DocValuesProducer, DocValuesProducer>();
            for (Map.Entry<String, DocValuesProducer> ent : other.formats.entrySet()) {
                DocValuesProducer values = ent.getValue();
                this.formats.put(ent.getKey(), values);
                oldToNew.put(ent.getValue(), values);
            }
            for (Map.Entry<String, DocValuesProducer> ent : other.fields.entrySet()) {
                DocValuesProducer producer = (DocValuesProducer)oldToNew.get(ent.getValue());
                assert (producer != null);
                this.fields.put(ent.getKey(), producer);
            }
        }

        @Override
        public NumericDocValues getNumeric(FieldInfo field) throws IOException {
            DocValuesProducer producer = this.fields.get(field.name);
            return producer == null ? null : producer.getNumeric(field);
        }

        @Override
        public BinaryDocValues getBinary(FieldInfo field) throws IOException {
            DocValuesProducer producer = this.fields.get(field.name);
            return producer == null ? null : producer.getBinary(field);
        }

        @Override
        public SortedDocValues getSorted(FieldInfo field) throws IOException {
            DocValuesProducer producer = this.fields.get(field.name);
            return producer == null ? null : producer.getSorted(field);
        }

        @Override
        public SortedSetDocValues getSortedSet(FieldInfo field) throws IOException {
            DocValuesProducer producer = this.fields.get(field.name);
            return producer == null ? null : producer.getSortedSet(field);
        }

        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }

        public DocValuesProducer clone() {
            return new FieldsReader(this);
        }
    }

    private class FieldsWriter
    extends DocValuesConsumer {
        private final Map<DocValuesFormat, ConsumerAndSuffix> formats = new HashMap<DocValuesFormat, ConsumerAndSuffix>();
        private final Map<String, Integer> suffixes = new HashMap<String, Integer>();
        private final SegmentWriteState segmentWriteState;

        public FieldsWriter(SegmentWriteState state) {
            this.segmentWriteState = state;
        }

        @Override
        public void addNumericField(FieldInfo field, Iterable<Number> values) throws IOException {
            this.getInstance(field).addNumericField(field, values);
        }

        @Override
        public void addBinaryField(FieldInfo field, Iterable<BytesRef> values) throws IOException {
            this.getInstance(field).addBinaryField(field, values);
        }

        @Override
        public void addSortedField(FieldInfo field, Iterable<BytesRef> values, Iterable<Number> docToOrd) throws IOException {
            this.getInstance(field).addSortedField(field, values, docToOrd);
        }

        @Override
        public void addSortedSetField(FieldInfo field, Iterable<BytesRef> values, Iterable<Number> docToOrdCount, Iterable<Number> ords) throws IOException {
            this.getInstance(field).addSortedSetField(field, values, docToOrdCount, ords);
        }

        private DocValuesConsumer getInstance(FieldInfo field) throws IOException {
            Integer suffix;
            DocValuesFormat format = PerFieldDocValuesFormat.this.getDocValuesFormatForField(field.name);
            if (format == null) {
                throw new IllegalStateException("invalid null DocValuesFormat for field=\"" + field.name + "\"");
            }
            String formatName = format.getName();
            String previousValue = field.putAttribute(PER_FIELD_FORMAT_KEY, formatName);
            assert (previousValue == null) : "formatName=" + formatName + " prevValue=" + previousValue;
            ConsumerAndSuffix consumer = this.formats.get(format);
            if (consumer == null) {
                suffix = this.suffixes.get(formatName);
                suffix = suffix == null ? Integer.valueOf(0) : Integer.valueOf(suffix + 1);
                this.suffixes.put(formatName, suffix);
                String segmentSuffix = PerFieldDocValuesFormat.getFullSegmentSuffix(field.name, this.segmentWriteState.segmentSuffix, PerFieldDocValuesFormat.getSuffix(formatName, Integer.toString(suffix)));
                consumer = new ConsumerAndSuffix();
                consumer.consumer = format.fieldsConsumer(new SegmentWriteState(this.segmentWriteState, segmentSuffix));
                consumer.suffix = suffix;
                this.formats.put(format, consumer);
            } else {
                assert (this.suffixes.containsKey(formatName));
                suffix = consumer.suffix;
            }
            previousValue = field.putAttribute(PER_FIELD_SUFFIX_KEY, Integer.toString(suffix));
            assert (previousValue == null);
            return consumer.consumer;
        }

        @Override
        public void close() throws IOException {
            IOUtils.close(this.formats.values());
        }
    }

    static class ConsumerAndSuffix
    implements Closeable {
        DocValuesConsumer consumer;
        int suffix;

        ConsumerAndSuffix() {
        }

        @Override
        public void close() throws IOException {
            this.consumer.close();
        }
    }
}

