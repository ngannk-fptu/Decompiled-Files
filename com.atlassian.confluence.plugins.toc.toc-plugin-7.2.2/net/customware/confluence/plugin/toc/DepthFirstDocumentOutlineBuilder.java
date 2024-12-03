/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import net.customware.confluence.plugin.toc.DocumentOutline;

public interface DepthFirstDocumentOutlineBuilder {
    public DepthFirstDocumentOutlineBuilder add(String var1, String var2, int var3);

    public DepthFirstDocumentOutlineBuilder nextLevel();

    public DepthFirstDocumentOutlineBuilder previousLevel();

    public DocumentOutline getDocumentOutline();
}

