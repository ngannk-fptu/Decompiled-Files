/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ServletDef;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class ServletDefCreateRule
extends Rule {
    ServletDefCreateRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        ServletDef servletDef = new ServletDef();
        this.digester.push(servletDef);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)("new " + servletDef.getClass().getName()));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(ServletDef.class.getName()).append(' ').append(this.digester.toVariableName(servletDef)).append(" = new ");
            code.append(ServletDef.class.getName()).append("();").append(System.lineSeparator());
        }
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        StringBuilder code;
        ServletDef servletDef = (ServletDef)this.digester.pop();
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)("pop " + servletDef.getClass().getName()));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
        }
    }
}

