/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.AbstractObjectCreationFactory
 */
package org.apache.catalina.users;

import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

class MemoryGroupCreationFactory
extends AbstractObjectCreationFactory {
    private final MemoryUserDatabase database;

    MemoryGroupCreationFactory(MemoryUserDatabase database) {
        this.database = database;
    }

    public Object createObject(Attributes attributes) {
        String groupname = attributes.getValue("groupname");
        if (groupname == null) {
            groupname = attributes.getValue("name");
        }
        String description = attributes.getValue("description");
        String roles = attributes.getValue("roles");
        Group group = this.database.findGroup(groupname);
        if (group == null) {
            group = this.database.createGroup(groupname, description);
        } else if (group.getDescription() == null) {
            group.setDescription(description);
        }
        if (roles != null) {
            while (roles.length() > 0) {
                String rolename = null;
                int comma = roles.indexOf(44);
                if (comma >= 0) {
                    rolename = roles.substring(0, comma).trim();
                    roles = roles.substring(comma + 1);
                } else {
                    rolename = roles.trim();
                    roles = "";
                }
                if (rolename.length() <= 0) continue;
                Role role = this.database.findRole(rolename);
                if (role == null) {
                    role = this.database.createRole(rolename, null);
                }
                group.addRole(role);
            }
        }
        return group;
    }
}

