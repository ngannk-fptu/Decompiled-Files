/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public final class Term
implements Comparable<Term> {
    String field;
    BytesRef bytes;

    public Term(String fld, BytesRef bytes) {
        this.field = fld;
        this.bytes = bytes;
    }

    public Term(String fld, String text) {
        this(fld, new BytesRef(text));
    }

    public Term(String fld) {
        this(fld, new BytesRef());
    }

    public final String field() {
        return this.field;
    }

    public final String text() {
        return Term.toString(this.bytes);
    }

    public static final String toString(BytesRef termText) {
        CharsetDecoder decoder = IOUtils.CHARSET_UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(termText.bytes, termText.offset, termText.length)).toString();
        }
        catch (CharacterCodingException e) {
            return termText.toString();
        }
    }

    public final BytesRef bytes() {
        return this.bytes;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Term other = (Term)obj;
        if (this.field == null ? other.field != null : !this.field.equals(other.field)) {
            return false;
        }
        return !(this.bytes == null ? other.bytes != null : !this.bytes.equals(other.bytes));
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
        result = 31 * result + (this.bytes == null ? 0 : this.bytes.hashCode());
        return result;
    }

    @Override
    public final int compareTo(Term other) {
        if (this.field.equals(other.field)) {
            return this.bytes.compareTo(other.bytes);
        }
        return this.field.compareTo(other.field);
    }

    final void set(String fld, BytesRef bytes) {
        this.field = fld;
        this.bytes = bytes;
    }

    public final String toString() {
        return this.field + ":" + this.text();
    }
}

