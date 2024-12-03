/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.basicauth.service;

import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthDao;
import com.atlassian.plugins.authentication.impl.basicauth.service.BasicAuthRequestMatcher;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ThreadSafe
public class CachingBasicAuthService {
    private final BasicAuthDao basicAuthDao;
    private volatile BasicAuthRequestMatcher matcher;

    @Inject
    public CachingBasicAuthService(BasicAuthDao basicAuthDao) {
        this.basicAuthDao = basicAuthDao;
        this.update();
    }

    public void update() {
        this.matcher = new BasicAuthRequestMatcher(this.basicAuthDao.get());
    }

    public BasicAuthConfig getConfig() {
        return this.matcher.getConfig();
    }

    public BasicAuthRequestMatcher getMatcher() {
        return this.matcher;
    }
}

