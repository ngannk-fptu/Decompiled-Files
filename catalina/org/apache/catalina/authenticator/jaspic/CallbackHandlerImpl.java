/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.callback.CallerPrincipalCallback
 *  javax.security.auth.message.callback.GroupPrincipalCallback
 *  javax.security.auth.message.callback.PasswordValidationCallback
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.authenticator.jaspic;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.PasswordValidationCallback;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class CallbackHandlerImpl
implements CallbackHandler,
Contained {
    private static final StringManager sm = StringManager.getManager(CallbackHandlerImpl.class);
    private final Log log = LogFactory.getLog(CallbackHandlerImpl.class);
    private Container container;

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String name = null;
        Principal principal = null;
        Subject subject = null;
        String[] groups = null;
        if (callbacks != null) {
            for (Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback) {
                    CallerPrincipalCallback cpc = (CallerPrincipalCallback)callback;
                    name = cpc.getName();
                    principal = cpc.getPrincipal();
                    subject = cpc.getSubject();
                    continue;
                }
                if (callback instanceof GroupPrincipalCallback) {
                    GroupPrincipalCallback gpc = (GroupPrincipalCallback)callback;
                    groups = gpc.getGroups();
                    continue;
                }
                if (callback instanceof PasswordValidationCallback) {
                    if (this.container == null) {
                        this.log.warn((Object)sm.getString("callbackHandlerImpl.containerMissing", new Object[]{callback.getClass().getName()}));
                        continue;
                    }
                    if (this.container.getRealm() == null) {
                        this.log.warn((Object)sm.getString("callbackHandlerImpl.realmMissing", new Object[]{callback.getClass().getName(), this.container.getName()}));
                        continue;
                    }
                    PasswordValidationCallback pvc = (PasswordValidationCallback)callback;
                    principal = this.container.getRealm().authenticate(pvc.getUsername(), String.valueOf(pvc.getPassword()));
                    pvc.setResult(principal != null);
                    subject = pvc.getSubject();
                    continue;
                }
                this.log.error((Object)sm.getString("callbackHandlerImpl.jaspicCallbackMissing", new Object[]{callback.getClass().getName()}));
            }
            Principal gp = this.getPrincipal(principal, name, groups);
            if (subject != null && gp != null) {
                subject.getPrivateCredentials().add(gp);
            }
        }
    }

    private Principal getPrincipal(Principal principal, String name, String[] groups) {
        if (principal instanceof GenericPrincipal) {
            return principal;
        }
        if (name == null && principal != null) {
            name = principal.getName();
        }
        if (name == null) {
            return null;
        }
        List<Object> roles = groups == null || groups.length == 0 ? Collections.emptyList() : Arrays.asList(groups);
        return new GenericPrincipal(name, null, roles, principal);
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}

