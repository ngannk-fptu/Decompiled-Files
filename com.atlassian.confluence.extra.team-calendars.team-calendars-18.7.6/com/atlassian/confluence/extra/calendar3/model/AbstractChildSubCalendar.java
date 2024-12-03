/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import javax.xml.bind.annotation.XmlElement;

public abstract class AbstractChildSubCalendar
extends LocallyManagedSubCalendar {
    private String id;
    private String creator;

    @Override
    @XmlElement
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    @XmlElement
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public abstract String getType();

    @Override
    @XmlElement
    public String getSpaceName() {
        PersistedSubCalendar parent = this.getParent();
        return parent != null ? parent.getSpaceName() : null;
    }

    @Override
    @XmlElement
    public String getSpaceKey() {
        PersistedSubCalendar parent = this.getParent();
        return parent != null ? parent.getSpaceKey() : null;
    }

    @Override
    @XmlElement
    public boolean isWatchable() {
        return true;
    }

    @Override
    @XmlElement
    public boolean isRestrictable() {
        return true;
    }

    @Override
    @XmlElement
    public boolean isEventInviteesSupported() {
        return true;
    }
}

