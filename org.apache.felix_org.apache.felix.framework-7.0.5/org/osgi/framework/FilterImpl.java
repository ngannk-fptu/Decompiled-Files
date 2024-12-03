/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

abstract class FilterImpl
implements Filter {
    private transient String filterString;

    static FilterImpl createFilter(String filterString) throws InvalidSyntaxException {
        return new Parser(filterString).parse();
    }

    FilterImpl() {
    }

    @Override
    public boolean match(ServiceReference<?> reference) {
        return this.matches0(reference != null ? new ServiceReferenceMap(reference) : Collections.emptyMap());
    }

    @Override
    public boolean match(Dictionary<String, ?> dictionary) {
        return this.matches0(dictionary != null ? new CaseInsensitiveMap(dictionary) : Collections.emptyMap());
    }

    @Override
    public boolean matchCase(Dictionary<String, ?> dictionary) {
        return this.matches0(dictionary != null ? DictionaryMap.asMap(dictionary) : Collections.emptyMap());
    }

    @Override
    public boolean matches(Map<String, ?> map) {
        return this.matches0(map != null ? map : Collections.emptyMap());
    }

    abstract boolean matches0(Map<String, ?> var1);

    @Override
    public String toString() {
        String result = this.filterString;
        if (result == null) {
            this.filterString = result = this.normalize(new StringBuilder()).toString();
        }
        return result;
    }

    abstract StringBuilder normalize(StringBuilder var1);

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Filter)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    private static final class ServiceReferenceMap
    extends AbstractMap<String, Object>
    implements Map<String, Object> {
        private final ServiceReference<?> reference;

        ServiceReferenceMap(ServiceReference<?> reference) {
            this.reference = Objects.requireNonNull(reference);
        }

        @Override
        public Object get(Object key) {
            return this.reference.getProperty((String)key);
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class CaseInsensitiveMap
    extends DictionaryMap
    implements Map<String, Object> {
        private final String[] keys;

        CaseInsensitiveMap(Dictionary<String, ?> dictionary) {
            super(dictionary);
            ArrayList<String> keyList = new ArrayList<String>(dictionary.size());
            Enumeration<String> e = dictionary.keys();
            while (e.hasMoreElements()) {
                String k = e.nextElement();
                if (!(k instanceof String)) continue;
                String key = k;
                for (String i : keyList) {
                    if (!key.equalsIgnoreCase(i)) continue;
                    throw new IllegalArgumentException();
                }
                keyList.add(key);
            }
            this.keys = keyList.toArray(new String[0]);
        }

        @Override
        public Object get(Object o) {
            String k = (String)o;
            for (String key : this.keys) {
                if (!key.equalsIgnoreCase(k)) continue;
                return super.get(key);
            }
            return null;
        }
    }

    private static class DictionaryMap
    extends AbstractMap<String, Object>
    implements Map<String, Object> {
        private final Dictionary<String, ?> dictionary;

        static Map<String, ?> asMap(Dictionary<String, ?> dictionary) {
            if (dictionary instanceof Map) {
                Map coerced = (Map)((Object)dictionary);
                return coerced;
            }
            return new DictionaryMap(dictionary);
        }

        DictionaryMap(Dictionary<String, ?> dictionary) {
            this.dictionary = Objects.requireNonNull(dictionary);
        }

        @Override
        public Object get(Object key) {
            return this.dictionary.get(key);
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class Parser {
        private final String filterstring;
        private final char[] filterChars;
        private int pos;

        Parser(String filterstring) {
            this.filterstring = filterstring;
            this.filterChars = filterstring.toCharArray();
            this.pos = 0;
        }

        FilterImpl parse() throws InvalidSyntaxException {
            FilterImpl filter;
            try {
                filter = this.parse_filter();
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new InvalidSyntaxException("Filter ended abruptly", this.filterstring, e);
            }
            if (this.pos != this.filterChars.length) {
                throw new InvalidSyntaxException("Extraneous trailing characters: " + this.filterstring.substring(this.pos), this.filterstring);
            }
            return filter;
        }

        private FilterImpl parse_filter() throws InvalidSyntaxException {
            this.skipWhiteSpace();
            if (this.filterChars[this.pos] != '(') {
                throw new InvalidSyntaxException("Missing '(': " + this.filterstring.substring(this.pos), this.filterstring);
            }
            ++this.pos;
            FilterImpl filter = this.parse_filtercomp();
            this.skipWhiteSpace();
            if (this.filterChars[this.pos] != ')') {
                throw new InvalidSyntaxException("Missing ')': " + this.filterstring.substring(this.pos), this.filterstring);
            }
            ++this.pos;
            this.skipWhiteSpace();
            return filter;
        }

        private FilterImpl parse_filtercomp() throws InvalidSyntaxException {
            this.skipWhiteSpace();
            char c = this.filterChars[this.pos];
            switch (c) {
                case '&': {
                    ++this.pos;
                    return this.parse_and();
                }
                case '|': {
                    ++this.pos;
                    return this.parse_or();
                }
                case '!': {
                    ++this.pos;
                    return this.parse_not();
                }
            }
            return this.parse_item();
        }

        private FilterImpl parse_and() throws InvalidSyntaxException {
            int lookahead = this.pos;
            this.skipWhiteSpace();
            if (this.filterChars[this.pos] != '(') {
                this.pos = lookahead - 1;
                return this.parse_item();
            }
            ArrayList<FilterImpl> operands = new ArrayList<FilterImpl>(10);
            while (this.filterChars[this.pos] == '(') {
                FilterImpl child = this.parse_filter();
                operands.add(child);
            }
            return new And(operands.toArray(new FilterImpl[0]));
        }

        private FilterImpl parse_or() throws InvalidSyntaxException {
            int lookahead = this.pos;
            this.skipWhiteSpace();
            if (this.filterChars[this.pos] != '(') {
                this.pos = lookahead - 1;
                return this.parse_item();
            }
            ArrayList<FilterImpl> operands = new ArrayList<FilterImpl>(10);
            while (this.filterChars[this.pos] == '(') {
                FilterImpl child = this.parse_filter();
                operands.add(child);
            }
            return new Or(operands.toArray(new FilterImpl[0]));
        }

        private FilterImpl parse_not() throws InvalidSyntaxException {
            int lookahead = this.pos;
            this.skipWhiteSpace();
            if (this.filterChars[this.pos] != '(') {
                this.pos = lookahead - 1;
                return this.parse_item();
            }
            FilterImpl child = this.parse_filter();
            return new Not(child);
        }

        private FilterImpl parse_item() throws InvalidSyntaxException {
            String attr = this.parse_attr();
            this.skipWhiteSpace();
            switch (this.filterChars[this.pos]) {
                case '~': {
                    if (this.filterChars[this.pos + 1] != '=') break;
                    this.pos += 2;
                    return new Approx(attr, this.parse_value());
                }
                case '>': {
                    if (this.filterChars[this.pos + 1] != '=') break;
                    this.pos += 2;
                    return new GreaterEqual(attr, this.parse_value());
                }
                case '<': {
                    if (this.filterChars[this.pos + 1] != '=') break;
                    this.pos += 2;
                    return new LessEqual(attr, this.parse_value());
                }
                case '=': {
                    String single;
                    if (this.filterChars[this.pos + 1] == '*') {
                        int oldpos = this.pos;
                        this.pos += 2;
                        this.skipWhiteSpace();
                        if (this.filterChars[this.pos] == ')') {
                            return new Present(attr);
                        }
                        this.pos = oldpos;
                    }
                    ++this.pos;
                    String[] substrings = this.parse_substring();
                    int length = substrings.length;
                    if (length == 0) {
                        return new Equal(attr, "");
                    }
                    if (length == 1 && (single = substrings[0]) != null) {
                        return new Equal(attr, single);
                    }
                    return new Substring(attr, substrings);
                }
            }
            throw new InvalidSyntaxException("Invalid operator: " + this.filterstring.substring(this.pos), this.filterstring);
        }

        private String parse_attr() throws InvalidSyntaxException {
            this.skipWhiteSpace();
            int begin = this.pos;
            int end = this.pos;
            char c = this.filterChars[this.pos];
            while (c != '~' && c != '<' && c != '>' && c != '=' && c != '(' && c != ')') {
                ++this.pos;
                if (!Character.isWhitespace(c)) {
                    end = this.pos;
                }
                c = this.filterChars[this.pos];
            }
            int length = end - begin;
            if (length == 0) {
                throw new InvalidSyntaxException("Missing attr: " + this.filterstring.substring(this.pos), this.filterstring);
            }
            return new String(this.filterChars, begin, length);
        }

        private String parse_value() throws InvalidSyntaxException {
            StringBuilder sb = new StringBuilder(this.filterChars.length - this.pos);
            block5: while (true) {
                char c = this.filterChars[this.pos];
                switch (c) {
                    case ')': {
                        break block5;
                    }
                    case '(': {
                        throw new InvalidSyntaxException("Invalid value: " + this.filterstring.substring(this.pos), this.filterstring);
                    }
                    case '\\': {
                        ++this.pos;
                        c = this.filterChars[this.pos];
                    }
                    default: {
                        sb.append(c);
                        ++this.pos;
                        continue block5;
                    }
                }
                break;
            }
            if (sb.length() == 0) {
                throw new InvalidSyntaxException("Missing value: " + this.filterstring.substring(this.pos), this.filterstring);
            }
            return sb.toString();
        }

        private String[] parse_substring() throws InvalidSyntaxException {
            StringBuilder sb = new StringBuilder(this.filterChars.length - this.pos);
            ArrayList<String> operands = new ArrayList<String>(10);
            block6: while (true) {
                char c = this.filterChars[this.pos];
                switch (c) {
                    case ')': {
                        if (sb.length() <= 0) break block6;
                        operands.add(sb.toString());
                        break block6;
                    }
                    case '(': {
                        throw new InvalidSyntaxException("Invalid value: " + this.filterstring.substring(this.pos), this.filterstring);
                    }
                    case '*': {
                        if (sb.length() > 0) {
                            operands.add(sb.toString());
                        }
                        sb.setLength(0);
                        operands.add(null);
                        ++this.pos;
                        continue block6;
                    }
                    case '\\': {
                        ++this.pos;
                        c = this.filterChars[this.pos];
                    }
                    default: {
                        sb.append(c);
                        ++this.pos;
                        continue block6;
                    }
                }
                break;
            }
            return operands.toArray(new String[0]);
        }

        private void skipWhiteSpace() {
            int length = this.filterChars.length;
            while (this.pos < length && Character.isWhitespace(this.filterChars[this.pos])) {
                ++this.pos;
            }
        }
    }

    static final class Approx
    extends Equal {
        final String approx;

        Approx(String attr, String value) {
            super(attr, value);
            this.approx = Approx.approxString(value);
        }

        @Override
        boolean compare_String(String string) {
            string = Approx.approxString(string);
            return string.equalsIgnoreCase(this.approx);
        }

        @Override
        boolean compare_Character(char charval) {
            char charval2;
            try {
                charval2 = this.approx.charAt(0);
            }
            catch (IndexOutOfBoundsException e) {
                return false;
            }
            return charval == charval2 || Character.toUpperCase(charval) == Character.toUpperCase(charval2) || Character.toLowerCase(charval) == Character.toLowerCase(charval2);
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append(this.attr).append('~').append('=');
            return Approx.encodeValue(sb, this.approx).append(')');
        }

        static String approxString(String input) {
            boolean changed = false;
            char[] output = input.toCharArray();
            int cursor = 0;
            for (char c : output) {
                if (Character.isWhitespace(c)) {
                    changed = true;
                    continue;
                }
                output[cursor] = c;
                ++cursor;
            }
            return changed ? new String(output, 0, cursor) : input;
        }
    }

    static final class GreaterEqual
    extends Equal {
        GreaterEqual(String attr, String value) {
            super(attr, value);
        }

        @Override
        boolean comparison(int compare) {
            return compare >= 0;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append(this.attr).append('>').append('=');
            return GreaterEqual.encodeValue(sb, this.value).append(')');
        }
    }

    static final class LessEqual
    extends Equal {
        LessEqual(String attr, String value) {
            super(attr, value);
        }

        @Override
        boolean comparison(int compare) {
            return compare <= 0;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append(this.attr).append('<').append('=');
            return LessEqual.encodeValue(sb, this.value).append(')');
        }
    }

    static class Equal
    extends Item {
        final String value;
        private Object cached;

        Equal(String attr, String value) {
            super(attr);
            this.value = value;
        }

        private <T> T convert(Class<T> type, Function<String, ? extends T> converter) {
            Object converted = this.cached;
            if (converted != null && type.isInstance(converted)) {
                return (T)converted;
            }
            this.cached = converted = converter.apply(this.value.trim());
            return (T)converted;
        }

        boolean comparison(int compare) {
            return compare == 0;
        }

        @Override
        boolean compare_String(String string) {
            return this.comparison(string == this.value ? 0 : string.compareTo(this.value));
        }

        @Override
        boolean compare_Version(Version value1) {
            try {
                Version version2 = this.convert(Version.class, Version::valueOf);
                return this.comparison(value1.compareTo(version2));
            }
            catch (Exception e) {
                return false;
            }
        }

        @Override
        boolean compare_Boolean(boolean boolval) {
            boolean boolval2 = this.convert(Boolean.class, Boolean::valueOf);
            return this.comparison(Boolean.compare(boolval, boolval2));
        }

        @Override
        boolean compare_Character(char charval) {
            char charval2;
            try {
                charval2 = this.value.charAt(0);
            }
            catch (IndexOutOfBoundsException e) {
                return false;
            }
            return this.comparison(Character.compare(charval, charval2));
        }

        @Override
        boolean compare_Double(double doubleval) {
            double doubleval2;
            try {
                doubleval2 = this.convert(Double.class, Double::valueOf);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            return this.comparison(Double.compare(doubleval, doubleval2));
        }

        @Override
        boolean compare_Float(float floatval) {
            float floatval2;
            try {
                floatval2 = this.convert(Float.class, Float::valueOf).floatValue();
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            return this.comparison(Float.compare(floatval, floatval2));
        }

        @Override
        boolean compare_Long(long longval) {
            long longval2;
            try {
                longval2 = this.convert(Long.class, Long::valueOf);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            return this.comparison(Long.compare(longval, longval2));
        }

        @Override
        boolean compare_Comparable(Comparable<Object> value1) {
            Object value2 = this.valueOf(value1.getClass());
            if (value2 == null) {
                return false;
            }
            try {
                return this.comparison(value1.compareTo(value2));
            }
            catch (Exception e) {
                return false;
            }
        }

        @Override
        boolean compare_Unknown(Object value1) {
            Object value2 = this.valueOf(value1.getClass());
            if (value2 == null) {
                return false;
            }
            try {
                return value1.equals(value2);
            }
            catch (Exception e) {
                return false;
            }
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append(this.attr).append('=');
            return Equal.encodeValue(sb, this.value).append(')');
        }

        Object valueOf(Class<?> target) {
            block12: {
                Constructor<?> constructor;
                block11: {
                    Method method;
                    try {
                        method = target.getMethod("valueOf", String.class);
                    }
                    catch (NoSuchMethodException e) {
                        break block11;
                    }
                    if (Modifier.isStatic(method.getModifiers()) && target.isAssignableFrom(method.getReturnType())) {
                        Equal.setAccessible(method);
                        try {
                            return method.invoke(null, this.value.trim());
                        }
                        catch (Error e) {
                            throw e;
                        }
                        catch (Throwable e) {
                            return null;
                        }
                    }
                }
                try {
                    constructor = target.getConstructor(String.class);
                }
                catch (NoSuchMethodException e) {
                    break block12;
                }
                Equal.setAccessible(constructor);
                try {
                    return constructor.newInstance(this.value.trim());
                }
                catch (Error e) {
                    throw e;
                }
                catch (Throwable e) {
                    return null;
                }
            }
            return null;
        }

        private static void setAccessible(AccessibleObject accessible) {
            if (!accessible.isAccessible()) {
                AccessController.doPrivileged(() -> {
                    accessible.setAccessible(true);
                    return null;
                });
            }
        }
    }

    static final class Substring
    extends Item {
        final String[] substrings;

        Substring(String attr, String[] substrings) {
            super(attr);
            this.substrings = substrings;
        }

        @Override
        boolean compare_String(String string) {
            int pos = 0;
            int size = this.substrings.length;
            for (int i = 0; i < size; ++i) {
                String substr = this.substrings[i];
                if (i + 1 < size) {
                    if (substr == null) {
                        String substr2 = this.substrings[i + 1];
                        if (substr2 == null) continue;
                        int index = string.indexOf(substr2, pos);
                        if (index == -1) {
                            return false;
                        }
                        pos = index + substr2.length();
                        if (i + 2 >= size) continue;
                        ++i;
                        continue;
                    }
                    int len = substr.length();
                    if (string.regionMatches(pos, substr, 0, len)) {
                        pos += len;
                        continue;
                    }
                    return false;
                }
                if (substr == null) {
                    return true;
                }
                return string.endsWith(substr);
            }
            return true;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append(this.attr).append('=');
            for (String substr : this.substrings) {
                if (substr == null) {
                    sb.append('*');
                    continue;
                }
                Substring.encodeValue(sb, substr);
            }
            return sb.append(')');
        }
    }

    static final class Present
    extends Item {
        Present(String attr) {
            super(attr);
        }

        @Override
        boolean matches0(Map<String, ?> map) {
            return map.get(this.attr) != null;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            return sb.append('(').append(this.attr).append('=').append('*').append(')');
        }
    }

    static abstract class Item
    extends FilterImpl {
        final String attr;

        Item(String attr) {
            this.attr = attr;
        }

        @Override
        boolean matches0(Map<String, ?> map) {
            return this.compare(map.get(this.attr));
        }

        private boolean compare(Object value1) {
            if (value1 == null) {
                return false;
            }
            if (value1 instanceof String) {
                return this.compare_String((String)value1);
            }
            if (value1 instanceof Version) {
                return this.compare_Version((Version)value1);
            }
            Class<?> clazz = value1.getClass();
            if (clazz.isArray()) {
                Class<?> type = clazz.getComponentType();
                if (type.isPrimitive()) {
                    return this.compare_PrimitiveArray(type, value1);
                }
                return this.compare_ObjectArray((Object[])value1);
            }
            if (value1 instanceof Collection) {
                return this.compare_Collection((Collection)value1);
            }
            if (value1 instanceof Integer || value1 instanceof Long || value1 instanceof Byte || value1 instanceof Short) {
                return this.compare_Long(((Number)value1).longValue());
            }
            if (value1 instanceof Character) {
                return this.compare_Character(((Character)value1).charValue());
            }
            if (value1 instanceof Float) {
                return this.compare_Float(((Float)value1).floatValue());
            }
            if (value1 instanceof Double) {
                return this.compare_Double((Double)value1);
            }
            if (value1 instanceof Boolean) {
                return this.compare_Boolean((Boolean)value1);
            }
            if (value1 instanceof Comparable) {
                Comparable comparable = (Comparable)value1;
                return this.compare_Comparable(comparable);
            }
            return this.compare_Unknown(value1);
        }

        private boolean compare_Collection(Collection<?> collection) {
            for (Object value1 : collection) {
                if (!this.compare(value1)) continue;
                return true;
            }
            return false;
        }

        private boolean compare_ObjectArray(Object[] array) {
            for (Object value1 : array) {
                if (!this.compare(value1)) continue;
                return true;
            }
            return false;
        }

        private boolean compare_PrimitiveArray(Class<?> type, Object primarray) {
            if (Integer.TYPE.isAssignableFrom(type)) {
                int[] array;
                for (int value1 : array = (int[])primarray) {
                    if (!this.compare_Long(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Long.TYPE.isAssignableFrom(type)) {
                long[] array;
                for (long value1 : array = (long[])primarray) {
                    if (!this.compare_Long(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Byte.TYPE.isAssignableFrom(type)) {
                byte[] array;
                for (byte value1 : array = (byte[])primarray) {
                    if (!this.compare_Long(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Short.TYPE.isAssignableFrom(type)) {
                short[] array;
                for (short value1 : array = (short[])primarray) {
                    if (!this.compare_Long(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Character.TYPE.isAssignableFrom(type)) {
                char[] array;
                for (char value1 : array = (char[])primarray) {
                    if (!this.compare_Character(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Float.TYPE.isAssignableFrom(type)) {
                float[] array;
                for (float value1 : array = (float[])primarray) {
                    if (!this.compare_Float(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Double.TYPE.isAssignableFrom(type)) {
                double[] array;
                for (double value1 : array = (double[])primarray) {
                    if (!this.compare_Double(value1)) continue;
                    return true;
                }
                return false;
            }
            if (Boolean.TYPE.isAssignableFrom(type)) {
                boolean[] array;
                for (boolean value1 : array = (boolean[])primarray) {
                    if (!this.compare_Boolean(value1)) continue;
                    return true;
                }
                return false;
            }
            return false;
        }

        boolean compare_String(String string) {
            return false;
        }

        boolean compare_Version(Version value1) {
            return false;
        }

        boolean compare_Comparable(Comparable<Object> value1) {
            return false;
        }

        boolean compare_Unknown(Object value1) {
            return false;
        }

        boolean compare_Boolean(boolean boolval) {
            return false;
        }

        boolean compare_Character(char charval) {
            return false;
        }

        boolean compare_Double(double doubleval) {
            return false;
        }

        boolean compare_Float(float floatval) {
            return false;
        }

        boolean compare_Long(long longval) {
            return false;
        }

        static StringBuilder encodeValue(StringBuilder sb, String value) {
            int len = value.length();
            for (int i = 0; i < len; ++i) {
                char c = value.charAt(i);
                switch (c) {
                    case '(': 
                    case ')': 
                    case '*': 
                    case '\\': {
                        sb.append('\\');
                    }
                }
                sb.append(c);
            }
            return sb;
        }
    }

    static final class Not
    extends FilterImpl {
        private final FilterImpl operand;

        Not(FilterImpl operand) {
            this.operand = operand;
        }

        @Override
        boolean matches0(Map<String, ?> map) {
            return !this.operand.matches0(map);
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append('!');
            this.operand.normalize(sb);
            return sb.append(')');
        }
    }

    static final class Or
    extends FilterImpl {
        private final FilterImpl[] operands;

        Or(FilterImpl[] operands) {
            this.operands = operands;
        }

        @Override
        boolean matches0(Map<String, ?> map) {
            for (FilterImpl operand : this.operands) {
                if (!operand.matches0(map)) continue;
                return true;
            }
            return false;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append('|');
            for (FilterImpl operand : this.operands) {
                operand.normalize(sb);
            }
            return sb.append(')');
        }
    }

    static final class And
    extends FilterImpl {
        private final FilterImpl[] operands;

        And(FilterImpl[] operands) {
            this.operands = operands;
        }

        @Override
        boolean matches0(Map<String, ?> map) {
            for (FilterImpl operand : this.operands) {
                if (operand.matches0(map)) continue;
                return false;
            }
            return true;
        }

        @Override
        StringBuilder normalize(StringBuilder sb) {
            sb.append('(').append('&');
            for (FilterImpl operand : this.operands) {
                operand.normalize(sb);
            }
            return sb.append(')');
        }
    }
}

