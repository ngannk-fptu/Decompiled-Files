/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.links.GenericLinkParser;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

public class PageCreateLink
extends AbstractPageLink {
    public static final String CREATE_ICON = "create";

    public PageCreateLink(GenericLinkParser parser, PageContext context) throws ParseException {
        super(parser, context);
        this.iconName = CREATE_ICON;
        if (StringUtils.isNotEmpty((CharSequence)context.getSpaceKey()) && this.spaceKey.equals(context.getSpaceKey())) {
            this.setI18nTitle("renderer.create.page", Collections.singletonList(this.entityName));
        } else {
            this.setI18nTitle("renderer.create.page.in.space", Arrays.asList(this.spaceKey, this.entityName));
        }
        this.makeCreatePage(context);
    }

    public String getLinkAttributes() {
        return " class=\"createlink\"";
    }

    private void makeCreatePage(PageContext context) {
        this.url = "/pages/createpage.action?spaceKey=" + this.spaceKey + this.makeTitle(this.entityName);
        if (context.getEntity() != null && context.getEntity() instanceof Page && this.spaceKey.equalsIgnoreCase(context.getSpaceKey())) {
            this.url = this.url + "&linkCreation=true&fromPageId=" + context.getEntity().getId();
        }
    }

    private String makeTitle(String entityTitle) {
        if (GeneralUtil.isAllAscii(entityTitle)) {
            return "&title=" + HtmlUtil.urlEncode(entityTitle);
        }
        return "&encodedTitle=" + GeneralUtil.base64Encode(entityTitle);
    }

    @Override
    protected boolean isOnSamePage(PageContext pageContext) {
        return false;
    }
}

