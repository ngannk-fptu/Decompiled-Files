/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.plugin.copyspace.context.ContentRewriterContext;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContentRewriter;
import java.util.List;
import javax.xml.stream.events.XMLEvent;

public abstract class AbstractChainedContentRewriter
implements ContentRewriter {
    private final ContentRewriter nextContentRewriter;
    protected final ContentRewriterContext contentRewriterContext;
    protected final CopySpaceContext copySpaceContext;

    public AbstractChainedContentRewriter(ContentRewriter nextContentRewriter, ContentRewriterContext contentRewriterContext, CopySpaceContext copySpaceContext) {
        this.nextContentRewriter = nextContentRewriter;
        this.contentRewriterContext = contentRewriterContext;
        this.copySpaceContext = copySpaceContext;
    }

    protected abstract List<XMLEvent> updateLinkEventsInternal(List<XMLEvent> var1);

    @Override
    public List<XMLEvent> updateLinkEvents(List<XMLEvent> xmlEvents) {
        List<XMLEvent> updatedEvents = this.updateLinkEventsInternal(xmlEvents);
        if (this.nextContentRewriter != null) {
            updatedEvents = this.nextContentRewriter.updateLinkEvents(updatedEvents);
        }
        return updatedEvents;
    }
}

