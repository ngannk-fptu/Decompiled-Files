/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetSessionConfig
extends Rule {
    boolean isSessionConfigSet = false;

    SetSessionConfig() {
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.isSessionConfigSet) {
            throw new IllegalArgumentException("<session-config> element is limited to 1 occurrence");
        }
        this.isSessionConfigSet = true;
    }
}

