/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import java.security.PrivilegedAction;
import javax.ws.rs.core.SecurityContext;

public interface SubjectSecurityContext
extends SecurityContext {
    public Object doAsSubject(PrivilegedAction var1);
}

