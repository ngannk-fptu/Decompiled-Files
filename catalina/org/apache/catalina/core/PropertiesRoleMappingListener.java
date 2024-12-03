/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.ContextNamingInfoListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

public class PropertiesRoleMappingListener
implements LifecycleListener {
    private static final String WEBAPP_PROTOCOL = "webapp:";
    private static final Log log = LogFactory.getLog(PropertiesRoleMappingListener.class);
    private static final StringManager sm = StringManager.getManager(ContextNamingInfoListener.class);
    private String roleMappingFile = "webapp:/WEB-INF/role-mapping.properties";
    private String keyPrefix;

    public void setRoleMappingFile(String roleMappingFile) {
        Objects.requireNonNull(roleMappingFile, sm.getString("propertiesRoleMappingListener.roleMappingFileNull"));
        if (roleMappingFile.isEmpty()) {
            throw new IllegalArgumentException(sm.getString("propertiesRoleMappingListener.roleMappingFileEmpty"));
        }
        this.roleMappingFile = roleMappingFile;
    }

    public String getRoleMappingFile() {
        return this.roleMappingFile;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals("configure_start")) {
            if (!(event.getLifecycle() instanceof Context)) {
                log.warn((Object)sm.getString("listener.notContext", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
                return;
            }
            Properties props = new Properties();
            Context context = (Context)event.getLifecycle();
            try (ConfigurationSource.Resource resource = context.findConfigFileResource(this.roleMappingFile);){
                props.load(resource.getInputStream());
            }
            catch (IOException e) {
                throw new IllegalStateException(sm.getString("propertiesRoleMappingListener.roleMappingFileFail", new Object[]{this.roleMappingFile}), e);
            }
            int linkCount = 0;
            for (Map.Entry<Object, Object> prop : props.entrySet()) {
                String role = (String)prop.getKey();
                if (this.keyPrefix != null) {
                    if (!role.startsWith(this.keyPrefix)) continue;
                    role = role.substring(this.keyPrefix.length());
                }
                String link = (String)prop.getValue();
                if (log.isTraceEnabled()) {
                    log.trace((Object)sm.getString("propertiesRoleMappingListener.linkedRole", new Object[]{role, link}));
                }
                context.addRoleMapping(role, link);
                ++linkCount;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("propertiesRoleMappingListener.linkedRoleCount", new Object[]{linkCount}));
            }
        }
    }
}

