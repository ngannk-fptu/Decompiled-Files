/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.realm;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.Wrapper;
import org.apache.catalina.realm.RealmBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

public class CombinedRealm
extends RealmBase {
    private static final Log log = LogFactory.getLog(CombinedRealm.class);
    protected final List<Realm> realms = new ArrayList<Realm>();

    public void addRealm(Realm theRealm) {
        this.realms.add(theRealm);
        if (log.isDebugEnabled()) {
            sm.getString("combinedRealm.addRealm", new Object[]{theRealm.getClass().getName(), Integer.toString(this.realms.size())});
        }
    }

    public ObjectName[] getRealms() {
        ObjectName[] result = new ObjectName[this.realms.size()];
        for (Realm realm : this.realms) {
            if (!(realm instanceof RealmBase)) continue;
            result[this.realms.indexOf((Object)realm)] = ((RealmBase)realm).getObjectName();
        }
        return result;
    }

    public Realm[] getNestedRealms() {
        return this.realms.toArray(new Realm[0]);
    }

    @Override
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm) {
        Principal authenticatedUser = null;
        for (Realm realm : this.realms) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{username, realm.getClass().getName()}));
            }
            if ((authenticatedUser = realm.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realmName, digestA2, algorithm)) == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{username, realm.getClass().getName()}));
                continue;
            }
            if (!log.isDebugEnabled()) break;
            log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{username, realm.getClass().getName()}));
            break;
        }
        return authenticatedUser;
    }

    @Override
    public Principal authenticate(String username) {
        Principal authenticatedUser = null;
        for (Realm realm : this.realms) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{username, realm.getClass().getName()}));
            }
            if ((authenticatedUser = realm.authenticate(username)) == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{username, realm.getClass().getName()}));
                continue;
            }
            if (!log.isDebugEnabled()) break;
            log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{username, realm.getClass().getName()}));
            break;
        }
        return authenticatedUser;
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        Principal authenticatedUser = null;
        for (Realm realm : this.realms) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{username, realm.getClass().getName()}));
            }
            if ((authenticatedUser = realm.authenticate(username, credentials)) == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{username, realm.getClass().getName()}));
                continue;
            }
            if (!log.isDebugEnabled()) break;
            log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{username, realm.getClass().getName()}));
            break;
        }
        return authenticatedUser;
    }

    @Override
    public void setContainer(Container container) {
        for (Realm realm : this.realms) {
            if (realm instanceof RealmBase) {
                ((RealmBase)realm).setRealmPath(this.getRealmPath() + "/realm" + this.realms.indexOf(realm));
            }
            realm.setContainer(container);
        }
        super.setContainer(container);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        Iterator<Realm> iter = this.realms.iterator();
        while (iter.hasNext()) {
            Realm realm = iter.next();
            if (!(realm instanceof Lifecycle)) continue;
            try {
                ((Lifecycle)((Object)realm)).start();
            }
            catch (LifecycleException e) {
                iter.remove();
                log.error((Object)sm.getString("combinedRealm.realmStartFail", new Object[]{realm.getClass().getName()}), (Throwable)e);
            }
        }
        if (this.getCredentialHandler() == null) {
            super.setCredentialHandler(new CombinedRealmCredentialHandler());
        }
        super.startInternal();
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        for (Realm realm : this.realms) {
            if (!(realm instanceof Lifecycle)) continue;
            ((Lifecycle)((Object)realm)).stop();
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        for (Realm realm : this.realms) {
            if (!(realm instanceof Lifecycle)) continue;
            ((Lifecycle)((Object)realm)).destroy();
        }
        super.destroyInternal();
    }

    @Override
    public void backgroundProcess() {
        super.backgroundProcess();
        for (Realm r : this.realms) {
            r.backgroundProcess();
        }
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) {
        Principal authenticatedUser = null;
        String username = null;
        if (certs != null && certs.length > 0) {
            username = certs[0].getSubjectX500Principal().toString();
        }
        for (Realm realm : this.realms) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{username, realm.getClass().getName()}));
            }
            if ((authenticatedUser = realm.authenticate(certs)) == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{username, realm.getClass().getName()}));
                continue;
            }
            if (!log.isDebugEnabled()) break;
            log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{username, realm.getClass().getName()}));
            break;
        }
        return authenticatedUser;
    }

    @Override
    public Principal authenticate(GSSContext gssContext, boolean storeCred) {
        if (gssContext.isEstablished()) {
            Principal authenticatedUser = null;
            GSSName gssName = null;
            try {
                gssName = gssContext.getSrcName();
            }
            catch (GSSException e) {
                log.warn((Object)sm.getString("realmBase.gssNameFail"), (Throwable)e);
                return null;
            }
            for (Realm realm : this.realms) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{gssName, realm.getClass().getName()}));
                }
                if ((authenticatedUser = realm.authenticate(gssContext, storeCred)) == null) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{gssName, realm.getClass().getName()}));
                    continue;
                }
                if (!log.isDebugEnabled()) break;
                log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{gssName, realm.getClass().getName()}));
                break;
            }
            return authenticatedUser;
        }
        return null;
    }

    @Override
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        Principal authenticatedUser = null;
        for (Realm realm : this.realms) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("combinedRealm.authStart", new Object[]{gssName, realm.getClass().getName()}));
            }
            if ((authenticatedUser = realm.authenticate(gssName, gssCredential)) == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("combinedRealm.authFail", new Object[]{gssName, realm.getClass().getName()}));
                continue;
            }
            if (!log.isDebugEnabled()) break;
            log.debug((Object)sm.getString("combinedRealm.authSuccess", new Object[]{gssName, realm.getClass().getName()}));
            break;
        }
        return authenticatedUser;
    }

    @Override
    public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
        for (Realm realm : this.realms) {
            if (!realm.hasRole(wrapper, principal, role)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected String getPassword(String username) {
        UnsupportedOperationException uoe = new UnsupportedOperationException(sm.getString("combinedRealm.getPassword"));
        log.error((Object)sm.getString("combinedRealm.unexpectedMethod"), (Throwable)uoe);
        throw uoe;
    }

    @Override
    protected Principal getPrincipal(String username) {
        UnsupportedOperationException uoe = new UnsupportedOperationException(sm.getString("combinedRealm.getPrincipal"));
        log.error((Object)sm.getString("combinedRealm.unexpectedMethod"), (Throwable)uoe);
        throw uoe;
    }

    @Override
    public boolean isAvailable() {
        for (Realm realm : this.realms) {
            if (realm.isAvailable()) continue;
            return false;
        }
        return true;
    }

    @Override
    public void setCredentialHandler(CredentialHandler credentialHandler) {
        log.warn((Object)sm.getString("combinedRealm.setCredentialHandler"));
        super.setCredentialHandler(credentialHandler);
    }

    private class CombinedRealmCredentialHandler
    implements CredentialHandler {
        private CombinedRealmCredentialHandler() {
        }

        @Override
        public boolean matches(String inputCredentials, String storedCredentials) {
            for (Realm realm : CombinedRealm.this.realms) {
                if (!realm.getCredentialHandler().matches(inputCredentials, storedCredentials)) continue;
                return true;
            }
            return false;
        }

        @Override
        public String mutate(String inputCredentials) {
            if (CombinedRealm.this.realms.isEmpty()) {
                return null;
            }
            for (Realm realm : CombinedRealm.this.realms) {
                String mutatedCredentials = realm.getCredentialHandler().mutate(inputCredentials);
                if (mutatedCredentials == null) continue;
                return mutatedCredentials;
            }
            return null;
        }
    }
}

