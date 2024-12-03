/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.jackrabbit.commons.cnd.ParseException;

public class Lexer {
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '\"';
    public static final char BEGIN_NODE_TYPE_NAME = '[';
    public static final char END_NODE_TYPE_NAME = ']';
    public static final char EXTENDS = '>';
    public static final char LIST_DELIMITER = ',';
    public static final char PROPERTY_DEFINITION = '-';
    public static final char CHILD_NODE_DEFINITION = '+';
    public static final char BEGIN_TYPE = '(';
    public static final char END_TYPE = ')';
    public static final char DEFAULT = '=';
    public static final char CONSTRAINT = '<';
    public static final String[] ORDERABLE = new String[]{"orderable", "ord", "o"};
    public static final String[] MIXIN = new String[]{"mixin", "mix", "m"};
    public static final String[] ABSTRACT = new String[]{"abstract", "abs", "a"};
    public static final String[] NOQUERY = new String[]{"noquery", "nq"};
    public static final String[] QUERY = new String[]{"query", "q"};
    public static final String[] PRIMARYITEM = new String[]{"primaryitem", "!"};
    public static final String[] PRIMARY = new String[]{"primary", "pri", "!"};
    public static final String[] AUTOCREATED = new String[]{"autocreated", "aut", "a"};
    public static final String[] MANDATORY = new String[]{"mandatory", "man", "m"};
    public static final String[] PROTECTED = new String[]{"protected", "pro", "p"};
    public static final String[] MULTIPLE = new String[]{"multiple", "mul", "*"};
    public static final String[] SNS = new String[]{"sns", "*", "multiple"};
    public static final String[] QUERYOPS = new String[]{"queryops", "qop"};
    public static final String[] NOFULLTEXT = new String[]{"nofulltext", "nof"};
    public static final String[] NOQUERYORDER = new String[]{"noqueryorder", "nqord"};
    public static final String[] COPY = new String[]{"COPY"};
    public static final String[] VERSION = new String[]{"VERSION"};
    public static final String[] INITIALIZE = new String[]{"INITIALIZE"};
    public static final String[] COMPUTE = new String[]{"COMPUTE"};
    public static final String[] IGNORE = new String[]{"IGNORE"};
    public static final String[] ABORT = new String[]{"ABORT"};
    public static final String[] PROP_ATTRIBUTE;
    public static final String[] NODE_ATTRIBUTE;
    public static final String QUEROPS_EQUAL = "=";
    public static final String QUEROPS_NOTEQUAL = "<>";
    public static final String QUEROPS_LESSTHAN = "<";
    public static final String QUEROPS_LESSTHANOREQUAL = "<=";
    public static final String QUEROPS_GREATERTHAN = ">";
    public static final String QUEROPS_GREATERTHANOREQUAL = ">=";
    public static final String QUEROPS_LIKE = "LIKE";
    public static final String[] STRING;
    public static final String[] BINARY;
    public static final String[] LONG;
    public static final String[] DOUBLE;
    public static final String[] BOOLEAN;
    public static final String[] DATE;
    public static final String[] NAME;
    public static final String[] PATH;
    public static final String[] REFERENCE;
    public static final String[] WEAKREFERENCE;
    public static final String[] URI;
    public static final String[] DECIMAL;
    public static final String[] UNDEFINED;
    public static final String EOF = "eof";
    private final StreamTokenizer st;
    private final String systemId;

    public Lexer(Reader r, String systemId) {
        this.systemId = systemId;
        this.st = new StreamTokenizer(r);
        this.st.eolIsSignificant(false);
        this.st.lowerCaseMode(false);
        this.st.slashSlashComments(true);
        this.st.slashStarComments(true);
        this.st.wordChars(97, 122);
        this.st.wordChars(65, 90);
        this.st.wordChars(58, 58);
        this.st.wordChars(95, 95);
        this.st.quoteChar(39);
        this.st.quoteChar(34);
        this.st.ordinaryChar(91);
        this.st.ordinaryChar(93);
        this.st.ordinaryChar(62);
        this.st.ordinaryChar(44);
        this.st.ordinaryChar(45);
        this.st.ordinaryChar(43);
        this.st.ordinaryChar(40);
        this.st.ordinaryChar(41);
        this.st.ordinaryChar(61);
        this.st.ordinaryChar(60);
    }

