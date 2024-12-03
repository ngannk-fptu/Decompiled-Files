/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.web.servlet.view;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

public class InternalResourceViewResolver
extends UrlBasedViewResolver {
    private static final boolean jstlPresent = ClassUtils.isPresent((String)"javax.servlet.jsp.jstl.core.Config", (ClassLoader)InternalResourceViewResolver.class.getClassLoader());
    @Nullable
    private Boolean alwaysInclude;

    public InternalResourceViewResolver() {
        Class<Object> viewClass = this.requiredViewClass();
        if (InternalResourceView.class == viewClass && jstlPresent) {
            viewClass = JstlView.class;
        }
        this.setViewClass(viewClass);
    }

    public InternalResourceViewResolver(String prefix, String suffix) {
        this();
        this.setPrefix(prefix);
        this.setSuffix(suffix);
    }

    public void setAlwaysInclude(boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    @Override
    protected Class<?> requiredViewClass() {
        return InternalResourceView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == InternalResourceView.class ? new InternalResourceView() : (this.getViewClass() == JstlView.class ? new JstlView() : super.instantiateView());
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        InternalResourceView view = (InternalResourceView)super.buildView(viewName);
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude);
        }
        view.setPreventDispatchLoop(true);
        return view;
    }
}

