/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.NamingException;
import javax.naming.ldap.HasControls;
import org.springframework.ldap.core.ContextMapper;

public interface ContextMapperWithControls<T>
extends ContextMapper<T> {
    public T mapFromContextWithControls(Object var1, HasControls var2) throws NamingException;
}

