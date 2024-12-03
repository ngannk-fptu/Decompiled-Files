/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tiles.request.render.Renderer
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.request.render.Renderer;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesView;

public class TilesViewResolver
extends UrlBasedViewResolver {
    @Nullable
    private Renderer renderer;
    @Nullable
    private Boolean alwaysInclude;

    public TilesViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void setAlwaysInclude(Boolean alwaysInclude) {
        this.alwaysInclude = alwaysInclude;
    }

    @Override
    protected Class<?> requiredViewClass() {
        return TilesView.class;
    }

    @Override
    protected AbstractUrlBasedView instantiateView() {
        return this.getViewClass() == TilesView.class ? new TilesView() : super.instantiateView();
    }

    @Override
    protected TilesView buildView(String viewName) throws Exception {
        TilesView view = (TilesView)super.buildView(viewName);
        if (this.renderer != null) {
            view.setRenderer(this.renderer);
        }
        if (this.alwaysInclude != null) {
            view.setAlwaysInclude(this.alwaysInclude);
        }
        return view;
    }
}

