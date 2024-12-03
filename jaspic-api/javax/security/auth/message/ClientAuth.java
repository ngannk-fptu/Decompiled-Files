/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message;

import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;

public interface ClientAuth {
    public AuthStatus secureRequest(MessageInfo var1, Subject var2) throws AuthException;

    public AuthStatus validateResponse(MessageInfo var1, Subject var2, Subject var3) throws AuthException;

    public void cleanSubject(MessageInfo var1, Subject var2) throws AuthException;
}

