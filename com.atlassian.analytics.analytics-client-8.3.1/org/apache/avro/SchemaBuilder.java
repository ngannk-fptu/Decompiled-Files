/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilderException;
import org.apache.avro.SchemaParseException;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.internal.JacksonUtils;

public class SchemaBuilder {
    private static final Schema NULL_SCHEMA = Schema.create(Schema.Type.NULL);

    private SchemaBuilder() {
    }

    public static TypeBuilder<Schema> builder() {
        return new TypeBuilder<Schema>(new SchemaCompletion(), new NameContext());
    }

    public static TypeBuilder<Schema> builder(String namespace) {
        return new TypeBuilder<Schema>(new SchemaCompletion(), new NameContext().namespace(namespace));
    }

    public static RecordBuilder<Schema> record(String name) {
        return SchemaBuilder.builder().record(name);
    }

    public static EnumBuilder<Schema> enumeration(String name) {
        return SchemaBuilder.builder().enumeration(name);
    }

    public static FixedBuilder<Schema> fixed(String name) {
        return SchemaBuilder.builder().fixed(name);
    }

    public static ArrayBuilder<Schema> array() {
        return SchemaBuilder.builder().array();
    }

    public static MapBuilder<Schema> map() {
        return SchemaBuilder.builder().map();
    }

    public static BaseTypeBuilder<UnionAccumulator<Schema>> unionOf() {
        return SchemaBuilder.builder().unionOf();
    }

    public static BaseTypeBuilder<Schema> nullable() {
        return SchemaBuilder.builder().nullable();
    }

    private static JsonNode toJsonNode(Object o) {
        try {
            String s;
            if (o instanceof ByteBuffer) {
                ByteBuffer bytes = (ByteBuffer)o;
                ((Buffer)bytes).mark();
                byte[] data = new byte[bytes.remaining()];
                bytes.get(data);
                ((Buffer)bytes).reset();
                s = new String(data, StandardCharsets.ISO_8859_1);
                char[] quoted = JsonStringEncoder.getInstance().quoteAsString(s);
                s = "\"" + new String(quoted) + "\"";
            } else if (o instanceof byte[]) {
                s = new String((byte[])o, StandardCharsets.ISO_8859_1);
                char[] quoted = JsonStringEncoder.getInstance().quoteAsString(s);
                s = '\"' + new String(quoted) + '\"';
            } else {
                s = GenericData.get().toString(o);
            }
            return new ObjectMapper().readTree(s);
        }
        catch (IOException e) {
            throw new SchemaBuilderException(e);
        }
    }

    public static final class UnionAccumulator<R> {
        private final Completion<R> context;
        private final NameContext names;
        private final List<Schema> schemas;

        private UnionAccumulator(Completion<R> context, NameContext names, List<Schema> schemas) {
            this.context = context;
            this.names = names;
            this.schemas = schemas;
        }

        public BaseTypeBuilder<UnionAccumulator<R>> and() {
            return new UnionBuilder(this.context, this.names, this.schemas);
        }

        public R endUnion() {
            Schema schema = Schema.createUnion(this.schemas);
            return this.context.complete(schema);
        }
    }

    private static class UnionCompletion<R>
    extends Completion<UnionAccumulator<R>> {
        private final Completion<R> context;
        private final NameContext names;
        private final List<Schema> schemas;

        private UnionCompletion(Completion<R> context, NameContext names, List<Schema> schemas) {
            this.context = context;
            this.names = names;
            this.schemas = schemas;
        }

        @Override
        protected UnionAccumulator<R> complete(Schema schema) {
            ArrayList<Schema> updated = new ArrayList<Schema>(this.schemas);
            updated.add(schema);
            return new UnionAccumulator(this.context, this.names, updated);
        }
    }

    private static class ArrayCompletion<R>
    extends NestedCompletion<R> {
        private ArrayCompletion(ArrayBuilder<R> assembler, Completion<R> context) {
            super(assembler, context);
        }

        @Override
        protected Schema outerSchema(Schema inner) {
            return Schema.createArray(inner);
        }
    }

    private static class MapCompletion<R>
    extends NestedCompletion<R> {
        private MapCompletion(MapBuilder<R> assembler, Completion<R> context) {
            super(assembler, context);
        }

        @Override
        protected Schema outerSchema(Schema inner) {
            return Schema.createMap(inner);
        }
    }

    private static abstract class NestedCompletion<R>
    extends Completion<R> {
        private final Completion<R> context;
        private final PropBuilder<?> assembler;

        private NestedCompletion(PropBuilder<?> assembler, Completion<R> context) {
            this.context = context;
            this.assembler = assembler;
        }

        @Override
        protected final R complete(Schema schema) {
            Schema outer = this.outerSchema(schema);
            this.assembler.addPropsTo(outer);
            return this.context.complete(outer);
        }

        protected abstract Schema outerSchema(Schema var1);
    }

    private static final class NullableCompletionWrapper
    extends CompletionWrapper {
        private NullableCompletionWrapper() {
        }

        @Override
        <R> Completion<R> wrap(Completion<R> completion) {
            return new NullableCompletion(completion);
        }
    }

    private static abstract class CompletionWrapper {
        private CompletionWrapper() {
        }

