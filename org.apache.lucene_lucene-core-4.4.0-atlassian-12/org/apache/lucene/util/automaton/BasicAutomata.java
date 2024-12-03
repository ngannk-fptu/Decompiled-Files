/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.DaciukMihovAutomatonBuilder;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.StatePair;
import org.apache.lucene.util.automaton.Transition;

public final class BasicAutomata {
    private BasicAutomata() {
    }

    public static Automaton makeEmpty() {
        State s;
        Automaton a = new Automaton();
        a.initial = s = new State();
        a.deterministic = true;
        return a;
    }

    public static Automaton makeEmptyString() {
        Automaton a = new Automaton();
        a.singleton = "";
        a.deterministic = true;
        return a;
    }

    public static Automaton makeAnyString() {
        State s;
        Automaton a = new Automaton();
        a.initial = s = new State();
        s.accept = true;
        s.addTransition(new Transition(0, 0x10FFFF, s));
        a.deterministic = true;
        return a;
    }

    public static Automaton makeAnyChar() {
        return BasicAutomata.makeCharRange(0, 0x10FFFF);
    }

    public static Automaton makeChar(int c) {
        Automaton a = new Automaton();
        a.singleton = new String(Character.toChars(c));
        a.deterministic = true;
        return a;
    }

    public static Automaton makeCharRange(int min, int max) {
        if (min == max) {
            return BasicAutomata.makeChar(min);
        }
        Automaton a = new Automaton();
        State s1 = new State();
        State s2 = new State();
        a.initial = s1;
        s2.accept = true;
        if (min <= max) {
            s1.addTransition(new Transition(min, max, s2));
        }
        a.deterministic = true;
        return a;
    }

    private static State anyOfRightLength(String x, int n) {
        State s = new State();
        if (x.length() == n) {
            s.setAccept(true);
        } else {
            s.addTransition(new Transition(48, 57, BasicAutomata.anyOfRightLength(x, n + 1)));
        }
        return s;
    }

    private static State atLeast(String x, int n, Collection<State> initials, boolean zeros) {
        State s = new State();
        if (x.length() == n) {
            s.setAccept(true);
        } else {
            if (zeros) {
                initials.add(s);
            }
            char c = x.charAt(n);
            s.addTransition(new Transition(c, BasicAutomata.atLeast(x, n + 1, initials, zeros && c == '0')));
            if (c < '9') {
                s.addTransition(new Transition((char)(c + '\u0001'), 57, BasicAutomata.anyOfRightLength(x, n + 1)));
            }
        }
        return s;
    }

    private static State atMost(String x, int n) {
        State s = new State();
        if (x.length() == n) {
            s.setAccept(true);
        } else {
            char c = x.charAt(n);
            s.addTransition(new Transition(c, BasicAutomata.atMost(x, (char)n + '\u0001')));
            if (c > '0') {
                s.addTransition(new Transition(48, (char)(c - '\u0001'), BasicAutomata.anyOfRightLength(x, n + 1)));
            }
        }
        return s;
    }

    private static State between(String x, String y, int n, Collection<State> initials, boolean zeros) {
        State s = new State();
        if (x.length() == n) {
            s.setAccept(true);
        } else {
            char cy;
            char cx;
            if (zeros) {
                initials.add(s);
            }
            if ((cx = x.charAt(n)) == (cy = y.charAt(n))) {
                s.addTransition(new Transition(cx, BasicAutomata.between(x, y, n + 1, initials, zeros && cx == '0')));
            } else {
                s.addTransition(new Transition(cx, BasicAutomata.atLeast(x, n + 1, initials, zeros && cx == '0')));
                s.addTransition(new Transition(cy, BasicAutomata.atMost(y, n + 1)));
                if (cx + '\u0001' < cy) {
                    s.addTransition(new Transition((char)(cx + '\u0001'), (char)(cy - '\u0001'), BasicAutomata.anyOfRightLength(x, n + 1)));
                }
            }
        }
        return s;
    }

    public static Automaton makeInterval(int min, int max, int digits) throws IllegalArgumentException {
        Automaton a = new Automaton();
        String x = Integer.toString(min);
        String y = Integer.toString(max);
        if (min > max || digits > 0 && y.length() > digits) {
            throw new IllegalArgumentException();
        }
        int d = digits > 0 ? digits : y.length();
        StringBuilder bx = new StringBuilder();
        for (int i = x.length(); i < d; ++i) {
            bx.append('0');
        }
        bx.append(x);
        x = bx.toString();
        StringBuilder by = new StringBuilder();
        for (int i = y.length(); i < d; ++i) {
            by.append('0');
        }
        by.append(y);
        y = by.toString();
        ArrayList<State> initials = new ArrayList<State>();
        a.initial = BasicAutomata.between(x, y, 0, initials, digits <= 0);
        if (digits <= 0) {
            ArrayList<StatePair> pairs = new ArrayList<StatePair>();
            for (State p : initials) {
                if (a.initial == p) continue;
                pairs.add(new StatePair(a.initial, p));
            }
            BasicOperations.addEpsilons(a, pairs);
            a.initial.addTransition(new Transition(48, a.initial));
            a.deterministic = false;
        } else {
            a.deterministic = true;
        }
        a.checkMinimizeAlways();
        return a;
    }

    public static Automaton makeString(String s) {
        Automaton a = new Automaton();
        a.singleton = s;
        a.deterministic = true;
        return a;
    }

    public static Automaton makeString(int[] word, int offset, int length) {
        State s;
        Automaton a = new Automaton();
        a.setDeterministic(true);
        a.initial = s = new State();
        for (int i = offset; i < offset + length; ++i) {
            State s2 = new State();
            s.addTransition(new Transition(word[i], s2));
            s = s2;
        }
        s.accept = true;
        return a;
    }

    public static Automaton makeStringUnion(Collection<BytesRef> utf8Strings) {
        if (utf8Strings.isEmpty()) {
            return BasicAutomata.makeEmpty();
        }
        return DaciukMihovAutomatonBuilder.build(utf8Strings);
    }
}

