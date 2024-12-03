/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper
 *  com.opensymphony.module.sitemesh.mapper.DefaultDecorator
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.sitemesh;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.velocity.DecoratorName;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeContext;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.velocity.VelocityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSpaceDecoratorMapper
extends AbstractDecoratorMapper {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSpaceDecoratorMapper.class);
    private static final Decorator HACK_NULL_DECORATOR = new DefaultDecorator(null, null, null);
    private final Supplier<ThemeManager> themeManager;
    private final Supplier<VelocityManager> velocityManager;

    public ConfluenceSpaceDecoratorMapper(ThemeManager themeManager, VelocityManager velocityManager) {
        this.themeManager = () -> themeManager;
        this.velocityManager = () -> velocityManager;
    }

    @Deprecated
    public ConfluenceSpaceDecoratorMapper() {
        this.themeManager = new LazyComponentReference("themeManager");
        this.velocityManager = () -> (VelocityManager)BootstrapUtils.getBootstrapContext().getBean("velocityManager", VelocityManager.class);
    }

    public Decorator getNamedDecorator(HttpServletRequest request, String decoratorName) {
        Decorator parentDecorator = super.getNamedDecorator(request, decoratorName);
        Decorator decorator = this.getDecorator(parentDecorator, request);
        if (decorator == null || decorator.getName().equals(decoratorName)) {
            return decorator;
        }
        return parentDecorator;
    }

    public Decorator getDecorator(HttpServletRequest httpServletRequest, Page page) {
        Decorator parentDecorator = this.parent.getDecorator(httpServletRequest, page);
        return this.getDecorator(parentDecorator, httpServletRequest);
    }

    private Decorator getDecorator(Decorator parentDecorator, HttpServletRequest request) {
        if (!StringUtils.isNotEmpty((CharSequence)parentDecorator.getPage())) {
            return parentDecorator;
        }
        ThemeContext themeContext = ThemeContext.get((ServletRequest)request);
        Decorator decorator = null;
        if (themeContext.hasSpaceTheme()) {
            decorator = this.getThemeDecorator(themeContext.getSpaceTheme(), parentDecorator, request);
        }
        if (decorator == null) {
            decorator = this.getSpaceCustomDecorator(parentDecorator, themeContext.getSpaceKey());
        }
        if (decorator == null && themeContext.hasGlobalTheme()) {
            decorator = this.getThemeDecorator(themeContext.getGlobalTheme(), parentDecorator, request);
        }
        if (decorator == null && this.getThemeManager() != null) {
            decorator = this.getThemeDecorator(this.getThemeManager().getGlobalTheme(), parentDecorator, request);
        }
        if (decorator == null) {
            decorator = parentDecorator;
        }
        return decorator == HACK_NULL_DECORATOR ? null : decorator;
    }

    private Decorator getSpaceCustomDecorator(Decorator parentDecorator, String spaceKey) {
        String decoratorPage = parentDecorator.getPage();
        String spaceDecoratorPage = new DecoratorName(spaceKey, decoratorPage).getSource();
        DefaultDecorator spaceCustomDecorator = null;
        if (!Space.isValidSpaceKey(spaceKey)) {
            return null;
        }
        if (!((VelocityManager)this.velocityManager.get()).getVelocityEngine().resourceExists(spaceDecoratorPage)) {
            return null;
        }
        try {
            spaceCustomDecorator = new DefaultDecorator(parentDecorator.getName(), spaceDecoratorPage, null);
        }
        catch (Exception e) {
            log.error("Error retrieving space decorator:" + spaceDecoratorPage, (Throwable)e);
        }
        return spaceCustomDecorator;
    }

    private Decorator getThemeDecorator(Theme theme, Decorator parentDecorator, HttpServletRequest request) {
        if (theme == null) {
            return null;
        }
        if (theme.isDisableSitemesh()) {
            return HACK_NULL_DECORATOR;
        }
        ThemedDecorator themedDecorator = theme.getDecorator(parentDecorator.getPage());
        if (themedDecorator == null) {
            return null;
        }
        request.setAttribute("theme.resource.path", (Object)("/download/resources/" + HtmlUtil.urlEncode(themedDecorator.getResourceKey()) + "/"));
        return themedDecorator.getDecorator(parentDecorator);
    }

    private ThemeManager getThemeManager() {
        return (ThemeManager)this.themeManager.get();
    }
}

