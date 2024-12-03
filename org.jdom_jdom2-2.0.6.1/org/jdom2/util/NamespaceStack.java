/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.internal.ArrayCopy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NamespaceStack
implements Iterable<Namespace> {
    private static final Namespace[] EMPTY = new Namespace[0];
    private static final List<Namespace> EMPTYLIST = Collections.emptyList();
    private static final Iterable<Namespace> EMPTYITER = new EmptyIterable();
    private static final Comparator<Namespace> NSCOMP = new Comparator<Namespace>(){

        @Override
        public int compare(Namespace ns1, Namespace ns2) {
            return ns1.getPrefix().compareTo(ns2.getPrefix());
        }
    };
    private static final Namespace[] DEFAULTSEED = new Namespace[]{Namespace.NO_NAMESPACE, Namespace.XML_NAMESPACE};
    private Namespace[][] added = new Namespace[10][];
    private Namespace[][] scope = new Namespace[10][];
    private int depth = -1;

    private static final int binarySearch(Namespace[] data, int left, int right, Namespace key) {
        --right;
        while (left <= right) {
            int mid = left + right >>> 1;
            if (data[mid] == key) {
                return mid;
            }
            int cmp = NSCOMP.compare(data[mid], key);
            if (cmp < 0) {
                left = mid + 1;
                continue;
            }
            if (cmp > 0) {
                right = mid - 1;
                continue;
            }
            return mid;
        }
        return -left - 1;
    }

    public NamespaceStack() {
        this(DEFAULTSEED);
    }

    public NamespaceStack(Namespace[] seed) {
        ++this.depth;
        this.added[this.depth] = seed;
        this.scope[this.depth] = this.added[this.depth];
    }

    private static final Namespace[] checkNamespace(List<Namespace> store, Namespace namespace, Namespace[] scope) {
        if (namespace == scope[0]) {
            return scope;
        }
        if (namespace.getPrefix().equals(scope[0].getPrefix())) {
            store.add(namespace);
            Namespace[] nscope = ArrayCopy.copyOf(scope, scope.length);
            nscope[0] = namespace;
            return nscope;
        }
        int ip = NamespaceStack.binarySearch(scope, 1, scope.length, namespace);
        if (ip >= 0 && namespace == scope[ip]) {
            return scope;
        }
        store.add(namespace);
        if (ip >= 0) {
            Namespace[] nscope = ArrayCopy.copyOf(scope, scope.length);
            nscope[ip] = namespace;
            return nscope;
        }
        Namespace[] nscope = ArrayCopy.copyOf(scope, scope.length + 1);
        ip = -ip - 1;
        System.arraycopy(nscope, ip, nscope, ip + 1, nscope.length - ip - 1);
        nscope[ip] = namespace;
        return nscope;
    }

    public void push(Element element) {
        ArrayList<Namespace> toadd = new ArrayList<Namespace>(8);
        Namespace mns = element.getNamespace();
        Namespace[] newscope = NamespaceStack.checkNamespace(toadd, mns, this.scope[this.depth]);
        if (element.hasAdditionalNamespaces()) {
            for (Namespace ns : element.getAdditionalNamespaces()) {
                if (ns == mns) continue;
                newscope = NamespaceStack.checkNamespace(toadd, ns, newscope);
            }
        }
        if (element.hasAttributes()) {
            for (Attribute a : element.getAttributes()) {
                Namespace ns = a.getNamespace();
                if (ns == Namespace.NO_NAMESPACE || ns == mns) continue;
                newscope = NamespaceStack.checkNamespace(toadd, ns, newscope);
            }
        }
        this.pushStack(mns, newscope, toadd);
    }

    public void push(Attribute att) {
        ArrayList<Namespace> toadd = new ArrayList<Namespace>(1);
        Namespace mns = att.getNamespace();
        Namespace[] newscope = NamespaceStack.checkNamespace(toadd, mns, this.scope[this.depth]);
        this.pushStack(mns, newscope, toadd);
    }

    public void push(Iterable<Namespace> namespaces) {
        ArrayList<Namespace> toadd = new ArrayList<Namespace>(8);
        Namespace[] newscope = this.scope[this.depth];
        for (Namespace ns : namespaces) {
            newscope = NamespaceStack.checkNamespace(toadd, ns, newscope);
        }
        this.pushStack(Namespace.XML_NAMESPACE, newscope, toadd);
    }

    public void push(Namespace ... namespaces) {
        if (namespaces == null || namespaces.length == 0) {
            this.pushStack(this.scope[this.depth][0], this.scope[this.depth], EMPTYLIST);
            return;
        }
        ArrayList<Namespace> toadd = new ArrayList<Namespace>(8);
        Namespace[] newscope = this.scope[this.depth];
        for (Namespace ns : namespaces) {
            newscope = NamespaceStack.checkNamespace(toadd, ns, newscope);
        }
        this.pushStack(namespaces[0], newscope, toadd);
    }

    private final void pushStack(Namespace mns, Namespace[] newscope, List<Namespace> toadd) {
        ++this.depth;
        if (this.depth >= this.scope.length) {
            this.scope = (Namespace[][])ArrayCopy.copyOf(this.scope, this.scope.length * 2);
            this.added = (Namespace[][])ArrayCopy.copyOf(this.added, this.scope.length);
        }
        if (toadd.isEmpty()) {
            this.added[this.depth] = EMPTY;
        } else {
            this.added[this.depth] = toadd.toArray(new Namespace[toadd.size()]);
            if (this.added[this.depth][0] == mns) {
                Arrays.sort(this.added[this.depth], 1, this.added[this.depth].length, NSCOMP);
            } else {
                Arrays.sort(this.added[this.depth], NSCOMP);
            }
        }
        if (mns != newscope[0]) {
            if (toadd.isEmpty()) {
                newscope = ArrayCopy.copyOf(newscope, newscope.length);
            }
            Namespace tmp = newscope[0];
            int ip = -NamespaceStack.binarySearch(newscope, 1, newscope.length, tmp) - 1;
            System.arraycopy(newscope, 1, newscope, 0, --ip);
            newscope[ip] = tmp;
            ip = NamespaceStack.binarySearch(newscope, 0, newscope.length, mns);
            System.arraycopy(newscope, 0, newscope, 1, ip);
            newscope[0] = mns;
        }
        this.scope[this.depth] = newscope;
    }

    public void pop() {
        if (this.depth <= 0) {
            throw new IllegalStateException("Cannot over-pop the stack.");
        }
        this.scope[this.depth] = null;
        this.added[this.depth] = null;
        --this.depth;
    }

    public Iterable<Namespace> addedForward() {
        if (this.added[this.depth].length == 0) {
            return EMPTYITER;
        }
        return new NamespaceIterable(this.added[this.depth], true);
    }

    public Iterable<Namespace> addedReverse() {
        if (this.added[this.depth].length == 0) {
            return EMPTYITER;
        }
        return new NamespaceIterable(this.added[this.depth], false);
    }

    @Override
    public Iterator<Namespace> iterator() {
        return new ForwardWalker(this.scope[this.depth]);
    }

    public Namespace[] getScope() {
        return ArrayCopy.copyOf(this.scope[this.depth], this.scope[this.depth].length);
    }

    public boolean isInScope(Namespace ns) {
        if (ns == this.scope[this.depth][0]) {
            return true;
        }
        int ip = NamespaceStack.binarySearch(this.scope[this.depth], 1, this.scope[this.depth].length, ns);
        if (ip >= 0) {
            return ns == this.scope[this.depth][ip];
        }
        return false;
    }

    public Namespace getNamespaceForPrefix(String prefix) {
        Namespace[] nsa;
        if (prefix == null) {
            return this.getNamespaceForPrefix("");
        }
        for (Namespace ns : nsa = this.scope[this.depth]) {
            if (!prefix.equals(ns.getPrefix())) continue;
            return ns;
        }
        return null;
    }

    public Namespace getFirstNamespaceForURI(String uri) {
        if (uri == null) {
            return this.getFirstNamespaceForURI("");
        }
        for (Namespace ns : this.scope[this.depth]) {
            if (!uri.equals(ns.getURI())) continue;
            return ns;
        }
        return null;
    }

    public Namespace[] getAllNamespacesForURI(String uri) {
        if (uri == null) {
            return this.getAllNamespacesForURI("");
        }
        ArrayList<Namespace> al = new ArrayList<Namespace>(4);
        for (Namespace ns : this.scope[this.depth]) {
            if (!uri.equals(ns.getURI())) continue;
            al.add(ns);
        }
        return al.toArray(new Namespace[al.size()]);
    }

    public Namespace getRebound(String prefix) {
        if (this.depth <= 0) {
            return null;
        }
        for (Namespace nsa : this.added[this.depth]) {
            if (!nsa.getPrefix().equals(prefix)) continue;
            for (Namespace nsp : this.scope[this.depth - 1]) {
                if (!nsp.getPrefix().equals(prefix)) continue;
                return nsp;
            }
            return null;
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EmptyIterable
    implements Iterable<Namespace>,
    Iterator<Namespace> {
        private EmptyIterable() {
        }

        @Override
        public Iterator<Namespace> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Namespace next() {
            throw new NoSuchElementException("Can not call next() on an empty Iterator.");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Namespaces from iterator");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class NamespaceIterable
    implements Iterable<Namespace> {
        private final boolean forward;
        private final Namespace[] namespaces;

        public NamespaceIterable(Namespace[] data, boolean forward) {
            this.forward = forward;
            this.namespaces = data;
        }

        @Override
        public Iterator<Namespace> iterator() {
            return this.forward ? new ForwardWalker(this.namespaces) : new BackwardWalker(this.namespaces);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class BackwardWalker
    implements Iterator<Namespace> {
        private final Namespace[] namespaces;
        int cursor = -1;

        public BackwardWalker(Namespace[] namespaces) {
            this.namespaces = namespaces;
            this.cursor = namespaces.length - 1;
        }

        @Override
        public boolean hasNext() {
            return this.cursor >= 0;
        }

        @Override
        public Namespace next() {
            if (this.cursor < 0) {
                throw new NoSuchElementException("Cannot over-iterate...");
            }
            return this.namespaces[this.cursor--];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Namespaces from iterator");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class ForwardWalker
    implements Iterator<Namespace> {
        private final Namespace[] namespaces;
        int cursor = 0;

        public ForwardWalker(Namespace[] namespaces) {
            this.namespaces = namespaces;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.namespaces.length;
        }

        @Override
        public Namespace next() {
            if (this.cursor >= this.namespaces.length) {
                throw new NoSuchElementException("Cannot over-iterate...");
            }
            return this.namespaces[this.cursor++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Namespaces from iterator");
        }
    }
}

