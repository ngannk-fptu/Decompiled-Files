/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.security.Principal;
import org.apache.catalina.UserDatabase;

public interface Role
extends Principal {
    public String getDescription();

    public void setDescription(String var1);

    public String getRolename();

    public void setRolename(String var1);

    public UserDatabase getUserDatabase();
}

