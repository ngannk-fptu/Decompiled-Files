/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.HashSet;
import java.util.StringTokenizer;

public class StringSet
extends HashSet {
    private static final long serialVersionUID = 1L;

    public StringSet(String set) {
        StringTokenizer st = new StringTokenizer(set, ",");
        while (st.hasMoreTokens()) {
            this.add(st.nextToken().trim());
        }
    }
}

