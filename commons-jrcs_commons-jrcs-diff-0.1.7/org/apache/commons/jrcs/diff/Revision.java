/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.PatchFailedException;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.util.ToString;

public class Revision
extends ToString {
    List deltas_ = new LinkedList();

    public synchronized void addDelta(Delta delta) {
        if (delta == null) {
            throw new IllegalArgumentException("new delta is null");
        }
        this.deltas_.add(delta);
    }

    public synchronized void insertDelta(Delta delta) {
        if (delta == null) {
            throw new IllegalArgumentException("new delta is null");
        }
        this.deltas_.add(0, delta);
    }

    public Delta getDelta(int i) {
        return (Delta)this.deltas_.get(i);
    }

    public int size() {
        return this.deltas_.size();
    }

    public Object[] patch(Object[] src) throws PatchFailedException {
        ArrayList<Object> target = new ArrayList<Object>(Arrays.asList(src));
        this.applyTo(target);
        return target.toArray();
    }

    public synchronized void applyTo(List target) throws PatchFailedException {
        ListIterator i = this.deltas_.listIterator(this.deltas_.size());
        while (i.hasPrevious()) {
            Delta delta = (Delta)i.previous();
            delta.patch(target);
        }
    }

    public synchronized void toString(StringBuffer s) {
        Iterator i = this.deltas_.iterator();
        while (i.hasNext()) {
            ((Delta)i.next()).toString(s);
        }
    }

    public synchronized void toRCSString(StringBuffer s, String EOL) {
        Iterator i = this.deltas_.iterator();
        while (i.hasNext()) {
            ((Delta)i.next()).toRCSString(s, EOL);
        }
    }

    public void toRCSString(StringBuffer s) {
        this.toRCSString(s, Diff.NL);
    }

    public String toRCSString(String EOL) {
        StringBuffer s = new StringBuffer();
        this.toRCSString(s, EOL);
        return s.toString();
    }

    public String toRCSString() {
        return this.toRCSString(Diff.NL);
    }

    public void accept(RevisionVisitor visitor) {
        visitor.visit(this);
        Iterator iter = this.deltas_.iterator();
        while (iter.hasNext()) {
            ((Delta)iter.next()).accept(visitor);
        }
    }
}

