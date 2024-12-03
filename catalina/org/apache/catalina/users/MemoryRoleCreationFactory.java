/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.AbstractObjectCreationFactory
 */
package org.apache.catalina.users;

import org.apache.catalina.Role;
import org.apache.catalina.users.MemoryUserDatabase;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

class MemoryRoleCreationFactory
extends AbstractObjectCreationFactory {
    private final MemoryUserDatabase database;

    MemoryRoleCreationFactory(MemoryUserDatabase database) {
        this.database = database;
    }

    public Object createObject(Attributes attributes) {
        String rolename = attributes.getValue("rolename");
        if (rolename == null) {
            rolename = attributes.getValue("name");
        }
        String description = attributes.getValue("description");
        Role existingRole = this.database.findRole(rolename);
        if (existingRole == null) {
            return this.database.createRole(rolename, description);
        }
        if (existingRole.getDescription() == null) {
            existingRole.setDescription(description);
        }
        return existingRole;
    }
}

