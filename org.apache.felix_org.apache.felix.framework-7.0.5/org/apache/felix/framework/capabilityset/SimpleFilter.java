/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.capabilityset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.osgi.framework.VersionRange;

public class SimpleFilter {
    public static final int MATCH_ALL = 0;
    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;
    public static final int EQ = 4;
    public static final int LTE = 5;
    public static final int GTE = 6;
    public static final int SUBSTRING = 7;
    public static final int PRESENT = 8;
    public static final int APPROX = 9;
    private final String m_name;
    private final Object m_value;
    private final int m_op;

    public SimpleFilter(String attr, Object value, int op) {
        this.m_name = attr;
        this.m_value = value;
        this.m_op = op;
    }

    public boolean equals(Object o) {
        if (o instanceof SimpleFilter) {
            SimpleFilter other = (SimpleFilter)o;
            return this.m_op == other.m_op && Objects.equals(this.m_name, other.m_name) && Objects.equals(this.m_value, other.m_value);
        }
        return false;
    }

    public int hashCode() {
        return this.m_op + Objects.hashCode(this.m_name) + Objects.hashCode(this.m_value);
    }

    public String getName() {
        return this.m_name;
    }

    public Object getValue() {
        return this.m_value;
    }

    public int getOperation() {
        return this.m_op;
    }

    public String toString() {
        String s = null;
        switch (this.m_op) {
            case 1: {
                s = "(&" + SimpleFilter.toString((List)this.m_value) + ")";
                break;
            }
            case 2: {
                s = "(|" + SimpleFilter.toString((List)this.m_value) + ")";
                break;
            }
            case 3: {
                s = "(!" + SimpleFilter.toString((List)this.m_value) + ")";
                break;
            }
            case 4: {
                s = "(" + this.m_name + "=" + SimpleFilter.toEncodedString(this.m_value) + ")";
                break;
            }
            case 5: {
                s = "(" + this.m_name + "<=" + SimpleFilter.toEncodedString(this.m_value) + ")";
                break;
            }
            case 6: {
                s = "(" + this.m_name + ">=" + SimpleFilter.toEncodedString(this.m_value) + ")";
                break;
            }
            case 7: {
                s = "(" + this.m_name + "=" + SimpleFilter.unparseSubstring((List)this.m_value) + ")";
                break;
            }
            case 8: {
                s = "(" + this.m_name + "=*)";
                break;
            }
            case 9: {
                s = "(" + this.m_name + "~=" + SimpleFilter.toEncodedString(this.m_value) + ")";
                break;
            }
            case 0: {
                s = "(*)";
            }
        }
        return s;
    }

