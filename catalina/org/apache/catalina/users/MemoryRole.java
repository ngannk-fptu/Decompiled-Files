/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.users;

import org.apache.catalina.users.GenericRole;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.tomcat.util.security.Escape;

@Deprecated
public class MemoryRole
extends GenericRole<MemoryUserDatabase> {
    MemoryRole(MemoryUserDatabase database, String rolename, String description) {
        super(database, rolename, description);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<role rolename=\"");
        sb.append(Escape.xml((String)this.rolename));
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(Escape.xml((String)this.description));
            sb.append("\"");
        }
        sb.append("/>");
        return sb.toString();
    }
}

