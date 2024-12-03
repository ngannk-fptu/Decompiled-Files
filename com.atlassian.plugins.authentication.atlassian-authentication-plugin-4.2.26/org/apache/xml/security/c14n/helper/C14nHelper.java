/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.helper;

import org.w3c.dom.Attr;

public final class C14nHelper {
    private C14nHelper() {
    }

    public static boolean namespaceIsRelative(Attr namespace) {
        return !C14nHelper.namespaceIsAbsolute(namespace);
    }

    public static boolean namespaceIsRelative(String namespaceValue) {
        return !C14nHelper.namespaceIsAbsolute(namespaceValue);
    }

    public static boolean namespaceIsAbsolute(Attr namespace) {
        return C14nHelper.namespaceIsAbsolute(namespace.getValue());
    }

    public static boolean namespaceIsAbsolute(String namespaceValue) {
        if (namespaceValue.length() == 0) {
            return true;
        }
        return namespaceValue.indexOf(58) > 0;
    }
}

