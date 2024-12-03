/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.util.Utf8;

public class RandomData
implements Iterable<Object> {
    public static final String USE_DEFAULT = "use-default";
    private final Schema root;
    private final long seed;
    private final int count;
    private final boolean utf8ForString;
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public RandomData(Schema schema, int count) {
        this(schema, count, false);
    }

    public RandomData(Schema schema, int count, long seed) {
        this(schema, count, seed, false);
    }

    public RandomData(Schema schema, int count, boolean utf8ForString) {
        this(schema, count, System.currentTimeMillis(), utf8ForString);
    }

    public RandomData(Schema schema, int count, long seed, boolean utf8ForString) {
        this.root = schema;
        this.seed = seed;
        this.count = count;
        this.utf8ForString = utf8ForString;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>(){
            private int n;
            private Random random;
            {
                this.random = new Random(RandomData.this.seed);
            }

            @Override
            public boolean hasNext() {
                return this.n < RandomData.this.count;
            }

            @Override
            public Object next() {
                ++this.n;
                return RandomData.this.generate(RandomData.this.root, this.random, 0);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Object generate(Schema schema, Random random, int d) {
        switch (schema.getType()) {
            case RECORD: {
                GenericData.Record record = new GenericData.Record(schema);
                for (Schema.Field field : schema.getFields()) {
                    Object value = field.getObjectProp(USE_DEFAULT) == null ? this.generate(field.schema(), random, d + 1) : GenericData.get().getDefaultValue(field);
                    record.put(field.name(), value);
                }
                return record;
            }
            case ENUM: {
                List<String> symbols = schema.getEnumSymbols();
                return new GenericData.EnumSymbol(schema, symbols.get(random.nextInt(symbols.size())));
            }
            case ARRAY: {
                int length = random.nextInt(5) + 2 - d;
                GenericData.Array array = new GenericData.Array(length <= 0 ? 0 : length, schema);
                for (int i = 0; i < length; ++i) {
                    array.add(this.generate(schema.getElementType(), random, d + 1));
                }
                return array;
            }
            case MAP: {
                int length = random.nextInt(5) + 2 - d;
                HashMap<Object, Object> map = new HashMap<Object, Object>(length <= 0 ? 0 : length);
                for (int i = 0; i < length; ++i) {
                    map.put(this.randomString(random, 40), this.generate(schema.getValueType(), random, d + 1));
                }
                return map;
            }
            case UNION: {
                List<Schema> types = schema.getTypes();
                return this.generate(types.get(random.nextInt(types.size())), random, d);
            }
            case FIXED: {
                byte[] bytes = new byte[schema.getFixedSize()];
                random.nextBytes(bytes);
                return new GenericData.Fixed(schema, bytes);
            }
            case STRING: {
                return this.randomString(random, 40);
            }
            case BYTES: {
                return RandomData.randomBytes(random, 40);
            }
            case INT: {
                return random.nextInt();
            }
            case LONG: {
                return random.nextLong();
            }
            case FLOAT: {
                return Float.valueOf(random.nextFloat());
            }
            case DOUBLE: {
                return random.nextDouble();
            }
            case BOOLEAN: {
                return random.nextBoolean();
            }
            case NULL: {
                return null;
            }
        }
        throw new RuntimeException("Unknown type: " + schema);
    }

    private Object randomString(Random random, int maxLength) {
        int length = random.nextInt(maxLength);
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            bytes[i] = (byte)(97 + random.nextInt(25));
        }
        return this.utf8ForString ? new Utf8(bytes) : new String(bytes, UTF8);
    }

    private static ByteBuffer randomBytes(Random rand, int maxLength) {
        ByteBuffer bytes = ByteBuffer.allocate(rand.nextInt(maxLength));
        ((Buffer)bytes).limit(bytes.capacity());
        rand.nextBytes(bytes.array());
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3 || args.length > 4) {
            System.out.println("Usage: RandomData <schemafile> <outputfile> <count> [codec]");
            System.exit(-1);
        }
        Schema sch = new Schema.Parser().parse(new File(args[0]));
        try (DataFileWriter<Object> writer = new DataFileWriter<Object>(new GenericDatumWriter());){
            writer.setCodec(CodecFactory.fromString(args.length >= 4 ? args[3] : "null"));
            writer.setMeta("user_metadata", "someByteArray".getBytes(StandardCharsets.UTF_8));
            writer.create(sch, new File(args[1]));
            for (Object datum : new RandomData(sch, Integer.parseInt(args[2]))) {
                writer.append(datum);
            }
        }
    }
}

