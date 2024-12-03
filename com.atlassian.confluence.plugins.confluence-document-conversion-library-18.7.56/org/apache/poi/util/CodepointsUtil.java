/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.util.Iterator;
import org.apache.poi.util.Internal;

@Internal
public class CodepointsUtil {
    public static Iterator<String> iteratorFor(String text) {
        return text.codePoints().mapToObj(codePoint -> new StringBuilder().appendCodePoint(codePoint).toString()).iterator();
    }
}

