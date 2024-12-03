/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.util.HtmlUtil
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.pagetree;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.HtmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

public class PageTreeSearchAction
extends ConfluenceActionSupport {
    private String queryString;
    private String ancestorId;
    private String searchActionString;
    private String spaceKey;

    public String execute() throws Exception {
        StringBuilder actionString = new StringBuilder("/dosearchsite.action?searchQuery.queryString=");
        ArrayList<Object> searchTerms = new ArrayList<Object>();
        if (this.ancestorId != null && this.ancestorId.length() > 0) {
            searchTerms.add("ancestorIds%3A" + this.ancestorId);
        }
        if (this.queryString != null && this.queryString.length() > 0) {
            searchTerms.add(HtmlUtil.urlEncode((String)this.queryString));
        }
        Iterator iter = searchTerms.iterator();
        while (iter.hasNext()) {
            String searchTerm = (String)iter.next();
            actionString.append(searchTerm);
            if (!iter.hasNext()) continue;
            actionString.append("+AND+");
        }
        actionString.append("&searchQuery.spaceKey=");
        if (StringUtils.isNotEmpty((CharSequence)this.spaceKey)) {
            actionString.append(HtmlUtil.urlEncode((String)this.spaceKey));
        } else {
            actionString.append(HtmlUtil.urlEncode((String)"conf_all"));
        }
        this.searchActionString = actionString.toString();
        return "search";
    }

    public String getAncestorId() {
        return this.ancestorId;
    }

    public void setAncestorId(String ancestorId) {
        this.ancestorId = ancestorId;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getSearchActionString() {
        return this.searchActionString;
    }

    public void setSearchActionString(String searchActionString) {
        this.searchActionString = searchActionString;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

