/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.jaas;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.util.Assert;

public class DefaultJaasAuthenticationProvider
extends AbstractJaasAuthenticationProvider {
    private Configuration configuration;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull((Object)this.configuration, (String)"configuration cannot be null.");
    }

    @Override
    protected LoginContext createLoginContext(CallbackHandler handler) throws LoginException {
        return new LoginContext(this.getLoginContextName(), null, handler, this.getConfiguration());
    }

    protected Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}

