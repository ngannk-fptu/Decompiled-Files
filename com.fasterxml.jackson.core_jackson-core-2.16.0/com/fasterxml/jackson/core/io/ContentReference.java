/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.core.io.CharTypes;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ContentReference
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final ContentReference UNKNOWN_CONTENT = new ContentReference(false, null);
    protected static final ContentReference REDACTED_CONTENT = new ContentReference(false, null);
    @Deprecated
    public static final int DEFAULT_MAX_CONTENT_SNIPPET = 500;
    protected final transient Object _rawContent;
    protected final int _offset;
    protected final int _length;
    protected final boolean _isContentTextual;
    protected final int _maxRawContentLength;

    @Deprecated
    protected ContentReference(boolean isContentTextual, Object rawContent) {
        this(isContentTextual, rawContent, -1, -1, ErrorReportConfiguration.defaults());
    }

    @Deprecated
    protected ContentReference(boolean isContentTextual, Object rawContent, int offset, int length) {
        this(isContentTextual, rawContent, offset, length, ErrorReportConfiguration.defaults());
    }

    protected ContentReference(boolean isContentTextual, Object rawContent, ErrorReportConfiguration errorReportConfiguration) {
        this(isContentTextual, rawContent, -1, -1, errorReportConfiguration);
    }

    protected ContentReference(boolean isContentTextual, Object rawContent, int offset, int length, ErrorReportConfiguration errorReportConfiguration) {
        this._isContentTextual = isContentTextual;
        this._rawContent = rawContent;
        this._offset = offset;
        this._length = length;
        this._maxRawContentLength = errorReportConfiguration.getMaxRawContentLength();
    }

    public static ContentReference unknown() {
        return UNKNOWN_CONTENT;
    }

    public static ContentReference redacted() {
        return REDACTED_CONTENT;
    }

    @Deprecated
    public static ContentReference construct(boolean isContentTextual, Object rawContent) {
        return new ContentReference(isContentTextual, rawContent, ErrorReportConfiguration.defaults());
    }

    @Deprecated
    public static ContentReference construct(boolean isContentTextual, Object rawContent, int offset, int length) {
        return new ContentReference(isContentTextual, rawContent, offset, length, ErrorReportConfiguration.defaults());
    }

    public static ContentReference construct(boolean isContentTextual, Object rawContent, int offset, int length, ErrorReportConfiguration errorReportConfiguration) {
        return new ContentReference(isContentTextual, rawContent, offset, length, errorReportConfiguration);
    }

    public static ContentReference construct(boolean isContentTextual, Object rawContent, ErrorReportConfiguration errorReportConfiguration) {
        return new ContentReference(isContentTextual, rawContent, errorReportConfiguration);
    }

    public static ContentReference rawReference(boolean isContentTextual, Object rawContent) {
        if (rawContent instanceof ContentReference) {
            return (ContentReference)rawContent;
        }
        return new ContentReference(isContentTextual, rawContent);
    }

    public static ContentReference rawReference(Object rawContent) {
        return ContentReference.rawReference(false, rawContent);
    }

    private void readObject(ObjectInputStream in) throws IOException {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
    }

    protected Object readResolve() {
        return UNKNOWN_CONTENT;
    }

    public boolean hasTextualContent() {
        return this._isContentTextual;
    }

    public Object getRawContent() {
        return this._rawContent;
    }

    public int contentOffset() {
        return this._offset;
    }

    public int contentLength() {
        return this._length;
    }

    protected int maxRawContentLength() {
        return this._maxRawContentLength;
    }

    public String buildSourceDescription() {
        return this.appendSourceDescription(new StringBuilder(200)).toString();
    }

    public StringBuilder appendSourceDescription(StringBuilder sb) {
        Object srcRef = this.getRawContent();
        if (srcRef == null) {
            if (this == REDACTED_CONTENT) {
                sb.append("REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled)");
            } else {
                sb.append("UNKNOWN");
            }
            return sb;
        }
        Class<?> srcType = srcRef instanceof Class ? (Class<?>)srcRef : srcRef.getClass();
        String tn = srcType.getName();
        if (tn.startsWith("java.")) {
            tn = srcType.getSimpleName();
        } else if (srcRef instanceof byte[]) {
            tn = "byte[]";
        } else if (srcRef instanceof char[]) {
            tn = "char[]";
        }
        sb.append('(').append(tn).append(')');
        if (this.hasTextualContent()) {
            String trimmed;
            String unitStr = " chars";
            int maxLen = this.maxRawContentLength();
            int[] offsets = new int[]{this.contentOffset(), this.contentLength()};
            if (srcRef instanceof CharSequence) {
                trimmed = this._truncate((CharSequence)srcRef, offsets, maxLen);
            } else if (srcRef instanceof char[]) {
                trimmed = this._truncate((char[])srcRef, offsets, maxLen);
            } else if (srcRef instanceof byte[]) {
                trimmed = this._truncate((byte[])srcRef, offsets, maxLen);
                unitStr = " bytes";
            } else {
                trimmed = null;
            }
            if (trimmed != null) {
                this._append(sb, trimmed);
                if (offsets[1] > maxLen) {
                    sb.append("[truncated ").append(offsets[1] - maxLen).append(unitStr).append(']');
                }
            }
        } else if (srcRef instanceof byte[]) {
            int length = this.contentLength();
            if (length < 0) {
                length = ((byte[])srcRef).length;
            }
            sb.append('[').append(length).append(" bytes]");
        }
        return sb;
    }

    protected String _truncate(CharSequence cs, int[] offsets, int maxSnippetLen) {
        this._truncateOffsets(offsets, cs.length());
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return cs.subSequence(start, start + length).toString();
    }

    protected String _truncate(char[] cs, int[] offsets, int maxSnippetLen) {
        this._truncateOffsets(offsets, cs.length);
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return new String(cs, start, length);
    }

    protected String _truncate(byte[] b, int[] offsets, int maxSnippetLen) {
        this._truncateOffsets(offsets, b.length);
        int start = offsets[0];
        int length = Math.min(offsets[1], maxSnippetLen);
        return new String(b, start, length, StandardCharsets.UTF_8);
    }

    protected void _truncateOffsets(int[] offsets, int actualLength) {
        int start = offsets[0];
        if (start < 0) {
            start = 0;
        } else if (start >= actualLength) {
            start = actualLength;
        }
        offsets[0] = start;
        int length = offsets[1];
        int maxLength = actualLength - start;
        if (length < 0 || length > maxLength) {
            offsets[1] = maxLength;
        }
    }

    protected int _append(StringBuilder sb, String content) {
        sb.append('\"');
        int end = content.length();
        for (int i = 0; i < end; ++i) {
            char ch = content.charAt(i);
            if (Character.isISOControl(ch) && this._appendEscaped(sb, ch)) continue;
            sb.append(ch);
        }
        sb.append('\"');
        return content.length();
    }

    protected boolean _appendEscaped(StringBuilder sb, int ctrlChar) {
        if (ctrlChar == 13 || ctrlChar == 10) {
            return false;
        }
        sb.append('\\');
        sb.append('u');
        sb.append(CharTypes.hexToChar(ctrlChar >> 12 & 0xF));
        sb.append(CharTypes.hexToChar(ctrlChar >> 8 & 0xF));
        sb.append(CharTypes.hexToChar(ctrlChar >> 4 & 0xF));
        sb.append(CharTypes.hexToChar(ctrlChar & 0xF));
        return true;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof ContentReference)) {
            return false;
        }
        ContentReference otherSrc = (ContentReference)other;
        if (this._offset != otherSrc._offset || this._length != otherSrc._length) {
            return false;
        }
        Object otherRaw = otherSrc._rawContent;
        if (this._rawContent == null) {
            return otherRaw == null;
        }
        if (otherRaw == null) {
            return false;
        }
        if (this._rawContent instanceof File || this._rawContent instanceof URL || this._rawContent instanceof URI) {
            return this._rawContent.equals(otherRaw);
        }
        return this._rawContent == otherSrc._rawContent;
    }

    public int hashCode() {
        return Objects.hashCode(this._rawContent);
    }
}

