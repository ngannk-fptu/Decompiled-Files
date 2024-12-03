/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.Token
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerFMTQuery;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import com.microsoft.sqlserver.jdbc.SQLServerTokenIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.antlr.v4.runtime.Token;

final class SQLServerParser {
    private static final List<Integer> SELECT_DELIMITING_WORDS = Arrays.asList(8, 10, 9, 11, 12);
    private static final List<Integer> INSERT_DELIMITING_WORDS = Arrays.asList(14, 15, 71, 1, 7, 17, 19);
    private static final List<Integer> DELETE_DELIMITING_WORDS = Arrays.asList(12, 8, 15, 5);
    private static final List<Integer> UPDATE_DELIMITING_WORDS = Arrays.asList(20, 15, 8, 12);
    private static final List<Integer> FROM_DELIMITING_WORDS = Arrays.asList(8, 10, 9, 11, 12, 35);
    private static final List<Integer> OPERATORS = Arrays.asList(50, 51, 52, 53, 54, 55, 57, 58, 59, 60, 61, 62, 63, 64, 80, 81, 82, 83, 84, 30, 31, 34);

    private SQLServerParser() {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    static void parseQuery(SQLServerTokenIterator iter, SQLServerFMTQuery query) throws SQLServerException {
        Token t = null;
        block8: while (iter.hasNext()) {
            t = iter.next();
            switch (t.getType()) {
                case 1: {
                    t = SQLServerParser.skipTop(iter);
                    while (t.getType() != 78) {
                        if (t.getType() == 89) {
                            String columnName = SQLServerParser.findColumnAroundParameter(iter);
                            query.getColumns().add(columnName);
                        }
                        if (t.getType() == 5) {
                            query.getTableTarget().add(SQLServerParser.getTableTargetChunk(iter, query.getAliases(), SELECT_DELIMITING_WORDS));
                            continue block8;
                        }
                        if (!iter.hasNext()) continue block8;
                        t = iter.next();
                    }
                    continue block8;
                }
                case 2: {
                    t = SQLServerParser.skipTop(iter);
                    if (t.getType() != 6) {
                        t = iter.previous();
                    }
                    query.getTableTarget().add(SQLServerParser.getTableTargetChunk(iter, query.getAliases(), INSERT_DELIMITING_WORDS));
                    if (!iter.hasNext()) continue block8;
                    List<String> tableValues = SQLServerParser.getValuesList(iter);
                    boolean valuesFound = false;
                    int valuesMarker = iter.nextIndex();
                    while (!valuesFound && iter.hasNext()) {
                        t = iter.next();
                        if (t.getType() != 14) continue;
                        valuesFound = true;
                        do {
                            query.getValuesList().add(SQLServerParser.getValuesList(iter));
                        } while (iter.hasNext() && iter.next().getType() == 77);
                        iter.previous();
                    }
                    if (!valuesFound) {
                        SQLServerParser.resetIteratorIndex(iter, valuesMarker);
                    }
                    if (query.getValuesList().isEmpty()) continue block8;
                    for (List<String> ls : query.getValuesList()) {
                        if (tableValues.isEmpty()) {
                            query.getColumns().add("*");
                        }
                        for (int i = 0; i < ls.size(); ++i) {
                            if (!"?".equalsIgnoreCase(ls.get(i))) continue;
                            if (tableValues.isEmpty()) {
                                query.getColumns().add("?");
                                continue;
                            }
                            if (i < tableValues.size()) {
                                query.getColumns().add(tableValues.get(i));
                                continue;
                            }
                            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidInsertValuesQuery"), null, false);
                        }
                    }
                    continue block8;
                }
                case 3: {
                    t = SQLServerParser.skipTop(iter);
                    if (t.getType() != 5) {
                        t = iter.previous();
                    }
                    query.getTableTarget().add(SQLServerParser.getTableTargetChunk(iter, query.getAliases(), DELETE_DELIMITING_WORDS));
                    continue block8;
                }
                case 4: {
                    SQLServerParser.skipTop(iter);
                    t = iter.previous();
                    query.getTableTarget().add(SQLServerParser.getTableTargetChunk(iter, query.getAliases(), UPDATE_DELIMITING_WORDS));
                    continue block8;
                }
                case 5: {
                    query.getTableTarget().add(SQLServerParser.getTableTargetChunk(iter, query.getAliases(), FROM_DELIMITING_WORDS));
                    continue block8;
                }
                case 89: {
                    int parameterIndex = iter.nextIndex();
                    String columnName = SQLServerParser.findColumnAroundParameter(iter);
                    query.getColumns().add(columnName);
                    SQLServerParser.resetIteratorIndex(iter, parameterIndex);
                    continue block8;
                }
            }
        }
    }

    static void resetIteratorIndex(SQLServerTokenIterator iter, int index) {
        block3: {
            block2: {
                if (iter.nextIndex() >= index) break block2;
                while (iter.nextIndex() != index) {
                    iter.next();
                }
                break block3;
            }
            if (iter.nextIndex() <= index) break block3;
            while (iter.nextIndex() != index) {
                iter.previous();
            }
        }
    }

    private static String getRoundBracketChunk(SQLServerTokenIterator iter) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        Stack<String> s = new Stack<String>();
        s.push("(");
        while (!s.empty() && iter.hasNext()) {
            Token t = iter.next();
            if (t.getType() == 72) {
                sb.append(")");
                s.pop();
                continue;
            }
            if (t.getType() == 71) {
                sb.append("(");
                s.push("(");
                continue;
            }
            sb.append(t.getText()).append(" ");
        }
        return sb.toString();
    }

