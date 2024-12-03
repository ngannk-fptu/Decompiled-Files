/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class WebdavSettings
implements Serializable {
    private Set<String> excludedClientUserAgentRegexes = new LinkedHashSet<String>();
    private boolean contentExportsResourceEnabled;
    private boolean contentVersionsResourceEnabled;
    private boolean contentUrlResourceEnabled;
    private boolean strictPageResourcePathCheckingDisabled;

    public WebdavSettings() {
        this(null);
    }

    public WebdavSettings(WebdavSettings webdavSettings) {
        this.setContentExportsResourceEnabled(true);
        this.setContentVersionsResourceEnabled(true);
        this.setContentUrlResourceEnabled(true);
        this.setStrictPageResourcePathCheckingDisabled(false);
        if (null != webdavSettings) {
            this.setExcludedClientUserAgentRegexes(webdavSettings.getExcludedClientUserAgentRegexes());
            this.setContentExportsResourceEnabled(webdavSettings.isContentExportsResourceEnabled());
            this.setContentVersionsResourceEnabled(webdavSettings.isContentVersionsResourceEnabled());
            this.setContentUrlResourceEnabled(webdavSettings.isContentUrlResourceEnabled());
            this.setStrictPageResourcePathCheckingDisabled(webdavSettings.isStrictPageResourcePathCheckingDisabled());
        }
    }

    public Set<String> getExcludedClientUserAgentRegexes() {
        return Collections.unmodifiableSet(this.excludedClientUserAgentRegexes);
    }

    public void setExcludedClientUserAgentRegexes(Collection<String> regexes) {
        this.excludedClientUserAgentRegexes.clear();
        this.excludedClientUserAgentRegexes.addAll(regexes);
    }

    public boolean isContentExportsResourceEnabled() {
        return this.contentExportsResourceEnabled;
    }

    public void setContentExportsResourceEnabled(boolean exportsDirectoryEnabled) {
        this.contentExportsResourceEnabled = exportsDirectoryEnabled;
    }

    public boolean isContentVersionsResourceEnabled() {
        return this.contentVersionsResourceEnabled;
    }

    public void setContentVersionsResourceEnabled(boolean contentVersionsResourceEnabled) {
        this.contentVersionsResourceEnabled = contentVersionsResourceEnabled;
    }

    public boolean isContentUrlResourceEnabled() {
        return this.contentUrlResourceEnabled;
    }

    public void setContentUrlResourceEnabled(boolean contentUrlResourceEnabled) {
        this.contentUrlResourceEnabled = contentUrlResourceEnabled;
    }

    public boolean isStrictPageResourcePathCheckingDisabled() {
        return this.strictPageResourcePathCheckingDisabled;
    }

    public void setStrictPageResourcePathCheckingDisabled(boolean strictPageResourcePathCheckingDisabled) {
        this.strictPageResourcePathCheckingDisabled = strictPageResourcePathCheckingDisabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WebdavSettings that = (WebdavSettings)o;
        if (this.contentExportsResourceEnabled != that.contentExportsResourceEnabled) {
            return false;
        }
        if (this.contentUrlResourceEnabled != that.contentUrlResourceEnabled) {
            return false;
        }
        if (this.contentVersionsResourceEnabled != that.contentVersionsResourceEnabled) {
            return false;
        }
        if (this.strictPageResourcePathCheckingDisabled != that.strictPageResourcePathCheckingDisabled) {
            return false;
        }
        return Objects.equals(this.excludedClientUserAgentRegexes, that.excludedClientUserAgentRegexes);
    }

    public int hashCode() {
        return Objects.hash(this.excludedClientUserAgentRegexes, this.contentExportsResourceEnabled, this.contentVersionsResourceEnabled, this.contentUrlResourceEnabled, this.strictPageResourcePathCheckingDisabled);
    }
}

