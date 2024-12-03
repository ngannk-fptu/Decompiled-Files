/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.internal.search.listeners;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.event.events.label.LabelAddEvent;
import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.event.events.label.LabelRemoveEvent;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.search.ChangeIndexer;
import com.atlassian.event.api.EventListener;

public class UpdateLabelsInChangeIndexListener {
    private final ChangeIndexer changeIndexer;

    public UpdateLabelsInChangeIndexListener(ChangeIndexer changeIndexer) {
        this.changeIndexer = changeIndexer;
    }

    @EventListener
    public void handleLabelAdd(LabelAddEvent addEvent) {
        this.handleLabelEvent(addEvent);
    }

    @EventListener
    public void handleLabelRemove(LabelRemoveEvent removeEvent) {
        this.handleLabelEvent(removeEvent);
    }

    private void handleLabelEvent(LabelEvent event) {
        Labelable labelable = event.getLabelled();
        if (labelable instanceof Searchable) {
            this.changeIndexer.reIndexAllVersions((Searchable)labelable);
        }
    }
}

