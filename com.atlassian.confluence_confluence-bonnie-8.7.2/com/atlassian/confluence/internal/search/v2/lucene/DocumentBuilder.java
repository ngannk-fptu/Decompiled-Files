/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import org.apache.lucene.document.Document;

public interface DocumentBuilder {
    public Document getDocument(Searchable var1);

    public Handle getHandle(Object var1);
}

