/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginState
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.jira.handlers;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

public class JiraIssuesMacroInstallHandler
implements InitializingBean,
BeanFactoryAware {
    private static final Logger log = LoggerFactory.getLogger(JiraIssuesMacroInstallHandler.class);
    private static final String PLUGIN_KEY_JIRA_CONNECTOR = "com.atlassian.confluence.plugins.jira.jira-connector";
    private static final String PLUGIN_KEY_CONFLUENCE_PASTE = "com.atlassian.confluence.plugins.confluence-paste";
    private static final String PLUGIN_MODULE_KEY_JIRA_PASTE = "com.atlassian.confluence.plugins.confluence-paste:autoconvert-jira";
    private final PluginController pluginController;
    private BeanFactory beanFactory;
    private PluginAccessor pluginAccessor;

    public JiraIssuesMacroInstallHandler(PluginController pluginController) {
        this.pluginController = pluginController;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void uninstallJiraConnectorPlugin() {
        Plugin jiraConnectorPlugin = this.pluginAccessor.getPlugin(PLUGIN_KEY_JIRA_CONNECTOR);
        if (jiraConnectorPlugin != null) {
            log.debug("JiraConnector plugin detected, about to uninstall");
            this.pluginController.uninstall(jiraConnectorPlugin);
            log.debug("Finish uninstalling JiraConnector plugin");
        }
    }

    private void disableJiraPaste() {
        Plugin jiraConfluencePastePlugin = this.pluginAccessor.getPlugin(PLUGIN_KEY_CONFLUENCE_PASTE);
        if (jiraConfluencePastePlugin != null && jiraConfluencePastePlugin.getPluginState() == PluginState.ENABLED) {
            ModuleDescriptor moduleDescriptor = this.pluginAccessor.getEnabledPluginModule(PLUGIN_MODULE_KEY_JIRA_PASTE);
            if (moduleDescriptor == null) {
                return;
            }
            try {
                this.pluginController.disablePluginModule(PLUGIN_MODULE_KEY_JIRA_PASTE);
            }
            catch (Exception e) {
                log.warn("unable to disable com.atlassian.confluence.plugins.confluence-paste:autoconvert-jira", (Throwable)e);
            }
            log.debug("Finish disabling JiraPaste module: com.atlassian.confluence.plugins.confluence-paste:autoconvert-jira");
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.pluginAccessor = (PluginAccessor)this.beanFactory.getBean(this.beanFactory.containsBean("pluginAccessor") ? "pluginAccessor" : "pluginManager");
        this.uninstallJiraConnectorPlugin();
        this.disableJiraPaste();
    }
}

