/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.impl.search.actions.AbstractFlushIndexQueueAction;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.internal.search.IncrementalIndexManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@WebSudoRequired
@AdminOnly
public class FlushContentIndexQueueAction
extends AbstractFlushIndexQueueAction {
    public void setLuceneContentIndexManager(IncrementalIndexManager luceneContentIndexManager) {
        this.indexManager = luceneContentIndexManager;
    }
}

