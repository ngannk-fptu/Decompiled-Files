/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import java.util.Iterator;

public interface DocumentOutline {
    public Iterator<Heading> iterator();

    public Iterator<Heading> iterator(int var1, int var2, String var3, String var4);

    public static interface Heading {
        public String getName();

        public String getAnchor();

        public int getEffectiveLevel();

        public int getType();

        public int getChildCount();
    }
}

