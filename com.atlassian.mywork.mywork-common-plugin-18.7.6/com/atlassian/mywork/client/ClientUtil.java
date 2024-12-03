/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.util.concurrent.ThreadFactories
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.util.concurrent.ThreadFactories;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUtil {
    private static final Logger log = LoggerFactory.getLogger(ClientUtil.class);

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExecutorService newExecutorService(final String name, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000), ThreadFactories.namedThreadFactory((String)name), new RejectedExecutionHandler(){
            private long minute;
            private int discardsThisMinute;

            @Override
            public synchronized void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                long minute = System.currentTimeMillis() / 1000L / 60L;
                if (minute != this.minute) {
                    this.minute = minute;
                    this.discardsThisMinute = 0;
                }
                ++this.discardsThisMinute;
                if (this.discardsThisMinute <= 10 || this.discardsThisMinute <= 100 && this.discardsThisMinute % 10 == 0 || this.discardsThisMinute <= 1000 && this.discardsThisMinute % 100 == 0 || this.discardsThisMinute <= 10000 && this.discardsThisMinute % 1000 == 0) {
                    log.error("Discarded " + this.discardsThisMinute + " tasks so far this minute due to ExecutorService overflow: " + name);
                }
            }
        });
        return threadLocalDelegateExecutorFactory.createExecutorService((ExecutorService)threadPool);
    }

    public static boolean credentialsRequired(ApplicationLink host, ApplicationId clientId) {
        boolean credentialsRequired;
        ApplicationLinkRequestFactory requestFactory = host.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class);
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.POST, "/rest/mywork/1/client/pong?appId=" + clientId.get());
            request.setHeader("X-Atlassian-Token", "no-check");
            credentialsRequired = (Boolean)request.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Boolean>(){

                public Boolean credentialsRequired(Response response) {
                    return true;
                }

                public Boolean handle(Response response) {
                    return false;
                }
            });
        }
        catch (CredentialsRequiredException e) {
            credentialsRequired = true;
        }
        catch (ResponseException e) {
            throw new RuntimeException("Authorisation check with " + host.getName() + " failed: " + e.getMessage(), e);
        }
        return credentialsRequired;
    }
}

