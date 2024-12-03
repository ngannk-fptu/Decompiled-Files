/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component
public class ErrorMessages {
    private final I18NBeanFactory i18NBeanFactory;
    private final DocumentationBeanFactory documentationBeanFactory;

    public ErrorMessages(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport DocumentationBeanFactory documentationBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public String pageTimeoutMessage(String pageTitle) {
        return this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.sandbox.convert.timeout", (Object[])new String[]{pageTitle, this.documentationUrl()});
    }

    public String pageErrorMessage(String pageTitle) {
        return this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.sandbox.convert.error", (Object[])new String[]{pageTitle, this.documentationUrl()});
    }

    public String joinTimeoutMessage() {
        return this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.sandbox.join.timeout", (Object[])new String[]{this.documentationUrl()});
    }

    public String joinErrorMessage() {
        return this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.sandbox.join.error", (Object[])new String[]{this.documentationUrl()});
    }

    public String tooManyConcurrentExports() {
        return this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.error.too.many.concurrent.exports", (Object[])new String[]{this.documentationUrl()});
    }

    private String documentationUrl() {
        return this.documentationBeanFactory.getDocumentationBean().getLink("PDF+export+in+Confluence+Data+Center");
    }
}

