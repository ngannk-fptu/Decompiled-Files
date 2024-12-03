/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.gadgets.view.GadgetRenderingException
 *  com.atlassian.gadgets.view.GadgetViewFactory
 *  com.atlassian.gadgets.view.ModuleId
 *  com.atlassian.gadgets.view.RenderedGadgetUriBuilder
 *  com.atlassian.gadgets.view.View
 *  com.atlassian.gadgets.view.ViewComponent
 *  com.atlassian.gadgets.view.ViewType
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.embedded.internal;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.embedded.internal.GadgetViewComponent;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.gadgets.view.GadgetRenderingException;
import com.atlassian.gadgets.view.GadgetViewFactory;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.RenderedGadgetUriBuilder;
import com.atlassian.gadgets.view.View;
import com.atlassian.gadgets.view.ViewComponent;
import com.atlassian.gadgets.view.ViewType;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class GadgetViewFactoryImpl
implements GadgetViewFactory {
    private final RenderedGadgetUriBuilder renderedUriBuilder;
    private final GadgetSpecFactory specFactory;

    @Autowired
    public GadgetViewFactoryImpl(@ComponentImport GadgetSpecFactory specFactory, RenderedGadgetUriBuilder renderedUriBuilder) {
        this.specFactory = specFactory;
        this.renderedUriBuilder = renderedUriBuilder;
    }

    @Deprecated
    public ViewComponent createGadgetView(GadgetState state, View view, GadgetRequestContext gadgetRequestContext) throws GadgetParsingException, GadgetRenderingException {
        return this.createGadgetView(state, ModuleId.valueOf((String)state.getId().value()), view, gadgetRequestContext);
    }

    public ViewComponent createGadgetView(GadgetState state, ModuleId moduleId, View view, GadgetRequestContext gadgetRequestContext) throws GadgetParsingException, GadgetRenderingException {
        GadgetSpec spec = this.fetchGadgetSpec(state, gadgetRequestContext);
        if (!this.canRenderInViewType(spec, view.getViewType())) {
            throw new GadgetRenderingException("Gadget does not define a '" + view.getViewType() + "' view", state);
        }
        String renderedUrl = this.renderedUriBuilder.build(state, moduleId, view, gadgetRequestContext).toString();
        return new GadgetViewComponent(moduleId, view.getViewType(), spec, renderedUrl);
    }

    public boolean canRenderInViewType(GadgetState state, ViewType viewType, GadgetRequestContext gadgetRequestContext) {
        return this.canRenderInViewType(this.fetchGadgetSpec(state, gadgetRequestContext), viewType);
    }

    private boolean canRenderInViewType(GadgetSpec spec, ViewType viewType) {
        return spec.supportsViewType(viewType);
    }

    private GadgetSpec fetchGadgetSpec(GadgetState state, GadgetRequestContext gadgetRequestContext) {
        return this.specFactory.getGadgetSpec(state, gadgetRequestContext);
    }
}

