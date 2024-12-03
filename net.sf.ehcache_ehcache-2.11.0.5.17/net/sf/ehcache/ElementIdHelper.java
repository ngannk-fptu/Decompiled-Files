/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.Element;

public class ElementIdHelper {
    public static boolean hasId(Element e) {
        return e.hasId();
    }

    public static long getId(Element e) {
        return e.getId();
    }

    public static void setId(Element e, long id) {
        e.setId(id);
    }
}

