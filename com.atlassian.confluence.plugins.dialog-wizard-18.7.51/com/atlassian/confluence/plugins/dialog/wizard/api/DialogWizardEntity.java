/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogPageEntity;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizard;
import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardPage;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DialogWizardEntity {
    @XmlElement
    private List<DialogPageEntity> pages;

    @Nonnull
    List<DialogPageEntity> getPages() {
        return this.pages;
    }

    public DialogWizardEntity() {
    }

    public DialogWizardEntity(@Nonnull I18NBean i18NBean, @Nonnull DialogWizard dialogWizard) {
        this.pages = Lists.newArrayList();
        for (DialogWizardPage page : dialogWizard.getPages()) {
            this.pages.add(new DialogPageEntity(i18NBean, page));
        }
    }
}

