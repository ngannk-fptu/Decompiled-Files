/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.module;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.ServerAuth;

public interface ServerAuthModule
extends ServerAuth {
    public void initialize(MessagePolicy var1, MessagePolicy var2, CallbackHandler var3, Map var4) throws AuthException;

    public Class[] getSupportedMessageTypes();
}

