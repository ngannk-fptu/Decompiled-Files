/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetLoginConfig
extends Rule {
    boolean isLoginConfigSet = false;

    SetLoginConfig() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isLoginConfigSet) {
            throw new IllegalArgumentException("<login-config> element is limited to 1 occurrence");
        }
        this.isLoginConfigSet = true;
    }
}

