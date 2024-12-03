/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

import java.security.KeyPair;
import java.util.Comparator;

public class KeyPairComparator
implements Comparator {
    public int compare(Object o1, Object o2) {
        KeyPair keyPair1 = (KeyPair)o1;
        KeyPair keyPair2 = (KeyPair)o2;
        if (!keyPair1.getPublic().equals(keyPair2.getPublic())) {
            return 1;
        }
        if (!keyPair1.getPrivate().equals(keyPair2.getPrivate())) {
            return 1;
        }
        return 0;
    }
}

