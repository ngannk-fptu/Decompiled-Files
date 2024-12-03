/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.config.properties.LookAndFeelBean
 */
package com.atlassian.oauth2.provider.data.themes;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.LookAndFeelBean;
import com.atlassian.oauth2.provider.data.themes.ProductCustomTheme;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;

public class JiraCustomThemeFactory
extends ProductCustomThemeFactory {
    private final ApplicationProperties jiraApplicationProperties;
    private final LookAndFeelBean lookAndFeelBean;

    public JiraCustomThemeFactory(ApplicationProperties jiraApplicationProperties, LookAndFeelBean lookAndFeelBean) {
        this.jiraApplicationProperties = jiraApplicationProperties;
        this.lookAndFeelBean = lookAndFeelBean;
    }

    @Override
    public ProductCustomTheme get() {
        return new ProductCustomTheme(this.jiraApplicationProperties.getString("jira.lf.top.bgcolour"), this.lookAndFeelBean.getAbsoluteLogoUrl());
    }
}

