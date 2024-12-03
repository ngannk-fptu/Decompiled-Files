/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.lookandfeel.AbstractLookAndFeelAction;
import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.util.LayoutHelper;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractThemeAction
extends AbstractLookAndFeelAction {
    protected String themeKey;

    public void setThemeKey(String themeKey) {
        this.themeKey = themeKey;
    }

    public String getThemeKey() {
        return this.themeKey;
    }

    public List getAvailableThemeDescriptors() {
        return this.themeManager.getAvailableThemeDescriptors();
    }

    @Override
    public void validate() {
        super.validate();
        if (StringUtils.isNotEmpty((CharSequence)this.themeKey) && this.layoutHelper.findThemeDescriptor(this.themeKey) == null) {
            this.addActionError("theme.not.found", this.themeKey);
        }
    }

    public String execute() {
        if (this.themeKey != null) {
            this.setTheme(this.themeKey);
            LayoutHelper.flushThemeComponents(this.getSpaceKey());
        }
        return "success";
    }

    protected abstract void setTheme(String var1);

    public ThemeModuleDescriptor getCurrentThemeDescriptor() {
        if (StringUtils.isNotEmpty((CharSequence)this.getCurrentThemeKey())) {
            return this.layoutHelper.findThemeDescriptor(this.getCurrentThemeKey());
        }
        return null;
    }

    public boolean hasIcon(ThemeModuleDescriptor descriptor) {
        return this.layoutHelper.hasIcon(descriptor);
    }

    protected abstract String getCurrentThemeKey();

    protected abstract String getConfigPath(ThemeModuleDescriptor var1);
}

