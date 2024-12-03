/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model;

import com.sun.jersey.server.impl.uri.PathPattern;
import java.util.ArrayList;
import java.util.TreeMap;

public class RulesMap<R>
extends TreeMap<PathPattern, R> {
    public RulesMap() {
        super(PathPattern.COMPARATOR);
    }

    public PathPattern hasConflict(PathPattern p) {
        for (PathPattern cp : this.keySet()) {
            if (!cp.equals(p)) continue;
            return cp;
        }
        return null;
    }

    public void processConflicts(ConflictClosure cc) {
        ArrayList ks = new ArrayList(this.keySet());
        for (int i = 0; i < ks.size(); ++i) {
            PathPattern p1 = (PathPattern)ks.get(i);
            for (int j = i + 1; j < ks.size(); ++j) {
                PathPattern p2 = (PathPattern)ks.get(j);
                if (!p1.equals(p2)) continue;
                cc.onConflict(p1, p2);
            }
        }
    }

    public static interface ConflictClosure {
        public void onConflict(PathPattern var1, PathPattern var2);
    }
}

