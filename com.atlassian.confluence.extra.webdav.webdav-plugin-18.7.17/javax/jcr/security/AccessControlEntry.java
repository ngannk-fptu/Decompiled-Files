/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.security;

import java.security.Principal;
import javax.jcr.security.Privilege;

public interface AccessControlEntry {
    public Principal getPrincipal();

    public Privilege[] getPrivileges();
}

