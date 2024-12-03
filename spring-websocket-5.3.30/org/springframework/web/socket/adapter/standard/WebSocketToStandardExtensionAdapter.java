/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 */
package org.springframework.web.socket.adapter.standard;

import java.util.ArrayList;
import java.util.List;
import javax.websocket.Extension;
import org.springframework.web.socket.WebSocketExtension;

public class WebSocketToStandardExtensionAdapter
implements Extension {
    private final String name;
    private final List<Extension.Parameter> parameters = new ArrayList<Extension.Parameter>();

    public WebSocketToStandardExtensionAdapter(final WebSocketExtension extension) {
        this.name = extension.getName();
        for (final String paramName : extension.getParameters().keySet()) {
            this.parameters.add(new Extension.Parameter(){

                public String getName() {
                    return paramName;
                }

                public String getValue() {
                    return extension.getParameters().get(paramName);
                }
            });
        }
    }

    public String getName() {
        return this.name;
    }

    public List<Extension.Parameter> getParameters() {
        return this.parameters;
    }
}

