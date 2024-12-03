/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.HttpHeaderValidationUtil;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHttpHeaders
extends HttpHeaders {
    static final DefaultHeaders.NameValidator<CharSequence> HttpNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

        @Override
        public void validateName(CharSequence name) {
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("empty headers are not allowed [" + name + ']');
            }
            int index = HttpHeaderValidationUtil.validateToken(name);
            if (index != -1) {
                throw new IllegalArgumentException("a header name can only contain \"token\" characters, but found invalid character 0x" + Integer.toHexString(name.charAt(index)) + " at index " + index + " of header '" + name + "'.");
            }
        }
    };
    private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

    public DefaultHttpHeaders() {
        this(true);
    }

    public DefaultHttpHeaders(boolean validate) {
        this(validate, DefaultHttpHeaders.nameValidator(validate));
    }

    protected DefaultHttpHeaders(boolean validate, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
        this(new DefaultHeadersImpl<CharSequence, CharSequence>(AsciiString.CASE_INSENSITIVE_HASHER, HeaderValueConverter.INSTANCE, nameValidator, 16, DefaultHttpHeaders.valueValidator(validate)));
    }

    protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
        this.headers = headers;
    }

    @Override
    public HttpHeaders add(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            this.headers.add(((DefaultHttpHeaders)headers).headers);
            return this;
        }
        return super.add(headers);
    }

    @Override
    public HttpHeaders set(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            this.headers.set(((DefaultHttpHeaders)headers).headers);
            return this;
        }
        return super.set(headers);
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        this.headers.addObject((CharSequence)name, value);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Object value) {
        this.headers.addObject(name, value);
        return this;
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        this.headers.addObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        this.headers.addObject(name, values);
        return this;
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        this.headers.addInt(name, value);
        return this;
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        this.headers.addShort(name, value);
        return this;
    }

    @Override
    public HttpHeaders remove(String name) {
        this.headers.remove(name);
        return this;
    }

    @Override
    public HttpHeaders remove(CharSequence name) {
        this.headers.remove(name);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        this.headers.setObject((CharSequence)name, value);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Object value) {
        this.headers.setObject(name, value);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        this.headers.setObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        this.headers.setObject(name, values);
        return this;
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        this.headers.setInt(name, value);
        return this;
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        this.headers.setShort(name, value);
        return this;
    }

    @Override
    public HttpHeaders clear() {
        this.headers.clear();
        return this;
    }

    @Override
    public String get(String name) {
        return this.get((CharSequence)name);
    }

    @Override
    public String get(CharSequence name) {
        return HeadersUtils.getAsString(this.headers, name);
    }

    @Override
    public Integer getInt(CharSequence name) {
        return this.headers.getInt(name);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return this.headers.getInt(name, defaultValue);
    }

    @Override
    public Short getShort(CharSequence name) {
        return this.headers.getShort(name);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return this.headers.getShort(name, defaultValue);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return this.headers.getTimeMillis(name);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return this.headers.getTimeMillis(name, defaultValue);
    }

    @Override
    public List<String> getAll(String name) {
        return this.getAll((CharSequence)name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return HeadersUtils.getAllAsString(this.headers, name);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map.Entry<String, String>> entriesConverted = new ArrayList<Map.Entry<String, String>>(this.headers.size());
        for (Map.Entry<String, String> entry : this) {
            entriesConverted.add(entry);
        }
        return entriesConverted;
    }

    @Override
    @Deprecated
    public Iterator<Map.Entry<String, String>> iterator() {
        return HeadersUtils.iteratorAsString(this.headers);
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return this.headers.iterator();
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        final Iterator<CharSequence> itr = this.valueCharSequenceIterator(name);
        return new Iterator<String>(){

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public String next() {
                return ((CharSequence)itr.next()).toString();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return this.headers.valueIterator(name);
    }

    @Override
    public boolean contains(String name) {
        return this.contains((CharSequence)name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return this.headers.contains(name);
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.contains((CharSequence)name, (CharSequence)value, ignoreCase);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return this.headers.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public Set<String> names() {
        return HeadersUtils.namesAsString(this.headers);
    }

    public boolean equals(Object o) {
        return o instanceof DefaultHttpHeaders && this.headers.equals(((DefaultHttpHeaders)o).headers, AsciiString.CASE_SENSITIVE_HASHER);
    }

    public int hashCode() {
        return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public HttpHeaders copy() {
        return new DefaultHttpHeaders(this.headers.copy());
    }

    static ValueConverter<CharSequence> valueConverter() {
        return HeaderValueConverter.INSTANCE;
    }

    static DefaultHeaders.ValueValidator<CharSequence> valueValidator(boolean validate) {
        return validate ? HeaderValueValidator.INSTANCE : DefaultHeaders.ValueValidator.NO_VALIDATION;
    }

    static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean validate) {
        return validate ? HttpNameValidator : DefaultHeaders.NameValidator.NOT_NULL;
    }

    private static final class HeaderValueValidator
    implements DefaultHeaders.ValueValidator<CharSequence> {
        static final HeaderValueValidator INSTANCE = new HeaderValueValidator();

        private HeaderValueValidator() {
        }

        @Override
        public void validate(CharSequence value) {
            int index = HttpHeaderValidationUtil.validateValidHeaderValue(value);
            if (index != -1) {
                throw new IllegalArgumentException("a header value contains prohibited character 0x" + Integer.toHexString(value.charAt(index)) + " at index " + index + '.');
            }
        }
    }

    private static class HeaderValueConverter
    extends CharSequenceValueConverter {
        static final HeaderValueConverter INSTANCE = new HeaderValueConverter();

        private HeaderValueConverter() {
        }

        @Override
        public CharSequence convertObject(Object value) {
            if (value instanceof CharSequence) {
                return (CharSequence)value;
            }
            if (value instanceof Date) {
                return DateFormatter.format((Date)value);
            }
            if (value instanceof Calendar) {
                return DateFormatter.format(((Calendar)value).getTime());
            }
            return value.toString();
        }
    }
}

