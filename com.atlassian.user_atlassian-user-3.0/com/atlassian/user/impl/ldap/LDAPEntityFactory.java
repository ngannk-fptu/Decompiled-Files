/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import javax.naming.directory.Attributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LDAPEntityFactory<T extends Entity> {
    public T getEntity(Attributes var1, String var2) throws EntityException;
}

