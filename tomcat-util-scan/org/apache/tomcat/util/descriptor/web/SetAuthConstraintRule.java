/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetAuthConstraintRule
extends Rule {
    SetAuthConstraintRule() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        SecurityConstraint securityConstraint = (SecurityConstraint)this.digester.peek();
        securityConstraint.setAuthConstraint(true);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)"Calling SecurityConstraint.setAuthConstraint(true)");
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(securityConstraint)).append(".setAuthConstraint(true);");
            code.append(System.lineSeparator());
        }
    }
}