    public String getNextToken() throws ParseException {
        try {
            int tokenType = this.st.nextToken();
            if (tokenType == -1) {
                return EOF;
            }
            if (tokenType == -3 || tokenType == 39 || tokenType == 34) {
                return this.st.sval;
            }
            if (tokenType == -2) {
                return String.valueOf(this.st.nval);
            }
            return new String(new char[]{(char)tokenType});
        }
        catch (IOException e) {
            this.fail("IOException while attempting to read input stream", e);
            return null;
        }
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getLineNumber() {
        return this.st.lineno();
    }

    public void fail(String message) throws ParseException {
        throw new ParseException(message, this.getLineNumber(), -1, this.systemId);
    }

    public void fail(String message, Throwable e) throws ParseException {
        throw new ParseException(message, e, this.getLineNumber(), -1, this.systemId);
    }

    public void fail(Throwable e) throws ParseException {
        throw new ParseException(e, this.getLineNumber(), -1, this.systemId);
    }

    static {
        ArrayList<String> attr = new ArrayList<String>();
        attr.addAll(Arrays.asList(PRIMARY));
        attr.addAll(Arrays.asList(AUTOCREATED));
        attr.addAll(Arrays.asList(MANDATORY));
        attr.addAll(Arrays.asList(PROTECTED));
        attr.addAll(Arrays.asList(MULTIPLE));
        attr.addAll(Arrays.asList(QUERYOPS));
        attr.addAll(Arrays.asList(NOFULLTEXT));
        attr.addAll(Arrays.asList(NOQUERYORDER));
        attr.addAll(Arrays.asList(COPY));
        attr.addAll(Arrays.asList(VERSION));
        attr.addAll(Arrays.asList(INITIALIZE));
        attr.addAll(Arrays.asList(COMPUTE));
        attr.addAll(Arrays.asList(IGNORE));
        attr.addAll(Arrays.asList(ABORT));
        PROP_ATTRIBUTE = attr.toArray(new String[attr.size()]);
        attr = new ArrayList();
        attr.addAll(Arrays.asList(PRIMARY));
        attr.addAll(Arrays.asList(AUTOCREATED));
        attr.addAll(Arrays.asList(MANDATORY));
        attr.addAll(Arrays.asList(PROTECTED));
        attr.addAll(Arrays.asList(SNS));
        attr.addAll(Arrays.asList(COPY));
        attr.addAll(Arrays.asList(VERSION));
        attr.addAll(Arrays.asList(INITIALIZE));
        attr.addAll(Arrays.asList(COMPUTE));
        attr.addAll(Arrays.asList(IGNORE));
        attr.addAll(Arrays.asList(ABORT));
        NODE_ATTRIBUTE = attr.toArray(new String[attr.size()]);
        STRING = new String[]{"STRING"};
        BINARY = new String[]{"BINARY"};
        LONG = new String[]{"LONG"};
        DOUBLE = new String[]{"DOUBLE"};
        BOOLEAN = new String[]{"BOOLEAN"};
        DATE = new String[]{"DATE"};
        NAME = new String[]{"NAME"};
        PATH = new String[]{"PATH"};
        REFERENCE = new String[]{"REFERENCE"};
        WEAKREFERENCE = new String[]{"WEAKREFERENCE"};
        URI = new String[]{"URI"};
        DECIMAL = new String[]{"DECIMAL"};
        UNDEFINED = new String[]{"UNDEFINED", "*"};
    }
}

