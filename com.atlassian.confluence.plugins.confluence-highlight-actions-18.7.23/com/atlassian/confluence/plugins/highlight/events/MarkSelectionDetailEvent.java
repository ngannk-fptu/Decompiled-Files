/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.highlight.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.plugins.highlight.model.TextCollection;
import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class MarkSelectionDetailEvent
extends ConfluenceEvent {
    private int frontendTotalOccurrence;
    private int frontendIndexOccurrence;
    private long storageTotalOccurrence;
    private boolean storageMatchSelection;
    private boolean storageLastMatchSelectionModifiable;

    public MarkSelectionDetailEvent(Object source, TextSearch textSearch, TextCollection textCollection, TextMatch textMatch) {
        super(source);
        this.frontendTotalOccurrence = Objects.requireNonNull(textSearch).getNumMatches();
        this.frontendIndexOccurrence = textSearch.getMatchIndex();
        this.storageTotalOccurrence = StringUtils.countMatches((CharSequence)textCollection.getAggregatedText(), (CharSequence)textSearch.getText());
        this.storageMatchSelection = false;
        this.storageLastMatchSelectionModifiable = false;
        if (textMatch != null) {
            this.storageMatchSelection = true;
            this.storageLastMatchSelectionModifiable = false;
            if (textMatch.getLastMatchingItem() != null) {
                this.storageLastMatchSelectionModifiable = textMatch.getLastMatchingItem().isModifiable();
            }
        }
    }

    @EventName
    public String buildName() {
        return "confluence.highlight.mark.detail";
    }

    public int getFrontendTotalOccurrence() {
        return this.frontendTotalOccurrence;
    }

    public int getFrontendIndexOccurrence() {
        return this.frontendIndexOccurrence;
    }

    public long getStorageTotalOccurrence() {
        return this.storageTotalOccurrence;
    }

    public boolean isStorageMatchSelection() {
        return this.storageMatchSelection;
    }

    public boolean isStorageLastMatchSelectionModifiable() {
        return this.storageLastMatchSelectionModifiable;
    }
}

