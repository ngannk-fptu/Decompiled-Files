/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service;

import java.util.List;
import javax.xml.stream.events.XMLEvent;

public interface ContentRewriter {
    public List<XMLEvent> updateLinkEvents(List<XMLEvent> var1);
}

