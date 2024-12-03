/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.core.listeners;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.event.api.EventListener;

public class TitleChangeListener {
    private final ChangeIndexer changeIndexer;

    public TitleChangeListener(ChangeIndexer changeIndexer) {
        this.changeIndexer = changeIndexer;
    }

    @EventListener
    public void handleEvent(ConfluenceEntityUpdated updated) {
        ConfluenceEntityObject oldEntity = updated.getOld();
        ConfluenceEntityObject newEntity = updated.getNew();
        if (oldEntity instanceof Addressable && newEntity instanceof Addressable && newEntity instanceof Searchable) {
            String oldDisplayTitle = ((Addressable)((Object)oldEntity)).getDisplayTitle();
            String newDisplayTitle = ((Addressable)((Object)newEntity)).getDisplayTitle();
            if (oldDisplayTitle == null && newDisplayTitle != null || oldDisplayTitle != null && !oldDisplayTitle.equals(newDisplayTitle)) {
                this.changeIndexer.reIndexAllVersions((Searchable)newEntity);
            }
        }
    }
}

