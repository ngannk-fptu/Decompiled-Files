/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.LabelUtil;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class CombinedLabel
implements DisplayableLabel {
    public static final String ADD_SEPARATOR = "+";
    private String name;
    private String urlTail;
    private String realTitle;
    private boolean realTitleSafeForUrl;

    public CombinedLabel(List labels) {
        this.init(labels);
    }

    private void init(List labels) {
        this.realTitleSafeForUrl = true;
        this.name = "";
        this.realTitle = "";
        for (Label label : labels) {
            String title = label.getDisplayTitle();
            if (this.name.length() > 0) {
                this.name = this.name + ADD_SEPARATOR;
                this.realTitle = this.realTitle + ADD_SEPARATOR;
            }
            this.name = this.name + label.getName();
            this.realTitle = this.realTitle + title;
            if (GeneralUtil.isSafeTitleForUrl(title)) continue;
            this.realTitleSafeForUrl = false;
        }
        this.urlTail = this.realTitleSafeForUrl ? this.realTitle : "ids=" + LabelUtil.joinIds(labels, "&ids=");
    }

    @Override
    public boolean isRealTitleSafeForUrl() {
        return this.realTitleSafeForUrl;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUrlPath() {
        if (this.realTitleSafeForUrl) {
            return "/label/" + this.urlTail;
        }
        return "/labels/viewlabel.action?" + this.urlTail;
    }

    @Override
    public String getUrlPath(String spaceKey) {
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            return this.getUrlPath();
        }
        if (this.realTitleSafeForUrl) {
            return "/label/" + spaceKey + "/" + this.urlTail;
        }
        return "/labels/viewlabel.action?key=" + spaceKey + "&" + this.urlTail;
    }

    @Override
    public String getRealTitle() {
        return this.realTitle;
    }
}

