/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.macro.browser.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.macro.browser.MacroBrowserManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.web.filter.CachingHeaders;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

@RequiresAnyConfluenceAccess
public class BrowseMacrosAction
extends ConfluenceActionSupport
implements Beanable {
    protected Map<String, Object> bean = new HashMap<String, Object>();
    private MacroBrowserManager macroBrowserManager;
    private String whitelist;
    private boolean isDetailed = true;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.bean.put("categories", this.macroBrowserManager.getMacroCategories());
        if (this.isDetailed) {
            Set<MacroMetadata> macros = this.getMacros();
            this.bean.put("macros", macros);
            this.addCacheHeader(macros.size());
        } else {
            Set<MacroSummary> summaries = this.getSummaries();
            this.bean.put("macros", summaries);
            this.addCacheHeader(summaries.size());
        }
        this.bean.put("title", this.getText("macro.browser.title"));
        this.bean.put("insertTitle", this.getText("macro.browser.insert.macro.title"));
        this.bean.put("editTitle", this.getText("macro.browser.edit.macro.title"));
        return super.execute();
    }

    private void addCacheHeader(int macroCount) {
        if (macroCount > 0) {
            CachingHeaders.PUBLIC_LONG_TERM.apply(ServletActionContext.getResponse());
        }
    }

    private Set<MacroMetadata> getMacros() {
        if (StringUtils.isNotBlank((CharSequence)this.whitelist)) {
            return this.macroBrowserManager.getMacroMetadata(this.parseWhitelist());
        }
        return this.macroBrowserManager.getMacroMetadata();
    }

    private Set<MacroSummary> getSummaries() {
        return this.macroBrowserManager.getMacroSummaries();
    }

    private List<String> parseWhitelist() {
        return ImmutableList.copyOf((Iterable)Splitter.on((String)",").trimResults().split((CharSequence)this.whitelist));
    }

    @Override
    public Map<String, Object> getBean() {
        return this.bean;
    }

    public void setMacroBrowserManager(MacroBrowserManager macroBrowserManager) {
        this.macroBrowserManager = macroBrowserManager;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public void setDetailed(boolean isDetailed) {
        this.isDetailed = isDetailed;
    }
}

