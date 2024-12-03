/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.spring.container.ContainerManager;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SetupRestoreHelper {
    private static final Logger log = LoggerFactory.getLogger(SetupRestoreHelper.class);

    public static void prepareForRestore() {
        SetupRestoreHelper.logoutCurrentUser();
        SetupRestoreHelper.recreateSearchIndex();
    }

    private static void recreateSearchIndex() {
        try {
            ILuceneConnection contentConnection = (ILuceneConnection)ContainerManager.getInstance().getContainerContext().getComponent((Object)"luceneConnection");
            ILuceneConnection changeConnection = (ILuceneConnection)ContainerManager.getInstance().getContainerContext().getComponent((Object)"changeLuceneConnection");
            contentConnection.truncateIndex();
            changeConnection.truncateIndex();
        }
        catch (LuceneException e) {
            log.error("Error clearing existing search index", (Throwable)e);
        }
    }

    private static void logoutCurrentUser() {
        try {
            SecurityConfigFactory.getInstance().getAuthenticator().logout(ServletActionContext.getRequest(), ServletActionContext.getResponse());
            log.info("Successfully logged out existing user from the session");
        }
        catch (AuthenticatorException e) {
            log.error("Error logging out the current logged-in user from the session", (Throwable)e);
        }
    }

    public static void postRestoreSteps() {
        BootstrapConfigurer.getBootstrapConfigurer().getSetupPersister().progessSetupStep();
        BootstrapConfigurer.getBootstrapConfigurer().getSetupPersister().progessSetupStep();
        BootstrapConfigurer.getBootstrapConfigurer().getSetupPersister().progessSetupStep();
    }
}

