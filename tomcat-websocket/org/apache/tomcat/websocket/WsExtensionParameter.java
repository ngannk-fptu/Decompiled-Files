/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension$Parameter
 */
package org.apache.tomcat.websocket;

import javax.websocket.Extension;

public class WsExtensionParameter
implements Extension.Parameter {
    private final String name;
    private final String value;

    WsExtensionParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}

