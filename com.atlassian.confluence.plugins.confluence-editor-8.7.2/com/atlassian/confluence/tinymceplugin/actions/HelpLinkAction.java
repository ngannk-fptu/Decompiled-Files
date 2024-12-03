/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.tinymceplugin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;

public class HelpLinkAction
extends ConfluenceActionSupport {
    private String linkNameKey;
    private String linkUrlKey;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public void setLinkNameKey(String linkNameKey) {
        this.linkNameKey = linkNameKey;
    }

    public void setLinkUrlKey(String linkUrlKey) {
        this.linkUrlKey = linkUrlKey;
    }

    @HtmlSafe
    public String getLinkUrlKey() {
        return HtmlUtil.urlEncode((String)this.linkUrlKey);
    }

    public String getLinkName() {
        if (StringUtils.isBlank((CharSequence)this.linkNameKey)) {
            return this.getText("help.name");
        }
        return this.getText(this.linkNameKey, this.linkNameKey);
    }

    public boolean isHelpAvailable() {
        return !StringUtils.isBlank((CharSequence)this.linkUrlKey);
    }
}

