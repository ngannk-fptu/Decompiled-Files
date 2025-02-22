/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.authenticator;

import java.io.Serializable;
import org.apache.catalina.Authenticator;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionEvent;
import org.apache.catalina.SessionListener;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.authenticator.SingleSignOn;

public class SingleSignOnListener
implements SessionListener,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String ssoId;

    public SingleSignOnListener(String ssoId) {
        this.ssoId = ssoId;
    }

    @Override
    public void sessionEvent(SessionEvent event) {
        if (!"destroySession".equals(event.getType())) {
            return;
        }
        Session session = event.getSession();
        Manager manager = session.getManager();
        if (manager == null) {
            return;
        }
        Context context = manager.getContext();
        Authenticator authenticator = context.getAuthenticator();
        if (!(authenticator instanceof AuthenticatorBase)) {
            return;
        }
        SingleSignOn sso = ((AuthenticatorBase)authenticator).sso;
        if (sso == null) {
            return;
        }
        sso.sessionDestroyed(this.ssoId, session);
    }
}

