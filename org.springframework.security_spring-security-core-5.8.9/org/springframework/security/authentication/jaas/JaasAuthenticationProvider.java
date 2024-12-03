/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.core.io.Resource
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.jaas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Security;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationFailedEvent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

public class JaasAuthenticationProvider
extends AbstractJaasAuthenticationProvider {
    protected static final Log log = LogFactory.getLog(JaasAuthenticationProvider.class);
    private Resource loginConfig;
    private boolean refreshConfigurationOnStartup = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength((String)this.getLoginContextName(), () -> "loginContextName must be set on " + this.getClass());
        Assert.notNull((Object)this.loginConfig, () -> "loginConfig must be set on " + this.getClass());
        this.configureJaas(this.loginConfig);
        Assert.notNull((Object)Configuration.getConfiguration(), (String)"As per https://java.sun.com/j2se/1.5.0/docs/api/javax/security/auth/login/Configuration.html \"If a Configuration object was set via the Configuration.setConfiguration method, then that object is returned. Otherwise, a default Configuration object is returned\". Your JRE returned null to Configuration.getConfiguration().");
    }

    @Override
    protected LoginContext createLoginContext(CallbackHandler handler) throws LoginException {
        return new LoginContext(this.getLoginContextName(), handler);
    }

    protected void configureJaas(Resource loginConfig) throws IOException {
        this.configureJaasUsingLoop();
        if (this.refreshConfigurationOnStartup) {
            Configuration.getConfiguration().refresh();
        }
    }

    private void configureJaasUsingLoop() throws IOException {
        String existing;
        String loginConfigUrl = this.convertLoginConfigToUrl();
        boolean alreadySet = false;
        int n = 1;
        String prefix = "login.config.url.";
        while ((existing = Security.getProperty("login.config.url." + n)) != null && !(alreadySet = existing.equals(loginConfigUrl))) {
            ++n;
        }
        if (!alreadySet) {
            String key = "login.config.url." + n;
            log.debug((Object)LogMessage.format((String)"Setting security property [%s] to: %s", (Object)key, (Object)loginConfigUrl));
            Security.setProperty(key, loginConfigUrl);
        }
    }

    private String convertLoginConfigToUrl() throws IOException {
        try {
            String loginConfigPath = this.loginConfig.getFile().getAbsolutePath().replace(File.separatorChar, '/');
            if (!loginConfigPath.startsWith("/")) {
                loginConfigPath = "/" + loginConfigPath;
            }
            return new URL("file", "", loginConfigPath).toString();
        }
        catch (IOException ex) {
            return this.loginConfig.getURL().toString();
        }
    }

    @Override
    protected void publishFailureEvent(UsernamePasswordAuthenticationToken token, AuthenticationException ase) {
        this.getApplicationEventPublisher().publishEvent((ApplicationEvent)new JaasAuthenticationFailedEvent(token, ase));
    }

    public Resource getLoginConfig() {
        return this.loginConfig;
    }

    public void setLoginConfig(Resource loginConfig) {
        this.loginConfig = loginConfig;
    }

    public void setRefreshConfigurationOnStartup(boolean refresh) {
        this.refreshConfigurationOnStartup = refresh;
    }
}

