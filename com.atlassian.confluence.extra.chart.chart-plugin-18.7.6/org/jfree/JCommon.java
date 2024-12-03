/*
 * Decompiled with CFR 0.152.
 */
package org.jfree;

import org.jfree.JCommonInfo;
import org.jfree.ui.about.ProjectInfo;

public final class JCommon {
    public static final ProjectInfo INFO = JCommonInfo.getInstance();

    private JCommon() {
    }

    public static void main(String[] args) {
        System.out.println(INFO.toString());
    }
}

