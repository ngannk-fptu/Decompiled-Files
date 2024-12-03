/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.servlet.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class ViewResolverComposite
implements ViewResolver,
Ordered,
InitializingBean,
ApplicationContextAware,
ServletContextAware {
    private final List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();
    private int order = Integer.MAX_VALUE;

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers.clear();
        if (!CollectionUtils.isEmpty(viewResolvers)) {
            this.viewResolvers.addAll(viewResolvers);
        }
    }

    public List<ViewResolver> getViewResolvers() {
        return Collections.unmodifiableList(this.viewResolvers);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (!(viewResolver instanceof ApplicationContextAware)) continue;
            ((ApplicationContextAware)viewResolver).setApplicationContext(applicationContext);
        }
    }

    public void setServletContext(ServletContext servletContext) {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (!(viewResolver instanceof ServletContextAware)) continue;
            ((ServletContextAware)viewResolver).setServletContext(servletContext);
        }
    }

    public void afterPropertiesSet() throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            if (!(viewResolver instanceof InitializingBean)) continue;
            ((InitializingBean)viewResolver).afterPropertiesSet();
        }
    }

    @Override
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view == null) continue;
            return view;
        }
        return null;
    }
}

