/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

public abstract class Rule {
    protected static final StringManager sm = StringManager.getManager(Rule.class);
    protected Digester digester = null;
    protected String namespaceURI = null;

    public Digester getDigester() {
        return this.digester;
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
    }

    public void body(String namespace, String name, String text) throws Exception {
    }

    public void end(String namespace, String name) throws Exception {
    }

    public void finish() throws Exception {
    }
}

