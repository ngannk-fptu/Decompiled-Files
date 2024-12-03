/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.callback;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

public class CallerPrincipalCallback
implements Callback {
    private final Subject subject;
    private final Principal principal;
    private final String name;

    public CallerPrincipalCallback(Subject subject, Principal principal) {
        this.subject = subject;
        this.principal = principal;
        this.name = null;
    }

    public CallerPrincipalCallback(Subject subject, String name) {
        this.subject = subject;
        this.principal = null;
        this.name = name;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public Principal getPrincipal() {
        return this.principal;
    }

    public String getName() {
        return this.name;
    }
}