    private static String getRoundBracketChunkBefore(SQLServerTokenIterator iter) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        Stack<String> s = new Stack<String>();
        s.push(")");
        while (!s.empty()) {
            Token t = iter.previous();
            if (t.getType() == 72) {
                sb.append("(");
                s.push(")");
                continue;
            }
            if (t.getType() == 71) {
                sb.append(")");
                s.pop();
                continue;
            }
            sb.append(t.getText()).append(" ");
        }
        return sb.toString();
    }

    static String findColumnAroundParameter(SQLServerTokenIterator iter) {
        int index = iter.nextIndex();
        iter.previous();
        String value = SQLServerParser.findColumnBeforeParameter(iter);
        SQLServerParser.resetIteratorIndex(iter, index);
        if ("".equalsIgnoreCase(value)) {
            value = SQLServerParser.findColumnAfterParameter(iter);
            SQLServerParser.resetIteratorIndex(iter, index);
        }
        return value;
    }

    private static String findColumnAfterParameter(SQLServerTokenIterator iter) {
        StringBuilder sb = new StringBuilder();
        while (0 == sb.length() && iter.hasNext()) {
            Token t = iter.next();
            if (t.getType() == 33 && iter.hasNext()) {
                t = iter.next();
            }
            if (OPERATORS.contains(t.getType()) && iter.hasNext()) {
                t = iter.next();
                if (t.getType() == 89) continue;
                if (t.getType() == 71) {
                    sb.append(SQLServerParser.getRoundBracketChunk(iter));
                } else {
                    sb.append(t.getText());
                }
                for (int i = 0; i < 3 && iter.hasNext(); ++i) {
                    t = iter.next();
                    if (t.getType() != 66) continue;
                    sb.append(".");
                    if (!iter.hasNext()) continue;
                    t = iter.next();
                    sb.append(t.getText());
                }
                continue;
            }
            return "";
        }
        return sb.toString();
    }

    private static String findColumnBeforeParameter(SQLServerTokenIterator iter) {
        StringBuilder sb = new StringBuilder();
        while (0 == sb.length() && iter.hasPrevious()) {
            Token t = iter.previous();
            if (t.getType() == 70 && iter.hasPrevious()) {
                t = iter.previous();
            }
            if (t.getType() == 35 && iter.hasPrevious()) {
                t = iter.previous();
                if (iter.hasPrevious()) {
                    t = iter.previous();
                    if (t.getType() == 34 && iter.hasNext()) {
                        iter.next();
                        continue;
                    }
                    return "";
                }
            }
            if (OPERATORS.contains(t.getType()) && iter.hasPrevious()) {
                t = iter.previous();
                if (t.getType() == 33) {
                    t = iter.previous();
                }
                if (t.getType() == 89) continue;
                ArrayDeque<String> d = new ArrayDeque<String>();
                if (t.getType() == 72) {
                    d.push(SQLServerParser.getRoundBracketChunkBefore(iter));
                } else {
                    d.push(t.getText());
                }
                for (int i = 0; i < 3 && iter.hasPrevious(); ++i) {
                    t = iter.previous();
                    if (t.getType() != 66) continue;
                    d.push(".");
                    if (!iter.hasPrevious()) continue;
                    t = iter.previous();
                    d.push(t.getText());
                }
                d.stream().forEach(sb::append);
                continue;
            }
            return "";
        }
        return sb.toString();
    }

    static List<String> getValuesList(SQLServerTokenIterator iter) throws SQLServerException {
        Token t = iter.next();
        if (t.getType() == 71) {
            ArrayList<String> parameterColumns = new ArrayList<String>();
            ArrayDeque<Integer> d = new ArrayDeque<Integer>();
            StringBuilder sb = new StringBuilder();
            do {
                switch (t.getType()) {
                    case 71: {
                        if (!d.isEmpty()) {
                            sb.append('(');
                        }
                        d.push(71);
                        break;
                    }
                    case 72: {
                        if ((Integer)d.peek() == 71) {
                            d.pop();
                        }
                        if (!d.isEmpty()) {
                            sb.append(')');
                            break;
                        }
                        parameterColumns.add(sb.toString().trim());
                        break;
                    }
                    case 77: {
                        if (d.size() == 1) {
                            parameterColumns.add(sb.toString().trim());
                            sb = new StringBuilder();
                            break;
                        }
                        sb.append(',');
                        break;
                    }
                    default: {
                        sb.append(t.getText());
                    }
                }
                if (iter.hasNext() && !d.isEmpty()) {
                    t = iter.next();
                    continue;
                }
                if (iter.hasNext() || d.isEmpty()) continue;
                SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidValuesList"), null, false);
            } while (!d.isEmpty());
            return parameterColumns;
        }
        iter.previous();
        return new ArrayList<String>();
    }

    static Token skipTop(SQLServerTokenIterator iter) throws SQLServerException {
        Token t;
        if (!iter.hasNext()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidUserSQL"), null, false);
        }
        if ((t = iter.next()).getType() == 26) {
            t = iter.next();
            if (t.getType() == 71) {
                SQLServerParser.getRoundBracketChunk(iter);
            }
            if ((t = iter.next()).getType() == 28) {
                t = iter.next();
            }
            if (t.getType() == 17) {
                t = iter.next();
                t = t.getType() == 29 ? iter.next() : iter.previous();
            }
        }
        return t;
    }

    static String getCTE(SQLServerTokenIterator iter) throws SQLServerException {
        if (iter.hasNext()) {
            Token t = iter.next();
            if (t.getType() == 17) {
                StringBuilder sb = new StringBuilder("WITH ");
                SQLServerParser.getCTESegment(iter, sb);
                return sb.toString();
            }
            iter.previous();
        }
        return "";
    }

    static void getCTESegment(SQLServerTokenIterator iter, StringBuilder sb) throws SQLServerException {
        try {
            sb.append(SQLServerParser.getTableTargetChunk(iter, null, Arrays.asList(18)));
            iter.next();
            Token t = iter.next();
            sb.append(" AS ");
            if (t.getType() != 71) {
                SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
            }
            int leftRoundBracketCount = 0;
            do {
                sb.append(t.getText()).append(' ');
                if (t.getType() == 71) {
                    ++leftRoundBracketCount;
                } else if (t.getType() == 72) {
                    --leftRoundBracketCount;
                }
                t = iter.next();
            } while (leftRoundBracketCount > 0);
            if (t.getType() == 77) {
                sb.append(", ");
                SQLServerParser.getCTESegment(iter, sb);
            } else {
                iter.previous();
            }
        }
        catch (NoSuchElementException e) {
            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
        }
    }

    private static String getTableTargetChunk(SQLServerTokenIterator iter, List<String> possibleAliases, List<Integer> delimiters) throws SQLServerException {
        StringBuilder sb = new StringBuilder();
        if (iter.hasNext()) {
            Token t = iter.next();
            do {
                switch (t.getType()) {
                    case 71: {
                        sb.append(SQLServerParser.getRoundBracketChunk(iter));
                        break;
                    }
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: {
                        sb.append(t.getText());
                        t = iter.next();
                        if (t.getType() != 71) {
                            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidOpenqueryCall"), null, false);
                        }
                        sb.append(SQLServerParser.getRoundBracketChunk(iter));
                        break;
                    }
                    case 18: {
                        sb.append(t.getText());
                        if (!iter.hasNext()) break;
                        String s = iter.next().getText();
                        if (possibleAliases != null) {
                            possibleAliases.add(s);
                        } else {
                            SQLServerException.makeFromDriverError(null, null, SQLServerResource.getResource("R_invalidCTEFormat"), null, false);
                        }
                        sb.append(" ").append(s);
                        break;
                    }
                    default: {
                        sb.append(t.getText());
                    }
                }
                if (!iter.hasNext()) break;
                sb.append(' ');
            } while (!delimiters.contains((t = iter.next()).getType()) && t.getType() != 78);
            if (iter.hasNext()) {
                iter.previous();
            }
        }
        return sb.toString().trim();
    }
}

