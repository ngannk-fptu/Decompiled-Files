/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.util.ArrayList;
import java.util.List;

public final class Modifications {
    public final List<String> modifiedFiles = new ArrayList<String>();
    public final List<String> removedFiles = new ArrayList<String>();

    public Modifications append(Modifications another) {
        Modifications both = new Modifications();
        both.modifiedFiles.addAll(this.modifiedFiles);
        both.modifiedFiles.addAll(another.modifiedFiles);
        both.removedFiles.addAll(this.removedFiles);
        both.removedFiles.addAll(another.removedFiles);
        return both;
    }
}

