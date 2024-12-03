/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.DateParser;
import org.eclipse.jetty.http.EmptyHttpFields;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.http.QuotedCSVParser;
import org.eclipse.jetty.http.QuotedQualityCSV;

public interface HttpFields
extends Iterable<HttpField> {
    public static final HttpFields EMPTY = new EmptyHttpFields();

    public static Mutable build() {
        return new Mutable();
    }

    public static Mutable build(int capacity) {
        return new Mutable(capacity);
    }

    public static Mutable build(HttpFields fields) {
        return new Mutable(fields);
    }

    public static Mutable build(HttpFields fields, HttpField replaceField) {
        return new Mutable(fields, replaceField);
    }

    public static Mutable build(HttpFields fields, EnumSet<HttpHeader> removeFields) {
        return new Mutable(fields, removeFields);
    }

    public static Immutable from(HttpField ... fields) {
        return new Immutable(fields);
    }

    public Immutable asImmutable();

    default public String asString() {
        StringBuilder buffer = new StringBuilder();
        for (HttpField field : this) {
            if (field == null) continue;
            String tmp = field.getName();
            if (tmp != null) {
                buffer.append(tmp);
            }
            buffer.append(": ");
            tmp = field.getValue();
            if (tmp != null) {
                buffer.append(tmp);
            }
            buffer.append("\r\n");
        }
        buffer.append("\r\n");
        return buffer.toString();
    }

    default public boolean contains(HttpField field) {
        for (HttpField f : this) {
            if (!f.isSameName(field) || !f.equals(field) && !f.contains(field.getValue())) continue;
            return true;
        }
        return false;
    }

    default public boolean contains(HttpHeader header, String value) {
        for (HttpField f : this) {
            if (f.getHeader() != header || !f.contains(value)) continue;
            return true;
        }
        return false;
    }

    default public boolean contains(String name, String value) {
        for (HttpField f : this) {
            if (!f.is(name) || !f.contains(value)) continue;
            return true;
        }
        return false;
    }

    default public boolean contains(HttpHeader header) {
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            return true;
        }
        return false;
    }

    default public boolean contains(EnumSet<HttpHeader> headers) {
        for (HttpField f : this) {
            if (!headers.contains((Object)f.getHeader())) continue;
            return true;
        }
        return false;
    }

    default public boolean contains(String name) {
        for (HttpField f : this) {
            if (!f.is(name)) continue;
            return true;
        }
        return false;
    }

    default public String get(HttpHeader header) {
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            return f.getValue();
        }
        return null;
    }

    default public String get(String header) {
        for (HttpField f : this) {
            if (!f.is(header)) continue;
            return f.getValue();
        }
        return null;
    }

    default public List<String> getCSV(HttpHeader header, boolean keepQuotes) {
        QuotedCSV values = null;
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            if (values == null) {
                values = new QuotedCSV(keepQuotes, new String[0]);
            }
            values.addValue(f.getValue());
        }
        return values == null ? Collections.emptyList() : values.getValues();
    }

    default public List<String> getCSV(String name, boolean keepQuotes) {
        QuotedCSV values = null;
        for (HttpField f : this) {
            if (!f.is(name)) continue;
            if (values == null) {
                values = new QuotedCSV(keepQuotes, new String[0]);
            }
            values.addValue(f.getValue());
        }
        return values == null ? Collections.emptyList() : values.getValues();
    }

    default public long getDateField(String name) {
        HttpField field = this.getField(name);
        if (field == null) {
            return -1L;
        }
        String val = HttpField.getValueParameters(field.getValue(), null);
        if (val == null) {
            return -1L;
        }
        long date = DateParser.parseDate(val);
        if (date == -1L) {
            throw new IllegalArgumentException("Cannot convert date: " + val);
        }
        return date;
    }

    public HttpField getField(int var1);

    default public HttpField getField(HttpHeader header) {
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            return f;
        }
        return null;
    }

    default public HttpField getField(String name) {
        for (HttpField f : this) {
            if (!f.is(name)) continue;
            return f;
        }
        return null;
    }

    default public Enumeration<String> getFieldNames() {
        return Collections.enumeration(this.getFieldNamesCollection());
    }

    default public Set<String> getFieldNamesCollection() {
        return this.stream().map(HttpField::getName).collect(Collectors.toSet());
    }

    default public List<HttpField> getFields(HttpHeader header) {
        return this.getFields(header, (f, h) -> f.getHeader() == h);
    }

    default public List<HttpField> getFields(String name) {
        return this.getFields(name, (f, n) -> f.is(name));
    }

    private <T> List<HttpField> getFields(T header, BiPredicate<HttpField, T> predicate) {
        return this.stream().filter(f -> predicate.test((HttpField)f, header)).collect(Collectors.toList());
    }

    default public long getLongField(String name) throws NumberFormatException {
        HttpField field = this.getField(name);
        return field == null ? -1L : field.getLongValue();
    }

    default public long getLongField(HttpHeader header) throws NumberFormatException {
        HttpField field = this.getField(header);
        return field == null ? -1L : field.getLongValue();
    }

    default public List<String> getQualityCSV(HttpHeader header) {
        return this.getQualityCSV(header, null);
    }

    default public List<String> getQualityCSV(HttpHeader header, ToIntFunction<String> secondaryOrdering) {
        QuotedQualityCSV values = null;
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            if (values == null) {
                values = new QuotedQualityCSV(secondaryOrdering);
            }
            values.addValue(f.getValue());
        }
        return values == null ? Collections.emptyList() : values.getValues();
    }

    default public List<String> getQualityCSV(String name) {
        QuotedQualityCSV values = null;
        for (HttpField f : this) {
            if (!f.is(name)) continue;
            if (values == null) {
                values = new QuotedQualityCSV();
            }
            values.addValue(f.getValue());
        }
        return values == null ? Collections.emptyList() : values.getValues();
    }

    default public Enumeration<String> getValues(final String name) {
        final Iterator i = this.iterator();
        return new Enumeration<String>(){
            HttpField _field;

            @Override
            public boolean hasMoreElements() {
                if (this._field != null) {
                    return true;
                }
                while (i.hasNext()) {
                    HttpField f = (HttpField)i.next();
                    if (!f.is(name) || f.getValue() == null) continue;
                    this._field = f;
                    return true;
                }
                return false;
            }

            @Override
            public String nextElement() {
                if (this.hasMoreElements()) {
                    String value = this._field.getValue();
                    this._field = null;
                    return value;
                }
                throw new NoSuchElementException();
            }
        };
    }

    default public List<String> getValuesList(HttpHeader header) {
        ArrayList<String> list = new ArrayList<String>();
        for (HttpField f : this) {
            if (f.getHeader() != header) continue;
            list.add(f.getValue());
        }
        return list;
    }

    default public List<String> getValuesList(String name) {
        ArrayList<String> list = new ArrayList<String>();
        for (HttpField f : this) {
            if (!f.is(name)) continue;
            list.add(f.getValue());
        }
        return list;
    }

    default public boolean isEqualTo(HttpFields that) {
        if (this.size() != that.size()) {
            return false;
        }
        Iterator i = that.iterator();
        for (HttpField f : this) {
            if (!i.hasNext()) {
                return false;
            }
            if (f.equals(i.next())) continue;
            return false;
        }
        return !i.hasNext();
    }

    public int size();

    public Stream<HttpField> stream();

    public static class Mutable
    implements Iterable<HttpField>,
    HttpFields {
        private HttpField[] _fields;
        private int _size;

        protected Mutable() {
            this(16);
        }

        Mutable(int capacity) {
            this._fields = new HttpField[capacity];
        }

        Mutable(HttpFields fields) {
            this.add(fields);
        }

        Mutable(HttpFields fields, HttpField replaceField) {
            this._fields = new HttpField[fields.size() + 4];
            this._size = 0;
            boolean put = false;
            for (HttpField f : fields) {
                if (replaceField.isSameName(f)) {
                    if (!put) {
                        this._fields[this._size++] = replaceField;
                    }
                    put = true;
                    continue;
                }
                this._fields[this._size++] = f;
            }
            if (!put) {
                this._fields[this._size++] = replaceField;
            }
        }

        Mutable(HttpFields fields, EnumSet<HttpHeader> removeFields) {
            this._fields = new HttpField[fields.size() + 4];
            this._size = 0;
            for (HttpField f : fields) {
                if (f.getHeader() != null && removeFields.contains((Object)f.getHeader())) continue;
                this._fields[this._size++] = f;
            }
        }

        public Mutable add(String name, String value) {
            if (value != null) {
                return this.add(new HttpField(name, value));
            }
            return this;
        }

        public Mutable add(HttpHeader header, HttpHeaderValue value) {
            return this.add(header, value.toString());
        }

        public Mutable add(HttpHeader header, String value) {
            if (value == null) {
                throw new IllegalArgumentException("null value");
            }
            HttpField field = new HttpField(header, value);
            return this.add(field);
        }

        public Mutable add(HttpField field) {
            if (field != null) {
                if (this._size == this._fields.length) {
                    this._fields = Arrays.copyOf(this._fields, this._size * 2);
                }
                this._fields[this._size++] = field;
            }
            return this;
        }

        public Mutable add(HttpFields fields) {
            if (this._fields == null) {
                this._fields = new HttpField[fields.size() + 4];
            } else if (this._size + fields.size() >= this._fields.length) {
                this._fields = Arrays.copyOf(this._fields, this._size + fields.size() + 4);
            }
            if (fields.size() == 0) {
                return this;
            }
            if (fields instanceof Immutable) {
                Immutable b = (Immutable)fields;
                System.arraycopy(b._fields, 0, this._fields, this._size, b._fields.length);
                this._size += b._fields.length;
            } else if (fields instanceof Mutable) {
                Mutable b = (Mutable)fields;
                System.arraycopy(b._fields, 0, this._fields, this._size, b._size);
                this._size += b._size;
            } else {
                for (HttpField f : fields) {
                    this._fields[this._size++] = f;
                }
            }
            return this;
        }

        public Mutable addCSV(HttpHeader header, String ... values) {
            QuotedCSVParser existing = null;
            for (HttpField f : this) {
                if (f.getHeader() != header) continue;
                if (existing == null) {
                    existing = new QuotedCSV(false, new String[0]);
                }
                existing.addValue(f.getValue());
            }
            String value = this.formatCsvExcludingExisting((QuotedCSV)existing, values);
            if (value != null) {
                this.add(header, value);
            }
            return this;
        }

        public Mutable addCSV(String name, String ... values) {
            QuotedCSVParser existing = null;
            for (HttpField f : this) {
                if (!f.is(name)) continue;
                if (existing == null) {
                    existing = new QuotedCSV(false, new String[0]);
                }
                existing.addValue(f.getValue());
            }
            String value = this.formatCsvExcludingExisting((QuotedCSV)existing, values);
            if (value != null) {
                this.add(name, value);
            }
            return this;
        }

        public Mutable addDateField(String name, long date) {
            this.add(name, DateGenerator.formatDate(date));
            return this;
        }

        @Override
        public Immutable asImmutable() {
            return new Immutable(Arrays.copyOf(this._fields, this._size));
        }

        public Mutable clear() {
            this._size = 0;
            return this;
        }

        public void ensureField(HttpField field) {
            if (field.getValue().indexOf(44) < 0) {
                if (field.getHeader() != null) {
                    this.computeField(field.getHeader(), (HttpHeader h, List<HttpField> l) -> Mutable.computeEnsure(field, l));
                } else {
                    this.computeField(field.getName(), (String h, List<HttpField> l) -> Mutable.computeEnsure(field, l));
                }
            } else if (field.getHeader() != null) {
                this.computeField(field.getHeader(), (HttpHeader h, List<HttpField> l) -> Mutable.computeEnsure(field, field.getValues(), l));
            } else {
                this.computeField(field.getName(), (String h, List<HttpField> l) -> Mutable.computeEnsure(field, field.getValues(), l));
            }
        }

        private static HttpField computeEnsure(HttpField ensure, List<HttpField> fields) {
            if (fields == null || fields.isEmpty()) {
                return ensure;
            }
            String ensureValue = ensure.getValue();
            if (fields.size() == 1) {
                HttpField f = fields.get(0);
                return f.contains(ensureValue) ? f : new HttpField(ensure.getHeader(), ensure.getName(), f.getValue() + ", " + ensureValue);
            }
            StringBuilder v = new StringBuilder();
            for (HttpField f : fields) {
                if (v.length() > 0) {
                    v.append(", ");
                }
                v.append(f.getValue());
                if (ensureValue == null || !f.contains(ensureValue)) continue;
                ensureValue = null;
            }
            if (ensureValue != null) {
                v.append(", ").append(ensureValue);
            }
            return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
        }

        private static HttpField computeEnsure(HttpField ensure, String[] values, List<HttpField> fields) {
            if (fields == null || fields.isEmpty()) {
                return ensure;
            }
            if (fields.size() == 1) {
                HttpField f = fields.get(0);
                int ensured = values.length;
                for (int i = 0; i < values.length; ++i) {
                    if (!f.contains(values[i])) continue;
                    --ensured;
                    values[i] = null;
                }
                if (ensured == 0) {
                    return f;
                }
                if (ensured == values.length) {
                    return new HttpField(ensure.getHeader(), ensure.getName(), f.getValue() + ", " + ensure.getValue());
                }
                StringBuilder v = new StringBuilder(f.getValue());
                for (String value : values) {
                    if (value == null) continue;
                    v.append(", ").append(value);
                }
                return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
            }
            StringBuilder v = new StringBuilder();
            int ensured = values.length;
            for (HttpField f : fields) {
                if (v.length() > 0) {
                    v.append(", ");
                }
                v.append(f.getValue());
                for (int i = 0; i < values.length; ++i) {
                    if (values[i] == null || !f.contains(values[i])) continue;
                    --ensured;
                    values[i] = null;
                }
            }
            if (ensured == values.length) {
                v.append(", ").append(ensure.getValue());
            } else if (ensured > 0) {
                for (String value : values) {
                    if (value == null) continue;
                    v.append(", ").append(value);
                }
            }
            return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Mutable)) {
                return false;
            }
            return this.isEqualTo((HttpFields)o);
        }

        @Override
        public HttpField getField(int index) {
            if (index >= this._size || index < 0) {
                throw new NoSuchElementException();
            }
            return this._fields[index];
        }

        public int hashCode() {
            int hash = 0;
            int i = this._fields.length;
            while (i-- > 0) {
                hash ^= this._fields[i].hashCode();
            }
            return hash;
        }

        @Override
        public Iterator<HttpField> iterator() {
            return new Iterator<HttpField>(){
                int _index = 0;

                @Override
                public boolean hasNext() {
                    return this._index < _size;
                }

                @Override
                public HttpField next() {
                    return _fields[this._index++];
                }

                @Override
                public void remove() {
                    if (_size == 0) {
                        throw new IllegalStateException();
                    }
                    this.remove(--this._index);
                }
            };
        }

        public ListIterator<HttpField> listIterator() {
            return new ListItr();
        }

        public Mutable put(HttpField field) {
            boolean put = false;
            for (int i = 0; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (!f.isSameName(field)) continue;
                if (put) {
                    System.arraycopy(this._fields, i + 1, this._fields, i, this._size-- - i-- - 1);
                    continue;
                }
                this._fields[i] = field;
                put = true;
            }
            if (!put) {
                this.add(field);
            }
            return this;
        }

        public Mutable put(String name, String value) {
            return value == null ? this.remove(name) : this.put(new HttpField(name, value));
        }

        public Mutable put(HttpHeader header, HttpHeaderValue value) {
            return this.put(header, value.toString());
        }

        public Mutable put(HttpHeader header, String value) {
            return value == null ? this.remove(header) : this.put(new HttpField(header, value));
        }

        public Mutable put(String name, List<String> list) {
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(list, "list must not be null");
            this.remove(name);
            for (String v : list) {
                if (v == null) continue;
                this.add(name, v);
            }
            return this;
        }

        public Mutable putDateField(HttpHeader name, long date) {
            return this.put(name, DateGenerator.formatDate(date));
        }

        public Mutable putDateField(String name, long date) {
            return this.put(name, DateGenerator.formatDate(date));
        }

        public Mutable putLongField(HttpHeader name, long value) {
            return this.put(name, Long.toString(value));
        }

        public Mutable putLongField(String name, long value) {
            return this.put(name, Long.toString(value));
        }

        public void computeField(HttpHeader header, BiFunction<HttpHeader, List<HttpField>, HttpField> computeFn) {
            this.computeField(header, computeFn, (f, h) -> f.getHeader() == h);
        }

        public void computeField(String name, BiFunction<String, List<HttpField>, HttpField> computeFn) {
            this.computeField(name, computeFn, HttpField::is);
        }

        private <T> void computeField(T header, BiFunction<T, List<HttpField>, HttpField> computeFn, BiPredicate<HttpField, T> matcher) {
            int first = -1;
            for (int i = 0; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (!matcher.test(f, (HttpField)header)) continue;
                first = i;
                break;
            }
            if (first < 0) {
                HttpField newField = computeFn.apply(header, null);
                if (newField != null) {
                    this.add(newField);
                }
                return;
            }
            List<Object> found = null;
            for (int i = first + 1; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (!matcher.test(f, (HttpField)header)) continue;
                if (found == null) {
                    found = new ArrayList();
                    found.add(this._fields[first]);
                }
                found.add(f);
                this.remove(i--);
            }
            HttpField newField = computeFn.apply(header, found = found == null ? Collections.singletonList(this._fields[first]) : Collections.unmodifiableList(found));
            if (newField == null) {
                this.remove(first);
            } else {
                this._fields[first] = newField;
            }
        }

        public Mutable remove(HttpHeader name) {
            for (int i = 0; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (f.getHeader() != name) continue;
                this.remove(i--);
            }
            return this;
        }

        public Mutable remove(EnumSet<HttpHeader> fields) {
            for (int i = 0; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (!fields.contains((Object)f.getHeader())) continue;
                this.remove(i--);
            }
            return this;
        }

        public Mutable remove(String name) {
            for (int i = 0; i < this._size; ++i) {
                HttpField f = this._fields[i];
                if (!f.is(name)) continue;
                this.remove(i--);
            }
            return this;
        }

        private void remove(int i) {
            --this._size;
            System.arraycopy(this._fields, i + 1, this._fields, i, this._size - i);
            this._fields[this._size] = null;
        }

        @Override
        public int size() {
            return this._size;
        }

        @Override
        public Stream<HttpField> stream() {
            return Arrays.stream(this._fields, 0, this._size);
        }

        public String toString() {
            return this.asString();
        }

        private String formatCsvExcludingExisting(QuotedCSV existing, String ... values) {
            boolean add = true;
            if (existing != null && !existing.isEmpty()) {
                add = false;
                int i = values.length;
                while (i-- > 0) {
                    String unquoted = QuotedCSV.unquote(values[i]);
                    if (existing.getValues().contains(unquoted)) {
                        values[i] = null;
                        continue;
                    }
                    add = true;
                }
            }
            if (add) {
                StringBuilder value = new StringBuilder();
                for (String v : values) {
                    if (v == null) continue;
                    if (value.length() > 0) {
                        value.append(", ");
                    }
                    value.append(v);
                }
                if (value.length() > 0) {
                    return value.toString();
                }
            }
            return null;
        }

        private class ListItr
        implements ListIterator<HttpField> {
            int _cursor;
            int _current = -1;

            private ListItr() {
            }

            @Override
            public void add(HttpField field) {
                if (field == null) {
                    return;
                }
                Mutable.this._fields = Arrays.copyOf(Mutable.this._fields, Mutable.this._fields.length + 1);
                System.arraycopy(Mutable.this._fields, this._cursor, Mutable.this._fields, this._cursor + 1, Mutable.this._size++);
                Mutable.this._fields[this._cursor++] = field;
                this._current = -1;
            }

            @Override
            public boolean hasNext() {
                return this._cursor != Mutable.this._size;
            }

            @Override
            public boolean hasPrevious() {
                return this._cursor > 0;
            }

            @Override
            public HttpField next() {
                if (this._cursor == Mutable.this._size) {
                    throw new NoSuchElementException();
                }
                this._current = this._cursor++;
                return Mutable.this._fields[this._current];
            }

            @Override
            public int nextIndex() {
                return this._cursor + 1;
            }

            @Override
            public HttpField previous() {
                if (this._cursor == 0) {
                    throw new NoSuchElementException();
                }
                this._current = --this._cursor;
                return Mutable.this._fields[this._current];
            }

            @Override
            public int previousIndex() {
                return this._cursor - 1;
            }

            @Override
            public void remove() {
                if (this._current < 0) {
                    throw new IllegalStateException();
                }
                Mutable.this.remove(this._current);
                this._cursor = this._current;
                this._current = -1;
            }

            @Override
            public void set(HttpField field) {
                if (this._current < 0) {
                    throw new IllegalStateException();
                }
                if (field == null) {
                    this.remove();
                } else {
                    Mutable.this._fields[this._current] = field;
                }
            }
        }
    }

    public static class Immutable
    implements HttpFields {
        final HttpField[] _fields;

        public Immutable(HttpField[] fields) {
            this._fields = fields;
        }

        @Override
        public Immutable asImmutable() {
            return this;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Immutable)) {
                return false;
            }
            return this.isEqualTo((HttpFields)o);
        }

        @Override
        public String get(String header) {
            for (HttpField f : this._fields) {
                if (!f.is(header)) continue;
                return f.getValue();
            }
            return null;
        }

        @Override
        public String get(HttpHeader header) {
            for (HttpField f : this._fields) {
                if (f.getHeader() != header) continue;
                return f.getValue();
            }
            return null;
        }

        @Override
        public HttpField getField(HttpHeader header) {
            for (HttpField f : this._fields) {
                if (f.getHeader() != header) continue;
                return f;
            }
            return null;
        }

        @Override
        public HttpField getField(String name) {
            for (HttpField f : this._fields) {
                if (!f.is(name)) continue;
                return f;
            }
            return null;
        }

        @Override
        public HttpField getField(int index) {
            if (index >= this._fields.length) {
                throw new NoSuchElementException();
            }
            return this._fields[index];
        }

        public int hashCode() {
            int hash = 0;
            int i = this._fields.length;
            while (i-- > 0) {
                hash ^= this._fields[i].hashCode();
            }
            return hash;
        }

        @Override
        public Iterator<HttpField> iterator() {
            return new Iterator<HttpField>(){
                int _index = 0;

                @Override
                public boolean hasNext() {
                    return this._index < _fields.length;
                }

                @Override
                public HttpField next() {
                    return _fields[this._index++];
                }
            };
        }

        @Override
        public int size() {
            return this._fields.length;
        }

        @Override
        public Stream<HttpField> stream() {
            return Arrays.stream(this._fields);
        }

        public String toString() {
            return this.asString();
        }
    }
}

