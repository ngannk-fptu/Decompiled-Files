/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.pool.validation;

import javax.naming.directory.DirContext;
import org.springframework.ldap.pool.DirContextType;

public interface DirContextValidator {
    public boolean validateDirContext(DirContextType var1, DirContext var2);
}

