/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Resolver;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.io.parsing.ValidatingGrammarGenerator;
import org.apache.avro.util.internal.Accessor;

public class ResolvingGrammarGenerator
extends ValidatingGrammarGenerator {
    private static EncoderFactory factory;

    public final Symbol generate(Schema writer, Schema reader) throws IOException {
        Resolver.Action r = Resolver.resolve(writer, reader);
        return Symbol.root(this.generate(r, new HashMap<Object, Symbol>()));
    }

    private Symbol generate(Resolver.Action action, Map<Object, Symbol> seen) throws IOException {
        if (action instanceof Resolver.DoNothing) {
            return this.simpleGen(action.writer, seen);
        }
        if (action instanceof Resolver.ErrorAction) {
            return Symbol.error(action.toString());
        }
        if (action instanceof Resolver.Skip) {
            return Symbol.skipAction(this.simpleGen(action.writer, seen));
        }
        if (action instanceof Resolver.Promote) {
            return Symbol.resolve(this.simpleGen(action.writer, seen), this.simpleGen(action.reader, seen));
        }
        if (action instanceof Resolver.ReaderUnion) {
            Resolver.ReaderUnion ru = (Resolver.ReaderUnion)action;
            Symbol s = this.generate(ru.actualAction, seen);
            return Symbol.seq(Symbol.unionAdjustAction(ru.firstMatch, s), Symbol.UNION);
        }
        if (action.writer.getType() == Schema.Type.ARRAY) {
            Symbol es = this.generate(((Resolver.Container)action).elementAction, seen);
            return Symbol.seq(Symbol.repeat(Symbol.ARRAY_END, es), Symbol.ARRAY_START);
        }
        if (action.writer.getType() == Schema.Type.MAP) {
            Symbol es = this.generate(((Resolver.Container)action).elementAction, seen);
            return Symbol.seq(Symbol.repeat(Symbol.MAP_END, es, Symbol.STRING), Symbol.MAP_START);
        }
        if (action.writer.getType() == Schema.Type.UNION) {
            if (((Resolver.WriterUnion)action).unionEquiv) {
                return this.simpleGen(action.reader, seen);
            }
            Resolver.Action[] branches = ((Resolver.WriterUnion)action).actions;
            Symbol[] symbols = new Symbol[branches.length];
            String[] labels = new String[branches.length];
            int i = 0;
            for (Resolver.Action branch : branches) {
                symbols[i] = this.generate(branch, seen);
                labels[i] = action.writer.getTypes().get(i).getFullName();
                ++i;
            }
            return Symbol.seq(Symbol.alt(symbols, labels), Symbol.WRITER_UNION_ACTION);
        }
        if (action instanceof Resolver.EnumAdjust) {
            Resolver.EnumAdjust e = (Resolver.EnumAdjust)action;
            Object[] adjs = new Object[e.adjustments.length];
            for (int i = 0; i < adjs.length; ++i) {
                adjs[i] = 0 <= e.adjustments[i] ? Integer.valueOf(e.adjustments[i]) : "No match for " + e.writer.getEnumSymbols().get(i);
            }
            return Symbol.seq(Symbol.enumAdjustAction(e.reader.getEnumSymbols().size(), adjs), Symbol.ENUM);
        }
        if (action instanceof Resolver.RecordAdjust) {
            Symbol result = seen.get(action);
            if (result == null) {
                Resolver.Action[] actions;
                Resolver.RecordAdjust ra = (Resolver.RecordAdjust)action;
                int defaultCount = ra.readerOrder.length - ra.firstDefault;
                int count = 1 + ra.fieldActions.length + 3 * defaultCount;
                Symbol[] production = new Symbol[count];
                result = Symbol.seq(production);
                seen.put(action, result);
                production[--count] = Symbol.fieldOrderAction(ra.readerOrder);
                for (Resolver.Action wfa : actions = ra.fieldActions) {
                    production[--count] = this.generate(wfa, seen);
                }
                for (int i = ra.firstDefault; i < ra.readerOrder.length; ++i) {
                    Schema.Field rf = ra.readerOrder[i];
                    byte[] bb = ResolvingGrammarGenerator.getBinary(rf.schema(), Accessor.defaultValue(rf));
                    production[--count] = Symbol.defaultStartAction(bb);
                    production[--count] = this.simpleGen(rf.schema(), seen);
                    production[--count] = Symbol.DEFAULT_END_ACTION;
                }
            }
            return result;
        }
        throw new IllegalArgumentException("Unrecognized Resolver.Action: " + action);
    }

    private Symbol simpleGen(Schema s, Map<Object, Symbol> seen) {
        switch (s.getType()) {
            case NULL: {
                return Symbol.NULL;
            }
            case BOOLEAN: {
                return Symbol.BOOLEAN;
            }
            case INT: {
                return Symbol.INT;
            }
            case LONG: {
                return Symbol.LONG;
            }
            case FLOAT: {
                return Symbol.FLOAT;
            }
            case DOUBLE: {
                return Symbol.DOUBLE;
            }
            case BYTES: {
                return Symbol.BYTES;
            }
            case STRING: {
                return Symbol.STRING;
            }
            case FIXED: {
                return Symbol.seq(Symbol.intCheckAction(s.getFixedSize()), Symbol.FIXED);
            }
            case ENUM: {
                return Symbol.seq(Symbol.enumAdjustAction(s.getEnumSymbols().size(), null), Symbol.ENUM);
            }
            case ARRAY: {
                return Symbol.seq(Symbol.repeat(Symbol.ARRAY_END, this.simpleGen(s.getElementType(), seen)), Symbol.ARRAY_START);
            }
            case MAP: {
                return Symbol.seq(Symbol.repeat(Symbol.MAP_END, this.simpleGen(s.getValueType(), seen), Symbol.STRING), Symbol.MAP_START);
            }
            case UNION: {
                List<Schema> subs = s.getTypes();
                Symbol[] symbols = new Symbol[subs.size()];
                String[] labels = new String[subs.size()];
                int i = 0;
                for (Schema b : s.getTypes()) {
                    symbols[i] = this.simpleGen(b, seen);
                    labels[i++] = b.getFullName();
                }
                return Symbol.seq(Symbol.alt(symbols, labels), Symbol.UNION);
            }
            case RECORD: {
                Symbol result = seen.get(s);
                if (result == null) {
                    Symbol[] production = new Symbol[s.getFields().size() + 1];
                    result = Symbol.seq(production);
                    seen.put(s, result);
                    int i = production.length;
                    production[--i] = Symbol.fieldOrderAction(s.getFields().toArray(new Schema.Field[0]));
                    for (Schema.Field f : s.getFields()) {
                        production[--i] = this.simpleGen(f.schema(), seen);
                    }
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Unexpected schema: " + s);
    }

    private static byte[] getBinary(Schema s, JsonNode n) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder e = factory.binaryEncoder(out, null);
        ResolvingGrammarGenerator.encode(e, s, n);
        e.flush();
        return out.toByteArray();
    }

    public static void encode(Encoder e, Schema s, JsonNode n) throws IOException {
        switch (s.getType()) {
            case RECORD: {
                for (Schema.Field f : s.getFields()) {
                    String name = f.name();
                    JsonNode v = n.get(name);
                    if (v == null) {
                        v = Accessor.defaultValue(f);
                    }
                    if (v == null) {
                        throw new AvroTypeException("No default value for: " + name);
                    }
                    ResolvingGrammarGenerator.encode(e, f.schema(), v);
                }
                break;
            }
            case ENUM: {
                e.writeEnum(s.getEnumOrdinal(n.textValue()));
                break;
            }
            case ARRAY: {
                e.writeArrayStart();
                e.setItemCount(n.size());
                Schema i = s.getElementType();
                for (JsonNode node : n) {
                    e.startItem();
                    ResolvingGrammarGenerator.encode(e, i, node);
                }
                e.writeArrayEnd();
                break;
            }
            case MAP: {
                e.writeMapStart();
                e.setItemCount(n.size());
                Schema v = s.getValueType();
                Iterator<String> it = n.fieldNames();
                while (it.hasNext()) {
                    e.startItem();
                    String key = it.next();
                    e.writeString(key);
                    ResolvingGrammarGenerator.encode(e, v, n.get(key));
                }
                e.writeMapEnd();
                break;
            }
            case UNION: {
                e.writeIndex(0);
                ResolvingGrammarGenerator.encode(e, s.getTypes().get(0), n);
                break;
            }
            case FIXED: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for fixed: " + n);
                }
                byte[] bb = n.textValue().getBytes(StandardCharsets.ISO_8859_1);
                if (bb.length != s.getFixedSize()) {
                    bb = Arrays.copyOf(bb, s.getFixedSize());
                }
                e.writeFixed(bb);
                break;
            }
            case STRING: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for string: " + n);
                }
                e.writeString(n.textValue());
                break;
            }
            case BYTES: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for bytes: " + n);
                }
                e.writeBytes(n.textValue().getBytes(StandardCharsets.ISO_8859_1));
                break;
            }
            case INT: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for int: " + n);
                }
                e.writeInt(n.intValue());
                break;
            }
            case LONG: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for long: " + n);
                }
                e.writeLong(n.longValue());
                break;
            }
            case FLOAT: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for float: " + n);
                }
                e.writeFloat((float)n.doubleValue());
                break;
            }
            case DOUBLE: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for double: " + n);
                }
                e.writeDouble(n.doubleValue());
                break;
            }
            case BOOLEAN: {
                if (!n.isBoolean()) {
                    throw new AvroTypeException("Non-boolean default for boolean: " + n);
                }
                e.writeBoolean(n.booleanValue());
                break;
            }
            case NULL: {
                if (!n.isNull()) {
                    throw new AvroTypeException("Non-null default value for null type: " + n);
                }
                e.writeNull();
            }
        }
    }

    static {
        Accessor.setAccessor(new Accessor.ResolvingGrammarGeneratorAccessor(){

            @Override
            protected void encode(Encoder e, Schema s, JsonNode n) throws IOException {
                ResolvingGrammarGenerator.encode(e, s, n);
            }
        });
        factory = new EncoderFactory().configureBufferSize(32);
    }
}

