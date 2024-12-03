/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Rule
 */
package org.apache.catalina.realm;

import org.apache.catalina.realm.MemoryRealm;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class MemoryUserRule
extends Rule {
    MemoryUserRule() {
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String username = attributes.getValue("username");
        if (username == null) {
            username = attributes.getValue("name");
        }
        String password = attributes.getValue("password");
        String roles = attributes.getValue("roles");
        MemoryRealm realm = (MemoryRealm)this.digester.peek(this.digester.getCount() - 1);
        realm.addUser(username, password, roles);
    }
}

