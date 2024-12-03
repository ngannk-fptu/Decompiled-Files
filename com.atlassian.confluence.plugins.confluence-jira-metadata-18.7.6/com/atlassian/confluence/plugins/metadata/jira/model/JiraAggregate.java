/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraAggregate
implements Serializable {
    private static final long serialVersionUID = 855746771787071985L;
    @XmlElement
    private int count;
    private String entityType;
    private String entityName;
    private String entityUrl;
    @XmlElement
    private boolean incomplete;

    public JiraAggregate(int count, String entityType, String entityName, String entityUrl, boolean incomplete) {
        this.count = count;
        this.entityType = entityType;
        this.entityName = entityName;
        this.entityUrl = entityUrl;
        this.incomplete = incomplete;
    }

    public JiraAggregate(int count, String entityType) {
        this(count, entityType, null, null, false);
    }

    public JiraAggregate(int count, boolean incomplete) {
        this(count, null, null, null, incomplete);
    }

    public int getCount() {
        return this.count;
    }

    public boolean isSingleEntity() {
        return this.count == 1;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getEntityUrl() {
        return this.entityUrl;
    }

    public boolean isIncomplete() {
        return this.incomplete;
    }
}

