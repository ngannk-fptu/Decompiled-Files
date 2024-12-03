/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.fs;

import java.net.URL;

public class Revision {
    protected Revision() {
    }

    public boolean needsReloading() {
        return false;
    }

    public static Revision build(URL fileUrl) {
        return new Revision();
    }
}

