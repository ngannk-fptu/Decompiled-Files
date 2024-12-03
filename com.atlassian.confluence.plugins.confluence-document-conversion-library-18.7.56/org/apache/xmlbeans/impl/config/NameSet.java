/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NameSet {
    public static final NameSet EMPTY = new NameSet(true, Collections.EMPTY_SET);
    public static final NameSet EVERYTHING = new NameSet(false, Collections.EMPTY_SET);
    private boolean _isFinite;
    private Set<String> _finiteSet;

    private NameSet(boolean isFinite, Set<String> finiteSet) {
        this._isFinite = isFinite;
        this._finiteSet = finiteSet;
    }

    static NameSet newInstance(boolean isFinite, Set<String> finiteSet) {
        if (finiteSet.size() == 0) {
            if (isFinite) {
                return EMPTY;
            }
            return EVERYTHING;
        }
        HashSet<String> fs = new HashSet<String>();
        fs.addAll(finiteSet);
        return new NameSet(isFinite, fs);
    }

    private static Set<String> intersectFiniteSets(Set<String> a, Set<String> b) {
        HashSet<String> intersection = new HashSet<String>();
        while (a.iterator().hasNext()) {
            String name = a.iterator().next();
            if (!b.contains(name)) continue;
            intersection.add(name);
        }
        return intersection;
    }

    public NameSet union(NameSet with) {
        if (this._isFinite) {
            if (with._isFinite) {
                HashSet<String> union = new HashSet<String>();
                union.addAll(this._finiteSet);
                union.addAll(with._finiteSet);
                return NameSet.newInstance(true, union);
            }
            HashSet<String> subst = new HashSet<String>();
            subst.addAll(with._finiteSet);
            subst.removeAll(this._finiteSet);
            return NameSet.newInstance(false, subst);
        }
        if (with._isFinite) {
            HashSet<String> subst = new HashSet<String>();
            subst.addAll(this._finiteSet);
            subst.removeAll(with._finiteSet);
            return NameSet.newInstance(false, subst);
        }
        return NameSet.newInstance(false, NameSet.intersectFiniteSets(this._finiteSet, with._finiteSet));
    }

    public NameSet intersect(NameSet with) {
        if (this._isFinite) {
            if (with._isFinite) {
                return NameSet.newInstance(true, NameSet.intersectFiniteSets(this._finiteSet, with._finiteSet));
            }
            HashSet<String> subst = new HashSet<String>();
            subst.addAll(this._finiteSet);
            subst.removeAll(with._finiteSet);
            return NameSet.newInstance(false, subst);
        }
        if (with._isFinite) {
            HashSet<String> subst = new HashSet<String>();
            subst.addAll(with._finiteSet);
            subst.removeAll(this._finiteSet);
            return NameSet.newInstance(true, subst);
        }
        HashSet<String> union = new HashSet<String>();
        union.addAll(this._finiteSet);
        union.addAll(with._finiteSet);
        return NameSet.newInstance(false, union);
    }

    public NameSet substractFrom(NameSet from) {
        return from.substract(this);
    }

    public NameSet substract(NameSet what) {
        if (this._isFinite) {
            if (what._isFinite) {
                HashSet<String> subst = new HashSet<String>();
                subst.addAll(this._finiteSet);
                subst.removeAll(what._finiteSet);
                return NameSet.newInstance(true, subst);
            }
            return NameSet.newInstance(true, NameSet.intersectFiniteSets(this._finiteSet, what._finiteSet));
        }
        if (what._isFinite) {
            HashSet<String> union = new HashSet<String>();
            union.addAll(this._finiteSet);
            union.addAll(what._finiteSet);
            return NameSet.newInstance(false, union);
        }
        HashSet<String> subst = new HashSet<String>();
        subst.addAll(what._finiteSet);
        subst.removeAll(this._finiteSet);
        return NameSet.newInstance(true, subst);
    }

    public NameSet invert() {
        return NameSet.newInstance(!this._isFinite, this._finiteSet);
    }

    public boolean contains(String name) {
        if (this._isFinite) {
            return this._finiteSet.contains(name);
        }
        return !this._finiteSet.contains(name);
    }
}

