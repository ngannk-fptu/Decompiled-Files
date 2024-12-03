/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

public class AttributedStringUtilities {
    private AttributedStringUtilities() {
    }

    public static boolean equal(AttributedString s1, AttributedString s2) {
        if (s1 == null) {
            return s2 == null;
        }
        if (s2 == null) {
            return false;
        }
        AttributedCharacterIterator it1 = s1.getIterator();
        AttributedCharacterIterator it2 = s2.getIterator();
        char c1 = it1.first();
        char c2 = it2.first();
        int start = 0;
        while (c1 != '\uffff') {
            Map<AttributedCharacterIterator.Attribute, Object> m2;
            int limit2;
            int limit1 = it1.getRunLimit();
            if (limit1 != (limit2 = it2.getRunLimit())) {
                return false;
            }
            Map<AttributedCharacterIterator.Attribute, Object> m1 = it1.getAttributes();
            if (!((Object)m1).equals(m2 = it2.getAttributes())) {
                return false;
            }
            for (int i = start; i < limit1; ++i) {
                if (c1 != c2) {
                    return false;
                }
                c1 = it1.next();
                c2 = it2.next();
            }
            start = limit1;
        }
        return c2 == '\uffff';
    }
}

