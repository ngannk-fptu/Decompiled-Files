/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroErrorPlaceholder;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class ViewMacroErrorPlaceholderImpl
implements ViewMacroErrorPlaceholder {
    private I18NBeanFactory i18nBeanFactory;

    public ViewMacroErrorPlaceholderImpl(I18NBeanFactory i18nBeanFactory) {
        this.i18nBeanFactory = i18nBeanFactory;
    }

    @Override
    public String create(MacroDefinition macroDefinition, String errorMessage) {
        String title = this.i18nBeanFactory.getI18NBean().getText("xhtml.view.macro.error.title", new Object[]{macroDefinition.getName()});
        return "<div class=\"aui-message aui-message-error\"><p class=\"title\"><strong>" + title + "</strong></p><p>" + GeneralUtil.escapeXMLCharacters(errorMessage) + "</p></div>";
    }
}

