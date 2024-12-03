/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.apache.tomcat.util.digester.Rule;

final class SoapHeaderRule
extends Rule {
    SoapHeaderRule() {
    }

    @Override
    public void body(String namespace, String name, String text) throws Exception {
        String namespaceuri = null;
        String localpart = text;
        int colon = text.indexOf(58);
        if (colon >= 0) {
            String prefix = text.substring(0, colon);
            namespaceuri = this.digester.findNamespaceURI(prefix);
            localpart = text.substring(colon + 1);
        }
        ContextHandler contextHandler = (ContextHandler)this.digester.peek();
        contextHandler.addSoapHeaders(localpart, namespaceuri);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(contextHandler)).append(".addSoapHeaders(\"");
            code.append(localpart).append("\", \"").append(namespaceuri).append("\");");
            code.append(System.lineSeparator());
        }
    }
}

