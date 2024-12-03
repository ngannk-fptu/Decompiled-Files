/*
 * Decompiled with CFR 0.152.
 */
package groovy.json;

import groovy.json.JsonException;
import groovy.json.JsonTokenType;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonToken {
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);
    private long startLine;
    private long endLine;
    private long startColumn;
    private long endColumn;
    private JsonTokenType type;
    private String text;

    public Object getValue() {
        if (this.type == JsonTokenType.STRING) {
            if (this.text.length() == 2) {
                return "";
            }
            return this.text.substring(1, this.text.length() - 1);
        }
        if (this.type == JsonTokenType.NUMBER) {
            if (this.text.contains(".") || this.text.contains("e") || this.text.contains("E")) {
                return new BigDecimal(this.text);
            }
            BigInteger v = new BigInteger(this.text);
            if (v.compareTo(MAX_INTEGER) <= 0 && v.compareTo(MIN_INTEGER) >= 0) {
                return v.intValue();
            }
            if (v.compareTo(MAX_LONG) <= 0 && v.compareTo(MIN_LONG) >= 0) {
                return v.longValue();
            }
            return v;
        }
        if (this.type == JsonTokenType.TRUE) {
            return true;
        }
        if (this.type == JsonTokenType.FALSE) {
            return false;
        }
        if (this.type == JsonTokenType.NULL) {
            return null;
        }
        throw new JsonException("No appropriate value represented by '" + this.text + "' on line: " + this.startLine + ", column: " + this.startColumn);
    }

    public String toString() {
        return this.text + " (" + (Object)((Object)this.type) + ") [" + this.startLine + ":" + this.startColumn + "-" + this.endLine + ":" + this.endColumn + "]";
    }

    public long getStartLine() {
        return this.startLine;
    }

    public void setStartLine(long startLine) {
        this.startLine = startLine;
    }

    public long getEndLine() {
        return this.endLine;
    }

    public void setEndLine(long endLine) {
        this.endLine = endLine;
    }

    public long getStartColumn() {
        return this.startColumn;
    }

    public void setStartColumn(long startColumn) {
        this.startColumn = startColumn;
    }

    public long getEndColumn() {
        return this.endColumn;
    }

    public void setEndColumn(long endColumn) {
        this.endColumn = endColumn;
    }

    public JsonTokenType getType() {
        return this.type;
    }

    public void setType(JsonTokenType type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}

