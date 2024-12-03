/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.tasklist.transformer.xml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.stream.XMLEventFactory;

public class ParsingContext {
    private final ConversionContext conversionContext;
    private final XMLEventFactory eventFactory;
    private long lastSequenceId;
    List<String> taskIds = Lists.newArrayList();

    public ParsingContext(ConversionContext conversionContext, XMLEventFactory eventFactory, long lastSequenceId) {
        this.conversionContext = conversionContext;
        this.eventFactory = eventFactory;
        this.lastSequenceId = lastSequenceId;
    }

    public String newTaskId() {
        long taskIdLong = ++this.lastSequenceId;
        return String.valueOf(taskIdLong);
    }

    public XMLEventFactory getEventFactory() {
        return this.eventFactory;
    }

    public long getLastSequenceId() {
        return this.lastSequenceId;
    }

    public boolean contains(String taskId) {
        return this.taskIds.contains(taskId);
    }

    public void add(String taskId) {
        this.taskIds.add(taskId);
    }
}

