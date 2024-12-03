/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.CharBuf;
import groovy.json.internal.Chr;

public class JsonStringDecoder {
    public static String decode(char[] chars, int start, int to) {
        if (!Chr.contains(chars, '\\', start, to - start)) {
            return new String(chars, start, to - start);
        }
        return JsonStringDecoder.decodeForSure(chars, start, to);
    }

    public static String decodeForSure(char[] chars, int start, int to) {
        CharBuf builder = CharBuf.create(to - start);
        builder.decodeJsonString(chars, start, to);
        return builder.toString();
    }
}

