/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class TaglibLocationRule
extends Rule {
    final boolean isServlet24OrLater;

    TaglibLocationRule(boolean isServlet24OrLater) {
        this.isServlet24OrLater = isServlet24OrLater;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        boolean havePublicId;
        WebXml webXml = (WebXml)this.digester.peek(this.digester.getCount() - 1);
        boolean bl = havePublicId = webXml.getPublicId() != null;
        if (havePublicId == this.isServlet24OrLater) {
            throw new IllegalArgumentException("taglib definition not consistent with specification version");
        }
    }
}

