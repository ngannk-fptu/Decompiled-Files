/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataItem;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public abstract class JiraMetadataGroup {
    @XmlElement
    protected Type type;
    @XmlElement
    protected List<JiraMetadataItem> items;

    protected JiraMetadataGroup(Type type, List<JiraMetadataItem> items) {
        this.type = type;
        this.items = items;
    }

    public Type getType() {
        return this.type;
    }

    public List<JiraMetadataItem> getItems() {
        return this.items;
    }

    public static enum Type {
        VERSIONS(Integer.MAX_VALUE),
        SPRINTS(Integer.MAX_VALUE),
        EPICS(3),
        ISSUES(5);

        int maxItemsToDisplay;

        private Type(int maxItemsToDisplay) {
            this.maxItemsToDisplay = maxItemsToDisplay;
        }

        public int getMaxItemsToDisplay() {
            return this.maxItemsToDisplay;
        }
    }
}

