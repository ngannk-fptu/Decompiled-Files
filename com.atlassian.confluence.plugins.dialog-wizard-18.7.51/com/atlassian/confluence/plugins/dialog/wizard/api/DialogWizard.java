/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import com.atlassian.confluence.plugins.dialog.wizard.api.DialogWizardPage;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DialogWizard {
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String nameKey;
    @XmlElement
    private List<DialogWizardPage> pages;

    private DialogWizard() {
    }

    public DialogWizard(@Nonnull String key, String nameKey, @Nonnull List<DialogWizardPage> pages) {
        this.key = key;
        this.nameKey = nameKey;
        this.pages = pages;
    }

    public List<DialogWizardPage> getPages() {
        return this.pages;
    }

    public String getKey() {
        return this.key;
    }

    public String getNameKey() {
        return this.nameKey;
    }
}

