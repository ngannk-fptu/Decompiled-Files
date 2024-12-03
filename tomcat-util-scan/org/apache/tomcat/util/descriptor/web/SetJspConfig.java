/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetJspConfig
extends Rule {
    boolean isJspConfigSet = false;

    SetJspConfig() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isJspConfigSet) {
            throw new IllegalArgumentException("<jsp-config> element is limited to 1 occurrence");
        }
        this.isJspConfigSet = true;
    }
}

