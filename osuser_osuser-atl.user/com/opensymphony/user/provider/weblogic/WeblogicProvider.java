/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.jndi.Environment
 *  weblogic.management.MBeanHome
 *  weblogic.management.security.authentication.AuthenticationProviderMBean
 *  weblogic.management.security.authentication.GroupEditorMBean
 *  weblogic.management.security.authentication.GroupMemberListerMBean
 *  weblogic.management.security.authentication.MemberGroupListerMBean
 *  weblogic.management.security.authentication.UserEditorMBean
 *  weblogic.management.security.authentication.UserPasswordEditorMBean
 *  weblogic.management.security.authentication.UserReaderMBean
 *  weblogic.management.security.credentials.CredentialMapperMBean
 *  weblogic.management.security.credentials.UserPasswordCredentialMapEditorMBean
 *  weblogic.management.security.credentials.UserPasswordCredentialMapReaderMBean
 */
package com.opensymphony.user.provider.weblogic;

import com.opensymphony.user.Entity;
import com.opensymphony.user.provider.UserProvider;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.jndi.Environment;
import weblogic.management.MBeanHome;
import weblogic.management.security.authentication.AuthenticationProviderMBean;
import weblogic.management.security.authentication.GroupEditorMBean;
import weblogic.management.security.authentication.GroupMemberListerMBean;
import weblogic.management.security.authentication.MemberGroupListerMBean;
import weblogic.management.security.authentication.UserEditorMBean;
import weblogic.management.security.authentication.UserPasswordEditorMBean;
import weblogic.management.security.authentication.UserReaderMBean;
import weblogic.management.security.credentials.CredentialMapperMBean;
import weblogic.management.security.credentials.UserPasswordCredentialMapEditorMBean;
import weblogic.management.security.credentials.UserPasswordCredentialMapReaderMBean;

public abstract class WeblogicProvider
implements UserProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$weblogic$WeblogicProvider == null ? (class$com$opensymphony$user$provider$weblogic$WeblogicProvider = WeblogicProvider.class$("com.opensymphony.user.provider.weblogic.WeblogicProvider")) : class$com$opensymphony$user$provider$weblogic$WeblogicProvider));
    protected transient MBeanHome home;
    protected List groupMemberListers;
    protected List memberGroupListers;
    protected List userPasswordEditors;
    protected List userReaders;
    protected Properties originalProperties;
    protected int maxRecords;
    static /* synthetic */ Class class$com$opensymphony$user$provider$weblogic$WeblogicProvider;

    public void flushCaches() {
    }

    public boolean init(Properties properties) {
        try {
            int i;
            this.originalProperties = properties;
            String maxRecordsProperty = properties.getProperty("maxrecords");
            this.maxRecords = maxRecordsProperty != null ? Integer.parseInt(maxRecordsProperty) : Integer.MAX_VALUE;
            this.groupMemberListers = new ArrayList();
            this.memberGroupListers = new ArrayList();
            this.userReaders = new ArrayList();
            this.userPasswordEditors = new ArrayList();
            this.findHome();
            AuthenticationProviderMBean[] authProviders = this.home.getActiveDomain().getSecurityConfiguration().findDefaultRealm().getAuthenticationProviders();
            CredentialMapperMBean[] credMappers = this.home.getActiveDomain().getSecurityConfiguration().findDefaultRealm().getCredentialMappers();
            if (log.isDebugEnabled()) {
                log.debug((Object)("ignore credential mapping = " + this.home.getActiveDomain().getSecurityConfiguration().findDefaultRealm().isDeployCredentialMappingIgnored()));
            }
            for (i = 0; i < authProviders.length; ++i) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("provider: " + authProviders[i].wls_getDisplayName()));
                }
                if (authProviders[i] instanceof UserEditorMBean) {
                    log.debug((Object)("UserEditorMBean: " + authProviders[i].wls_getDisplayName()));
                }
                if (authProviders[i] instanceof GroupEditorMBean) {
                    log.debug((Object)("GroupEditorMBean: " + authProviders[i].wls_getDisplayName()));
                }
                if (authProviders[i] instanceof MemberGroupListerMBean) {
                    log.debug((Object)("MemberGroupListerMBean: " + authProviders[i].wls_getDisplayName()));
                    this.groupMemberListers.add(authProviders[i]);
                }
                if (authProviders[i] instanceof GroupMemberListerMBean) {
                    log.debug((Object)("GroupMemberListerMBean: " + authProviders[i].wls_getDisplayName()));
                    this.memberGroupListers.add(authProviders[i]);
                }
                if (!(authProviders[i] instanceof UserReaderMBean)) continue;
                log.debug((Object)("UserReaderMBean: " + authProviders[i].wls_getDisplayName()));
                this.userReaders.add(authProviders[i]);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(credMappers.length + " credmappers"));
            }
            for (i = 0; i < credMappers.length; ++i) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("credMapper: " + credMappers[i].wls_getDisplayName()));
                }
                if (authProviders[i] instanceof UserPasswordCredentialMapReaderMBean) {
                    log.debug((Object)("UserPasswordCredentialMapReaderMBean: " + credMappers[i].wls_getDisplayName()));
                }
                if (authProviders[i] instanceof UserPasswordCredentialMapEditorMBean) {
                    log.debug((Object)("UserPasswordCredentialMapEditorMBean: " + credMappers[i].wls_getDisplayName()));
                }
                if (!(authProviders[i] instanceof UserPasswordEditorMBean)) continue;
                log.debug((Object)("UserPasswordEditorMBean: " + credMappers[i].wls_getDisplayName()));
                this.userPasswordEditors.add(authProviders[i]);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(this.groupMemberListers.size() + " groupMemberListers"));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(this.memberGroupListers.size() + " memberGroupListers"));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)(this.userReaders.size() + " userReaders"));
            }
            return true;
        }
        catch (Exception ex) {
            log.error((Object)("Error in init(" + properties + ")"), (Throwable)ex);
            return false;
        }
    }

    public boolean load(String name, Entity.Accessor accessor) {
        accessor.setMutable(true);
        return true;
    }

    public boolean store(String name, Entity.Accessor accessor) {
        return true;
    }

    protected void findHome() {
        Environment env = new Environment();
        env.setProviderUrl(this.originalProperties.getProperty("serverurl"));
        env.setSecurityPrincipal(this.originalProperties.getProperty("username"));
        env.setSecurityCredentials((Object)this.originalProperties.getProperty("password"));
        try {
            Context ctx = env.getInitialContext();
            this.home = (MBeanHome)ctx.lookup("weblogic.management.adminhome");
        }
        catch (NamingException e) {
            log.error((Object)"error getting home: ", (Throwable)e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init(null);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

