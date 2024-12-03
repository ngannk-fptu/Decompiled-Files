/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.springframework.ldap.core.support.AbstractContextSource;

public class DirContextSource
extends AbstractContextSource {
    protected DirContext getDirContextInstance(Hashtable environment) throws NamingException {
        return new InitialDirContext(environment);
    }
}

