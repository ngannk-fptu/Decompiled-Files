/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.plugins.dialog.wizard;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogManager;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;

public class DialogManagerImpl
implements DialogManager {
    private final PluginAccessor pluginAccessor;

    public DialogManagerImpl(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public DialogWizard getDialogWizardByKey(String dialogWizardKey) {
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(DialogWizardModuleDescriptor.class);
        for (DialogWizardModuleDescriptor descriptor : descriptors) {
            DialogWizard wizard = descriptor.getModule();
            if (!dialogWizardKey.equals(wizard.getKey())) continue;
            return wizard;
        }
        return null;
    }
}

