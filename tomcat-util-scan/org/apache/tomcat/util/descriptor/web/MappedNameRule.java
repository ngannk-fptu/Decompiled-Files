/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.digester.Rule;

final class MappedNameRule
extends Rule {
    MappedNameRule() {
    }

    @Override
    public void body(String namespace, String name, String text) throws Exception {
        ResourceBase resourceBase = (ResourceBase)this.digester.peek();
        resourceBase.setProperty("mappedName", text.trim());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(resourceBase));
            code.append(".setProperty(\"mappedName\", \"").append(text.trim()).append("\");");
            code.append(System.lineSeparator());
        }
    }
}

