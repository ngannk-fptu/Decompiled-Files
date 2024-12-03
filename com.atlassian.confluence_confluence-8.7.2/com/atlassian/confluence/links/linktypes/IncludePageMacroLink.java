/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.Link
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.renderer.links.Link;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncludePageMacroLink
extends Link {
    private boolean isValid = false;
    public static final Pattern pattern = Pattern.compile("\\{include:([^\\}\\{]+?)\\}");

    public IncludePageMacroLink(String linkText) {
        super(linkText);
        Matcher matcher = pattern.matcher(linkText);
        if (matcher.matches()) {
            this.title = matcher.group(1);
            this.isValid = true;
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public OutgoingLink toOutgoingLink(ContentEntityObject sourceContent) {
        OutgoingLink link = new OutgoingLink();
        link.setSourceContent(sourceContent);
        if (sourceContent instanceof SpaceContentEntityObject) {
            String spaceKey = ((SpaceContentEntityObject)sourceContent).getSpaceKey();
            link.setDestinationSpaceKey(spaceKey);
        }
        link.setDestinationPageTitle(this.getTitle());
        return link;
    }
}

