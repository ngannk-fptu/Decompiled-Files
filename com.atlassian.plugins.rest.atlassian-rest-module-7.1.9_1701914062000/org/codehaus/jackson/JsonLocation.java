/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class JsonLocation
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final JsonLocation NA = new JsonLocation("N/A", -1L, -1L, -1, -1);
    final long _totalBytes;
    final long _totalChars;
    final int _lineNr;
    final int _columnNr;
    final Object _sourceRef;

    public JsonLocation(Object srcRef, long totalChars, int lineNr, int colNr) {
        this(srcRef, -1L, totalChars, lineNr, colNr);
    }

    @JsonCreator
    public JsonLocation(@JsonProperty(value="sourceRef") Object sourceRef, @JsonProperty(value="byteOffset") long totalBytes, @JsonProperty(value="charOffset") long totalChars, @JsonProperty(value="lineNr") int lineNr, @JsonProperty(value="columnNr") int columnNr) {
        this._sourceRef = sourceRef;
        this._totalBytes = totalBytes;
        this._totalChars = totalChars;
        this._lineNr = lineNr;
        this._columnNr = columnNr;
    }

    public Object getSourceRef() {
        return this._sourceRef;
    }

    public int getLineNr() {
        return this._lineNr;
    }

    public int getColumnNr() {
        return this._columnNr;
    }

    public long getCharOffset() {
        return this._totalChars;
    }

    public long getByteOffset() {
        return this._totalBytes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("[Source: ");
        if (this._sourceRef == null) {
            sb.append("UNKNOWN");
        } else {
            sb.append(this._sourceRef.toString());
        }
        sb.append("; line: ");
        sb.append(this._lineNr);
        sb.append(", column: ");
        sb.append(this._columnNr);
        sb.append(']');
        return sb.toString();
    }

    public int hashCode() {
        int hash = this._sourceRef == null ? 1 : this._sourceRef.hashCode();
        hash ^= this._lineNr;
        hash += this._columnNr;
        hash ^= (int)this._totalChars;
        return hash += (int)this._totalBytes;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof JsonLocation)) {
            return false;
        }
        JsonLocation otherLoc = (JsonLocation)other;
        if (this._sourceRef == null ? otherLoc._sourceRef != null : !this._sourceRef.equals(otherLoc._sourceRef)) {
            return false;
        }
        return this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && this.getByteOffset() == otherLoc.getByteOffset();
    }
}

