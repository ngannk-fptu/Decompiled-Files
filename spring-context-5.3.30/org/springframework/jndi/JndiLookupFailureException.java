/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 */
package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.core.NestedRuntimeException;

public class JndiLookupFailureException
extends NestedRuntimeException {
    public JndiLookupFailureException(String msg, NamingException cause) {
        super(msg, (Throwable)cause);
    }
}

