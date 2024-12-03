/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.core.migration.confluence.macros;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.google.common.base.Preconditions;
import java.util.Collection;

public final class BandanaMacroWhitelistXmlData {
    private final boolean isAllAllowed;
    private final Collection<WhitelistRule> acceptRules;

    public BandanaMacroWhitelistXmlData(boolean isAllAllowed, Collection<WhitelistRule> acceptRules) {
        this.isAllAllowed = isAllAllowed;
        this.acceptRules = (Collection)Preconditions.checkNotNull(acceptRules);
    }

    public boolean isAllAllowed() {
        return this.isAllAllowed;
    }

    public Collection<WhitelistRule> getRules() {
        return this.acceptRules;
    }

    public String toString() {
        return "BandanaMacroWhitelistXmlData{isAllAllowed=" + this.isAllAllowed + ", acceptRules=" + this.acceptRules + '}';
    }
}

