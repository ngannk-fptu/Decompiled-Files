/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.legacyapi.model.SpaceSummary
 *  com.atlassian.confluence.legacyapi.service.Expansions
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.builder;

import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.legacyapi.model.SpaceSummary;
import com.atlassian.confluence.legacyapi.service.Expansions;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LegacySpaceSummaryBuilder {
    SpaceLogoManager spaceLogoManager;
    private static final int DEFAULT_ICON_HEIGHT = 48;
    private static final int DEFAULT_ICON_WIDTH = 48;

    @Autowired
    public LegacySpaceSummaryBuilder(@ComponentImport SpaceLogoManager spaceLogoManager) {
        this.spaceLogoManager = spaceLogoManager;
    }

    public SpaceSummary buildFrom(Space space, Expansions expansions) {
        Option<Icon> iconIfExpanded = this.getIconIfExpanded(space, expansions);
        Option<String> descriptionIfExpanded = this.getDescriptionIfExpanded(space, expansions);
        return new SpaceSummary(space.getId(), space.getKey(), space.getName(), iconIfExpanded, descriptionIfExpanded);
    }

    private Option<Icon> getIconIfExpanded(Space space, Expansions expansions) {
        if (expansions.canExpand("icon")) {
            String path = this.spaceLogoManager.getLogoDownloadPath(space, (User)AuthenticatedUserThreadLocal.get());
            return Option.some((Object)new Icon(path, 48, 48, false));
        }
        return Option.none();
    }

    private Option<String> getDescriptionIfExpanded(Space space, Expansions expansions) {
        if (expansions.canExpand("description")) {
            return Option.some((Object)space.getDescription().getBodyAsString());
        }
        return Option.none();
    }
}

