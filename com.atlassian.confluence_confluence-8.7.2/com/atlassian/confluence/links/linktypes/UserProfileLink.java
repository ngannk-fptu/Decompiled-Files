/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.linktypes.AbstractContentEntityLink;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.links.GenericLinkParser;
import java.text.ParseException;
import java.util.Collections;

public class UserProfileLink
extends AbstractContentEntityLink {
    public static final String USER_ICON = "user";
    public static final String PERSONAL_SPACE_USER_ICON = "personal_space_user";
    private ConfluenceUser user;
    private PersonalInformation info;

    public static String getLinkPath(String username) {
        return "/display/~" + HtmlUtil.urlEncode(username);
    }

    public UserProfileLink(GenericLinkParser parser, ConfluenceUserResolver userResolver, PersonalInformationManager personalInformationManager) throws ParseException {
        super(parser);
        this.iconName = USER_ICON;
        if (parser.getDestinationTitle().length() == 1) {
            throw new ParseException("No username supplied", 0);
        }
        StringBuilder buf = new StringBuilder(parser.getDestinationTitle().substring(1));
        if (buf.charAt(buf.length() - 1) == '/') {
            buf.deleteCharAt(buf.length() - 1);
        }
        this.user = userResolver.getUserByName(buf.toString());
        if (this.user != null) {
            this.info = personalInformationManager.getOrCreatePersonalInformation(this.user);
            if ("true".equals(this.info.getHasPersonalSpace().toLowerCase())) {
                this.iconName = PERSONAL_SPACE_USER_ICON;
            }
            this.url = UserProfileLink.getLinkPath(this.user.getName());
            this.setI18nTitle("renderer.view.profile", Collections.singletonList(this.user.getFullName()));
            if (this.linkBody.equalsIgnoreCase(parser.getDestinationTitle())) {
                this.linkBody = this.user.getFullName();
            }
        }
    }

    @Override
    public ContentEntityObject getDestinationContent() {
        return this.info;
    }

    @Override
    public boolean hasDestination() {
        return this.user != null;
    }

    public String getLinkAttributes() {
        return " class=\"confluence-userlink user-mention\" data-username=\"" + HtmlUtil.htmlEncode(this.user.getName()) + "\" " + super.getLinkAttributes();
    }
}

