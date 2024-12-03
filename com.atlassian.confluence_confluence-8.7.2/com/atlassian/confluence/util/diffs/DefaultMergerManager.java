/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.util.diffs;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.util.diffs.Merger;
import com.atlassian.confluence.util.diffs.MergerManager;
import com.atlassian.confluence.util.diffs.PageLayoutAwareMerger;
import com.atlassian.confluence.util.diffs.SimpleMergeResult;
import com.atlassian.plugin.PluginAccessor;
import javax.xml.stream.XMLOutputFactory;

public class DefaultMergerManager
implements MergerManager {
    private final PluginAccessor pluginAccessor;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLOutputFactory xmlOutputFactory;

    public DefaultMergerManager(PluginAccessor pluginAccessor, XmlEventReaderFactory xmlEventReaderFactory, XMLOutputFactory xmlOutputFactory) {
        this.pluginAccessor = pluginAccessor;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Merger getMerger() {
        return this.pluginAccessor.getEnabledModulesByClass(Merger.class).stream().findFirst().map(this::wrap).orElseGet(this::defaultMerger);
    }

    private Merger wrap(Merger merger) {
        return new PageLayoutAwareMerger(merger, this.xmlEventReaderFactory, this.xmlOutputFactory);
    }

    private Merger defaultMerger() {
        return (base, left, right) -> SimpleMergeResult.FAIL_MERGE_RESULT;
    }
}

