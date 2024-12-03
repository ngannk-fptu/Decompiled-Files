/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.annotations.Internal;
import com.atlassian.plugins.authentication.api.config.LoginGatewayType;
import com.atlassian.plugins.authentication.api.config.LoginOption;
import java.util.List;

@Transactional
@Internal
public interface LoginOptionsService {
    public List<LoginOption> getLoginOptions(boolean var1, LoginGatewayType var2);
}

