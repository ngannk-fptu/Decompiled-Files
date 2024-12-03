/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.users;

import org.apache.catalina.users.GenericUser;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;

@Deprecated
public class MemoryUser
extends GenericUser<MemoryUserDatabase> {
    MemoryUser(MemoryUserDatabase database, String username, String password, String fullName) {
        super(database, username, password, fullName, null, null);
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder("<user username=\"");
        sb.append(Escape.xml((String)this.username));
        sb.append("\" password=\"");
        sb.append(Escape.xml((String)this.password));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(" fullName=\"");
            sb.append(Escape.xml((String)this.fullName));
            sb.append("\"");
        }
        sb.append(" groups=\"");
        StringUtils.join((Iterable)this.groups, (char)',', x -> Escape.xml((String)x.getGroupname()), (StringBuilder)sb);
        sb.append("\"");
        sb.append(" roles=\"");
        StringUtils.join((Iterable)this.roles, (char)',', x -> Escape.xml((String)x.getRolename()), (StringBuilder)sb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User username=\"");
        sb.append(Escape.xml((String)this.username));
        sb.append("\"");
        if (this.fullName != null) {
            sb.append(", fullName=\"");
            sb.append(Escape.xml((String)this.fullName));
            sb.append("\"");
        }
        sb.append(", groups=\"");
        StringUtils.join((Iterable)this.groups, (char)',', x -> Escape.xml((String)x.getGroupname()), (StringBuilder)sb);
        sb.append("\"");
        sb.append(", roles=\"");
        StringUtils.join((Iterable)this.roles, (char)',', x -> Escape.xml((String)x.getRolename()), (StringBuilder)sb);
        sb.append("\"");
        return sb.toString();
    }
}

