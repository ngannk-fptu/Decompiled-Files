/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 */
package com.atlassian.confluence.tinymceplugin.events;

import com.atlassian.confluence.pages.AbstractPage;

public class LayoutCreatedEvent {
    private String layoutType;
    private AbstractPage page;

    public LayoutCreatedEvent(String layoutType, AbstractPage page) {
        this.page = page;
        this.layoutType = layoutType;
    }

    public String getLayoutType() {
        return this.layoutType;
    }

    public AbstractPage getPage() {
        return this.page;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LayoutCreatedEvent that = (LayoutCreatedEvent)o;
        if (this.layoutType != null ? !this.layoutType.equals(that.layoutType) : that.layoutType != null) {
            return false;
        }
        return !(this.page != null ? !this.page.equals((Object)that.page) : that.page != null);
    }

    public String toString() {
        return "LayoutCreatedEvent{layoutType='" + this.layoutType + "', page=" + this.page + "}";
    }

    public int hashCode() {
        int result = this.layoutType != null ? this.layoutType.hashCode() : 0;
        result = 31 * result + (this.page != null ? this.page.hashCode() : 0);
        return result;
    }
}

