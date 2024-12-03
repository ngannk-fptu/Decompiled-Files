/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.parser;

import org.apache.el.parser.Token;

public class ParseException
extends Exception {
    private static final long serialVersionUID = 1L;
    protected static String EOL = System.getProperty("line.separator", "\n");
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;

    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super(ParseException.initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
        this.currentToken = currentTokenVal;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
    }

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
        StringBuilder expected = new StringBuilder();
        int maxSize = 0;
        for (int i = 0; i < expectedTokenSequences.length; ++i) {
            if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
            }
            for (int j = 0; j < expectedTokenSequences[i].length; ++j) {
                expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
            }
            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
            }
            expected.append(EOL).append("    ");
        }
        String retval = "Encountered \"";
        Token tok = currentToken.next;
        for (int i = 0; i < maxSize; ++i) {
            if (i != 0) {
                retval = retval + " ";
            }
            if (tok.kind == 0) {
                retval = retval + tokenImage[0];
                break;
            }
            retval = retval + " " + tokenImage[tok.kind];
            retval = retval + " \"";
            retval = retval + ParseException.add_escapes(tok.image);
            retval = retval + " \"";
            tok = tok.next;
        }
        if (currentToken.next != null) {
            retval = retval + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
        }
        retval = retval + "." + EOL;
        if (expectedTokenSequences.length != 0) {
            retval = expectedTokenSequences.length == 1 ? retval + "Was expecting:" + EOL + "    " : retval + "Was expecting one of:" + EOL + "    ";
            retval = retval + expected.toString();
        }
        return retval;
    }

    static String add_escapes(String str) {
        StringBuilder retval = new StringBuilder();
        block10: for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '\b': {
                    retval.append("\\b");
                    continue block10;
                }
                case '\t': {
                    retval.append("\\t");
                    continue block10;
                }
                case '\n': {
                    retval.append("\\n");
                    continue block10;
                }
                case '\f': {
                    retval.append("\\f");
                    continue block10;
                }
                case '\r': {
                    retval.append("\\r");
                    continue block10;
                }
                case '\"': {
                    retval.append("\\\"");
                    continue block10;
                }
                case '\'': {
                    retval.append("\\'");
                    continue block10;
                }
                case '\\': {
                    retval.append("\\\\");
                    continue block10;
                }
                default: {
                    char ch = str.charAt(i);
                    if (ch < ' ' || ch > '~') {
                        String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u" + s.substring(s.length() - 4));
                        continue block10;
                    }
                    retval.append(ch);
                }
            }
        }
        return retval.toString();
    }
}

