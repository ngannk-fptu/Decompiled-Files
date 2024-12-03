/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules.automata;

import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import com.sun.jersey.server.impl.uri.rules.automata.TrieArc;
import com.sun.jersey.server.impl.uri.rules.automata.TrieNode;
import com.sun.jersey.server.impl.uri.rules.automata.TrieNodeValue;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class AutomataMatchingUriTemplateRules<R>
implements UriRules<R> {
    private final TrieNode<R> automata;

    public AutomataMatchingUriTemplateRules(List<PatternRulePair<R>> rules) {
        this.automata = this.initTrie(rules);
    }

    @Override
    public Iterator<R> match(CharSequence path, UriMatchResultContext resultContext) {
        ArrayList<String> capturingGroupValues = new ArrayList<String>();
        TrieNode<R> node = this.find(path, capturingGroupValues);
        if (node != null) {
            return node.getValue();
        }
        return new TrieNodeValue.EmptyIterator();
    }

    private TrieNode<R> initTrie(List<PatternRulePair<R>> rules) {
        TrieNode a = new TrieNode();
        for (PatternRulePair<R> prp : rules) {
            if (prp.p instanceof PathPattern) {
                PathPattern p = (PathPattern)prp.p;
                a.add(p.getTemplate().getTemplate(), prp.r, prp.p);
                continue;
            }
            throw new IllegalArgumentException("The automata matching algorithm currently only worksfor UriPattern instance that are instances of PathPattern");
        }
        a.pack();
        return a;
    }

    private TrieNode<R> find(CharSequence uri, List<String> templateValues) {
        int length = uri.length();
        Stack<SearchState<R>> stack = new Stack<SearchState<R>>();
        Stack<TrieNode<R>> candidates = new Stack<TrieNode<R>>();
        HashSet<TrieArc<Object>> visitedArcs = new HashSet<TrieArc<Object>>();
        TrieNode<Object> node = this.automata;
        TrieArc<Object> nextArc = node.getFirstArc();
        int i = 0;
        while (true) {
            if (i >= length) {
                if (node.hasValue()) break;
                nextArc = null;
                while (!stack.isEmpty() && nextArc == null) {
                    SearchState state = (SearchState)stack.pop();
                    nextArc = state.arc.next;
                    node = state.node;
                    i = state.i;
                }
                if (nextArc != null) {
                    while (visitedArcs.contains(nextArc)) {
                        nextArc = nextArc.next;
                    }
                    if (nextArc != null) {
                        visitedArcs.add(nextArc);
                    }
                }
                if (nextArc != null) continue;
                break;
            }
            if (nextArc == null && node.isWildcard()) {
                int p = 0;
                TrieArc<R> exitArc = null;
                while (i + p < length && (exitArc = node.matchExitArc(uri, i + p)) == null) {
                    ++p;
                }
                if (exitArc != null) {
                    nextArc = exitArc;
                }
                i += p;
                continue;
            }
            if (nextArc == null && !node.isWildcard()) break;
            if (nextArc.next != null && node.isWildcard()) {
                stack.push(new SearchState<R>(node, nextArc, i));
            }
            if (node.hasValue()) {
                candidates.push(node);
            }
            if (node.isWildcard() && nextArc.match(uri, i) > 0) {
                i += nextArc.length();
                node = nextArc.target;
                nextArc = node.getFirstArc();
                continue;
            }
            if (node.isWildcard() && nextArc.match(uri, i) == 0) {
                nextArc = nextArc.next;
                if (nextArc != null) continue;
                ++i;
                continue;
            }
            if (!node.isWildcard() && nextArc.match(uri, i) > 0) {
                i += nextArc.length();
                node = nextArc.target;
                nextArc = node.getFirstArc();
                continue;
            }
            if (node.isWildcard() || nextArc.match(uri, i) != 0) continue;
            nextArc = nextArc.next;
        }
        if (node.hasValue() && node.getPattern().match(uri, templateValues)) {
            return node;
        }
        while (!candidates.isEmpty()) {
            TrieNode s = (TrieNode)candidates.pop();
            if (!s.getPattern().match(uri, templateValues)) continue;
            return s;
        }
        templateValues.clear();
        return null;
    }

    private static final class SearchState<E> {
        final TrieNode<E> node;
        final TrieArc<E> arc;
        final int i;

        public SearchState(TrieNode<E> node, TrieArc<E> arc, int i) {
            this.node = node;
            this.arc = arc;
            this.i = i;
        }
    }
}

