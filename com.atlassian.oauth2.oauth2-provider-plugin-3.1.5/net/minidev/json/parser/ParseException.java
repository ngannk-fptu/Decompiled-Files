/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

public class ParseException
extends Exception {
    private static final long serialVersionUID = 8879024178584091857L;
    public static final int ERROR_UNEXPECTED_CHAR = 0;
    public static final int ERROR_UNEXPECTED_TOKEN = 1;
    public static final int ERROR_UNEXPECTED_EXCEPTION = 2;
    public static final int ERROR_UNEXPECTED_EOF = 3;
    public static final int ERROR_UNEXPECTED_UNICODE = 4;
    public static final int ERROR_UNEXPECTED_DUPLICATE_KEY = 5;
    public static final int ERROR_UNEXPECTED_LEADING_0 = 6;
    public static final int ERROR_UNEXPECTED_JSON_DEPTH = 7;
    private int errorType;
    private Object unexpectedObject;
    private int position;

    public ParseException(int position, int errorType, Object unexpectedObject) {
        super(ParseException.toMessage(position, errorType, unexpectedObject));
        this.position = position;
        this.errorType = errorType;
        this.unexpectedObject = unexpectedObject;
    }

    public ParseException(int position, Throwable cause) {
        super(ParseException.toMessage(position, 2, cause), cause);
        this.position = position;
        this.errorType = 2;
        this.unexpectedObject = cause;
    }

    public int getErrorType() {
        return this.errorType;
    }

    public int getPosition() {
        return this.position;
    }

    public Object getUnexpectedObject() {
        return this.unexpectedObject;
    }

    private static String toMessage(int position, int errorType, Object unexpectedObject) {
        StringBuilder sb = new StringBuilder();
        if (errorType == 0) {
            sb.append("Unexpected character (");
            sb.append(unexpectedObject);
            sb.append(") at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 1) {
            sb.append("Unexpected token ");
            sb.append(unexpectedObject);
            sb.append(" at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 2) {
            sb.append("Unexpected exception ");
            sb.append(unexpectedObject);
            sb.append(" occur at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 3) {
            sb.append("Unexpected End Of File position ");
            sb.append(position);
            sb.append(": ");
            sb.append(unexpectedObject);
        } else if (errorType == 4) {
            sb.append("Unexpected unicode escape sequence ");
            sb.append(unexpectedObject);
            sb.append(" at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 5) {
            sb.append("Unexpected duplicate key:");
            sb.append(unexpectedObject);
            sb.append(" at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 6) {
            sb.append("Unexpected leading 0 in digit for token:");
            sb.append(unexpectedObject);
            sb.append(" at position ");
            sb.append(position);
            sb.append(".");
        } else if (errorType == 7) {
            sb.append("Malicious payload, having non natural depths, parsing stoped on ");
            sb.append(unexpectedObject);
            sb.append(" at position ");
            sb.append(position);
            sb.append(".");
        } else {
            sb.append("Unkown error at position ");
            sb.append(position);
            sb.append(".");
        }
        return sb.toString();
    }
}

