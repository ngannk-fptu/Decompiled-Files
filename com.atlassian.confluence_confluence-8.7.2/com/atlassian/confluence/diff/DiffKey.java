/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Draft;
import java.io.Serializable;
import java.util.Locale;

class DiffKey
implements Serializable {
    private final long originalId;
    private final long revisedId;
    private final int originalVersion;
    private final int revisedVersion;
    private final Locale locale;
    private final boolean containsDraft;

    public DiffKey(ContentEntityObject original, ContentEntityObject revised, Locale locale) {
        this.locale = locale;
        this.originalId = original.getId();
        this.revisedId = revised.getId();
        this.originalVersion = original.getVersion();
        this.revisedVersion = revised.getVersion();
        this.containsDraft = original instanceof Draft || revised instanceof Draft || original.isDraft() || revised.isDraft();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DiffKey diffKey = (DiffKey)o;
        if (this.originalId != diffKey.originalId) {
            return false;
        }
        if (this.originalVersion != diffKey.originalVersion) {
            return false;
        }
        if (this.revisedId != diffKey.revisedId) {
            return false;
        }
        if (this.revisedVersion != diffKey.revisedVersion) {
            return false;
        }
        return !(this.locale != null ? !this.locale.equals(diffKey.locale) : diffKey.locale != null);
    }

    public int hashCode() {
        int result = (int)(this.originalId ^ this.originalId >>> 32);
        result = 31 * result + (int)(this.revisedId ^ this.revisedId >>> 32);
        result = 31 * result + this.originalVersion;
        result = 31 * result + this.revisedVersion;
        result = 31 * result + (this.locale != null ? this.locale.hashCode() : 0);
        return result;
    }

    public boolean isCacheable() {
        return this.originalId != 0L && this.revisedId != 0L && !this.containsDraft;
    }
}

