/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.HttpHeaderValidationUtil;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.PlatformDependent;

public class DefaultHttp2Headers
extends DefaultHeaders<CharSequence, CharSequence, Http2Headers>
implements Http2Headers {
    private static final ByteProcessor HTTP2_NAME_VALIDATOR_PROCESSOR = new ByteProcessor(){

        @Override
        public boolean process(byte value) {
            return !AsciiString.isUpperCase(value);
        }
    };
    static final DefaultHeaders.NameValidator<CharSequence> HTTP2_NAME_VALIDATOR = new DefaultHeaders.NameValidator<CharSequence>(){

        @Override
        public void validateName(CharSequence name) {
            Http2Headers.PseudoHeaderName pseudoHeader;
            if (name == null || name.length() == 0) {
                PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "empty headers are not allowed [%s]", name));
            }
            if (name instanceof AsciiString) {
                int index;
                try {
                    index = ((AsciiString)name).forEachByte(HTTP2_NAME_VALIDATOR_PROCESSOR);
                }
                catch (Http2Exception e) {
                    PlatformDependent.throwException(e);
                    return;
                }
                catch (Throwable t) {
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, t, "unexpected error. invalid header name [%s]", name));
                    return;
                }
                if (index != -1) {
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", name));
                }
            } else {
                for (int i = 0; i < name.length(); ++i) {
                    if (!AsciiString.isUpperCase(name.charAt(i))) continue;
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", name));
                }
            }
            if (Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(name) && (pseudoHeader = Http2Headers.PseudoHeaderName.getPseudoHeader(name)) == null) {
                PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Invalid HTTP/2 pseudo-header '%s' encountered.", name));
            }
        }
    };
    private static final DefaultHeaders.ValueValidator<CharSequence> VALUE_VALIDATOR = new DefaultHeaders.ValueValidator<CharSequence>(){

        @Override
        public void validate(CharSequence value) {
            int index = HttpHeaderValidationUtil.validateValidHeaderValue(value);
            if (index != -1) {
                throw new IllegalArgumentException("a header value contains prohibited character 0x" + Integer.toHexString(value.charAt(index)) + " at index " + index + '.');
            }
        }
    };
    private DefaultHeaders.HeaderEntry<CharSequence, CharSequence> firstNonPseudo;

    public DefaultHttp2Headers() {
        this(true);
    }

    public DefaultHttp2Headers(boolean validate) {
        super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL);
        this.firstNonPseudo = this.head;
    }

    public DefaultHttp2Headers(boolean validate, int arraySizeHint) {
        super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL, arraySizeHint);
        this.firstNonPseudo = this.head;
    }

    public DefaultHttp2Headers(boolean validate, boolean validateValues, int arraySizeHint) {
        super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL, arraySizeHint, validateValues ? VALUE_VALIDATOR : DefaultHeaders.ValueValidator.NO_VALIDATION);
        this.firstNonPseudo = this.head;
    }

    @Override
    protected void validateName(DefaultHeaders.NameValidator<CharSequence> validator, boolean forAdd, CharSequence name) {
        super.validateName(validator, forAdd, name);
        if (this.nameValidator() == HTTP2_NAME_VALIDATOR && forAdd && Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(name) && this.contains(name)) {
            PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Duplicate HTTP/2 pseudo-header '%s' encountered.", name));
        }
    }

    @Override
    protected void validateValue(DefaultHeaders.ValueValidator<CharSequence> validator, CharSequence name, CharSequence value) {
        super.validateValue(validator, name, value);
        if (this.nameValidator() == HTTP2_NAME_VALIDATOR && (value == null || value.length() == 0) && Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(name)) {
            PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP/2 pseudo-header '%s' must not be empty.", name));
        }
    }

    @Override
    public Http2Headers clear() {
        this.firstNonPseudo = this.head;
        return (Http2Headers)super.clear();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Http2Headers && this.equals((Http2Headers)o, AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public int hashCode() {
        return this.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public Http2Headers method(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.METHOD.value(), value);
        return this;
    }

    @Override
    public Http2Headers scheme(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.SCHEME.value(), value);
        return this;
    }

    @Override
    public Http2Headers authority(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.AUTHORITY.value(), value);
        return this;
    }

    @Override
    public Http2Headers path(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.PATH.value(), value);
        return this;
    }

    @Override
    public Http2Headers status(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.STATUS.value(), value);
        return this;
    }

    @Override
    public CharSequence method() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.METHOD.value());
    }

    @Override
    public CharSequence scheme() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
    }

    @Override
    public CharSequence authority() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
    }

    @Override
    public CharSequence path() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.PATH.value());
    }

    @Override
    public CharSequence status() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.STATUS.value());
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value) {
        return this.contains(name, value, false);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean caseInsensitive) {
        return this.contains(name, value, caseInsensitive ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    protected final DefaultHeaders.HeaderEntry<CharSequence, CharSequence> newHeaderEntry(int h, CharSequence name, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next) {
        return new Http2HeaderEntry(h, name, value, next);
    }

    private final class Http2HeaderEntry
    extends DefaultHeaders.HeaderEntry<CharSequence, CharSequence> {
        Http2HeaderEntry(int hash, CharSequence key, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next) {
            super(hash, key);
            this.value = value;
            this.next = next;
            if (Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(key)) {
                this.after = DefaultHttp2Headers.this.firstNonPseudo;
                this.before = DefaultHttp2Headers.this.firstNonPseudo.before();
            } else {
                this.after = DefaultHttp2Headers.this.head;
                this.before = DefaultHttp2Headers.this.head.before();
                if (DefaultHttp2Headers.this.firstNonPseudo == DefaultHttp2Headers.this.head) {
                    DefaultHttp2Headers.this.firstNonPseudo = this;
                }
            }
            this.pointNeighborsToThis();
        }

        @Override
        protected void remove() {
            if (this == DefaultHttp2Headers.this.firstNonPseudo) {
                DefaultHttp2Headers.this.firstNonPseudo = DefaultHttp2Headers.this.firstNonPseudo.after();
            }
            super.remove();
        }
    }
}

