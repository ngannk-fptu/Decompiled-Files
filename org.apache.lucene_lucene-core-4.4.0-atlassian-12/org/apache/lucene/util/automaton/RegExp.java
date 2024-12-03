/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.AutomatonProvider;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.MinimizationOperations;

public class RegExp {
    public static final int INTERSECTION = 1;
    public static final int COMPLEMENT = 2;
    public static final int EMPTY = 4;
    public static final int ANYSTRING = 8;
    public static final int AUTOMATON = 16;
    public static final int INTERVAL = 32;
    public static final int ALL = 65535;
    public static final int NONE = 0;
    private static boolean allow_mutation = false;
    Kind kind;
    RegExp exp1;
    RegExp exp2;
    String s;
    int c;
    int min;
    int max;
    int digits;
    int from;
    int to;
    String b;
    int flags;
    int pos;

    RegExp() {
    }

    public RegExp(String s) throws IllegalArgumentException {
        this(s, 65535);
    }

    public RegExp(String s, int syntax_flags) throws IllegalArgumentException {
        RegExp e;
        this.b = s;
        this.flags = syntax_flags;
        if (s.length() == 0) {
            e = RegExp.makeString("");
        } else {
            e = this.parseUnionExp();
            if (this.pos < this.b.length()) {
                throw new IllegalArgumentException("end-of-string expected at position " + this.pos);
            }
        }
        this.kind = e.kind;
        this.exp1 = e.exp1;
        this.exp2 = e.exp2;
        this.s = e.s;
        this.c = e.c;
        this.min = e.min;
        this.max = e.max;
        this.digits = e.digits;
        this.from = e.from;
        this.to = e.to;
        this.b = null;
    }

    public Automaton toAutomaton() {
        return this.toAutomatonAllowMutate(null, null);
    }

    public Automaton toAutomaton(AutomatonProvider automaton_provider) throws IllegalArgumentException {
        return this.toAutomatonAllowMutate(null, automaton_provider);
    }

    public Automaton toAutomaton(Map<String, Automaton> automata) throws IllegalArgumentException {
        return this.toAutomatonAllowMutate(automata, null);
    }

    public boolean setAllowMutate(boolean flag) {
        boolean b = allow_mutation;
        allow_mutation = flag;
        return b;
    }

    private Automaton toAutomatonAllowMutate(Map<String, Automaton> automata, AutomatonProvider automaton_provider) throws IllegalArgumentException {
        boolean b = false;
        if (allow_mutation) {
            b = Automaton.setAllowMutate(true);
        }
        Automaton a = this.toAutomaton(automata, automaton_provider);
        if (allow_mutation) {
            Automaton.setAllowMutate(b);
        }
        return a;
    }

