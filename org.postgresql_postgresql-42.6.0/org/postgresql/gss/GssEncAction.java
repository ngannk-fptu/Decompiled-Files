/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.gss;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.postgresql.core.PGStream;
import org.postgresql.gss.GssAction;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

public class GssEncAction
implements PrivilegedAction<Exception>,
Callable<Exception> {
    private static final Logger LOGGER = Logger.getLogger(GssAction.class.getName());
    private final PGStream pgStream;
    private final String host;
    private final String user;
    private final String kerberosServerName;
    private final boolean useSpnego;
    private final @Nullable Subject subject;
    private final boolean logServerErrorDetail;

    public GssEncAction(PGStream pgStream, @Nullable Subject subject, String host, String user, String kerberosServerName, boolean useSpnego, boolean logServerErrorDetail) {
        this.pgStream = pgStream;
        this.subject = subject;
        this.host = host;
        this.user = user;
        this.kerberosServerName = kerberosServerName;
        this.useSpnego = useSpnego;
        this.logServerErrorDetail = logServerErrorDetail;
    }

    private static boolean hasSpnegoSupport(GSSManager manager) throws GSSException {
        Oid[] mechs;
        Oid spnego = new Oid("1.3.6.1.5.5.2");
        for (Oid mech : mechs = manager.getMechs()) {
            if (!mech.equals(spnego)) continue;
            return true;
        }
        return false;
    }

    @Override
    public @Nullable Exception run() {
        try {
            Set<GSSCredential> gssCreds;
            GSSManager manager = GSSManager.getInstance();
            GSSCredential clientCreds = null;
            Oid[] desiredMechs = new Oid[1];
            GSSCredential gssCredential = null;
            if (this.subject != null && (gssCreds = this.subject.getPrivateCredentials(GSSCredential.class)) != null && !gssCreds.isEmpty()) {
                gssCredential = gssCreds.iterator().next();
            }
            if (gssCredential == null) {
                desiredMechs[0] = this.useSpnego && GssEncAction.hasSpnegoSupport(manager) ? new Oid("1.3.6.1.5.5.2") : new Oid("1.2.840.113554.1.2.2");
                String principalName = this.user;
                if (this.subject != null) {
                    Set<Principal> principals = this.subject.getPrincipals();
                    Iterator<Principal> principalIterator = principals.iterator();
                    Principal principal = null;
                    if (principalIterator.hasNext()) {
                        principal = principalIterator.next();
                        principalName = principal.getName();
                    }
                }
                GSSName clientName = manager.createName(principalName, GSSName.NT_USER_NAME);
                clientCreds = manager.createCredential(clientName, 28800, desiredMechs, 1);
            } else {
                desiredMechs[0] = new Oid("1.2.840.113554.1.2.2");
                clientCreds = gssCredential;
            }
            GSSName serverName = manager.createName(this.kerberosServerName + "@" + this.host, GSSName.NT_HOSTBASED_SERVICE);
            GSSContext secContext = manager.createContext(serverName, desiredMechs[0], clientCreds, 0);
            secContext.requestMutualAuth(true);
            secContext.requestConf(true);
            secContext.requestInteg(true);
            byte[] inToken = new byte[]{};
            byte[] outToken = null;
            boolean established = false;
            while (!established) {
                outToken = secContext.initSecContext(inToken, 0, inToken.length);
                if (outToken != null) {
                    LOGGER.log(Level.FINEST, " FE=> Password(GSS Authentication Token)");
                    this.pgStream.sendInteger4(outToken.length);
                    this.pgStream.send(outToken);
                    this.pgStream.flush();
                }
                if (!secContext.isEstablished()) {
                    int len = this.pgStream.receiveInteger4();
                    inToken = this.pgStream.receive(len);
                    continue;
                }
                established = true;
                this.pgStream.setSecContext(secContext);
            }
        }
        catch (IOException e) {
            return e;
        }
        catch (GSSException gsse) {
            return new PSQLException(GT.tr("GSS Authentication failed", new Object[0]), PSQLState.CONNECTION_FAILURE, (Throwable)gsse);
        }
        return null;
    }

    @Override
    public @Nullable Exception call() throws Exception {
        return this.run();
    }
}

