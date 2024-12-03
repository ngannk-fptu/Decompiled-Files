/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.auth.message.AuthException
 *  javax.security.auth.message.AuthStatus
 *  javax.security.auth.message.MessageInfo
 *  javax.security.auth.message.config.ServerAuthContext
 *  javax.security.auth.message.module.ServerAuthModule
 */
package org.apache.catalina.authenticator.jaspic;

import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

public class SimpleServerAuthContext
implements ServerAuthContext {
    private final List<ServerAuthModule> modules;

    public SimpleServerAuthContext(List<ServerAuthModule> modules) {
        this.modules = modules;
    }

    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        for (int moduleIndex = 0; moduleIndex < this.modules.size(); ++moduleIndex) {
            ServerAuthModule module = this.modules.get(moduleIndex);
            AuthStatus result = module.validateRequest(messageInfo, clientSubject, serviceSubject);
            if (result == AuthStatus.SEND_FAILURE) continue;
            messageInfo.getMap().put("moduleIndex", moduleIndex);
            return result;
        }
        return AuthStatus.SEND_FAILURE;
    }

    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        ServerAuthModule module = this.modules.get((Integer)messageInfo.getMap().get("moduleIndex"));
        return module.secureResponse(messageInfo, serviceSubject);
    }

    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        for (ServerAuthModule module : this.modules) {
            module.cleanSubject(messageInfo, subject);
        }
    }
}

