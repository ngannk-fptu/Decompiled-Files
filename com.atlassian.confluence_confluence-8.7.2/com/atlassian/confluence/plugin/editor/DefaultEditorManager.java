/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.plugin.editor;

import com.atlassian.confluence.plugin.descriptor.EditorModuleDescriptor;
import com.atlassian.confluence.plugin.editor.Editor;
import com.atlassian.confluence.plugin.editor.EditorManager;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;

public class DefaultEditorManager
implements EditorManager {
    private PluginAccessor pluginAccessor;

    @Override
    public Editor getCurrentEditor() {
        EditorModuleDescriptor descriptor = this.getEditorModule();
        if (descriptor == null) {
            return null;
        }
        return descriptor.getModule();
    }

    @Override
    public String getCurrentEditorVersion() {
        EditorModuleDescriptor descriptor = this.getEditorModule();
        if (descriptor == null) {
            return "";
        }
        return descriptor.getPlugin().getPluginInformation().getVersion();
    }

    private EditorModuleDescriptor getEditorModule() {
        List editorModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(EditorModuleDescriptor.class);
        if (editorModuleDescriptors == null || editorModuleDescriptors.isEmpty()) {
            return null;
        }
        return (EditorModuleDescriptor)((Object)editorModuleDescriptors.get(0));
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }
}

