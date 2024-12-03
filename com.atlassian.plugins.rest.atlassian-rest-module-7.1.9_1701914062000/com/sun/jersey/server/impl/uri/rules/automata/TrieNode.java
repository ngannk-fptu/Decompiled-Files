/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules.automata;

import com.sun.jersey.api.uri.UriPattern;
import com.sun.jersey.server.impl.uri.rules.automata.TrieArc;
import com.sun.jersey.server.impl.uri.rules.automata.TrieNodeValue;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrieNode<T> {
    public static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([\\w-\\._~]+?)\\}");
    private static final char WILDCARD_CHAR = '\u0000';
    private TrieArc<T> firstArc;
    private TrieArc<T> lastArc;
    private int arcs = 0;
    private TrieNodeValue<T> value = new TrieNodeValue();
    private UriPattern pattern;
    private boolean wildcard = false;

    protected void setWildcard(boolean b) {
        this.wildcard = b;
    }

    protected void setValue(T value, UriPattern pattern) {
        this.value.set(value);
        this.pattern = pattern;
    }

    protected TrieNode() {
    }

    protected TrieNode(T value) {
        this.value.set(value);
    }

    protected TrieArc<T> matchExitArc(CharSequence seq, int i) {
        TrieArc<T> arc = this.firstArc;
        while (arc != null) {
            if (arc.match(seq, i) > 0) {
                return arc;
            }
            arc = arc.next;
        }
        return null;
    }

    protected boolean hasValue() {
        return !this.value.isEmpty();
    }

    private void addArc(TrieArc<T> arc) {
        if (this.firstArc == null) {
            this.firstArc = arc;
        } else {
            this.lastArc.next = arc;
        }
        this.lastArc = arc;
        ++this.arcs;
    }

    private boolean add(CharSequence path, int i, T value, UriPattern pattern) {
        if (i >= path.length()) {
            this.setValue(value, pattern);
            return true;
        }
        char input = path.charAt(i);
        boolean added = false;
        TrieArc<T> arc = this.firstArc;
        while (arc != null) {
            if (arc.match(path, i) > 0 && (added = super.add(path, i + 1, value, pattern))) {
                return added;
            }
            arc = arc.next;
        }
        if (input == '\u0000') {
            this.setWildcard(true);
            return this.add(path, i + 1, value, pattern);
        }
        TrieNode<T> node = new TrieNode<T>();
        this.addArc(new TrieArc<T>(node, input));
        return super.add(path, i + 1, value, pattern);
    }

    protected void add(String path, T value, UriPattern pattern) {
        Matcher matcher = PARAMETER_PATTERN.matcher(path);
        String uri = matcher.replaceAll(String.valueOf('\u0000'));
        if (uri.endsWith("/") && uri.length() > 1) {
            this.add(uri.substring(0, uri.length() - 1), 0, value, pattern);
        }
        this.add(uri, 0, value, pattern);
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        this.toStringRepresentation(out, 0, new char[]{'\u0000'});
        return out.toString();
    }

    private void toStringRepresentation(StringBuilder out, int level, char[] c) {
        for (int i = 0; i < level; ++i) {
            out.append(' ');
        }
        out.append("ARC(" + new String(c) + ") ->");
        out.append(this.getClass().getSimpleName() + (this.wildcard ? "*" : ""));
        out.append(" ");
        out.append(this.value);
        out.append('\n');
        TrieArc<T> arc = this.firstArc;
        while (arc != null) {
            super.toStringRepresentation(out, level + 2, arc.code);
            arc = arc.next;
        }
    }

    public UriPattern getPattern() {
        return this.pattern;
    }

    public Iterator<T> getValue() {
        return this.value.getIterator();
    }

    protected boolean isWildcard() {
        return this.wildcard;
    }

    protected TrieArc<T> getFirstArc() {
        return this.firstArc;
    }

    public int getArcs() {
        return this.arcs;
    }

    public void pack() {
        TrieArc<T> arc = this.firstArc;
        while (arc != null) {
            arc.pack();
            arc = arc.next;
        }
    }
}