    private static String toString(List list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            sb.append(list.get(i).toString());
        }
        return sb.toString();
    }

    private static String toDecodedString(String s, int startIdx, int endIdx) {
        StringBuilder sb = new StringBuilder(endIdx - startIdx);
        boolean escaped = false;
        for (int i = 0; i < endIdx - startIdx; ++i) {
            char c = s.charAt(startIdx + i);
            if (!escaped && c == '\\') {
                escaped = true;
                continue;
            }
            escaped = false;
            sb.append(c);
        }
        return sb.toString();
    }

    private static String toEncodedString(Object o) {
        if (o instanceof String) {
            String s = (String)o;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (c == '\\' || c == '(' || c == ')' || c == '*') {
                    sb.append('\\');
                }
                sb.append(c);
            }
            o = sb.toString();
        }
        return o.toString();
    }

    public static SimpleFilter parse(String filter) {
        int idx = SimpleFilter.skipWhitespace(filter, 0);
        if (filter == null || filter.length() == 0 || idx >= filter.length()) {
            throw new IllegalArgumentException("Null or empty filter.");
        }
        if (filter.charAt(idx) != '(') {
            throw new IllegalArgumentException("Missing opening parenthesis: " + filter);
        }
        SimpleFilter sf = null;
        ArrayList<Object> stack = new ArrayList<Object>();
        boolean isEscaped = false;
        while (idx < filter.length()) {
            if (sf != null) {
                throw new IllegalArgumentException("Only one top-level operation allowed: " + filter);
            }
            if (!isEscaped && filter.charAt(idx) == '(') {
                if (filter.charAt(idx = SimpleFilter.skipWhitespace(filter, idx + 1)) == '&') {
                    int peek = SimpleFilter.skipWhitespace(filter, idx + 1);
                    if (filter.charAt(peek) == '(') {
                        idx = peek - 1;
                        stack.add(0, new SimpleFilter(null, new ArrayList(), 1));
                    } else {
                        stack.add(0, new Integer(idx));
                    }
                } else if (filter.charAt(idx) == '|') {
                    int peek = SimpleFilter.skipWhitespace(filter, idx + 1);
                    if (filter.charAt(peek) == '(') {
                        idx = peek - 1;
                        stack.add(0, new SimpleFilter(null, new ArrayList(), 2));
                    } else {
                        stack.add(0, new Integer(idx));
                    }
                } else if (filter.charAt(idx) == '!') {
                    int peek = SimpleFilter.skipWhitespace(filter, idx + 1);
                    if (filter.charAt(peek) == '(') {
                        idx = peek - 1;
                        stack.add(0, new SimpleFilter(null, new ArrayList(), 3));
                    } else {
                        stack.add(0, new Integer(idx));
                    }
                } else {
                    stack.add(0, new Integer(idx));
                }
            } else if (!isEscaped && filter.charAt(idx) == ')') {
                Object top = stack.remove(0);
                if (top instanceof SimpleFilter) {
                    if (!stack.isEmpty() && stack.get(0) instanceof SimpleFilter) {
                        ((List)((SimpleFilter)stack.get((int)0)).m_value).add(top);
                    } else {
                        sf = (SimpleFilter)top;
                    }
                } else if (!stack.isEmpty() && stack.get(0) instanceof SimpleFilter) {
                    ((List)((SimpleFilter)stack.get((int)0)).m_value).add(SimpleFilter.subfilter(filter, (Integer)top, idx));
                } else {
                    sf = SimpleFilter.subfilter(filter, (Integer)top, idx);
                }
            } else {
                isEscaped = !isEscaped && filter.charAt(idx) == '\\';
            }
            idx = SimpleFilter.skipWhitespace(filter, idx + 1);
        }
        if (sf == null) {
            throw new IllegalArgumentException("Missing closing parenthesis: " + filter);
        }
        return sf;
    }

    private static SimpleFilter subfilter(String filter, int startIdx, int endIdx) {
        char c;
        String opChars = "=<>~";
        int attrEndIdx = startIdx;
        for (int i = 0; i < endIdx - startIdx && "=<>~".indexOf(c = filter.charAt(startIdx + i)) < 0; ++i) {
            if (Character.isWhitespace(c)) continue;
            attrEndIdx = startIdx + i + 1;
        }
        if (attrEndIdx == startIdx) {
            throw new IllegalArgumentException("Missing attribute name: " + filter.substring(startIdx, endIdx));
        }
        String attr = filter.substring(startIdx, attrEndIdx);
        startIdx = SimpleFilter.skipWhitespace(filter, attrEndIdx);
        int op = -1;
        switch (filter.charAt(startIdx)) {
            case '=': {
                op = 4;
                ++startIdx;
                break;
            }
            case '<': {
                if (filter.charAt(startIdx + 1) != '=') {
                    throw new IllegalArgumentException("Unknown operator: " + filter.substring(startIdx, endIdx));
                }
                op = 5;
                startIdx += 2;
                break;
            }
            case '>': {
                if (filter.charAt(startIdx + 1) != '=') {
                    throw new IllegalArgumentException("Unknown operator: " + filter.substring(startIdx, endIdx));
                }
                op = 6;
                startIdx += 2;
                break;
            }
            case '~': {
                if (filter.charAt(startIdx + 1) != '=') {
                    throw new IllegalArgumentException("Unknown operator: " + filter.substring(startIdx, endIdx));
                }
                op = 9;
                startIdx += 2;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown operator: " + filter.substring(startIdx, endIdx));
            }
        }
        Object value = SimpleFilter.toDecodedString(filter, startIdx, endIdx);
        if (op == 4) {
            String valueStr = filter.substring(startIdx, endIdx);
            List<String> values = SimpleFilter.parseSubstring(valueStr);
            if (values.size() == 2 && values.get(0).length() == 0 && values.get(1).length() == 0) {
                op = 8;
            } else if (values.size() > 1) {
                op = 7;
                value = values;
            }
        }
        return new SimpleFilter(attr, value, op);
    }

    public static List<String> parseSubstring(String value) {
        ArrayList<String> pieces = new ArrayList<String>();
        StringBuilder ss = new StringBuilder();
        boolean wasStar = false;
        boolean leftstar = false;
        boolean rightstar = false;
        int idx = 0;
        boolean escaped = false;
        while (true) {
            if (idx >= value.length()) {
                if (wasStar) {
                    rightstar = true;
                    break;
                }
                pieces.add(ss.toString());
                break;
            }
            char c = value.charAt(idx++);
            if (!escaped && c == '*') {
                if (wasStar) continue;
                if (ss.length() > 0) {
                    pieces.add(ss.toString());
                }
                ss.setLength(0);
                if (pieces.isEmpty()) {
                    leftstar = true;
                }
                wasStar = true;
                continue;
            }
            if (!escaped && c == '\\') {
                escaped = true;
                continue;
            }
            escaped = false;
            wasStar = false;
            ss.append(c);
        }
        ss.setLength(0);
        if (leftstar || rightstar || pieces.size() > 1) {
            if (rightstar) {
                pieces.add("");
            }
            if (leftstar) {
                pieces.add(0, "");
            }
        }
        return pieces;
    }

    public static String unparseSubstring(List<String> pieces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pieces.size(); ++i) {
            if (i > 0) {
                sb.append("*");
            }
            sb.append(SimpleFilter.toEncodedString(pieces.get(i)));
        }
        return sb.toString();
    }

    public static boolean compareSubstring(List<String> pieces, String s) {
        boolean result = true;
        int len = pieces.size();
        if (len == 1) {
            return s.equals(pieces.get(0));
        }
        int index = 0;
        for (int i = 0; i < len; ++i) {
            String piece = pieces.get(i);
            if (i == 0 && !s.startsWith(piece)) {
                result = false;
                break;
            }
            if (i == len - 1) {
                if (s.endsWith(piece) && s.length() >= index + piece.length()) {
                    result = true;
                    break;
                }
                result = false;
                break;
            }
            if (i > 0 && i < len - 1 && (index = s.indexOf(piece, index)) < 0) {
                result = false;
                break;
            }
            index += piece.length();
        }
        return result;
    }

    private static int skipWhitespace(String s, int startIdx) {
        int len = s.length();
        while (startIdx < len && Character.isWhitespace(s.charAt(startIdx))) {
            ++startIdx;
        }
        return startIdx;
    }

    public static SimpleFilter convert(Map<String, Object> attrs) {
        ArrayList<SimpleFilter> filters = new ArrayList<SimpleFilter>();
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            if (entry.getValue() instanceof VersionRange) {
                SimpleFilter not;
                VersionRange vr = (VersionRange)entry.getValue();
                if (vr.getLeftType() == '[') {
                    filters.add(new SimpleFilter(entry.getKey(), vr.getLeft().toString(), 6));
                } else {
                    not = new SimpleFilter(null, new ArrayList(), 3);
                    ((List)not.getValue()).add(new SimpleFilter(entry.getKey(), vr.getLeft().toString(), 5));
                    filters.add(not);
                }
                if (vr.getRight() == null) continue;
                if (vr.getRightType() == ']') {
                    filters.add(new SimpleFilter(entry.getKey(), vr.getRight().toString(), 5));
                    continue;
                }
                not = new SimpleFilter(null, new ArrayList(), 3);
                ((List)not.getValue()).add(new SimpleFilter(entry.getKey(), vr.getRight().toString(), 6));
                filters.add(not);
                continue;
            }
            List<String> values = SimpleFilter.parseSubstring(entry.getValue().toString());
            if (values.size() > 1) {
                filters.add(new SimpleFilter(entry.getKey(), values, 7));
                continue;
            }
            filters.add(new SimpleFilter(entry.getKey(), values.get(0), 4));
        }
        SimpleFilter sf = null;
        if (filters.size() == 1) {
            sf = (SimpleFilter)filters.get(0);
        } else if (attrs.size() > 1) {
            sf = new SimpleFilter(null, filters, 1);
        } else if (filters.isEmpty()) {
            sf = new SimpleFilter(null, null, 0);
        }
        return sf;
    }
}

