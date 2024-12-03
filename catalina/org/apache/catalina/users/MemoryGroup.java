/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.users;

import org.apache.catalina.users.GenericGroup;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.security.Escape;

@Deprecated
public class MemoryGroup
extends GenericGroup<MemoryUserDatabase> {
    MemoryGroup(MemoryUserDatabase database, String groupname, String description) {
        super(database, groupname, description, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<group groupname=\"");
        sb.append(Escape.xml((String)this.groupname));
        sb.append("\"");
        if (this.description != null) {
            sb.append(" description=\"");
            sb.append(Escape.xml((String)this.description));
            sb.append("\"");
        }
        sb.append(" roles=\"");
        StringBuilder rsb = new StringBuilder();
        StringUtils.join((Iterable)this.roles, (char)',', x -> Escape.xml((String)x.getRolename()), (StringBuilder)rsb);
        sb.append((CharSequence)rsb);
        sb.append("\"");
        sb.append("/>");
        return sb.toString();
    }
}

