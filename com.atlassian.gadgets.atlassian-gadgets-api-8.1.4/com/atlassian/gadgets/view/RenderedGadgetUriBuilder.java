/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.view;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.View;
import java.net.URI;

public interface RenderedGadgetUriBuilder {
    @Deprecated
    public URI build(GadgetState var1, View var2, GadgetRequestContext var3);

    public URI build(GadgetState var1, ModuleId var2, View var3, GadgetRequestContext var4);
}

