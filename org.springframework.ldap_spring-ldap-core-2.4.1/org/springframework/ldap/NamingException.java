/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 */
package org.springframework.ldap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.naming.Name;
import org.springframework.core.NestedRuntimeException;

public abstract class NamingException
extends NestedRuntimeException {
    private Throwable cause;

    public Throwable getCause() {
        return this.cause == this ? null : this.cause;
    }

    public NamingException(String msg) {
        super(msg);
    }

    public NamingException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public NamingException(Throwable cause) {
        this(cause != null ? cause.getMessage() : null, cause);
    }

    public String getExplanation() {
        if (this.getCause() instanceof javax.naming.NamingException) {
            return ((javax.naming.NamingException)this.getCause()).getExplanation();
        }
        return null;
    }

    public Name getRemainingName() {
        if (this.getCause() instanceof javax.naming.NamingException) {
            return ((javax.naming.NamingException)this.getCause()).getRemainingName();
        }
        return null;
    }

    public Name getResolvedName() {
        if (this.getCause() instanceof javax.naming.NamingException) {
            return ((javax.naming.NamingException)this.getCause()).getResolvedName();
        }
        return null;
    }

    public Object getResolvedObj() {
        if (this.getCause() instanceof javax.naming.NamingException) {
            return ((javax.naming.NamingException)this.getCause()).getResolvedObj();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        Object resolvedObj = this.getResolvedObj();
        boolean serializable = resolvedObj instanceof Serializable;
        if (resolvedObj != null && !serializable) {
            javax.naming.NamingException namingException = (javax.naming.NamingException)this.getCause();
            namingException.setResolvedObj(null);
            try {
                stream.defaultWriteObject();
            }
            finally {
                namingException.setResolvedObj(resolvedObj);
            }
        } else {
            stream.defaultWriteObject();
        }
    }
}