    private Automaton toAutomaton(Map<String, Automaton> automata, AutomatonProvider automaton_provider) throws IllegalArgumentException {
        Automaton a = null;
        switch (this.kind) {
            case REGEXP_UNION: {
                ArrayList<Automaton> list = new ArrayList<Automaton>();
                this.findLeaves(this.exp1, Kind.REGEXP_UNION, list, automata, automaton_provider);
                this.findLeaves(this.exp2, Kind.REGEXP_UNION, list, automata, automaton_provider);
                a = BasicOperations.union(list);
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_CONCATENATION: {
                ArrayList<Automaton> list = new ArrayList<Automaton>();
                this.findLeaves(this.exp1, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider);
                this.findLeaves(this.exp2, Kind.REGEXP_CONCATENATION, list, automata, automaton_provider);
                a = BasicOperations.concatenate(list);
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_INTERSECTION: {
                a = this.exp1.toAutomaton(automata, automaton_provider).intersection(this.exp2.toAutomaton(automata, automaton_provider));
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_OPTIONAL: {
                a = this.exp1.toAutomaton(automata, automaton_provider).optional();
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_REPEAT: {
                a = this.exp1.toAutomaton(automata, automaton_provider).repeat();
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_REPEAT_MIN: {
                a = this.exp1.toAutomaton(automata, automaton_provider).repeat(this.min);
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_REPEAT_MINMAX: {
                a = this.exp1.toAutomaton(automata, automaton_provider).repeat(this.min, this.max);
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_COMPLEMENT: {
                a = this.exp1.toAutomaton(automata, automaton_provider).complement();
                MinimizationOperations.minimize(a);
                break;
            }
            case REGEXP_CHAR: {
                a = BasicAutomata.makeChar(this.c);
                break;
            }
            case REGEXP_CHAR_RANGE: {
                a = BasicAutomata.makeCharRange(this.from, this.to);
                break;
            }
            case REGEXP_ANYCHAR: {
                a = BasicAutomata.makeAnyChar();
                break;
            }
            case REGEXP_EMPTY: {
                a = BasicAutomata.makeEmpty();
                break;
            }
            case REGEXP_STRING: {
                a = BasicAutomata.makeString(this.s);
                break;
            }
            case REGEXP_ANYSTRING: {
                a = BasicAutomata.makeAnyString();
                break;
            }
            case REGEXP_AUTOMATON: {
                Automaton aa = null;
                if (automata != null) {
                    aa = automata.get(this.s);
                }
                if (aa == null && automaton_provider != null) {
                    try {
                        aa = automaton_provider.getAutomaton(this.s);
                    }
                    catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
                if (aa == null) {
                    throw new IllegalArgumentException("'" + this.s + "' not found");
                }
                a = aa.clone();
                break;
            }
            case REGEXP_INTERVAL: {
                a = BasicAutomata.makeInterval(this.min, this.max, this.digits);
            }
        }
        return a;
    }

    private void findLeaves(RegExp exp, Kind kind, List<Automaton> list, Map<String, Automaton> automata, AutomatonProvider automaton_provider) {
        if (exp.kind == kind) {
            this.findLeaves(exp.exp1, kind, list, automata, automaton_provider);
            this.findLeaves(exp.exp2, kind, list, automata, automaton_provider);
        } else {
            list.add(exp.toAutomaton(automata, automaton_provider));
        }
    }

    public String toString() {
        return this.toStringBuilder(new StringBuilder()).toString();
    }

    StringBuilder toStringBuilder(StringBuilder b) {
        switch (this.kind) {
            case REGEXP_UNION: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("|");
                this.exp2.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_CONCATENATION: {
                this.exp1.toStringBuilder(b);
                this.exp2.toStringBuilder(b);
                break;
            }
            case REGEXP_INTERSECTION: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("&");
                this.exp2.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_OPTIONAL: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append(")?");
                break;
            }
            case REGEXP_REPEAT: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append(")*");
                break;
            }
            case REGEXP_REPEAT_MIN: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("){").append(this.min).append(",}");
                break;
            }
            case REGEXP_REPEAT_MINMAX: {
                b.append("(");
                this.exp1.toStringBuilder(b);
                b.append("){").append(this.min).append(",").append(this.max).append("}");
                break;
            }
            case REGEXP_COMPLEMENT: {
                b.append("~(");
                this.exp1.toStringBuilder(b);
                b.append(")");
                break;
            }
            case REGEXP_CHAR: {
                b.append("\\").appendCodePoint(this.c);
                break;
            }
            case REGEXP_CHAR_RANGE: {
                b.append("[\\").appendCodePoint(this.from).append("-\\").appendCodePoint(this.to).append("]");
                break;
            }
            case REGEXP_ANYCHAR: {
                b.append(".");
                break;
            }
            case REGEXP_EMPTY: {
                b.append("#");
                break;
            }
            case REGEXP_STRING: {
                b.append("\"").append(this.s).append("\"");
                break;
            }
            case REGEXP_ANYSTRING: {
                b.append("@");
                break;
            }
            case REGEXP_AUTOMATON: {
                b.append("<").append(this.s).append(">");
                break;
            }
            case REGEXP_INTERVAL: {
                int i;
                String s1 = Integer.toString(this.min);
                String s2 = Integer.toString(this.max);
                b.append("<");
                if (this.digits > 0) {
                    for (i = s1.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s1).append("-");
                if (this.digits > 0) {
                    for (i = s2.length(); i < this.digits; ++i) {
                        b.append('0');
                    }
                }
                b.append(s2).append(">");
            }
        }
        return b;
    }

    public Set<String> getIdentifiers() {
        HashSet<String> set = new HashSet<String>();
        this.getIdentifiers(set);
        return set;
    }

    void getIdentifiers(Set<String> set) {
        switch (this.kind) {
            case REGEXP_UNION: 
            case REGEXP_CONCATENATION: 
            case REGEXP_INTERSECTION: {
                this.exp1.getIdentifiers(set);
                this.exp2.getIdentifiers(set);
                break;
            }
            case REGEXP_OPTIONAL: 
            case REGEXP_REPEAT: 
            case REGEXP_REPEAT_MIN: 
            case REGEXP_REPEAT_MINMAX: 
            case REGEXP_COMPLEMENT: {
                this.exp1.getIdentifiers(set);
                break;
            }
            case REGEXP_AUTOMATON: {
                set.add(this.s);
                break;
            }
        }
    }

    static RegExp makeUnion(RegExp exp1, RegExp exp2) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_UNION;
        r.exp1 = exp1;
        r.exp2 = exp2;
        return r;
    }

    static RegExp makeConcatenation(RegExp exp1, RegExp exp2) {
        if (!(exp1.kind != Kind.REGEXP_CHAR && exp1.kind != Kind.REGEXP_STRING || exp2.kind != Kind.REGEXP_CHAR && exp2.kind != Kind.REGEXP_STRING)) {
            return RegExp.makeString(exp1, exp2);
        }
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CONCATENATION;
        if (!(exp1.kind != Kind.REGEXP_CONCATENATION || exp1.exp2.kind != Kind.REGEXP_CHAR && exp1.exp2.kind != Kind.REGEXP_STRING || exp2.kind != Kind.REGEXP_CHAR && exp2.kind != Kind.REGEXP_STRING)) {
            r.exp1 = exp1.exp1;
            r.exp2 = RegExp.makeString(exp1.exp2, exp2);
        } else if (!(exp1.kind != Kind.REGEXP_CHAR && exp1.kind != Kind.REGEXP_STRING || exp2.kind != Kind.REGEXP_CONCATENATION || exp2.exp1.kind != Kind.REGEXP_CHAR && exp2.exp1.kind != Kind.REGEXP_STRING)) {
            r.exp1 = RegExp.makeString(exp1, exp2.exp1);
            r.exp2 = exp2.exp2;
        } else {
            r.exp1 = exp1;
            r.exp2 = exp2;
        }
        return r;
    }

    private static RegExp makeString(RegExp exp1, RegExp exp2) {
        StringBuilder b = new StringBuilder();
        if (exp1.kind == Kind.REGEXP_STRING) {
            b.append(exp1.s);
        } else {
            b.appendCodePoint(exp1.c);
        }
        if (exp2.kind == Kind.REGEXP_STRING) {
            b.append(exp2.s);
        } else {
            b.appendCodePoint(exp2.c);
        }
        return RegExp.makeString(b.toString());
    }

    static RegExp makeIntersection(RegExp exp1, RegExp exp2) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_INTERSECTION;
        r.exp1 = exp1;
        r.exp2 = exp2;
        return r;
    }

    static RegExp makeOptional(RegExp exp) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_OPTIONAL;
        r.exp1 = exp;
        return r;
    }

    static RegExp makeRepeat(RegExp exp) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT;
        r.exp1 = exp;
        return r;
    }

    static RegExp makeRepeat(RegExp exp, int min) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT_MIN;
        r.exp1 = exp;
        r.min = min;
        return r;
    }

    static RegExp makeRepeat(RegExp exp, int min, int max) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_REPEAT_MINMAX;
        r.exp1 = exp;
        r.min = min;
        r.max = max;
        return r;
    }

