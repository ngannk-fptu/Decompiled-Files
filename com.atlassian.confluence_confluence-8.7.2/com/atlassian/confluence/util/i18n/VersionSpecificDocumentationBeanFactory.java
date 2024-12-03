/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.VersionSpecificDocumentationBean;

public class VersionSpecificDocumentationBeanFactory
implements DocumentationBeanFactory {
    private I18NBeanFactory i18NBeanFactory;

    public VersionSpecificDocumentationBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public DocumentationBean getDocumentationBean() {
        String versionNumber = BuildInformation.INSTANCE.getVersionNumber();
        return new VersionSpecificDocumentationBean(versionNumber, this.i18NBeanFactory.getI18NBean());
    }
}

