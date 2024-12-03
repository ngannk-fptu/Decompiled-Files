/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.realm;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.catalina.realm.JAASRealm;
import org.apache.tomcat.util.res.StringManager;

public class JAASCallbackHandler
implements CallbackHandler {
    protected static final StringManager sm = StringManager.getManager(JAASCallbackHandler.class);
    protected final String password;
    protected final JAASRealm realm;
    protected final String username;
    protected final String nonce;
    protected final String nc;
    protected final String cnonce;
    protected final String qop;
    protected final String realmName;
    protected final String digestA2;
    protected final String authMethod;

    public JAASCallbackHandler(JAASRealm realm, String username, String password) {
        this(realm, username, password, null, null, null, null, null, null, null, null);
    }

    public JAASCallbackHandler(JAASRealm realm, String username, String password, String nonce, String nc, String cnonce, String qop, String realmName, String digestA2, String algorithm, String authMethod) {
        this.realm = realm;
        this.username = username;
        this.password = password != null && realm.hasMessageDigest(algorithm) ? realm.getCredentialHandler().mutate(password) : password;
        this.nonce = nonce;
        this.nc = nc;
        this.cnonce = cnonce;
        this.qop = qop;
        this.realmName = realmName;
        this.digestA2 = digestA2;
        this.authMethod = authMethod;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                if (this.realm.getContainer().getLogger().isTraceEnabled()) {
                    this.realm.getContainer().getLogger().trace((Object)sm.getString("jaasCallback.username", new Object[]{this.username}));
                }
                ((NameCallback)callback).setName(this.username);
                continue;
            }
            if (callback instanceof PasswordCallback) {
                char[] passwordcontents = this.password != null ? this.password.toCharArray() : new char[]{};
                ((PasswordCallback)callback).setPassword(passwordcontents);
                continue;
            }
            if (callback instanceof TextInputCallback) {
                TextInputCallback cb = (TextInputCallback)callback;
                if (cb.getPrompt().equals("nonce")) {
                    cb.setText(this.nonce);
                    continue;
                }
                if (cb.getPrompt().equals("nc")) {
                    cb.setText(this.nc);
                    continue;
                }
                if (cb.getPrompt().equals("cnonce")) {
                    cb.setText(this.cnonce);
                    continue;
                }
                if (cb.getPrompt().equals("qop")) {
                    cb.setText(this.qop);
                    continue;
                }
                if (cb.getPrompt().equals("realmName")) {
                    cb.setText(this.realmName);
                    continue;
                }
                if (cb.getPrompt().equals("digestA2")) {
                    cb.setText(this.digestA2);
                    continue;
                }
                if (cb.getPrompt().equals("authMethod")) {
                    cb.setText(this.authMethod);
                    continue;
                }
                if (cb.getPrompt().equals("catalinaBase")) {
                    cb.setText(this.realm.getContainer().getCatalinaBase().getAbsolutePath());
                    continue;
                }
                throw new UnsupportedCallbackException(callback);
            }
            throw new UnsupportedCallbackException(callback);
        }
    }
}

