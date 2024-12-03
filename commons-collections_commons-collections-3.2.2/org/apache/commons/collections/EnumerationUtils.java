/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Enumeration;
import java.util.List;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.EnumerationIterator;

public class EnumerationUtils {
    public static List toList(Enumeration enumeration) {
        return IteratorUtils.toList(new EnumerationIterator(enumeration));
    }
}

