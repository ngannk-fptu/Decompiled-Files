/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

final class ApplicationProtocolUtil {
    private static final int DEFAULT_LIST_SIZE = 2;

    private ApplicationProtocolUtil() {
    }

    static List<String> toList(Iterable<String> protocols) {
        return ApplicationProtocolUtil.toList(2, protocols);
    }

    static List<String> toList(int initialListSize, Iterable<String> protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(initialListSize);
        for (String p : protocols) {
            result.add(ObjectUtil.checkNonEmpty((String)p, (String)"p"));
        }
        return (List)ObjectUtil.checkNonEmpty(result, (String)"result");
    }

    static List<String> toList(String ... protocols) {
        return ApplicationProtocolUtil.toList(2, protocols);
    }

    static List<String> toList(int initialListSize, String ... protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(initialListSize);
        for (String p : protocols) {
            result.add(ObjectUtil.checkNonEmpty((String)p, (String)"p"));
        }
        return (List)ObjectUtil.checkNonEmpty(result, (String)"result");
    }
}

