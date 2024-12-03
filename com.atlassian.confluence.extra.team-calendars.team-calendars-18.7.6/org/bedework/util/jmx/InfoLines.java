/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import java.util.ArrayList;

public class InfoLines
extends ArrayList<String> {
    public void addLn(String ln) {
        this.add(ln + "\n");
    }

    public void exceptionMsg(Throwable t) {
        this.addLn("Exception - check logs: " + t.getMessage());
    }
}

