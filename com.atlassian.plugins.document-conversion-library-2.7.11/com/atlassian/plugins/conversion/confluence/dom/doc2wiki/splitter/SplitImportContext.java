/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter;

import com.atlassian.plugins.conversion.confluence.dom.ImportContext;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.Doc2Wiki;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;

public interface SplitImportContext
extends ImportContext {
    public DocumentTreeNode getNextNode();

    public boolean splitPage(StringBuilder var1, Doc2Wiki var2);

    public int getNodeLevel();

    public int getSplitLevel();
}

