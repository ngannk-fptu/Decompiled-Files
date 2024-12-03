/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

public interface ConfluenceBootstrapConstants {
    public static final String CONFLUENCE_HOME_CONSTANT = "${confluenceHome}";
    public static final String CONFLUENCE_LOCAL_HOME_CONSTANT = "${localHome}";
    public static final String LUCENE_INDEX_DIR_PROP = "lucene.index.dir";
    public static final String ATTACHMENTS_DIR_PROP = "attachments.dir";
    public static final String TEMP_DIR_PROP = "struts.multipart.saveDir";
    @Deprecated
    public static final String LICENSE_HASH_KEY = "confluence.license.hash";
    @Deprecated
    public static final String LICENSE_MESSAGE_KEY = "confluence.license.message";
    public static final String ATLASSIAN_LICENSE_KEY = "atlassian.license.message";
    public static final String WEBAPP_CONTEXT_PATH_KEY = "confluence.webapp.context.path";
    public static final String GLOBAL_PLUGIN_STATE = "confluence.plugin.state.global";
    public static final String DEFAULT_LICENSE_REGISTRY_KEY = "CONF";
    public static final String INSTALLATION_DATE_KEY = "confluence.server.installation.date";
}

