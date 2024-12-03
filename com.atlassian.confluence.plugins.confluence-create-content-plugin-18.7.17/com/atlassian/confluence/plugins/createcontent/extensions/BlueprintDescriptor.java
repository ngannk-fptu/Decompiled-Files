/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;

public interface BlueprintDescriptor {
    @Deprecated
    public ModuleCompleteKey getContentTemplateKey();

    public ModuleCompleteKey getFirstContentTemplateKey();

    public List<ModuleCompleteKey> getContentTemplates();

    public ModuleCompleteKey getContentTemplateKey(String var1);

    public ModuleCompleteKey getIndexTemplate();

    public ModuleCompleteKey getBlueprintKey();

    public String getIndexKey();

    public String getCreateResult();

    public String getIndexTitleI18nKey();

    public String getHowToUseTemplate();

    public DialogWizard getDialogWizard();

    public boolean isIndexDisabled();
}

