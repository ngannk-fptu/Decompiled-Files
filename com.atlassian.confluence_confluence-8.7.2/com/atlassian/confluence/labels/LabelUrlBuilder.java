/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.ObjectUtils
 */
package com.atlassian.confluence.labels;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.user.User;
import org.apache.commons.lang3.ObjectUtils;

@Internal
public class LabelUrlBuilder {
    private Long id;
    private String name;
    private String namespace;
    private String owner;
    private String currentSpaceKey;
    private User currentUser;

    private LabelUrlBuilder() {
    }

    public static LabelUrlBuilder builder() {
        return new LabelUrlBuilder();
    }

    public LabelUrlBuilder id(long id) {
        this.id = id;
        return this;
    }

    public LabelUrlBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LabelUrlBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public LabelUrlBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }

    public LabelUrlBuilder currentSpaceKey(String currentSpaceKey) {
        this.currentSpaceKey = currentSpaceKey;
        return this;
    }

    public LabelUrlBuilder currentUser(User currentUser) {
        this.currentUser = currentUser;
        return this;
    }

    public String buildDisplayUrl() {
        if (LabelUrlBuilder.isTeamLabel(this.namespace)) {
            return "/spacedirectory/view.action?selectedSpaceCategory=" + HtmlUtil.urlEncode(this.name);
        }
        String displayTitle = this.getDisplayTitle();
        if (GeneralUtil.isSafeTitleForUrl(displayTitle)) {
            return this.buildSafeUrl(displayTitle);
        }
        return this.buildUnsafeUrl();
    }

    private String buildUnsafeUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append("/labels/viewlabel.action?ids=");
        builder.append(this.id);
        if (ObjectUtils.isNotEmpty((Object)this.currentSpaceKey)) {
            builder.append("&key=");
            builder.append(HtmlUtil.urlEncode(this.currentSpaceKey));
        }
        return builder.toString();
    }

    private String buildSafeUrl(String displayTitle) {
        StringBuilder builder = new StringBuilder();
        builder.append("/label/");
        if (ObjectUtils.isNotEmpty((Object)this.currentSpaceKey)) {
            builder.append(HtmlUtil.urlEncode(this.currentSpaceKey));
            builder.append("/");
        }
        builder.append(HtmlUtil.urlEncode(displayTitle));
        return builder.toString();
    }

    private static boolean isTeamLabel(String namespace) {
        return Namespace.TEAM.getPrefix().equals(namespace);
    }

    private String getDisplayTitle() {
        return LabelParser.render(new ParsedLabelName(this.name, this.owner), this.currentUser);
    }
}

