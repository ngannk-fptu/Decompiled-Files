/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.view;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.view.GadgetRenderingException;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.View;
import com.atlassian.gadgets.view.ViewComponent;
import com.atlassian.gadgets.view.ViewType;

public interface GadgetViewFactory {
    @Deprecated
    public ViewComponent createGadgetView(GadgetState var1, View var2, GadgetRequestContext var3) throws GadgetParsingException, GadgetRenderingException;

    public ViewComponent createGadgetView(GadgetState var1, ModuleId var2, View var3, GadgetRequestContext var4) throws GadgetParsingException, GadgetRenderingException;

    public boolean canRenderInViewType(GadgetState var1, ViewType var2, GadgetRequestContext var3) throws GadgetParsingException;
}

