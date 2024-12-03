/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.Comparator;

public abstract class AbstractComparator<T>
implements Comparator<T> {
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}

