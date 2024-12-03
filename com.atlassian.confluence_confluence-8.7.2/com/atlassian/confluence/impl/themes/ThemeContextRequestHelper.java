/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  javax.servlet.ServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.themes;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeContext;
import com.atlassian.confluence.themes.ThemeManager;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ThemeContextRequestHelper {
    private final ThemeManager themeManager;
    private final SpaceManager spaceManager;

    public ThemeContextRequestHelper(ThemeManager themeManager, SpaceManager spaceManager) {
        this.themeManager = Objects.requireNonNull(themeManager);
        this.spaceManager = Objects.requireNonNull(spaceManager);
    }

    public void initThemeContext(Object action, ServletRequest servletRequest) {
        Optional<Space> space = this.getSpace(action, servletRequest);
        ThemeContext.set(servletRequest, space.orElse(null), this.getSpaceTheme(space).orElse(null), this.themeManager.getGlobalTheme());
    }

    private Optional<Theme> getSpaceTheme(Optional<Space> space) {
        return space.map(Space::getKey).map(this.themeManager::getSpaceTheme);
    }

    private @NonNull Optional<Space> getSpace(@Nullable Object action, ServletRequest request) {
        if (action instanceof Spaced) {
            return Optional.ofNullable(((Spaced)action).getSpace());
        }
        if (action instanceof PageAware) {
            return ThemeContextRequestHelper.getSpaceFromPageAware((PageAware)action);
        }
        return this.getSpaceFromRequestParameters(request);
    }

    private Optional<Space> getSpaceFromRequestParameters(ServletRequest request) {
        Set paramNames = request.getParameterMap().keySet();
        if (paramNames.contains("key")) {
            return Optional.ofNullable(this.spaceManager.getSpace(request.getParameter("key")));
        }
        if (paramNames.contains("spaceKey")) {
            return Optional.ofNullable(this.spaceManager.getSpace(request.getParameter("spaceKey")));
        }
        if (paramNames.contains("pageId")) {
            return this.getSpaceFromPageId(request.getParameter("pageId"));
        }
        return Optional.empty();
    }

    private static Optional<Space> getSpaceFromPageAware(PageAware action) {
        return Optional.ofNullable(action.getPage()).map(AbstractPage::getLatestVersion).map(SpaceContentEntityObject::getSpace);
    }

    private Optional<Space> getSpaceFromPageId(String pageId) {
        return ThemeContextRequestHelper.parseId(pageId).map(this.spaceManager::getSpaceFromPageId).map(this.spaceManager::getSpace);
    }

    private static Optional<Long> parseId(String pageId) {
        return StringUtils.isNumeric((CharSequence)pageId) ? Optional.of(Long.valueOf(pageId)) : Optional.empty();
    }
}

