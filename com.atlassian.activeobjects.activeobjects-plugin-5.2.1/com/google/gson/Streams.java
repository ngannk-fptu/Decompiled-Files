/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

final class Streams {
    Streams() {
    }

    static JsonElement parse(JsonReader reader) throws JsonParseException {
        boolean isEmpty = true;
        try {
            reader.peek();
            isEmpty = false;
            return Streams.parseRecursive(reader);
        }
        catch (EOFException e) {
            if (isEmpty) {
                return JsonNull.createJsonNull();
            }
            throw new JsonIOException(e);
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException(e);
        }
        catch (IOException e) {
            throw new JsonIOException(e);
        }
        catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    private static JsonElement parseRecursive(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING: {
                return new JsonPrimitive(reader.nextString());
            }
            case NUMBER: {
                String number = reader.nextString();
                return new JsonPrimitive(JsonPrimitive.stringToNumber(number));
            }
            case BOOLEAN: {
                return new JsonPrimitive(reader.nextBoolean());
            }
            case NULL: {
                reader.nextNull();
                return JsonNull.createJsonNull();
            }
            case BEGIN_ARRAY: {
                JsonArray array = new JsonArray();
                reader.beginArray();
                while (reader.hasNext()) {
                    array.add(Streams.parseRecursive(reader));
                }
                reader.endArray();
                return array;
            }
            case BEGIN_OBJECT: {
                JsonObject object = new JsonObject();
                reader.beginObject();
                while (reader.hasNext()) {
                    object.add(reader.nextName(), Streams.parseRecursive(reader));
                }
                reader.endObject();
                return object;
            }
        }
        throw new IllegalArgumentException();
    }

    static void write(JsonElement element, boolean serializeNulls, JsonWriter writer) throws IOException {
        if (element == null || element.isJsonNull()) {
            if (serializeNulls) {
                writer.nullValue();
            }
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                writer.value(primitive.getAsNumber());
            } else if (primitive.isBoolean()) {
                writer.value(primitive.getAsBoolean());
            } else {
                writer.value(primitive.getAsString());
            }
        } else if (element.isJsonArray()) {
            writer.beginArray();
            for (JsonElement e : element.getAsJsonArray()) {
                if (e.isJsonNull()) {
                    writer.nullValue();
                    continue;
                }
                Streams.write(e, serializeNulls, writer);
            }
            writer.endArray();
        } else if (element.isJsonObject()) {
            writer.beginObject();
            for (Map.Entry<String, JsonElement> e : element.getAsJsonObject().entrySet()) {
                JsonElement value = e.getValue();
                if (!serializeNulls && value.isJsonNull()) continue;
                writer.name(e.getKey());
                Streams.write(value, serializeNulls, writer);
            }
            writer.endObject();
        } else {
            throw new IllegalArgumentException("Couldn't write " + element.getClass());
        }
    }

    static Writer writerForAppendable(Appendable appendable) {
        return appendable instanceof Writer ? (Writer)appendable : new AppendableWriter(appendable);
    }

    private static class AppendableWriter
    extends Writer {
        private final Appendable appendable;
        private final CurrentWrite currentWrite = new CurrentWrite();

        private AppendableWriter(Appendable appendable) {
            this.appendable = appendable;
        }

        public void write(char[] chars, int offset, int length) throws IOException {
            this.currentWrite.chars = chars;
            this.appendable.append(this.currentWrite, offset, offset + length);
        }

        public void write(int i) throws IOException {
            this.appendable.append((char)i);
        }

        public void flush() {
        }

        public void close() {
        }

        static class CurrentWrite
        implements CharSequence {
            char[] chars;

            CurrentWrite() {
            }

            public int length() {
                return this.chars.length;
            }

            public char charAt(int i) {
                return this.chars[i];
            }

            public CharSequence subSequence(int start, int end) {
                return new String(this.chars, start, end - start);
            }
        }
    }
}

