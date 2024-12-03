/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.AbstractRole;

public class GenericRole<UD extends UserDatabase>
extends AbstractRole {
    protected final UserDatabase database;

    GenericRole(UD database, String rolename, String description) {
        this.database = database;
        this.rolename = rolename;
        this.description = description;
    }

    @Override
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override
    public void setDescription(String description) {
        this.database.modifiedRole(this);
        super.setDescription(description);
    }

    @Override
    public void setRolename(String rolename) {
        this.database.modifiedRole(this);
        super.setRolename(rolename);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericRole) {
            GenericRole role = (GenericRole)obj;
            return role.database == this.database && this.rolename.equals(role.getRolename());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.database == null ? 0 : this.database.hashCode());
        result = 31 * result + (this.rolename == null ? 0 : this.rolename.hashCode());
        return result;
    }
}