    static RegExp makeComplement(RegExp exp) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_COMPLEMENT;
        r.exp1 = exp;
        return r;
    }

    static RegExp makeChar(int c) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CHAR;
        r.c = c;
        return r;
    }

    static RegExp makeCharRange(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("invalid range: from (" + from + ") cannot be > to (" + to + ")");
        }
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_CHAR_RANGE;
        r.from = from;
        r.to = to;
        return r;
    }

    static RegExp makeAnyChar() {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_ANYCHAR;
        return r;
    }

    static RegExp makeEmpty() {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_EMPTY;
        return r;
    }

    static RegExp makeString(String s) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_STRING;
        r.s = s;
        return r;
    }

    static RegExp makeAnyString() {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_ANYSTRING;
        return r;
    }

    static RegExp makeAutomaton(String s) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_AUTOMATON;
        r.s = s;
        return r;
    }

    static RegExp makeInterval(int min, int max, int digits) {
        RegExp r = new RegExp();
        r.kind = Kind.REGEXP_INTERVAL;
        r.min = min;
        r.max = max;
        r.digits = digits;
        return r;
    }

    private boolean peek(String s) {
        return this.more() && s.indexOf(this.b.codePointAt(this.pos)) != -1;
    }

    private boolean match(int c) {
        if (this.pos >= this.b.length()) {
            return false;
        }
        if (this.b.codePointAt(this.pos) == c) {
            this.pos += Character.charCount(c);
            return true;
        }
        return false;
    }

    private boolean more() {
        return this.pos < this.b.length();
    }

    private int next() throws IllegalArgumentException {
        if (!this.more()) {
            throw new IllegalArgumentException("unexpected end-of-string");
        }
        int ch = this.b.codePointAt(this.pos);
        this.pos += Character.charCount(ch);
        return ch;
    }

    private boolean check(int flag) {
        return (this.flags & flag) != 0;
    }

    final RegExp parseUnionExp() throws IllegalArgumentException {
        RegExp e = this.parseInterExp();
        if (this.match(124)) {
            e = RegExp.makeUnion(e, this.parseUnionExp());
        }
        return e;
    }

    final RegExp parseInterExp() throws IllegalArgumentException {
        RegExp e = this.parseConcatExp();
        if (this.check(1) && this.match(38)) {
            e = RegExp.makeIntersection(e, this.parseInterExp());
        }
        return e;
    }

    final RegExp parseConcatExp() throws IllegalArgumentException {
        RegExp e = this.parseRepeatExp();
        if (!(!this.more() || this.peek(")|") || this.check(1) && this.peek("&"))) {
            e = RegExp.makeConcatenation(e, this.parseConcatExp());
        }
        return e;
    }

    final RegExp parseRepeatExp() throws IllegalArgumentException {
        RegExp e = this.parseComplExp();
        while (this.peek("?*+{")) {
            if (this.match(63)) {
                e = RegExp.makeOptional(e);
                continue;
            }
            if (this.match(42)) {
                e = RegExp.makeRepeat(e);
                continue;
            }
            if (this.match(43)) {
                e = RegExp.makeRepeat(e, 1);
                continue;
            }
            if (!this.match(123)) continue;
            int start = this.pos;
            while (this.peek("0123456789")) {
                this.next();
            }
            if (start == this.pos) {
                throw new IllegalArgumentException("integer expected at position " + this.pos);
            }
            int n = Integer.parseInt(this.b.substring(start, this.pos));
            int m = -1;
            if (this.match(44)) {
                start = this.pos;
                while (this.peek("0123456789")) {
                    this.next();
                }
                if (start != this.pos) {
                    m = Integer.parseInt(this.b.substring(start, this.pos));
                }
            } else {
                m = n;
            }
            if (!this.match(125)) {
                throw new IllegalArgumentException("expected '}' at position " + this.pos);
            }
            if (m == -1) {
                e = RegExp.makeRepeat(e, n);
                continue;
            }
            e = RegExp.makeRepeat(e, n, m);
        }
        return e;
    }

    final RegExp parseComplExp() throws IllegalArgumentException {
        if (this.check(2) && this.match(126)) {
            return RegExp.makeComplement(this.parseComplExp());
        }
        return this.parseCharClassExp();
    }

    final RegExp parseCharClassExp() throws IllegalArgumentException {
        if (this.match(91)) {
            boolean negate = false;
            if (this.match(94)) {
                negate = true;
            }
            RegExp e = this.parseCharClasses();
            if (negate) {
                e = RegExp.makeIntersection(RegExp.makeAnyChar(), RegExp.makeComplement(e));
            }
            if (!this.match(93)) {
                throw new IllegalArgumentException("expected ']' at position " + this.pos);
            }
            return e;
        }
        return this.parseSimpleExp();
    }

    final RegExp parseCharClasses() throws IllegalArgumentException {
        RegExp e = this.parseCharClass();
        while (this.more() && !this.peek("]")) {
            e = RegExp.makeUnion(e, this.parseCharClass());
        }
        return e;
    }

    final RegExp parseCharClass() throws IllegalArgumentException {
        int c = this.parseCharExp();
        if (this.match(45)) {
            return RegExp.makeCharRange(c, this.parseCharExp());
        }
        return RegExp.makeChar(c);
    }

    final RegExp parseSimpleExp() throws IllegalArgumentException {
        if (this.match(46)) {
            return RegExp.makeAnyChar();
        }
        if (this.check(4) && this.match(35)) {
            return RegExp.makeEmpty();
        }
        if (this.check(8) && this.match(64)) {
            return RegExp.makeAnyString();
        }
        if (this.match(34)) {
            int start = this.pos;
            while (this.more() && !this.peek("\"")) {
                this.next();
            }
            if (!this.match(34)) {
                throw new IllegalArgumentException("expected '\"' at position " + this.pos);
            }
            return RegExp.makeString(this.b.substring(start, this.pos - 1));
        }
        if (this.match(40)) {
            if (this.match(41)) {
                return RegExp.makeString("");
            }
            RegExp e = this.parseUnionExp();
            if (!this.match(41)) {
                throw new IllegalArgumentException("expected ')' at position " + this.pos);
            }
            return e;
        }
        if ((this.check(16) || this.check(32)) && this.match(60)) {
            int start = this.pos;
            while (this.more() && !this.peek(">")) {
                this.next();
            }
            if (!this.match(62)) {
                throw new IllegalArgumentException("expected '>' at position " + this.pos);
            }
            String s = this.b.substring(start, this.pos - 1);
            int i = s.indexOf(45);
            if (i == -1) {
                if (!this.check(16)) {
                    throw new IllegalArgumentException("interval syntax error at position " + (this.pos - 1));
                }
                return RegExp.makeAutomaton(s);
            }
            if (!this.check(32)) {
                throw new IllegalArgumentException("illegal identifier at position " + (this.pos - 1));
            }
            try {
                if (i == 0 || i == s.length() - 1 || i != s.lastIndexOf(45)) {
                    throw new NumberFormatException();
                }
                String smin = s.substring(0, i);
                String smax = s.substring(i + 1, s.length());
                int imin = Integer.parseInt(smin);
                int imax = Integer.parseInt(smax);
                int digits = smin.length() == smax.length() ? smin.length() : 0;
                if (imin > imax) {
                    int t = imin;
                    imin = imax;
                    imax = t;
                }
                return RegExp.makeInterval(imin, imax, digits);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("interval syntax error at position " + (this.pos - 1));
            }
        }
        return RegExp.makeChar(this.parseCharExp());
    }

    final int parseCharExp() throws IllegalArgumentException {
        this.match(92);
        return this.next();
    }

    static enum Kind {
        REGEXP_UNION,
        REGEXP_CONCATENATION,
        REGEXP_INTERSECTION,
        REGEXP_OPTIONAL,
        REGEXP_REPEAT,
        REGEXP_REPEAT_MIN,
        REGEXP_REPEAT_MINMAX,
        REGEXP_COMPLEMENT,
        REGEXP_CHAR,
        REGEXP_CHAR_RANGE,
        REGEXP_ANYCHAR,
        REGEXP_EMPTY,
        REGEXP_STRING,
        REGEXP_ANYSTRING,
        REGEXP_AUTOMATON,
        REGEXP_INTERVAL;

    }
}

