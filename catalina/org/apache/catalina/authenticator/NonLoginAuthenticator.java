/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;

public final class NonLoginAuthenticator
extends AuthenticatorBase {
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (this.checkForCachedAuthentication(request, response, true)) {
            if (this.cache) {
                request.getSessionInternal(true).setPrincipal(request.getPrincipal());
            }
            return true;
        }
        if (this.containerLog.isDebugEnabled()) {
            this.containerLog.debug((Object)"User authenticated without any roles");
        }
        return true;
    }

    @Override
    protected String getAuthMethod() {
        return "NONE";
    }
}

