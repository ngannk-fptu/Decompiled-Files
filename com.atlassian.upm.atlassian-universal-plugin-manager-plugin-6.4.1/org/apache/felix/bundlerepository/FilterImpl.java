/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.bundlerepository;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.felix.bundlerepository.StringSet;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

public class FilterImpl
implements Filter {
    private static final char WILDCARD = '\uffff';
    private static final int EQ = 0;
    private static final int LE = 1;
    private static final int GE = 2;
    private static final int APPROX = 3;
    private static final int LESS = 4;
    private static final int GREATER = 5;
    private static final int SUBSET = 6;
    private static final int SUPERSET = 7;
    private String m_filter;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Character;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$math$BigInteger;

    public FilterImpl(String filter) throws IllegalArgumentException {
        this.m_filter = filter;
        if (filter == null || filter.length() == 0) {
            throw new IllegalArgumentException("Null query");
        }
    }

    public String toString() {
        return this.m_filter;
    }

    public boolean equals(Object obj) {
        return obj != null && obj instanceof FilterImpl && this.m_filter.equals(((FilterImpl)obj).m_filter);
    }

    public int hashCode() {
        return this.m_filter.hashCode();
    }

    private static boolean compareString(String s1, int op, String s2) {
        switch (op) {
            case 0: {
                return FilterImpl.patSubstr(s1, s2);
            }
            case 3: {
                return FilterImpl.patSubstr(FilterImpl.fixupString(s1), FilterImpl.fixupString(s2));
            }
        }
        return FilterImpl.compareSign(op, s2.compareTo(s1));
    }

    private static boolean compareSign(int op, int cmp) {
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
        }
        return cmp == 0;
    }

    private static String fixupString(String s) {
        StringBuffer sb = new StringBuffer();
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

    private static boolean patSubstr(String s, String pat) {
        if (s == null) {
            return false;
        }
        if (pat.length() == 0) {
            return s.length() == 0;
        }
        if (pat.charAt(0) == '\uffff') {
            pat = pat.substring(1);
            while (true) {
                if (FilterImpl.patSubstr(s, pat)) {
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
        return FilterImpl.patSubstr(s.substring(1), pat.substring(1));
    }

    public boolean match(Dictionary dict) {
        try {
            return new DictQuery(dict).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean match(ServiceReference reference) {
        try {
            return new ServiceReferenceQuery(reference).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean matchCase(Dictionary dictionary) {
        try {
            return new DictQuery(dictionary, true).match();
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    class ServiceReferenceQuery
    extends Query {
        private ServiceReference m_ref;

        public ServiceReferenceQuery(ServiceReference ref) {
            this.m_ref = ref;
        }

        Object getProp(String key) {
            if (this.m_caseSensitive) {
                return this.m_ref.getProperty(key);
            }
            String[] propertyKeys = this.m_ref.getPropertyKeys();
            for (int i = 0; i < propertyKeys.length; ++i) {
                String propertyKey = propertyKeys[i];
                if (!propertyKey.equalsIgnoreCase(key)) continue;
                return this.m_ref.getProperty(propertyKey);
            }
            return null;
        }
    }

    class DictQuery
    extends Query {
        private Dictionary m_dict;

        DictQuery(Dictionary dict) {
            this.m_dict = dict;
        }

        DictQuery(Dictionary dict, boolean caseSensitive) {
            this.m_dict = dict;
            this.m_caseSensitive = caseSensitive;
        }

        Object getProp(String key) {
            if (this.m_caseSensitive) {
                return this.m_dict.get(key);
            }
            Enumeration keys = this.m_dict.keys();
            while (keys.hasMoreElements()) {
                String propertyKey = (String)keys.nextElement();
                if (!propertyKey.equalsIgnoreCase(key)) continue;
                return this.m_dict.get(propertyKey);
            }
            return null;
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
        private String m_tail;
        protected boolean m_caseSensitive = false;

        Query() {
        }

        boolean match() throws IllegalArgumentException {
            this.m_tail = FilterImpl.this.m_filter;
            boolean val = this.doQuery();
            if (this.m_tail.length() > 0) {
                this.error(GARBAGE);
            }
            return val;
        }

        private boolean doQuery() throws IllegalArgumentException {
            boolean val;
            if (this.m_tail.length() < 3 || !this.prefix("(")) {
                this.error(MALFORMED);
            }
            switch (this.m_tail.charAt(0)) {
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

        private boolean doAnd() throws IllegalArgumentException {
            this.m_tail = this.m_tail.substring(1);
            boolean val = true;
            if (!this.m_tail.startsWith("(")) {
                this.error(EMPTY);
            }
            do {
                if (this.doQuery()) continue;
                val = false;
            } while (this.m_tail.startsWith("("));
            return val;
        }

        private boolean doOr() throws IllegalArgumentException {
            this.m_tail = this.m_tail.substring(1);
            boolean val = false;
            if (!this.m_tail.startsWith("(")) {
                this.error(EMPTY);
            }
            do {
                if (!this.doQuery()) continue;
                val = true;
            } while (this.m_tail.startsWith("("));
            return val;
        }

        private boolean doNot() throws IllegalArgumentException {
            this.m_tail = this.m_tail.substring(1);
            if (!this.m_tail.startsWith("(")) {
                this.error(SUBEXPR);
            }
            return !this.doQuery();
        }

        private boolean doSimple() throws IllegalArgumentException {
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
            } else if (this.prefix(":*>")) {
                op = 7;
            } else if (this.prefix(":<*")) {
                op = 6;
            } else if (this.prefix("<")) {
                op = 4;
            } else if (this.prefix(">")) {
                op = 5;
            } else {
                this.error(OPERATOR);
            }
            return this.compare(attr, op, this.getValue());
        }

        private boolean prefix(String pre) {
            if (!this.m_tail.startsWith(pre)) {
                return false;
            }
            this.m_tail = this.m_tail.substring(pre.length());
            return true;
        }

        private Object getAttr() {
            int ix;
            int len = this.m_tail.length();
            block3: for (ix = 0; ix < len; ++ix) {
                switch (this.m_tail.charAt(ix)) {
                    case '(': 
                    case ')': 
                    case '*': 
                    case ':': 
                    case '<': 
                    case '=': 
                    case '>': 
                    case '\\': 
                    case '{': 
                    case '}': 
                    case '~': {
                        break block3;
                    }
                    default: {
                        continue block3;
                    }
                }
            }
            String attr = this.m_tail.substring(0, ix).toLowerCase();
            this.m_tail = this.m_tail.substring(ix);
            return this.getProp(attr);
        }

        abstract Object getProp(String var1);

        private String getValue() {
            int ix;
            StringBuffer sb = new StringBuffer();
            int len = this.m_tail.length();
            block5: for (ix = 0; ix < len; ++ix) {
                char c = this.m_tail.charAt(ix);
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
                        sb.append(this.m_tail.charAt(++ix));
                        continue block5;
                    }
                    default: {
                        sb.append(c);
                    }
                }
            }
            this.m_tail = this.m_tail.substring(ix);
            return sb.toString();
        }

        private void error(String m) throws IllegalArgumentException {
            throw new IllegalArgumentException(m + " " + this.m_tail);
        }

        private boolean compare(Object obj, int op, String s) {
            block33: {
                if (obj == null && op != 6 && op != 7) {
                    return false;
                }
                try {
                    Class<?> numClass = null;
                    if (obj != null) {
                        numClass = obj.getClass();
                    }
                    if (numClass == (class$java$lang$String == null ? (class$java$lang$String = FilterImpl.class$("java.lang.String")) : class$java$lang$String) && op != 6 && op != 7) {
                        return FilterImpl.compareString((String)obj, op, s);
                    }
                    if (numClass == (class$java$lang$Character == null ? (class$java$lang$Character = FilterImpl.class$("java.lang.Character")) : class$java$lang$Character)) {
                        return FilterImpl.compareString(obj.toString(), op, s);
                    }
                    if (numClass == (class$java$lang$Long == null ? (class$java$lang$Long = FilterImpl.class$("java.lang.Long")) : class$java$lang$Long)) {
                        return FilterImpl.compareSign(op, Long.valueOf(s).compareTo((Long)obj));
                    }
                    if (numClass == (class$java$lang$Integer == null ? (class$java$lang$Integer = FilterImpl.class$("java.lang.Integer")) : class$java$lang$Integer)) {
                        return FilterImpl.compareSign(op, Integer.valueOf(s).compareTo((Integer)obj));
                    }
                    if (numClass == (class$java$lang$Short == null ? (class$java$lang$Short = FilterImpl.class$("java.lang.Short")) : class$java$lang$Short)) {
                        return FilterImpl.compareSign(op, Short.valueOf(s).compareTo((Short)obj));
                    }
                    if (numClass == (class$java$lang$Byte == null ? (class$java$lang$Byte = FilterImpl.class$("java.lang.Byte")) : class$java$lang$Byte)) {
                        return FilterImpl.compareSign(op, Byte.valueOf(s).compareTo((Byte)obj));
                    }
                    if (numClass == (class$java$lang$Double == null ? (class$java$lang$Double = FilterImpl.class$("java.lang.Double")) : class$java$lang$Double)) {
                        return FilterImpl.compareSign(op, Double.valueOf(s).compareTo((Double)obj));
                    }
                    if (numClass == (class$java$lang$Float == null ? (class$java$lang$Float = FilterImpl.class$("java.lang.Float")) : class$java$lang$Float)) {
                        return FilterImpl.compareSign(op, Float.valueOf(s).compareTo((Float)obj));
                    }
                    if (numClass == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = FilterImpl.class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
                        if (op != 0) {
                            return false;
                        }
                        int a = Boolean.valueOf(s) != false ? 1 : 0;
                        int b = (Boolean)obj != false ? 1 : 0;
                        return FilterImpl.compareSign(op, a - b);
                    }
                    if (numClass == (class$java$math$BigInteger == null ? (class$java$math$BigInteger = FilterImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger)) {
                        return FilterImpl.compareSign(op, new BigInteger(s).compareTo((BigInteger)obj));
                    }
                    if (obj instanceof Collection) {
                        if (op == 6 || op == 7) {
                            StringSet set = new StringSet(s);
                            if (op == 6) {
                                return set.containsAll((Collection)obj);
                            }
                            return ((Collection)obj).containsAll(set);
                        }
                        Iterator i = ((Collection)obj).iterator();
                        while (i.hasNext()) {
                            Object element = i.next();
                            if (!this.compare(element, op, s)) continue;
                            return true;
                        }
                        break block33;
                    }
                    if (numClass.isArray()) {
                        int len = Array.getLength(obj);
                        for (int i = 0; i < len; ++i) {
                            if (!this.compare(Array.get(obj, i), op, s)) continue;
                            return true;
                        }
                        break block33;
                    }
                    try {
                        if (op == 7 || op == 6) {
                            StringSet set = new StringSet(s);
                            StringSet objSet = new StringSet((String)obj);
                            if (op == 7) {
                                boolean found = true;
                                Iterator iterator = set.iterator();
                                while (iterator.hasNext() && found) {
                                    Object object = iterator.next();
                                    if (objSet.contains(object)) continue;
                                    found = false;
                                }
                                return found;
                            }
                            return set.containsAll(objSet);
                        }
                        Constructor<?> constructor = numClass.getConstructor(class$java$lang$String == null ? (class$java$lang$String = FilterImpl.class$("java.lang.String")) : class$java$lang$String);
                        Object instance = constructor.newInstance(s);
                        switch (op) {
                            case 0: {
                                return obj.equals(instance);
                            }
                            case 4: {
                                return ((Comparable)obj).compareTo(instance) < 0;
                            }
                            case 5: {
                                return ((Comparable)obj).compareTo(instance) > 0;
                            }
                            case 1: {
                                return ((Comparable)obj).compareTo(instance) <= 0;
                            }
                            case 2: {
                                return ((Comparable)obj).compareTo(instance) >= 0;
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            return false;
        }
    }
}

