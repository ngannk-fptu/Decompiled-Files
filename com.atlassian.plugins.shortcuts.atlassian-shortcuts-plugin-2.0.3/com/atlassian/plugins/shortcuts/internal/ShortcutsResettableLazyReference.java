/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.shortcuts.internal;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcut;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutModuleDescriptor;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutOperation;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.util.concurrent.ResettableLazyReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class ShortcutsResettableLazyReference
extends ResettableLazyReference<List<KeyboardShortcut>> {
    private final PluginAccessor pluginAccessor;
    private final HttpContext httpContext;

    public ShortcutsResettableLazyReference(PluginAccessor pluginAccessor, HttpContext httpContext) {
        this.pluginAccessor = pluginAccessor;
        this.httpContext = httpContext;
    }

    protected List<KeyboardShortcut> create() throws Exception {
        ArrayList<KeyboardShortcut> shortcuts = new ArrayList<KeyboardShortcut>();
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(KeyboardShortcutModuleDescriptor.class);
        HttpServletRequest request = this.httpContext.getRequest();
        for (KeyboardShortcutModuleDescriptor descriptor : descriptors) {
            URI uri;
            KeyboardShortcut original = descriptor.getModule();
            KeyboardShortcut.Builder shortcutBuilder = KeyboardShortcut.builder(original);
            KeyboardShortcutOperation operation = original.getOperation();
            if (operation.getType() == KeyboardShortcutOperation.OperationType.goTo && !(uri = new URI(operation.getParam())).isAbsolute() && !uri.isOpaque() && request != null) {
                shortcutBuilder.setOperationParam(request.getContextPath() + uri);
            }
            shortcuts.add(shortcutBuilder.build());
        }
        Collections.sort(shortcuts);
        return shortcuts;
    }
}

