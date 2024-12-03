/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.filter;

import aQute.lib.filter.Get;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class Filter {
    final char WILDCARD = (char)65535;
    static final int EQ = 0;
    static final int LE = 1;
    static final int GE = 2;
    static final int NEQ = 100;
    static final int LT = 101;
    static final int GT = 102;
    static final int APPROX = 3;
    final String filter;
    final boolean extended;

    public Filter(String filter, boolean extended) throws IllegalArgumentException {
        this.filter = filter;
        this.extended = extended;
        if (filter == null || filter.length() == 0) {
            throw new IllegalArgumentException("Null query");
        }
    }

    public Filter(String filter) throws IllegalArgumentException {
        this(filter, false);
    }

    public boolean match(Dictionary<?, ?> dict) throws Exception {
        try {
            return new DictQuery(dict).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean matchMap(Map<?, ?> dict) throws Exception {
        try {
            return new MapQuery(dict).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean match(Get get) throws Exception {
        try {
            return new GetQuery(get).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String verify() throws Exception {
        try {
            new DictQuery(new Hashtable()).match();
        }
        catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }

    public String toString() {
        return this.filter;
    }

    public boolean equals(Object obj) {
        return obj instanceof Filter && this.filter.equals(((Filter)obj).filter);
    }

    public int hashCode() {
        return this.filter.hashCode();
    }

    boolean compareString(String s1, int op, String s2) {
        switch (op) {
            case 0: {
                return this.patSubstr(s1, s2);
            }
            case 3: {
                return this.fixupString(s2).equals(this.fixupString(s1));
            }
        }
        return this.compareSign(op, s2.compareTo(s1));
    }

    boolean compareSign(int op, int cmp) {
        switch (op) {
            case 1: {
                return cmp >= 0;
            }
            case 2: {
                return cmp <= 0;
            }
            case 0: {
                return cmp == 0;
            }
            case 100: {
                return cmp != 0;
            }
            case 101: {
                return cmp > 0;
            }
            case 102: {
                return cmp < 0;
            }
        }
        return cmp == 0;
    }

    String fixupString(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        boolean isStart = true;
        boolean isWhite = false;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                isWhite = true;
                continue;
            }
            if (!isStart && isWhite) {
                sb.append(' ');
            }
            if (Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
            }
            sb.append(c);
            isStart = false;
            isWhite = false;
        }
        return sb.toString();
    }

    boolean patSubstr(String s, String pat) {
        if (s == null) {
            return false;
        }
        if (pat.length() == 0) {
            return s.length() == 0;
        }
        if (pat.charAt(0) == '\uffff') {
            pat = pat.substring(1);
            while (true) {
                if (this.patSubstr(s, pat)) {
                    return true;
                }
                if (s.length() == 0) {
                    return false;
                }
                s = s.substring(1);
            }
        }
        if (s.length() == 0 || s.charAt(0) != pat.charAt(0)) {
            return false;
        }
        return this.patSubstr(s.substring(1), pat.substring(1));
    }

    class GetQuery
    extends Query {
        private Get get;

        GetQuery(Get get) {
            this.get = get;
        }

        @Override
        Object getProp(String key) throws Exception {
            return this.get.get(key);
        }
    }

    class MapQuery
    extends Query {
        private Map<?, ?> map;

        MapQuery(Map<?, ?> dict) {
            this.map = dict;
        }

        @Override
        Object getProp(String key) {
            return this.map.get(key);
        }
    }

    class DictQuery
    extends Query {
        private Dictionary<?, ?> dict;

        DictQuery(Dictionary<?, ?> dict) {
            this.dict = dict;
        }

        @Override
        Object getProp(String key) {
            return this.dict.get(key);
        }
    }

    abstract class Query {
        static final String GARBAGE = "Trailing garbage";
        static final String MALFORMED = "Malformed query";
        static final String EMPTY = "Empty list";
        static final String SUBEXPR = "No subexpression";
        static final String OPERATOR = "Undefined operator";
        static final String TRUNCATED = "Truncated expression";
        static final String EQUALITY = "Only equality supported";
        private String tail;

        Query() {
        }

        boolean match() throws Exception {
            this.tail = Filter.this.filter;
            boolean val = this.doQuery();
            if (this.tail.length() > 0) {
                this.error(GARBAGE);
            }
            return val;
        }

        private boolean doQuery() throws Exception {
            boolean val;
            if (this.tail.length() < 3 || !this.prefix("(")) {
                this.error(MALFORMED);
            }
            switch (this.tail.charAt(0)) {
                case '&': {
                    val = this.doAnd();
                    break;
                }
                case '|': {
                    val = this.doOr();
                    break;
                }
                case '!': {
                    val = this.doNot();
                    break;
                }
                default: {
                    val = this.doSimple();
                }
            }
            if (!this.prefix(")")) {
                this.error(MALFORMED);
            }
            return val;
        }

        private boolean doAnd() throws Exception {
            this.tail = this.skip();
            boolean val = true;
            if (!this.tail.startsWith("(")) {
                this.error(EMPTY);
            }
            do {
                if (this.doQuery()) continue;
                val = false;
            } while (this.tail.startsWith("("));
            return val;
        }

        String skip() {
            String a = this.tail;
            while ((a = a.substring(1)).length() > 0 && Character.isWhitespace(a.charAt(0))) {
            }
            return a;
        }

        private boolean doOr() throws Exception {
            this.tail = this.skip();
            boolean val = false;
            if (!this.tail.startsWith("(")) {
                this.error(EMPTY);
            }
            do {
                if (!this.doQuery()) continue;
                val = true;
            } while (this.tail.startsWith("("));
            return val;
        }

        private boolean doNot() throws Exception {
            this.tail = this.skip();
            if (!this.tail.startsWith("(")) {
                this.error(SUBEXPR);
            }
            return !this.doQuery();
        }

        boolean doSimple() throws Exception {
            int op = 0;
            Object attr = this.getAttr();
            if (this.prefix("=")) {
                op = 0;
            } else if (this.prefix("<=")) {
                op = 1;
            } else if (this.prefix(">=")) {
                op = 2;
            } else if (this.prefix("~=")) {
                op = 3;
            } else if (Filter.this.extended && this.prefix("!=")) {
                op = 100;
            } else if (Filter.this.extended && this.prefix(">")) {
                op = 102;
            } else if (Filter.this.extended && this.prefix("<")) {
                op = 101;
            } else {
                this.error(OPERATOR);
            }
            return this.compare(attr, op, this.getValue());
        }

        boolean prefix(String pre) {
            if (!this.tail.startsWith(pre)) {
                return false;
            }
            this.tail = this.tail.substring(pre.length());
            return true;
        }

        Object getAttr() throws Exception {
            int ix;
            int len = this.tail.length();
            block3: for (ix = 0; ix < len; ++ix) {
                switch (this.tail.charAt(ix)) {
                    case '(': 
                    case ')': 
                    case '*': 
                    case '<': 
                    case '=': 
                    case '>': 
                    case '\\': 
                    case '~': {
                        break block3;
                    }
                    default: {
                        continue block3;
                    }
                }
            }
            String attr = this.tail.substring(0, ix);
            this.tail = this.tail.substring(ix);
            return this.getProp(attr);
        }

        abstract Object getProp(String var1) throws Exception;

        private String getValue() {
            int ix;
            StringBuilder sb = new StringBuilder();
            int len = this.tail.length();
            block5: for (ix = 0; ix < len; ++ix) {
                char c = this.tail.charAt(ix);
                switch (c) {
                    case '(': 
                    case ')': {
                        break block5;
                    }
                    case '*': {
                        sb.append('\uffff');
                        continue block5;
                    }
                    case '\\': {
                        if (ix == len - 1) break block5;
                        sb.append(this.tail.charAt(++ix));
                        continue block5;
                    }
                    default: {
                        sb.append(c);
                    }
                }
            }
            this.tail = this.tail.substring(ix);
            return sb.toString();
        }

        void error(String m) throws IllegalArgumentException {
            throw new IllegalArgumentException(m + " " + this.tail);
        }

        <T> boolean compare(T obj, int op, String s) {
            block21: {
                if (obj == null) {
                    return false;
                }
                if (op == 0 && s.length() == 1 && s.charAt(0) == '\uffff') {
                    return true;
                }
                try {
                    Class<?> numClass = obj.getClass();
                    if (numClass == String.class) {
                        return Filter.this.compareString((String)obj, op, s);
                    }
                    if (numClass == Character.class) {
                        return Filter.this.compareString(obj.toString(), op, s);
                    }
                    if (numClass == Long.class) {
                        return Filter.this.compareSign(op, Long.valueOf(s).compareTo((Long)obj));
                    }
                    if (numClass == Integer.class) {
                        return Filter.this.compareSign(op, Integer.valueOf(s).compareTo((Integer)obj));
                    }
                    if (numClass == Short.class) {
                        return Filter.this.compareSign(op, Short.valueOf(s).compareTo((Short)obj));
                    }
                    if (numClass == Byte.class) {
                        return Filter.this.compareSign(op, Byte.valueOf(s).compareTo((Byte)obj));
                    }
                    if (numClass == Double.class) {
                        return Filter.this.compareSign(op, Double.valueOf(s).compareTo((Double)obj));
                    }
                    if (numClass == Float.class) {
                        return Filter.this.compareSign(op, Float.valueOf(s).compareTo((Float)obj));
                    }
                    if (numClass == Boolean.class) {
                        if (op != 0) {
                            return false;
                        }
                        int a = Boolean.valueOf(s) != false ? 1 : 0;
                        int b = (Boolean)obj != false ? 1 : 0;
                        return Filter.this.compareSign(op, a - b);
                    }
                    if (numClass == BigInteger.class) {
                        return Filter.this.compareSign(op, new BigInteger(s).compareTo((BigInteger)obj));
                    }
                    if (numClass == BigDecimal.class) {
                        return Filter.this.compareSign(op, new BigDecimal(s).compareTo((BigDecimal)obj));
                    }
                    if (obj instanceof Collection) {
                        for (Object x : (Collection)obj) {
                            if (!this.compare(x, op, s)) continue;
                            return true;
                        }
                        break block21;
                    }
                    if (numClass.isArray()) {
                        int len = Array.getLength(obj);
                        for (int i = 0; i < len; ++i) {
                            if (!this.compare(Array.get(obj, i), op, s)) continue;
                            return true;
                        }
                        break block21;
                    }
                    Constructor<?> constructor = numClass.getConstructor(String.class);
                    Object source = constructor.newInstance(s);
                    if (op == 0) {
                        return source.equals(obj);
                    }
                    Comparable a = (Comparable)Comparable.class.cast(source);
                    Comparable b = (Comparable)Comparable.class.cast(obj);
                    return Filter.this.compareSign(op, a.compareTo(b));
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            return false;
        }
    }
}

