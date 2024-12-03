/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 */
package org.apache.tomcat.websocket;

import java.util.ArrayList;
import java.util.List;
import javax.websocket.Extension;

public class WsExtension
implements Extension {
    private final String name;
    private final List<Extension.Parameter> parameters = new ArrayList<Extension.Parameter>();

    WsExtension(String name) {
        this.name = name;
    }

    void addParameter(Extension.Parameter parameter) {
        this.parameters.add(parameter);
    }

    public String getName() {
        return this.name;
    }

    public List<Extension.Parameter> getParameters() {
        return this.parameters;
    }
}

