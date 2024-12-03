/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.search;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.ConfluenceIndexTask;

public interface IndexTaskFactory {
    public ConfluenceIndexTask createDeleteDocumentTask(Searchable var1);

    public ConfluenceIndexTask createDeleteDocumentTask(String var1);

    public ConfluenceIndexTask createUpdateDocumentTask(Searchable var1);

    public ConfluenceIndexTask createUpdateDocumentTask(Searchable var1, boolean var2);

    public ConfluenceIndexTask createAddDocumentTask(Searchable var1);

    public ConfluenceIndexTask createDeleteChangeDocumentsIndexTask(Searchable var1);

    public ConfluenceIndexTask createDeleteChangeDocumentsIndexTask(String var1);

    public ConfluenceIndexTask createRebuildChangeDocumentsIndexTask(Searchable var1);

    public ConfluenceIndexTask createAddChangeDocumentTask(Searchable var1);
}