        abstract <R> Completion<R> wrap(Completion<R> var1);
    }

    private static class OptionalCompletion<R>
    extends Completion<FieldAssembler<R>> {
        private final FieldBuilder<R> bldr;

        public OptionalCompletion(FieldBuilder<R> bldr) {
            this.bldr = bldr;
        }

        @Override
        protected FieldAssembler<R> complete(Schema schema) {
            Schema optional = Schema.createUnion(Arrays.asList(NULL_SCHEMA, schema));
            return ((FieldBuilder)this.bldr).completeField(optional, null);
        }
    }

    private static class NullableCompletion<R>
    extends Completion<R> {
        private final Completion<R> context;

        private NullableCompletion(Completion<R> context) {
            this.context = context;
        }

        @Override
        protected R complete(Schema schema) {
            Schema nullable = Schema.createUnion(Arrays.asList(schema, NULL_SCHEMA));
            return this.context.complete(nullable);
        }
    }

    private static class SchemaCompletion
    extends Completion<Schema> {
        private SchemaCompletion() {
        }

        @Override
        protected Schema complete(Schema schema) {
            return schema;
        }
    }

    private static abstract class Completion<R> {
        private Completion() {
        }

        abstract R complete(Schema var1);
    }

    public static final class GenericDefault<R> {
        private final FieldBuilder<R> field;
        private final Schema schema;

        private GenericDefault(FieldBuilder<R> field, Schema schema) {
            this.field = field;
            this.schema = schema;
        }

        public FieldAssembler<R> noDefault() {
            return ((FieldBuilder)this.field).completeField(this.schema);
        }

        public FieldAssembler<R> withDefault(Object defaultVal) {
            return ((FieldBuilder)this.field).completeField(this.schema, defaultVal);
        }
    }

