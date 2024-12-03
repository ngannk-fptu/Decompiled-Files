/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DefaultHeaders
 *  io.netty.handler.codec.DefaultHeaders$NameValidator
 *  io.netty.handler.codec.DefaultHeaders$ValueValidator
 *  io.netty.handler.codec.Headers
 *  io.netty.handler.codec.ValueConverter
 *  io.netty.util.AsciiString
 *  io.netty.util.HashingStrategy
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CombinedHttpHeaders
extends DefaultHttpHeaders {
    public CombinedHttpHeaders(boolean validate) {
        super(new CombinedHttpHeadersImpl((HashingStrategy<CharSequence>)AsciiString.CASE_INSENSITIVE_HASHER, CombinedHttpHeaders.valueConverter(), CombinedHttpHeaders.nameValidator(validate), CombinedHttpHeaders.valueValidator(validate)));
    }

    @Override
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        return super.containsValue(name, StringUtil.trimOws((CharSequence)value), ignoreCase);
    }

    private static final class CombinedHttpHeadersImpl
    extends DefaultHeaders<CharSequence, CharSequence, CombinedHttpHeadersImpl> {
        private static final int VALUE_LENGTH_ESTIMATE = 10;
        private CsvValueEscaper<Object> objectEscaper;
        private CsvValueEscaper<CharSequence> charSequenceEscaper;

        private CsvValueEscaper<Object> objectEscaper() {
            if (this.objectEscaper == null) {
                this.objectEscaper = new CsvValueEscaper<Object>(){

                    @Override
                    public CharSequence escape(CharSequence name, Object value) {
                        CharSequence converted;
                        try {
                            converted = (CharSequence)CombinedHttpHeadersImpl.this.valueConverter().convertObject(value);
                        }
                        catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Failed to convert object value for header '" + name + '\'', e);
                        }
                        return StringUtil.escapeCsv((CharSequence)converted, (boolean)true);
                    }
                };
            }
            return this.objectEscaper;
        }

        private CsvValueEscaper<CharSequence> charSequenceEscaper() {
            if (this.charSequenceEscaper == null) {
                this.charSequenceEscaper = new CsvValueEscaper<CharSequence>(){

                    @Override
                    public CharSequence escape(CharSequence name, CharSequence value) {
                        return StringUtil.escapeCsv((CharSequence)value, (boolean)true);
                    }
                };
            }
            return this.charSequenceEscaper;
        }

        CombinedHttpHeadersImpl(HashingStrategy<CharSequence> nameHashingStrategy, ValueConverter<CharSequence> valueConverter, DefaultHeaders.NameValidator<CharSequence> nameValidator, DefaultHeaders.ValueValidator<CharSequence> valueValidator) {
            super(nameHashingStrategy, valueConverter, nameValidator, 16, valueValidator);
        }

        public Iterator<CharSequence> valueIterator(CharSequence name) {
            Iterator itr = super.valueIterator((Object)name);
            if (!itr.hasNext() || CombinedHttpHeadersImpl.cannotBeCombined(name)) {
                return itr;
            }
            Iterator<CharSequence> unescapedItr = StringUtil.unescapeCsvFields((CharSequence)((CharSequence)itr.next())).iterator();
            if (itr.hasNext()) {
                throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            }
            return unescapedItr;
        }

        public List<CharSequence> getAll(CharSequence name) {
            List values = super.getAll((Object)name);
            if (values.isEmpty() || CombinedHttpHeadersImpl.cannotBeCombined(name)) {
                return values;
            }
            if (values.size() != 1) {
                throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            }
            return StringUtil.unescapeCsvFields((CharSequence)((CharSequence)values.get(0)));
        }

        public CombinedHttpHeadersImpl add(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                throw new IllegalArgumentException("can't add to itself.");
            }
            if (headers instanceof CombinedHttpHeadersImpl) {
                if (this.isEmpty()) {
                    this.addImpl(headers);
                } else {
                    for (Map.Entry header : headers) {
                        this.addEscapedValue((CharSequence)header.getKey(), (CharSequence)header.getValue());
                    }
                }
            } else {
                for (Map.Entry header : headers) {
                    this.add((CharSequence)header.getKey(), (CharSequence)header.getValue());
                }
            }
            return this;
        }

        public CombinedHttpHeadersImpl set(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                return this;
            }
            this.clear();
            return this.add(headers);
        }

        public CombinedHttpHeadersImpl setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                return this;
            }
            for (CharSequence key : headers.names()) {
                this.remove(key);
            }
            return this.add(headers);
        }

        public CombinedHttpHeadersImpl add(CharSequence name, CharSequence value) {
            return this.addEscapedValue(name, this.charSequenceEscaper().escape(name, value));
        }

        public CombinedHttpHeadersImpl add(CharSequence name, CharSequence ... values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(name, this.charSequenceEscaper(), values));
        }

        public CombinedHttpHeadersImpl add(CharSequence name, Iterable<? extends CharSequence> values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(name, this.charSequenceEscaper(), values));
        }

        public CombinedHttpHeadersImpl addObject(CharSequence name, Object value) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), value));
        }

        public CombinedHttpHeadersImpl addObject(CharSequence name, Iterable<?> values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), values));
        }

        public CombinedHttpHeadersImpl addObject(CharSequence name, Object ... values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), values));
        }

        public CombinedHttpHeadersImpl set(CharSequence name, CharSequence ... values) {
            this.set(name, CombinedHttpHeadersImpl.commaSeparate(name, this.charSequenceEscaper(), values));
            return this;
        }

        public CombinedHttpHeadersImpl set(CharSequence name, Iterable<? extends CharSequence> values) {
            this.set(name, CombinedHttpHeadersImpl.commaSeparate(name, this.charSequenceEscaper(), values));
            return this;
        }

        public CombinedHttpHeadersImpl setObject(CharSequence name, Object value) {
            this.set(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), value));
            return this;
        }

        public CombinedHttpHeadersImpl setObject(CharSequence name, Object ... values) {
            this.set(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), values));
            return this;
        }

        public CombinedHttpHeadersImpl setObject(CharSequence name, Iterable<?> values) {
            this.set(name, CombinedHttpHeadersImpl.commaSeparate(name, this.objectEscaper(), values));
            return this;
        }

        private static boolean cannotBeCombined(CharSequence name) {
            return HttpHeaderNames.SET_COOKIE.contentEqualsIgnoreCase(name);
        }

        private CombinedHttpHeadersImpl addEscapedValue(CharSequence name, CharSequence escapedValue) {
            CharSequence currentValue = (CharSequence)this.get(name);
            if (currentValue == null || CombinedHttpHeadersImpl.cannotBeCombined(name)) {
                super.add((Object)name, (Object)escapedValue);
            } else {
                this.set(name, CombinedHttpHeadersImpl.commaSeparateEscapedValues(currentValue, escapedValue));
            }
            return this;
        }

        private static <T> CharSequence commaSeparate(CharSequence name, CsvValueEscaper<T> escaper, T ... values) {
            StringBuilder sb = new StringBuilder(values.length * 10);
            if (values.length > 0) {
                int end = values.length - 1;
                for (int i = 0; i < end; ++i) {
                    sb.append(escaper.escape(name, values[i])).append(',');
                }
                sb.append(escaper.escape(name, values[end]));
            }
            return sb;
        }

        private static <T> CharSequence commaSeparate(CharSequence name, CsvValueEscaper<T> escaper, Iterable<? extends T> values) {
            StringBuilder sb = values instanceof Collection ? new StringBuilder(((Collection)values).size() * 10) : new StringBuilder();
            Iterator<T> iterator = values.iterator();
            if (iterator.hasNext()) {
                T next = iterator.next();
                while (iterator.hasNext()) {
                    sb.append(escaper.escape(name, next)).append(',');
                    next = iterator.next();
                }
                sb.append(escaper.escape(name, next));
            }
            return sb;
        }

        private static CharSequence commaSeparateEscapedValues(CharSequence currentValue, CharSequence value) {
            return new StringBuilder(currentValue.length() + 1 + value.length()).append(currentValue).append(',').append(value);
        }

        private static interface CsvValueEscaper<T> {
            public CharSequence escape(CharSequence var1, T var2);
        }
    }
}

