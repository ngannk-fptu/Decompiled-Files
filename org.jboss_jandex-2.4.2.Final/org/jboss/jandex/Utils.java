/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class Utils {
    private static Charset UTF8 = Charset.forName("UTF-8");

    Utils() {
    }

    static byte[] toUTF8(String string) {
        return string.getBytes(UTF8);
    }

    static String fromUTF8(byte[] bytes) {
        return new String(bytes, UTF8);
    }

    static <T> List<T> emptyOrWrap(List<T> list) {
        return list.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    static <T> Collection<T> emptyOrWrap(Collection<T> list) {
        return list.size() == 0 ? Collections.emptyList() : Collections.unmodifiableCollection(list);
    }

    static <K, V> Map<K, V> emptyOrWrap(Map<K, V> map) {
        return map.size() == 0 ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    static <T> List<T> listOfCapacity(int capacity) {
        return capacity > 0 ? new ArrayList(capacity) : Collections.emptyList();
    }
}

