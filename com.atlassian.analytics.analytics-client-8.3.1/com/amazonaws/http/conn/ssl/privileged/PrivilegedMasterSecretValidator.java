/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.http.conn.ssl.privileged;

import com.amazonaws.http.conn.ssl.MasterSecretValidators;
import java.lang.reflect.Method;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrivilegedMasterSecretValidator
implements MasterSecretValidators.MasterSecretValidator {
    private static final Log LOG = LogFactory.getLog(PrivilegedMasterSecretValidator.class);

    @Override
    public boolean isMasterSecretValid(final Socket socket) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                return PrivilegedMasterSecretValidator.this.privilegedIsMasterSecretValid(socket);
            }
        });
    }

    private boolean privilegedIsMasterSecretValid(Socket socket) {
        String className;
        SSLSession session;
        if (socket instanceof SSLSocket && (session = this.getSslSession(socket)) != null && "sun.security.ssl.SSLSessionImpl".equals(className = session.getClass().getName())) {
            try {
                Object masterSecret = this.getMasterSecret(session, className);
                if (masterSecret == null) {
                    session.invalidate();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug((Object)("Invalidated session " + session));
                    }
                    return false;
                }
            }
            catch (Exception e) {
                this.failedToVerifyMasterSecret(e);
            }
        }
        return true;
    }

    private SSLSession getSslSession(Socket socket) {
        return ((SSLSocket)socket).getSession();
    }

    private Object getMasterSecret(SSLSession session, String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod("getMasterSecret", new Class[0]);
        method.setAccessible(true);
        return method.invoke((Object)session, new Object[0]);
    }

    private void failedToVerifyMasterSecret(Throwable t) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)"Failed to verify the SSL master secret", t);
        }
    }
}

