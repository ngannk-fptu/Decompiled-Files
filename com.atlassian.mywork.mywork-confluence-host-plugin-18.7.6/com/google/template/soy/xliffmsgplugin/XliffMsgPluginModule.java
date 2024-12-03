/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.xliffmsgplugin;

import com.google.inject.AbstractModule;
import com.google.template.soy.msgs.SoyMsgPlugin;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;

public class XliffMsgPluginModule
extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(SoyMsgPlugin.class).to(XliffMsgPlugin.class);
    }
}

