/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JaasConfiguration;
import com.microsoft.sqlserver.jdbc.KerbCallback;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSPIAuthentication;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

final class KerbAuthentication
extends SSPIAuthentication {
    private static final Logger authLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.KerbAuthentication");
    private final SQLServerConnection con;
    private final String spn;
    private final GSSManager manager = GSSManager.getInstance();
    private LoginContext lc = null;
    private boolean isUserCreatedCredential = false;
    private GSSCredential peerCredentials = null;
    private GSSContext peerContext = null;

    private void initAuthInit() throws SQLServerException {
        block15: {
            try {
                Subject currentSubject;
                Oid kerberos = new Oid("1.2.840.113554.1.2.2");
                GSSName remotePeerName = this.manager.createName(this.spn, null);
                if (null != this.peerCredentials) {
                    this.peerContext = this.manager.createContext(remotePeerName, kerberos, this.peerCredentials, 0);
                    this.peerContext.requestCredDeleg(false);
                    this.peerContext.requestMutualAuth(true);
                    this.peerContext.requestInteg(true);
                    break block15;
                }
                String configName = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue());
                KerbCallback callback = new KerbCallback(this.con);
                try {
                    AccessControlContext context = AccessController.getContext();
                    currentSubject = Subject.getSubject(context);
                    if (null == currentSubject) {
                        this.lc = new LoginContext(configName, callback);
                        this.lc.login();
                        currentSubject = this.lc.getSubject();
                    }
                }
                catch (LoginException le) {
                    if (authLogger.isLoggable(Level.FINE)) {
                        authLogger.fine(this.toString() + "Failed to login using Kerberos due to " + le.getClass().getName() + ":" + le.getMessage());
                    }
                    try {
                        this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), le);
                    }
                    catch (SQLServerException alwaysTriggered) {
                        String message = MessageFormat.format(SQLServerException.getErrString("R_kerberosLoginFailed"), alwaysTriggered.getMessage(), le.getClass().getName(), le.getMessage());
                        if (callback.getUsernameRequested() != null) {
                            message = MessageFormat.format(SQLServerException.getErrString("R_kerberosLoginFailedForUsername"), callback.getUsernameRequested(), message);
                        }
                        throw new SQLServerException(message, alwaysTriggered.getSQLState(), 18456, (Throwable)le);
                    }
                    return;
                }
                if (authLogger.isLoggable(Level.FINER)) {
                    authLogger.finer(this.toString() + " Getting client credentials");
                }
                this.peerCredentials = KerbAuthentication.getClientCredential(currentSubject, this.manager, kerberos);
                if (authLogger.isLoggable(Level.FINER)) {
                    authLogger.finer(this.toString() + " creating security context");
                }
                this.peerContext = this.manager.createContext(remotePeerName, kerberos, this.peerCredentials, 0);
                this.peerContext.requestCredDeleg(true);
                this.peerContext.requestMutualAuth(true);
                this.peerContext.requestInteg(true);
            }
            catch (GSSException ge) {
                if (authLogger.isLoggable(Level.FINER)) {
                    authLogger.finer(this.toString() + "initAuthInit failed GSSException:-" + ge);
                }
                this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge);
            }
            catch (PrivilegedActionException ge) {
                if (authLogger.isLoggable(Level.FINER)) {
                    authLogger.finer(this.toString() + "initAuthInit failed privileged exception:-" + ge);
                }
                this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge);
            }
        }
    }

    private static GSSCredential getClientCredential(Subject subject, final GSSManager gssManager, final Oid kerboid) throws PrivilegedActionException {
        PrivilegedExceptionAction<GSSCredential> action = new PrivilegedExceptionAction<GSSCredential>(){

            @Override
            public GSSCredential run() throws GSSException {
                return gssManager.createCredential(null, 0, kerboid, 1);
            }
        };
        GSSCredential credential = Subject.doAs(subject, action);
        return credential;
    }

    private byte[] initAuthHandShake(byte[] pin, boolean[] done) throws SQLServerException {
        try {
            if (authLogger.isLoggable(Level.FINER)) {
                authLogger.finer(this.toString() + " Sending token to server over secure context");
            }
            byte[] byteToken = this.peerContext.initSecContext(pin, 0, pin.length);
            if (this.peerContext.isEstablished()) {
                done[0] = true;
                if (authLogger.isLoggable(Level.FINER)) {
                    authLogger.finer(this.toString() + "Authentication done.");
                }
            } else if (null == byteToken) {
                if (authLogger.isLoggable(Level.INFO)) {
                    authLogger.info(this.toString() + "byteToken is null in initSecContext.");
                }
                this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"));
            }
            return byteToken;
        }
        catch (GSSException ge) {
            if (authLogger.isLoggable(Level.FINER)) {
                authLogger.finer(this.toString() + "initSecContext Failed :-" + ge);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge);
            return null;
        }
    }

    KerbAuthentication(SQLServerConnection con, String address, int port) {
        this.con = con;
        this.spn = null != con ? this.getSpn(con) : null;
    }

    KerbAuthentication(SQLServerConnection con, String address, int port, GSSCredential impersonatedUserCred, boolean isUserCreated) {
        this(con, address, port);
        this.peerCredentials = impersonatedUserCred;
        this.isUserCreatedCredential = isUserCreated;
    }

    @Override
    byte[] generateClientContext(byte[] pin, boolean[] done) throws SQLServerException {
        if (null == this.peerContext) {
            this.initAuthInit();
        }
        return this.initAuthHandShake(pin, done);
    }

    @Override
    void releaseClientContext() {
        block9: {
            try {
                if (null != this.peerCredentials && !this.isUserCreatedCredential) {
                    this.peerCredentials.dispose();
                } else if (null != this.peerCredentials && this.isUserCreatedCredential) {
                    this.peerCredentials = null;
                }
                if (null != this.peerContext) {
                    this.peerContext.dispose();
                }
                if (null != this.lc) {
                    this.lc.logout();
                }
            }
            catch (LoginException e) {
                if (authLogger.isLoggable(Level.FINE)) {
                    authLogger.fine(this.toString() + " Release of the credentials failed LoginException: " + e);
                }
            }
            catch (GSSException e) {
                if (!authLogger.isLoggable(Level.FINE)) break block9;
                authLogger.fine(this.toString() + " Release of the credentials failed GSSException: " + e);
            }
        }
    }

    static {
        Configuration.setConfiguration(new JaasConfiguration(Configuration.getConfiguration()));
    }
}

