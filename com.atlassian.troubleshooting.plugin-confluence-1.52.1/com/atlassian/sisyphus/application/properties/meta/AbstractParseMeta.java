/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus.application.properties.meta;

import com.atlassian.sisyphus.application.properties.meta.ParseMeta;
import com.google.common.collect.Lists;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractParseMeta
implements ParseMeta {
    private static final Logger log = LoggerFactory.getLogger(AbstractParseMeta.class);
    protected Map<String, List<String>> pathMap = new LinkedHashMap<String, List<String>>();
    protected String propertyTitle;
    protected List<String> groupNode = Lists.newArrayList();

    protected abstract void fillParseData();

    protected abstract void setGroupNode();

    @Override
    public Map<String, List<String>> getPathMap() {
        return this.pathMap;
    }

    @Override
    public String getTitle() {
        return this.propertyTitle;
    }

    @Override
    public List<String> getGroupNode() {
        return this.groupNode;
    }

    protected AbstractParseMeta(String propertyTitle) {
        this.propertyTitle = propertyTitle;
        this.fillParseData();
        this.setGroupNode();
    }
}

