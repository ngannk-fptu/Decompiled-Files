/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.core.net.HttpClientRequest
 *  com.atlassian.sal.core.net.SystemPropertiesConnectionConfig
 *  org.springframework.aop.Advisor
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.confluence.api.impl.sal.AbstractHttpClientFactory;
import com.atlassian.confluence.api.impl.sal.util.HttpTimeoutParameters;
import com.atlassian.confluence.util.AopUtils;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.SystemPropertiesConnectionConfig;
import org.springframework.aop.Advisor;

public class SetupHttpClientRequestFactory
extends AbstractHttpClientFactory {
    public SetupHttpClientRequestFactory(Advisor classLoaderAdvisor) {
        super(classLoaderAdvisor);
    }

    public Request createRequest(Request.MethodType methodType, String url) {
        HttpClientRequest request = this.httpClientRequestFactory.createRequest(methodType, url);
        ConfluenceHttpParameters confluenceHttpParameters = new ConfluenceHttpParameters();
        SystemPropertiesConnectionConfig systemPropConfigs = new SystemPropertiesConnectionConfig();
        request.setConnectionTimeout(HttpTimeoutParameters.getConnectionTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
        request.setSoTimeout(HttpTimeoutParameters.getSocketTimeoutFrom(systemPropConfigs, confluenceHttpParameters));
        return AopUtils.createAdvisedProxy((Object)request, this.classLoaderAdvisor, Request.class);
    }
}

