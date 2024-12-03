/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.json.JsonDelegate;
import groovy.json.JsonException;
import groovy.json.JsonLexer;
import groovy.json.JsonToken;
import groovy.json.internal.CharBuf;
import groovy.json.internal.Chr;
import groovy.lang.Closure;
import groovy.util.Expando;
import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class JsonOutput {
    static final char OPEN_BRACKET = '[';
    static final char CLOSE_BRACKET = ']';
    static final char OPEN_BRACE = '{';
    static final char CLOSE_BRACE = '}';
    static final char COLON = ':';
    static final char COMMA = ',';
    static final char SPACE = ' ';
    static final char NEW_LINE = '\n';
    static final char QUOTE = '\"';
    private static final char[] EMPTY_STRING_CHARS = Chr.array('\"', '\"');
    private static final String NULL_VALUE = "null";
    private static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String DEFAULT_TIMEZONE = "GMT";
    private static final char[] EMPTY_MAP_CHARS = new char[]{'{', '}'};
    private static final char[] EMPTY_LIST_CHARS = new char[]{'[', ']'};

    public static String toJson(Boolean bool) {
        CharBuf buffer = CharBuf.create(4);
        JsonOutput.writeObject(bool, buffer);
        return buffer.toString();
    }

    public static String toJson(Number n) {
        if (n == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(3);
        Class<?> numberClass = n.getClass();
        JsonOutput.writeNumber(numberClass, n, buffer);
        return buffer.toString();
    }

    public static String toJson(Character c) {
        CharBuf buffer = CharBuf.create(3);
        JsonOutput.writeObject(c, buffer);
        return buffer.toString();
    }

    public static String toJson(String s) {
        if (s == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(s.length() + 2);
        JsonOutput.writeCharSequence(s, buffer);
        return buffer.toString();
    }

    public static String toJson(Date date) {
        if (date == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(26);
        JsonOutput.writeDate(date, buffer);
        return buffer.toString();
    }

    public static String toJson(Calendar cal) {
        if (cal == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(26);
        JsonOutput.writeDate(cal.getTime(), buffer);
        return buffer.toString();
    }

    public static String toJson(UUID uuid) {
        CharBuf buffer = CharBuf.create(64);
        JsonOutput.writeObject(uuid, buffer);
        return buffer.toString();
    }

    public static String toJson(URL url) {
        CharBuf buffer = CharBuf.create(64);
        JsonOutput.writeObject(url, buffer);
        return buffer.toString();
    }

    public static String toJson(Closure closure) {
        if (closure == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(255);
        JsonOutput.writeMap(JsonDelegate.cloneDelegateAndGetContent(closure), buffer);
        return buffer.toString();
    }

    public static String toJson(Expando expando) {
        if (expando == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(255);
        JsonOutput.writeMap(expando.getProperties(), buffer);
        return buffer.toString();
    }

    public static String toJson(Object object) {
        CharBuf buffer = CharBuf.create(255);
        JsonOutput.writeObject(object, buffer);
        return buffer.toString();
    }

    public static String toJson(Map m) {
        if (m == null) {
            return NULL_VALUE;
        }
        CharBuf buffer = CharBuf.create(255);
        JsonOutput.writeMap(m, buffer);
        return buffer.toString();
    }

    private static void writeNumber(Class<?> numberClass, Number value, CharBuf buffer) {
        if (numberClass == Integer.class) {
            buffer.addInt((Integer)value);
        } else if (numberClass == Long.class) {
            buffer.addLong((Long)value);
        } else if (numberClass == BigInteger.class) {
            buffer.addBigInteger((BigInteger)value);
        } else if (numberClass == BigDecimal.class) {
            buffer.addBigDecimal((BigDecimal)value);
        } else if (numberClass == Double.class) {
            Double doubleValue = (Double)value;
            if (doubleValue.isInfinite()) {
                throw new JsonException("Number " + value + " can't be serialized as JSON: infinite are not allowed in JSON.");
            }
            if (doubleValue.isNaN()) {
                throw new JsonException("Number " + value + " can't be serialized as JSON: NaN are not allowed in JSON.");
            }
            buffer.addDouble(doubleValue);
        } else if (numberClass == Float.class) {
            Float floatValue = (Float)value;
            if (floatValue.isInfinite()) {
                throw new JsonException("Number " + value + " can't be serialized as JSON: infinite are not allowed in JSON.");
            }
            if (floatValue.isNaN()) {
                throw new JsonException("Number " + value + " can't be serialized as JSON: NaN are not allowed in JSON.");
            }
            buffer.addFloat(floatValue);
        } else if (numberClass == Byte.class) {
            buffer.addByte((Byte)value);
        } else if (numberClass == Short.class) {
            buffer.addShort((Short)value);
        } else {
            buffer.addString(value.toString());
        }
    }

    private static void writeObject(Object object, CharBuf buffer) {
        if (object == null) {
            buffer.addNull();
        } else {
            Class<?> objectClass = object.getClass();
            if (CharSequence.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeCharSequence((CharSequence)object, buffer);
            } else if (objectClass == Boolean.class) {
                buffer.addBoolean((Boolean)object);
            } else if (Number.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeNumber(objectClass, (Number)object, buffer);
            } else if (Date.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeDate((Date)object, buffer);
            } else if (Calendar.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeDate(((Calendar)object).getTime(), buffer);
            } else if (Map.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeMap((Map)object, buffer);
            } else if (Iterable.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeIterator(((Iterable)object).iterator(), buffer);
            } else if (Iterator.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeIterator((Iterator)object, buffer);
            } else if (objectClass == Character.class) {
                buffer.addJsonEscapedString(Chr.array(((Character)object).charValue()));
            } else if (objectClass == URL.class) {
                buffer.addJsonEscapedString(object.toString());
            } else if (objectClass == UUID.class) {
                buffer.addQuoted(object.toString());
            } else if (objectClass == JsonUnescaped.class) {
                buffer.add(object.toString());
            } else if (Closure.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeMap(JsonDelegate.cloneDelegateAndGetContent((Closure)object), buffer);
            } else if (Expando.class.isAssignableFrom(objectClass)) {
                JsonOutput.writeMap(((Expando)object).getProperties(), buffer);
            } else if (Enumeration.class.isAssignableFrom(objectClass)) {
                ArrayList list = Collections.list((Enumeration)object);
                JsonOutput.writeIterator(list.iterator(), buffer);
            } else if (objectClass.isArray()) {
                JsonOutput.writeArray(objectClass, object, buffer);
            } else if (Enum.class.isAssignableFrom(objectClass)) {
                buffer.addQuoted(((Enum)object).name());
            } else if (File.class.isAssignableFrom(objectClass)) {
                Map<?, ?> properties = JsonOutput.getObjectProperties(object);
                Iterator<Map.Entry<?, ?>> iterator = properties.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<?, ?> entry = iterator.next();
                    if (!(entry.getValue() instanceof File)) continue;
                    iterator.remove();
                }
                JsonOutput.writeMap(properties, buffer);
            } else {
                Map<?, ?> properties = JsonOutput.getObjectProperties(object);
                JsonOutput.writeMap(properties, buffer);
            }
        }
    }

    private static Map<?, ?> getObjectProperties(Object object) {
        Map properties = DefaultGroovyMethods.getProperties(object);
        properties.remove("class");
        properties.remove("declaringClass");
        properties.remove("metaClass");
        return properties;
    }

    private static void writeCharSequence(CharSequence seq, CharBuf buffer) {
        if (seq.length() > 0) {
            buffer.addJsonEscapedString(seq.toString());
        } else {
            buffer.addChars(EMPTY_STRING_CHARS);
        }
    }

    private static void writeDate(Date date, CharBuf buffer) {
        SimpleDateFormat formatter = new SimpleDateFormat(JSON_DATE_FORMAT, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
        buffer.addQuoted(formatter.format(date));
    }

    private static void writeArray(Class<?> arrayClass, Object array, CharBuf buffer) {
        short[] shortArray;
        buffer.addChar('[');
        if (Object[].class.isAssignableFrom(arrayClass)) {
            Object[] objArray = (Object[])array;
            if (objArray.length > 0) {
                JsonOutput.writeObject(objArray[0], buffer);
                for (int i = 1; i < objArray.length; ++i) {
                    buffer.addChar(',');
                    JsonOutput.writeObject(objArray[i], buffer);
                }
            }
        } else if (int[].class.isAssignableFrom(arrayClass)) {
            int[] intArray = (int[])array;
            if (intArray.length > 0) {
                buffer.addInt(intArray[0]);
                for (int i = 1; i < intArray.length; ++i) {
                    buffer.addChar(',').addInt(intArray[i]);
                }
            }
        } else if (long[].class.isAssignableFrom(arrayClass)) {
            long[] longArray = (long[])array;
            if (longArray.length > 0) {
                buffer.addLong(longArray[0]);
                for (int i = 1; i < longArray.length; ++i) {
                    buffer.addChar(',').addLong(longArray[i]);
                }
            }
        } else if (boolean[].class.isAssignableFrom(arrayClass)) {
            boolean[] booleanArray = (boolean[])array;
            if (booleanArray.length > 0) {
                buffer.addBoolean(booleanArray[0]);
                for (int i = 1; i < booleanArray.length; ++i) {
                    buffer.addChar(',').addBoolean(booleanArray[i]);
                }
            }
        } else if (char[].class.isAssignableFrom(arrayClass)) {
            char[] charArray = (char[])array;
            if (charArray.length > 0) {
                buffer.addJsonEscapedString(Chr.array(charArray[0]));
                for (int i = 1; i < charArray.length; ++i) {
                    buffer.addChar(',').addJsonEscapedString(Chr.array(charArray[i]));
                }
            }
        } else if (double[].class.isAssignableFrom(arrayClass)) {
            double[] doubleArray = (double[])array;
            if (doubleArray.length > 0) {
                buffer.addDouble(doubleArray[0]);
                for (int i = 1; i < doubleArray.length; ++i) {
                    buffer.addChar(',').addDouble(doubleArray[i]);
                }
            }
        } else if (float[].class.isAssignableFrom(arrayClass)) {
            float[] floatArray = (float[])array;
            if (floatArray.length > 0) {
                buffer.addFloat(floatArray[0]);
                for (int i = 1; i < floatArray.length; ++i) {
                    buffer.addChar(',').addFloat(floatArray[i]);
                }
            }
        } else if (byte[].class.isAssignableFrom(arrayClass)) {
            byte[] byteArray = (byte[])array;
            if (byteArray.length > 0) {
                buffer.addByte(byteArray[0]);
                for (int i = 1; i < byteArray.length; ++i) {
                    buffer.addChar(',').addByte(byteArray[i]);
                }
            }
        } else if (short[].class.isAssignableFrom(arrayClass) && (shortArray = (short[])array).length > 0) {
            buffer.addShort(shortArray[0]);
            for (int i = 1; i < shortArray.length; ++i) {
                buffer.addChar(',').addShort(shortArray[i]);
            }
        }
        buffer.addChar(']');
    }

    private static void writeMap(Map<?, ?> map, CharBuf buffer) {
        if (!map.isEmpty()) {
            buffer.addChar('{');
            boolean firstItem = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) {
                    throw new IllegalArgumentException("Maps with null keys can't be converted to JSON");
                }
                if (!firstItem) {
                    buffer.addChar(',');
                } else {
                    firstItem = false;
                }
                buffer.addJsonFieldName(entry.getKey().toString());
                JsonOutput.writeObject(entry.getValue(), buffer);
            }
            buffer.addChar('}');
        } else {
            buffer.addChars(EMPTY_MAP_CHARS);
        }
    }

    private static void writeIterator(Iterator<?> iterator, CharBuf buffer) {
        if (iterator.hasNext()) {
            buffer.addChar('[');
            Object it = iterator.next();
            JsonOutput.writeObject(it, buffer);
            while (iterator.hasNext()) {
                it = iterator.next();
                buffer.addChar(',');
                JsonOutput.writeObject(it, buffer);
            }
            buffer.addChar(']');
        } else {
            buffer.addChars(EMPTY_LIST_CHARS);
        }
    }

    public static String prettyPrint(String jsonPayload) {
        int indentSize = 0;
        CharBuf output = CharBuf.create((int)((double)jsonPayload.length() * 1.2));
        JsonLexer lexer = new JsonLexer(new StringReader(jsonPayload));
        HashMap<Integer, char[]> indentCache = new HashMap<Integer, char[]>();
        block9: while (lexer.hasNext()) {
            JsonToken token = lexer.next();
            switch (token.getType()) {
                case OPEN_CURLY: {
                    output.addChars(Chr.array('{', '\n')).addChars(JsonOutput.getIndent(indentSize += 4, indentCache));
                    continue block9;
                }
                case CLOSE_CURLY: {
                    output.addChar('\n');
                    if ((indentSize -= 4) > 0) {
                        output.addChars(JsonOutput.getIndent(indentSize, indentCache));
                    }
                    output.addChar('}');
                    continue block9;
                }
                case OPEN_BRACKET: {
                    output.addChars(Chr.array('[', '\n')).addChars(JsonOutput.getIndent(indentSize += 4, indentCache));
                    continue block9;
                }
                case CLOSE_BRACKET: {
                    output.addChar('\n');
                    if ((indentSize -= 4) > 0) {
                        output.addChars(JsonOutput.getIndent(indentSize, indentCache));
                    }
                    output.addChar(']');
                    continue block9;
                }
                case COMMA: {
                    output.addChars(Chr.array(',', '\n')).addChars(JsonOutput.getIndent(indentSize, indentCache));
                    continue block9;
                }
                case COLON: {
                    output.addChars(Chr.array(':', ' '));
                    continue block9;
                }
                case STRING: {
                    String textStr = token.getText();
                    String textWithoutQuotes = textStr.substring(1, textStr.length() - 1);
                    if (textWithoutQuotes.length() > 0) {
                        output.addJsonEscapedString(textWithoutQuotes);
                        continue block9;
                    }
                    output.addQuoted(Chr.array(new char[0]));
                    continue block9;
                }
            }
            output.addString(token.getText());
        }
        return output.toString();
    }

    private static char[] getIndent(int indentSize, Map<Integer, char[]> indentCache) {
        char[] indent = indentCache.get(indentSize);
        if (indent == null) {
            indent = new char[indentSize];
            Arrays.fill(indent, ' ');
            indentCache.put(indentSize, indent);
        }
        return indent;
    }

    public static JsonUnescaped unescaped(CharSequence text) {
        return new JsonUnescaped(text);
    }

    public static class JsonUnescaped {
        private CharSequence text;

        public JsonUnescaped(CharSequence text) {
            this.text = text;
        }

        public CharSequence getText() {
            return this.text;
        }

        public String toString() {
            return this.text.toString();
        }
    }
}

