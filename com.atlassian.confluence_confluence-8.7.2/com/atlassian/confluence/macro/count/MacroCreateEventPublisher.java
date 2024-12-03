/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.RenderingEventPublisher;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.count.MacroCount;
import com.atlassian.confluence.macro.count.MacroCountEventFactory;
import com.atlassian.confluence.macro.count.MacroCountStore;
import com.atlassian.confluence.macro.count.MacroCounter;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroCreateEventPublisher
implements RenderingEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(MacroCreateEventPublisher.class);
    private static final String VERSION_FLAG = "macro-create-events-published-for-version";
    private final EventPublisher eventPublisher;
    private final MacroCountStore macroCountStore;

    public MacroCreateEventPublisher(EventPublisher eventPublisher, MacroCountStore macroCountStore) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.macroCountStore = (MacroCountStore)Preconditions.checkNotNull((Object)macroCountStore);
    }

    @Override
    public void publish(Object src, ConversionContext conversionContext) {
        try {
            MacroCounter macroCounter = (MacroCounter)conversionContext.getProperty("macroCounter");
            ContentEntityObject entity = conversionContext.getEntity();
            if (macroCounter != null && entity != null && !this.alreadyPublishedMacroCreateEvents(entity)) {
                for (MacroCount count : this.macroCountStore.updateAndGetNewCounts(macroCounter, entity, this.getVersion(entity))) {
                    this.eventPublisher.publish((Object)MacroCountEventFactory.newMacroCreateCountEvent(src, entity, conversionContext, count));
                }
                this.publishedMacroCreateEvents(entity);
            }
        }
        catch (Exception e) {
            log.debug("Unable to finish publishing macro create events.", (Throwable)e);
        }
    }

    private boolean alreadyPublishedMacroCreateEvents(ContentEntityObject entity) {
        return entity.getProperties().getLongProperty(VERSION_FLAG, -1L) >= (long)this.getVersion(entity);
    }

    private void publishedMacroCreateEvents(ContentEntityObject entity) {
        entity.getProperties().setLongProperty(VERSION_FLAG, this.getVersion(entity));
    }

    private int getVersion(ContentEntityObject entity) {
        return entity instanceof Draft ? 0 : entity.getVersion();
    }
}

