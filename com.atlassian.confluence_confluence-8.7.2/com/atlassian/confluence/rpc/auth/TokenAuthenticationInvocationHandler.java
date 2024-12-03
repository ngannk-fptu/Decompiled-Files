/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Throwables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.auth;

import com.atlassian.confluence.event.events.security.RpcAuthFailedEvent;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.auth.TokenAuthenticationManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Throwables;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenAuthenticationInvocationHandler
implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(TokenAuthenticationInvocationHandler.class);
    private TokenAuthenticationManager tokenAuthenticationManager;
    private Object wrappedObject;
    private EventPublisher eventPublisher;

    public static Object makeAuthenticatingProxy(Object rpcService, Class publishedInterface) {
        TokenAuthenticationInvocationHandler authHandler = (TokenAuthenticationInvocationHandler)ContainerManager.getComponent((String)"tokenAuthHandler");
        authHandler.setWrappedObject(rpcService);
        return Proxy.newProxyInstance(rpcService.getClass().getClassLoader(), new Class[]{publishedInterface}, (InvocationHandler)authHandler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if ("login".equals(method.getName())) {
                return this.tokenAuthenticationManager.login((String)args[0], (String)args[1]);
            }
        }
        catch (AuthenticationFailedException e) {
            this.eventPublisher.publish((Object)new RpcAuthFailedEvent(this, (String)args[0]));
            log.info(e.getMessage());
            throw e;
        }
        if ("logout".equals(method.getName())) {
            return this.tokenAuthenticationManager.logout((String)args[0]);
        }
        String token = (String)args[0];
        args[0] = "";
        ConfluenceUser user = this.getAuthenticatedUser(token);
        if (!this.tokenAuthenticationManager.hasUseConfluencePermission(user)) {
            throw new NotPermittedException("User does not have Use Confluence permission");
        }
        return this.invokeAuthenticatedMethod(user, method, args);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected Object invokeAuthenticatedMethod(ConfluenceUser user, Method method, Object[] args) throws Throwable {
        Object object;
        ConfluenceUser originalUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(user);
            LoggingContext.setUserName((String)(user != null ? user.getName() : null));
            LoggingContext.put((String)"method", (Object)method.getName());
            log.info("Executing remote API method [ {} ] as user [ {} ]", (Object)method.getName(), (Object)(user == null ? "anonymous" : user.getName()));
            log.debug("Arguments to remote API method [ {} ] are {}", (Object)method.getName(), Arrays.asList(args));
            object = method.invoke(this.wrappedObject, args);
        }
        catch (InvocationTargetException e) {
            try {
                Throwable cause = e.getTargetException();
                if (cause instanceof RemoteException) {
                    log.warn("Failure executing remote method [{}]: {}", (Object)method.getName(), (Object)Throwables.getRootCause((Throwable)cause).getMessage());
                    throw cause;
                }
                log.error("Error during invocation of method [{}]", (Object)method.getName(), (Object)cause);
                throw cause;
                catch (IllegalAccessException e2) {
                    throw new RuntimeException("Error during invocation of method " + method.getName(), e2);
                }
            }
            catch (Throwable throwable) {
                AuthenticatedUserThreadLocal.set(originalUser);
                LoggingContext.setUserName((String)(originalUser != null ? originalUser.getName() : null));
                LoggingContext.remove((String[])new String[]{"method"});
                throw throwable;
            }
        }
        AuthenticatedUserThreadLocal.set(originalUser);
        LoggingContext.setUserName((String)(originalUser != null ? originalUser.getName() : null));
        LoggingContext.remove((String[])new String[]{"method"});
        return object;
    }

    protected ConfluenceUser getAuthenticatedUser(String token) throws InvalidSessionException, NotPermittedException {
        ConfluenceUser user = null;
        if (StringUtils.isNotBlank((CharSequence)token)) {
            user = this.tokenAuthenticationManager.makeNonAnonymousConfluenceUserFromToken(token);
        }
        if (user == null) {
            user = AuthenticatedUserThreadLocal.get();
        }
        if (user == null) {
            user = this.tokenAuthenticationManager.makeAnonymousConfluenceUser();
        }
        return user;
    }

    public void setWrappedObject(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public void setTokenAuthenticationManager(TokenAuthenticationManager tokenAuthenticationManager) {
        this.tokenAuthenticationManager = tokenAuthenticationManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

