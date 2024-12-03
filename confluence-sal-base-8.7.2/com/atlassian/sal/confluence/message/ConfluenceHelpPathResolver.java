/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.sal.api.message.DefaultHelpPath
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 */
package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.sal.api.message.DefaultHelpPath;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;

public class ConfluenceHelpPathResolver
implements HelpPathResolver {
    private static final String HELP_PROPERTY_PREFIX = "help.";
    private final DocumentationBeanFactory documentationBeanFactory;

    public ConfluenceHelpPathResolver(DocumentationBeanFactory documentationBeanFactory) {
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public HelpPath getHelpPath(String key) {
        DocumentationBean bean = this.documentationBeanFactory.getDocumentationBean();
        String propertyKey = key != null && key.startsWith(HELP_PROPERTY_PREFIX) ? key : HELP_PROPERTY_PREFIX + key;
        return bean.exists(propertyKey) ? new DefaultHelpPath(propertyKey, bean.getLink(propertyKey), bean.getTitle(propertyKey), bean.getAlt(propertyKey), bean.isLocal(propertyKey)) : null;
    }
}