    public static class RecordDefault<R>
    extends FieldDefault<R, RecordDefault<R>> {
        private RecordDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> recordDefault(GenericRecord defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final RecordDefault<R> self() {
            return this;
        }
    }

    public static class EnumDefault<R>
    extends FieldDefault<R, EnumDefault<R>> {
        private EnumDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> enumDefault(String defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final EnumDefault<R> self() {
            return this;
        }
    }

    public static class FixedDefault<R>
    extends FieldDefault<R, FixedDefault<R>> {
        private FixedDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> fixedDefault(byte[] defaultVal) {
            return ((FieldDefault)this).usingDefault(ByteBuffer.wrap(defaultVal));
        }

        public final FieldAssembler<R> fixedDefault(ByteBuffer defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        public final FieldAssembler<R> fixedDefault(String defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final FixedDefault<R> self() {
            return this;
        }
    }

    public static class ArrayDefault<R>
    extends FieldDefault<R, ArrayDefault<R>> {
        private ArrayDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final <V> FieldAssembler<R> arrayDefault(List<V> defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final ArrayDefault<R> self() {
            return this;
        }
    }

    public static class MapDefault<R>
    extends FieldDefault<R, MapDefault<R>> {
        private MapDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final <K, V> FieldAssembler<R> mapDefault(Map<K, V> defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final MapDefault<R> self() {
            return this;
        }
    }

    public static class NullDefault<R>
    extends FieldDefault<R, NullDefault<R>> {
        private NullDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> nullDefault() {
            return ((FieldDefault)this).usingDefault(null);
        }

        @Override
        final NullDefault<R> self() {
            return this;
        }
    }

    public static class BytesDefault<R>
    extends FieldDefault<R, BytesDefault<R>> {
        private BytesDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> bytesDefault(byte[] defaultVal) {
            return ((FieldDefault)this).usingDefault(ByteBuffer.wrap(defaultVal));
        }

        public final FieldAssembler<R> bytesDefault(ByteBuffer defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        public final FieldAssembler<R> bytesDefault(String defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final BytesDefault<R> self() {
            return this;
        }
    }

    public static class StringDefault<R>
    extends FieldDefault<R, StringDefault<R>> {
        private StringDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> stringDefault(String defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final StringDefault<R> self() {
            return this;
        }
    }

    public static class DoubleDefault<R>
    extends FieldDefault<R, DoubleDefault<R>> {
        private DoubleDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> doubleDefault(double defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final DoubleDefault<R> self() {
            return this;
        }
    }

    public static class FloatDefault<R>
    extends FieldDefault<R, FloatDefault<R>> {
        private FloatDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> floatDefault(float defaultVal) {
            return ((FieldDefault)this).usingDefault(Float.valueOf(defaultVal));
        }

        @Override
        final FloatDefault<R> self() {
            return this;
        }
    }

    public static class LongDefault<R>
    extends FieldDefault<R, LongDefault<R>> {
        private LongDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> longDefault(long defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final LongDefault<R> self() {
            return this;
        }
    }

    public static class IntDefault<R>
    extends FieldDefault<R, IntDefault<R>> {
        private IntDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> intDefault(int defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final IntDefault<R> self() {
            return this;
        }
    }

    public static class BooleanDefault<R>
    extends FieldDefault<R, BooleanDefault<R>> {
        private BooleanDefault(FieldBuilder<R> field) {
            super(field);
        }

        public final FieldAssembler<R> booleanDefault(boolean defaultVal) {
            return ((FieldDefault)this).usingDefault(defaultVal);
        }

        @Override
        final BooleanDefault<R> self() {
            return this;
        }
    }

    public static abstract class FieldDefault<R, S extends FieldDefault<R, S>>
    extends Completion<S> {
        private final FieldBuilder<R> field;
        private Schema schema;

        FieldDefault(FieldBuilder<R> field) {
            this.field = field;
        }

        public final FieldAssembler<R> noDefault() {
            return ((FieldBuilder)this.field).completeField(this.schema);
        }

        private FieldAssembler<R> usingDefault(Object defaultVal) {
            return ((FieldBuilder)this.field).completeField(this.schema, defaultVal);
        }

        @Override
        final S complete(Schema schema) {
            this.schema = schema;
            return this.self();
        }

        abstract S self();
    }

    public static final class FieldBuilder<R>
    extends NamedBuilder<FieldBuilder<R>> {
        private final FieldAssembler<R> fields;
        private Schema.Field.Order order = Schema.Field.Order.ASCENDING;
        private boolean validatingDefaults = true;

        private FieldBuilder(FieldAssembler<R> fields, NameContext names, String name) {
            super(names, name);
            this.fields = fields;
        }

        public FieldBuilder<R> orderAscending() {
            this.order = Schema.Field.Order.ASCENDING;
            return this.self();
        }

        public FieldBuilder<R> orderDescending() {
            this.order = Schema.Field.Order.DESCENDING;
            return this.self();
        }

        public FieldBuilder<R> orderIgnore() {
            this.order = Schema.Field.Order.IGNORE;
            return this.self();
        }

        public FieldBuilder<R> validatingDefaults() {
            this.validatingDefaults = true;
            return this.self();
        }

        public FieldBuilder<R> notValidatingDefaults() {
            this.validatingDefaults = false;
            return this.self();
        }

        public FieldTypeBuilder<R> type() {
            return new FieldTypeBuilder(this);
        }

        public GenericDefault<R> type(Schema type) {
            return new GenericDefault(this, type);
        }

        public GenericDefault<R> type(String name) {
            return this.type(name, null);
        }

        public GenericDefault<R> type(String name, String namespace) {
            Schema schema = this.names().get(name, namespace);
            return this.type(schema);
        }

        private FieldAssembler<R> completeField(Schema schema, Object defaultVal) {
            JsonNode defaultNode = defaultVal == null ? NullNode.getInstance() : SchemaBuilder.toJsonNode(defaultVal);
            return this.completeField(schema, defaultNode);
        }

        private FieldAssembler<R> completeField(Schema schema) {
            return this.completeField(schema, null);
        }

        private FieldAssembler<R> completeField(Schema schema, JsonNode defaultVal) {
            Schema.Field field = new Schema.Field(this.name(), schema, this.doc(), defaultVal, this.validatingDefaults, this.order);
            this.addPropsTo(field);
            this.addAliasesTo(field);
            return ((FieldAssembler)this.fields).addField(field);
        }

        @Override
        protected FieldBuilder<R> self() {
            return this;
        }
    }

    public static final class FieldAssembler<R> {
        private final List<Schema.Field> fields = new ArrayList<Schema.Field>();
        private final Completion<R> context;
        private final NameContext names;
        private final Schema record;

        private FieldAssembler(Completion<R> context, NameContext names, Schema record) {
            this.context = context;
            this.names = names;
            this.record = record;
        }

        public FieldBuilder<R> name(String fieldName) {
            return new FieldBuilder(this, this.names, fieldName);
        }

        public FieldAssembler<R> requiredBoolean(String fieldName) {
            return this.name(fieldName).type().booleanType().noDefault();
        }

        public FieldAssembler<R> optionalBoolean(String fieldName) {
            return this.name(fieldName).type().optional().booleanType();
        }

        public FieldAssembler<R> nullableBoolean(String fieldName, boolean defaultVal) {
            return this.name(fieldName).type().nullable().booleanType().booleanDefault(defaultVal);
        }

        public FieldAssembler<R> requiredInt(String fieldName) {
            return this.name(fieldName).type().intType().noDefault();
        }

        public FieldAssembler<R> optionalInt(String fieldName) {
            return this.name(fieldName).type().optional().intType();
        }

        public FieldAssembler<R> nullableInt(String fieldName, int defaultVal) {
            return this.name(fieldName).type().nullable().intType().intDefault(defaultVal);
        }

        public FieldAssembler<R> requiredLong(String fieldName) {
            return this.name(fieldName).type().longType().noDefault();
        }

        public FieldAssembler<R> optionalLong(String fieldName) {
            return this.name(fieldName).type().optional().longType();
        }

        public FieldAssembler<R> nullableLong(String fieldName, long defaultVal) {
            return this.name(fieldName).type().nullable().longType().longDefault(defaultVal);
        }

        public FieldAssembler<R> requiredFloat(String fieldName) {
            return this.name(fieldName).type().floatType().noDefault();
        }

        public FieldAssembler<R> optionalFloat(String fieldName) {
            return this.name(fieldName).type().optional().floatType();
        }

        public FieldAssembler<R> nullableFloat(String fieldName, float defaultVal) {
            return this.name(fieldName).type().nullable().floatType().floatDefault(defaultVal);
        }

        public FieldAssembler<R> requiredDouble(String fieldName) {
            return this.name(fieldName).type().doubleType().noDefault();
        }

        public FieldAssembler<R> optionalDouble(String fieldName) {
            return this.name(fieldName).type().optional().doubleType();
        }

        public FieldAssembler<R> nullableDouble(String fieldName, double defaultVal) {
            return this.name(fieldName).type().nullable().doubleType().doubleDefault(defaultVal);
        }

        public FieldAssembler<R> requiredString(String fieldName) {
            return this.name(fieldName).type().stringType().noDefault();
        }

        public FieldAssembler<R> optionalString(String fieldName) {
            return this.name(fieldName).type().optional().stringType();
        }

        public FieldAssembler<R> nullableString(String fieldName, String defaultVal) {
            return this.name(fieldName).type().nullable().stringType().stringDefault(defaultVal);
        }

        public FieldAssembler<R> requiredBytes(String fieldName) {
            return this.name(fieldName).type().bytesType().noDefault();
        }

        public FieldAssembler<R> optionalBytes(String fieldName) {
            return this.name(fieldName).type().optional().bytesType();
        }

        public FieldAssembler<R> nullableBytes(String fieldName, byte[] defaultVal) {
            return this.name(fieldName).type().nullable().bytesType().bytesDefault(defaultVal);
        }

        public R endRecord() {
            this.record.setFields(this.fields);
            return this.context.complete(this.record);
        }

        private FieldAssembler<R> addField(Schema.Field field) {
            this.fields.add(field);
            return this;
        }
    }

    public static final class RecordBuilder<R>
    extends NamespacedBuilder<R, RecordBuilder<R>> {
        private RecordBuilder(Completion<R> context, NameContext names, String name) {
            super(context, names, name);
        }

        private static <R> RecordBuilder<R> create(Completion<R> context, NameContext names, String name) {
            return new RecordBuilder<R>(context, names, name);
        }

        @Override
        protected RecordBuilder<R> self() {
            return this;
        }

        public FieldAssembler<R> fields() {
            Schema record = Schema.createRecord(this.name(), this.doc(), this.space(), false);
            this.completeSchema(record);
            return new FieldAssembler(this.context(), this.names().namespace(record.getNamespace()), record);
        }
    }

    public static final class UnionFieldTypeBuilder<R> {
        private final FieldBuilder<R> bldr;
        private final NameContext names;

        private UnionFieldTypeBuilder(FieldBuilder<R> bldr) {
            this.bldr = bldr;
            this.names = bldr.names();
        }

        public UnionAccumulator<BooleanDefault<R>> booleanType() {
            return this.booleanBuilder().endBoolean();
        }

        public BooleanBuilder<UnionAccumulator<BooleanDefault<R>>> booleanBuilder() {
            return BooleanBuilder.create(this.completion(new BooleanDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<IntDefault<R>> intType() {
            return this.intBuilder().endInt();
        }

        public IntBuilder<UnionAccumulator<IntDefault<R>>> intBuilder() {
            return IntBuilder.create(this.completion(new IntDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<LongDefault<R>> longType() {
            return this.longBuilder().endLong();
        }

        public LongBuilder<UnionAccumulator<LongDefault<R>>> longBuilder() {
            return LongBuilder.create(this.completion(new LongDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<FloatDefault<R>> floatType() {
            return this.floatBuilder().endFloat();
        }

        public FloatBuilder<UnionAccumulator<FloatDefault<R>>> floatBuilder() {
            return FloatBuilder.create(this.completion(new FloatDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<DoubleDefault<R>> doubleType() {
            return this.doubleBuilder().endDouble();
        }

        public DoubleBuilder<UnionAccumulator<DoubleDefault<R>>> doubleBuilder() {
            return DoubleBuilder.create(this.completion(new DoubleDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<StringDefault<R>> stringType() {
            return this.stringBuilder().endString();
        }

        public StringBldr<UnionAccumulator<StringDefault<R>>> stringBuilder() {
            return StringBldr.create(this.completion(new StringDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<BytesDefault<R>> bytesType() {
            return this.bytesBuilder().endBytes();
        }

        public BytesBuilder<UnionAccumulator<BytesDefault<R>>> bytesBuilder() {
            return BytesBuilder.create(this.completion(new BytesDefault(this.bldr)), this.names);
        }

        public UnionAccumulator<NullDefault<R>> nullType() {
            return this.nullBuilder().endNull();
        }

        public NullBuilder<UnionAccumulator<NullDefault<R>>> nullBuilder() {
            return NullBuilder.create(this.completion(new NullDefault(this.bldr)), this.names);
        }

        public MapBuilder<UnionAccumulator<MapDefault<R>>> map() {
            return MapBuilder.create(this.completion(new MapDefault(this.bldr)), this.names);
        }

        public ArrayBuilder<UnionAccumulator<ArrayDefault<R>>> array() {
            return ArrayBuilder.create(this.completion(new ArrayDefault(this.bldr)), this.names);
        }

        public FixedBuilder<UnionAccumulator<FixedDefault<R>>> fixed(String name) {
            return FixedBuilder.create(this.completion(new FixedDefault(this.bldr)), this.names, name);
        }

        public EnumBuilder<UnionAccumulator<EnumDefault<R>>> enumeration(String name) {
            return EnumBuilder.create(this.completion(new EnumDefault(this.bldr)), this.names, name);
        }

        public RecordBuilder<UnionAccumulator<RecordDefault<R>>> record(String name) {
            return RecordBuilder.create(this.completion(new RecordDefault(this.bldr)), this.names, name);
        }

        private <C> UnionCompletion<C> completion(Completion<C> context) {
            return new UnionCompletion(context, this.names, Collections.emptyList());
        }
    }

    public static final class FieldTypeBuilder<R>
    extends BaseFieldTypeBuilder<R> {
        private FieldTypeBuilder(FieldBuilder<R> bldr) {
            super(bldr, null);
        }

        public UnionFieldTypeBuilder<R> unionOf() {
            return new UnionFieldTypeBuilder(this.bldr);
        }

        public BaseFieldTypeBuilder<R> nullable() {
            return new BaseFieldTypeBuilder(this.bldr, new NullableCompletionWrapper());
        }

        public BaseTypeBuilder<FieldAssembler<R>> optional() {
            return new BaseTypeBuilder<FieldAssembler<R>>(new OptionalCompletion(this.bldr), this.names);
        }
    }

    public static class BaseFieldTypeBuilder<R> {
        protected final FieldBuilder<R> bldr;
        protected final NameContext names;
        private final CompletionWrapper wrapper;

        protected BaseFieldTypeBuilder(FieldBuilder<R> bldr, CompletionWrapper wrapper) {
            this.bldr = bldr;
            this.names = bldr.names();
            this.wrapper = wrapper;
        }

        public final BooleanDefault<R> booleanType() {
            return this.booleanBuilder().endBoolean();
        }

        public final BooleanBuilder<BooleanDefault<R>> booleanBuilder() {
            return BooleanBuilder.create(this.wrap(new BooleanDefault(this.bldr)), this.names);
        }

        public final IntDefault<R> intType() {
            return this.intBuilder().endInt();
        }

        public final IntBuilder<IntDefault<R>> intBuilder() {
            return IntBuilder.create(this.wrap(new IntDefault(this.bldr)), this.names);
        }

        public final LongDefault<R> longType() {
            return this.longBuilder().endLong();
        }

        public final LongBuilder<LongDefault<R>> longBuilder() {
            return LongBuilder.create(this.wrap(new LongDefault(this.bldr)), this.names);
        }

        public final FloatDefault<R> floatType() {
            return this.floatBuilder().endFloat();
        }

        public final FloatBuilder<FloatDefault<R>> floatBuilder() {
            return FloatBuilder.create(this.wrap(new FloatDefault(this.bldr)), this.names);
        }

        public final DoubleDefault<R> doubleType() {
            return this.doubleBuilder().endDouble();
        }

        public final DoubleBuilder<DoubleDefault<R>> doubleBuilder() {
            return DoubleBuilder.create(this.wrap(new DoubleDefault(this.bldr)), this.names);
        }

        public final StringDefault<R> stringType() {
            return this.stringBuilder().endString();
        }

        public final StringBldr<StringDefault<R>> stringBuilder() {
            return StringBldr.create(this.wrap(new StringDefault(this.bldr)), this.names);
        }

        public final BytesDefault<R> bytesType() {
            return this.bytesBuilder().endBytes();
        }

        public final BytesBuilder<BytesDefault<R>> bytesBuilder() {
            return BytesBuilder.create(this.wrap(new BytesDefault(this.bldr)), this.names);
        }

        public final NullDefault<R> nullType() {
            return this.nullBuilder().endNull();
        }

        public final NullBuilder<NullDefault<R>> nullBuilder() {
            return NullBuilder.create(this.wrap(new NullDefault(this.bldr)), this.names);
        }

        public final MapBuilder<MapDefault<R>> map() {
            return MapBuilder.create(this.wrap(new MapDefault(this.bldr)), this.names);
        }

        public final ArrayBuilder<ArrayDefault<R>> array() {
            return ArrayBuilder.create(this.wrap(new ArrayDefault(this.bldr)), this.names);
        }

        public final FixedBuilder<FixedDefault<R>> fixed(String name) {
            return FixedBuilder.create(this.wrap(new FixedDefault(this.bldr)), this.names, name);
        }

        public final EnumBuilder<EnumDefault<R>> enumeration(String name) {
            return EnumBuilder.create(this.wrap(new EnumDefault(this.bldr)), this.names, name);
        }

        public final RecordBuilder<RecordDefault<R>> record(String name) {
            return RecordBuilder.create(this.wrap(new RecordDefault(this.bldr)), this.names, name);
        }

        private <C> Completion<C> wrap(Completion<C> completion) {
            if (this.wrapper != null) {
                return this.wrapper.wrap(completion);
            }
            return completion;
        }
    }

    private static final class UnionBuilder<R>
    extends BaseTypeBuilder<UnionAccumulator<R>> {
        private UnionBuilder(Completion<R> context, NameContext names) {
            this(context, names, Collections.emptyList());
        }

        private static <R> UnionBuilder<R> create(Completion<R> context, NameContext names) {
            return new UnionBuilder<R>(context, names);
        }

        private UnionBuilder(Completion<R> context, NameContext names, List<Schema> schemas) {
            super(new UnionCompletion(context, names, schemas), names);
        }
    }

    public static final class TypeBuilder<R>
    extends BaseTypeBuilder<R> {
        private TypeBuilder(Completion<R> context, NameContext names) {
            super(context, names);
        }

        @Override
        public BaseTypeBuilder<UnionAccumulator<R>> unionOf() {
            return super.unionOf();
        }

        @Override
        public BaseTypeBuilder<R> nullable() {
            return super.nullable();
        }
    }

    public static class BaseTypeBuilder<R> {
        private final Completion<R> context;
        private final NameContext names;

        private BaseTypeBuilder(Completion<R> context, NameContext names) {
            this.context = context;
            this.names = names;
        }

        public final R type(Schema schema) {
            return this.context.complete(schema);
        }

        public final R type(String name) {
            return this.type(name, null);
        }

        public final R type(String name, String namespace) {
            return this.type(this.names.get(name, namespace));
        }

        public final R booleanType() {
            return this.booleanBuilder().endBoolean();
        }

        public final BooleanBuilder<R> booleanBuilder() {
            return BooleanBuilder.create(this.context, this.names);
        }

        public final R intType() {
            return this.intBuilder().endInt();
        }

        public final IntBuilder<R> intBuilder() {
            return IntBuilder.create(this.context, this.names);
        }

        public final R longType() {
            return this.longBuilder().endLong();
        }

        public final LongBuilder<R> longBuilder() {
            return LongBuilder.create(this.context, this.names);
        }

        public final R floatType() {
            return this.floatBuilder().endFloat();
        }

        public final FloatBuilder<R> floatBuilder() {
            return FloatBuilder.create(this.context, this.names);
        }

        public final R doubleType() {
            return this.doubleBuilder().endDouble();
        }

        public final DoubleBuilder<R> doubleBuilder() {
            return DoubleBuilder.create(this.context, this.names);
        }

        public final R stringType() {
            return this.stringBuilder().endString();
        }

        public final StringBldr<R> stringBuilder() {
            return StringBldr.create(this.context, this.names);
        }

        public final R bytesType() {
            return this.bytesBuilder().endBytes();
        }

        public final BytesBuilder<R> bytesBuilder() {
            return BytesBuilder.create(this.context, this.names);
        }

        public final R nullType() {
            return this.nullBuilder().endNull();
        }

        public final NullBuilder<R> nullBuilder() {
            return NullBuilder.create(this.context, this.names);
        }

        public final MapBuilder<R> map() {
            return MapBuilder.create(this.context, this.names);
        }

        public final ArrayBuilder<R> array() {
            return ArrayBuilder.create(this.context, this.names);
        }

        public final FixedBuilder<R> fixed(String name) {
            return FixedBuilder.create(this.context, this.names, name);
        }

        public final EnumBuilder<R> enumeration(String name) {
            return EnumBuilder.create(this.context, this.names, name);
        }

        public final RecordBuilder<R> record(String name) {
            return RecordBuilder.create(this.context, this.names, name);
        }

        protected BaseTypeBuilder<UnionAccumulator<R>> unionOf() {
            return UnionBuilder.create(this.context, this.names);
        }

        protected BaseTypeBuilder<R> nullable() {
            return new BaseTypeBuilder(new NullableCompletion(this.context), this.names);
        }
    }

    private static class NameContext {
        private static final Set<String> PRIMITIVES = new HashSet<String>();
        private final HashMap<String, Schema> schemas;
        private final String namespace;

        private NameContext() {
            this.schemas = new HashMap();
            this.namespace = null;
            this.schemas.put("null", Schema.create(Schema.Type.NULL));
            this.schemas.put("boolean", Schema.create(Schema.Type.BOOLEAN));
            this.schemas.put("int", Schema.create(Schema.Type.INT));
            this.schemas.put("long", Schema.create(Schema.Type.LONG));
            this.schemas.put("float", Schema.create(Schema.Type.FLOAT));
            this.schemas.put("double", Schema.create(Schema.Type.DOUBLE));
            this.schemas.put("bytes", Schema.create(Schema.Type.BYTES));
            this.schemas.put("string", Schema.create(Schema.Type.STRING));
        }

        private NameContext(HashMap<String, Schema> schemas, String namespace) {
            this.schemas = schemas;
            this.namespace = "".equals(namespace) ? null : namespace;
        }

        private NameContext namespace(String namespace) {
            return new NameContext(this.schemas, namespace);
        }

        private Schema get(String name, String namespace) {
            return this.getFullname(this.resolveName(name, namespace));
        }

        private Schema getFullname(String fullName) {
            Schema schema = this.schemas.get(fullName);
            if (schema == null) {
                throw new SchemaParseException("Undefined name: " + fullName);
            }
            return schema;
        }

        private void put(Schema schema) {
            String fullName = schema.getFullName();
            if (this.schemas.containsKey(fullName)) {
                throw new SchemaParseException("Can't redefine: " + fullName);
            }
            this.schemas.put(fullName, schema);
        }

        private String resolveName(String name, String space) {
            if (PRIMITIVES.contains(name) && space == null) {
                return name;
            }
            int lastDot = name.lastIndexOf(46);
            if (lastDot < 0) {
                if (space == null) {
                    space = this.namespace;
                }
                if (space != null && !"".equals(space)) {
                    return space + "." + name;
                }
            }
            return name;
        }

        static {
            PRIMITIVES.add("null");
            PRIMITIVES.add("boolean");
            PRIMITIVES.add("int");
            PRIMITIVES.add("long");
            PRIMITIVES.add("float");
            PRIMITIVES.add("double");
            PRIMITIVES.add("bytes");
            PRIMITIVES.add("string");
        }
    }

    public static final class ArrayBuilder<R>
    extends PropBuilder<ArrayBuilder<R>> {
        private final Completion<R> context;
        private final NameContext names;

        public ArrayBuilder(Completion<R> context, NameContext names) {
            this.context = context;
            this.names = names;
        }

        private static <R> ArrayBuilder<R> create(Completion<R> context, NameContext names) {
            return new ArrayBuilder<R>(context, names);
        }

        @Override
        protected ArrayBuilder<R> self() {
            return this;
        }

        public TypeBuilder<R> items() {
            return new TypeBuilder(new ArrayCompletion(this, this.context), this.names);
        }

        public R items(Schema itemsSchema) {
            return new ArrayCompletion(this, this.context).complete(itemsSchema);
        }
    }

    public static final class MapBuilder<R>
    extends PropBuilder<MapBuilder<R>> {
        private final Completion<R> context;
        private final NameContext names;

        private MapBuilder(Completion<R> context, NameContext names) {
            this.context = context;
            this.names = names;
        }

        private static <R> MapBuilder<R> create(Completion<R> context, NameContext names) {
            return new MapBuilder<R>(context, names);
        }

        @Override
        protected MapBuilder<R> self() {
            return this;
        }

        public TypeBuilder<R> values() {
            return new TypeBuilder(new MapCompletion(this, this.context), this.names);
        }

        public R values(Schema valueSchema) {
            return new MapCompletion(this, this.context).complete(valueSchema);
        }
    }

    public static final class EnumBuilder<R>
    extends NamespacedBuilder<R, EnumBuilder<R>> {
        private String enumDefault = null;

        private EnumBuilder(Completion<R> context, NameContext names, String name) {
            super(context, names, name);
        }

        private static <R> EnumBuilder<R> create(Completion<R> context, NameContext names, String name) {
            return new EnumBuilder<R>(context, names, name);
        }

        @Override
        protected EnumBuilder<R> self() {
            return this;
        }

        public R symbols(String ... symbols) {
            Schema schema = Schema.createEnum(this.name(), this.doc(), this.space(), Arrays.asList(symbols), this.enumDefault);
            this.completeSchema(schema);
            return this.context().complete(schema);
        }

        public EnumBuilder<R> defaultSymbol(String enumDefault) {
            this.enumDefault = enumDefault;
            return this.self();
        }
    }

    public static final class FixedBuilder<R>
    extends NamespacedBuilder<R, FixedBuilder<R>> {
        private FixedBuilder(Completion<R> context, NameContext names, String name) {
            super(context, names, name);
        }

        private static <R> FixedBuilder<R> create(Completion<R> context, NameContext names, String name) {
            return new FixedBuilder<R>(context, names, name);
        }

        @Override
        protected FixedBuilder<R> self() {
            return this;
        }

        public R size(int size) {
            Schema schema = Schema.createFixed(this.name(), super.doc(), this.space(), size);
            this.completeSchema(schema);
            return this.context().complete(schema);
        }
    }

    public static final class NullBuilder<R>
    extends PrimitiveBuilder<R, NullBuilder<R>> {
        private NullBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.NULL);
        }

        private static <R> NullBuilder<R> create(Completion<R> context, NameContext names) {
            return new NullBuilder<R>(context, names);
        }

        @Override
        protected NullBuilder<R> self() {
            return this;
        }

        public R endNull() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class BytesBuilder<R>
    extends PrimitiveBuilder<R, BytesBuilder<R>> {
        private BytesBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.BYTES);
        }

        private static <R> BytesBuilder<R> create(Completion<R> context, NameContext names) {
            return new BytesBuilder<R>(context, names);
        }

        @Override
        protected BytesBuilder<R> self() {
            return this;
        }

        public R endBytes() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class StringBldr<R>
    extends PrimitiveBuilder<R, StringBldr<R>> {
        private StringBldr(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.STRING);
        }

        private static <R> StringBldr<R> create(Completion<R> context, NameContext names) {
            return new StringBldr<R>(context, names);
        }

        @Override
        protected StringBldr<R> self() {
            return this;
        }

        public R endString() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class DoubleBuilder<R>
    extends PrimitiveBuilder<R, DoubleBuilder<R>> {
        private DoubleBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.DOUBLE);
        }

        private static <R> DoubleBuilder<R> create(Completion<R> context, NameContext names) {
            return new DoubleBuilder<R>(context, names);
        }

        @Override
        protected DoubleBuilder<R> self() {
            return this;
        }

        public R endDouble() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class FloatBuilder<R>
    extends PrimitiveBuilder<R, FloatBuilder<R>> {
        private FloatBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.FLOAT);
        }

        private static <R> FloatBuilder<R> create(Completion<R> context, NameContext names) {
            return new FloatBuilder<R>(context, names);
        }

        @Override
        protected FloatBuilder<R> self() {
            return this;
        }

        public R endFloat() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class LongBuilder<R>
    extends PrimitiveBuilder<R, LongBuilder<R>> {
        private LongBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.LONG);
        }

        private static <R> LongBuilder<R> create(Completion<R> context, NameContext names) {
            return new LongBuilder<R>(context, names);
        }

        @Override
        protected LongBuilder<R> self() {
            return this;
        }

        public R endLong() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class IntBuilder<R>
    extends PrimitiveBuilder<R, IntBuilder<R>> {
        private IntBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.INT);
        }

        private static <R> IntBuilder<R> create(Completion<R> context, NameContext names) {
            return new IntBuilder<R>(context, names);
        }

        @Override
        protected IntBuilder<R> self() {
            return this;
        }

        public R endInt() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    public static final class BooleanBuilder<R>
    extends PrimitiveBuilder<R, BooleanBuilder<R>> {
        private BooleanBuilder(Completion<R> context, NameContext names) {
            super(context, names, Schema.Type.BOOLEAN);
        }

        private static <R> BooleanBuilder<R> create(Completion<R> context, NameContext names) {
            return new BooleanBuilder<R>(context, names);
        }

        @Override
        protected BooleanBuilder<R> self() {
            return this;
        }

        public R endBoolean() {
            return (R)((PrimitiveBuilder)this).end();
        }
    }

    private static abstract class PrimitiveBuilder<R, P extends PrimitiveBuilder<R, P>>
    extends PropBuilder<P> {
        private final Completion<R> context;
        private final Schema immutable;

        protected PrimitiveBuilder(Completion<R> context, NameContext names, Schema.Type type) {
            this.context = context;
            this.immutable = names.getFullname(type.getName());
        }

        private R end() {
            Schema schema = this.immutable;
            if (((PropBuilder)this).hasProps()) {
                schema = Schema.create(this.immutable.getType());
                this.addPropsTo(schema);
            }
            return this.context.complete(schema);
        }
    }

    public static abstract class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>>
    extends NamedBuilder<S> {
        private final Completion<R> context;
        private String namespace;

        protected NamespacedBuilder(Completion<R> context, NameContext names, String name) {
            super(names, name);
            this.context = context;
        }

        public final S namespace(String namespace) {
            this.namespace = namespace;
            return (S)((NamespacedBuilder)this.self());
        }

        final String space() {
            if (null == this.namespace) {
                return this.names().namespace;
            }
            return this.namespace;
        }

        final Schema completeSchema(Schema schema) {
            this.addPropsTo(schema);
            this.addAliasesTo(schema);
            this.names().put(schema);
            return schema;
        }

        final Completion<R> context() {
            return this.context;
        }
    }

    public static abstract class NamedBuilder<S extends NamedBuilder<S>>
    extends PropBuilder<S> {
        private final String name;
        private final NameContext names;
        private String doc;
        private String[] aliases;

        protected NamedBuilder(NameContext names, String name) {
            this.name = Objects.requireNonNull(name, "Type must have a name");
            this.names = names;
        }

        public final S doc(String doc) {
            this.doc = doc;
            return (S)((NamedBuilder)this.self());
        }

        public final S aliases(String ... aliases) {
            this.aliases = aliases;
            return (S)((NamedBuilder)this.self());
        }

        final String doc() {
            return this.doc;
        }

        final String name() {
            return this.name;
        }

        final NameContext names() {
            return this.names;
        }

        final Schema addAliasesTo(Schema schema) {
            if (null != this.aliases) {
                for (String alias : this.aliases) {
                    schema.addAlias(alias);
                }
            }
            return schema;
        }

        final Schema.Field addAliasesTo(Schema.Field field) {
            if (null != this.aliases) {
                for (String alias : this.aliases) {
                    field.addAlias(alias);
                }
            }
            return field;
        }
    }

    public static abstract class PropBuilder<S extends PropBuilder<S>> {
        private Map<String, JsonNode> props = null;

        protected PropBuilder() {
        }

        public final S prop(String name, String val) {
            return this.prop(name, TextNode.valueOf(val));
        }

        public final S prop(String name, Object value) {
            return this.prop(name, JacksonUtils.toJsonNode(value));
        }

        final S prop(String name, JsonNode val) {
            if (!this.hasProps()) {
                this.props = new HashMap<String, JsonNode>();
            }
            this.props.put(name, val);
            return this.self();
        }

        private boolean hasProps() {
            return this.props != null;
        }

        final <T extends JsonProperties> T addPropsTo(T jsonable) {
            if (this.hasProps()) {
                for (Map.Entry<String, JsonNode> prop : this.props.entrySet()) {
                    jsonable.addProp(prop.getKey(), (Object)prop.getValue());
                }
            }
            return jsonable;
        }

        protected abstract S self();
    }
}

