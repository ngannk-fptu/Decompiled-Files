/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.apache.jackrabbit.server.remoting.davex.DiffException;
import org.apache.jackrabbit.server.remoting.davex.DiffHandler;

class DiffParser {
    private final DiffHandler handler;
    private static final int EOF = -1;
    private static final char SYMBOL_ADD_NODE = '+';
    private static final char SYMBOL_MOVE = '>';
    private static final char SYMBOL_REMOVE = '-';
    private static final char SYMBOL_SET_PROPERTY = '^';
    private static final int STATE_START_LINE = 0;
    private static final int STATE_START_TARGET = 1;
    private static final int STATE_TARGET = 2;
    private static final int STATE_START_VALUE = 3;
    private static final int STATE_VALUE = 4;

    public DiffParser(DiffHandler handler) {
        this.handler = handler;
    }

    public void parse(String str) throws IOException, DiffException {
        this.parse(new BufferedReader(new StringReader(str)));
    }

    public void parse(InputStream input, String charSetName) throws IOException, DiffException {
        this.parse(new BufferedReader(new InputStreamReader(input, charSetName)));
    }

    public void parse(Reader reader) throws IOException, DiffException {
        int action = -1;
        String path = null;
        StringBuffer lineSeparator = null;
        StringBuffer bf = null;
        int state = 0;
        int next = reader.read();
        while (next != -1) {
            switch (state) {
                case 0: {
                    if (DiffParser.isSymbol(next)) {
                        if (action > -1) {
                            this.informAction(action, path, bf);
                        }
                        action = next;
                        bf = null;
                        lineSeparator = null;
                        state = 1;
                        break;
                    }
                    if (DiffParser.isLineSeparator(next)) {
                        if (lineSeparator == null) {
                            throw new DiffException("Invalid start of new line.");
                        }
                        lineSeparator.append((char)next);
                        break;
                    }
                    if (lineSeparator != null && bf != null) {
                        bf.append(lineSeparator);
                        bf.append((char)next);
                        lineSeparator = null;
                        state = 4;
                        break;
                    }
                    throw new DiffException("Invalid start of new line.");
                }
                case 1: {
                    if (Character.isWhitespace((char)next) || next == 58) {
                        throw new DiffException("Invalid start of target path '" + next + "'");
                    }
                    bf = new StringBuffer();
                    bf.append((char)next);
                    state = 2;
                    break;
                }
                case 2: {
                    if (Character.isWhitespace((char)next) && DiffParser.endsWithDelim(bf)) {
                        path = bf.substring(0, bf.lastIndexOf(":")).trim();
                        state = 3;
                        bf = null;
                        break;
                    }
                    bf.append((char)next);
                    break;
                }
                case 3: {
                    if (DiffParser.isLineSeparator(next)) {
                        lineSeparator = new StringBuffer();
                        lineSeparator.append((char)next);
                        bf = new StringBuffer();
                        state = 0;
                        break;
                    }
                    bf = new StringBuffer();
                    bf.append((char)next);
                    state = 4;
                    break;
                }
                case 4: {
                    if (DiffParser.isLineSeparator(next)) {
                        lineSeparator = new StringBuffer();
                        lineSeparator.append((char)next);
                        state = 0;
                        break;
                    }
                    bf.append((char)next);
                }
            }
            next = reader.read();
        }
        if (state == 1 || state == 2) {
            throw new DiffException("Invalid end of DIFF string: missing separator and value.");
        }
        if (state == 3 && (lineSeparator != null || bf != null)) {
            throw new DiffException("Invalid end of DIFF string.");
        }
        if (lineSeparator != null) {
            bf.append(lineSeparator);
        }
        this.informAction(action, path, bf);
    }

    private void informAction(int action, String path, StringBuffer diffVal) throws DiffException {
        if (path == null) {
            throw new DiffException("Missing path for action " + action + "(diffValue = '" + diffVal + "')");
        }
        String value = diffVal == null ? null : diffVal.toString();
        switch (action) {
            case 43: {
                this.handler.addNode(path, value);
                break;
            }
            case 94: {
                this.handler.setProperty(path, value);
                break;
            }
            case 62: {
                this.handler.move(path, value);
                break;
            }
            case 45: {
                this.handler.remove(path, value);
                break;
            }
            default: {
                throw new DiffException("Invalid action " + action);
            }
        }
    }

    private static boolean isSymbol(int c) {
        return c == 43 || c == 94 || c == 62 || c == 45;
    }

    private static boolean isLineSeparator(int c) {
        return c == 10 || c == 13;
    }

    private static boolean endsWithDelim(StringBuffer bf) {
        if (bf.length() < 2) {
            return false;
        }
        return ':' == bf.charAt(bf.length() - 1) && Character.isWhitespace(bf.charAt(bf.length() - 2));
    }
}

