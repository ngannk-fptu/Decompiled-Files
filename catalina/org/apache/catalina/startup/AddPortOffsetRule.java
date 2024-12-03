/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Rule
 */
package org.apache.catalina.startup;

import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class AddPortOffsetRule
extends Rule {
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Connector conn = (Connector)this.digester.peek();
        Server server = (Server)this.digester.peek(2);
        int portOffset = server.getPortOffset();
        conn.setPortOffset(portOffset);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName((Object)conn)).append(".setPortOffset(");
            code.append(this.digester.toVariableName((Object)server)).append(".getPortOffset());");
            code.append(System.lineSeparator());
        }
    }
}

