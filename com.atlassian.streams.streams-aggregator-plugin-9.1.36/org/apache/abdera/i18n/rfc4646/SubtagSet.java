/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.i18n.rfc4646.Subtag;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class SubtagSet
implements Serializable,
Cloneable,
Iterable<Subtag>,
Comparable<SubtagSet> {
    protected final Subtag primary;

    protected SubtagSet(Subtag primary) {
        this.primary = primary;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Subtag subtag : this) {
            if (buf.length() > 0) {
                buf.append('-');
            }
            buf.append(subtag.getName());
        }
        return buf.toString();
    }

    @Override
    public Iterator<Subtag> iterator() {
        return new SubtagIterator(this.primary);
    }

    public boolean contains(Subtag subtag) {
        for (Subtag tag : this) {
            if (!tag.equals(subtag)) continue;
            return true;
        }
        return false;
    }

    public boolean contains(String tag) {
        return this.contains(tag, Subtag.Type.SIMPLE);
    }

    public boolean contains(String tag, Subtag.Type type) {
        return this.contains(new Subtag(type, tag));
    }

    public int length() {
        return this.toString().length();
    }

    public boolean isValid() {
        for (Subtag subtag : this) {
            if (subtag.isValid()) continue;
            return false;
        }
        return true;
    }

    public int count() {
        int n = 0;
        for (Subtag tag : this) {
            ++n;
        }
        return n;
    }

    public Subtag get(int index) {
        if (index < 0 || index > this.count()) {
            throw new IndexOutOfBoundsException();
        }
        Subtag tag = this.primary;
        for (int n = 1; n <= index; ++n) {
            tag = tag.getNext();
        }
        return tag;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        for (Subtag tag : this) {
            result = 31 * result + tag.hashCode();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Lang other = (Lang)obj;
        return this.hashCode() == other.hashCode();
    }

    public Subtag[] toArray() {
        LinkedList<Subtag> tags = new LinkedList<Subtag>();
        for (Subtag tag : this) {
            tags.add(tag);
        }
        return tags.toArray(new Subtag[tags.size()]);
    }

    public List<Subtag> asList() {
        return Arrays.asList(this.toArray());
    }

    @Override
    public int compareTo(SubtagSet o) {
        Iterator<Subtag> i = this.iterator();
        Iterator<Subtag> e = o.iterator();
        while (i.hasNext() && e.hasNext()) {
            Subtag enext;
            Subtag inext = i.next();
            int c = inext.compareTo(enext = e.next());
            if (c == 0) continue;
            return c;
        }
        if (e.hasNext() && !i.hasNext()) {
            return -1;
        }
        if (i.hasNext() && !e.hasNext()) {
            return 1;
        }
        return 0;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class SubtagIterator
    implements Iterator<Subtag> {
        private Subtag current;

        SubtagIterator(Subtag current) {
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public Subtag next() {
            Subtag tag = this.current;
            this.current = tag.getNext();
            return tag;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

