/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 *  org.springframework.util.LinkedCaseInsensitiveMap
 */
package org.springframework.web.socket.adapter.standard;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.websocket.Extension;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.socket.WebSocketExtension;

public class StandardToWebSocketExtensionAdapter
extends WebSocketExtension {
    public StandardToWebSocketExtensionAdapter(Extension extension) {
        super(extension.getName(), StandardToWebSocketExtensionAdapter.initParameters(extension));
    }

    private static Map<String, String> initParameters(Extension extension) {
        List parameters = extension.getParameters();
        LinkedCaseInsensitiveMap result = new LinkedCaseInsensitiveMap(parameters.size(), Locale.ENGLISH);
        for (Extension.Parameter parameter : parameters) {
            result.put(parameter.getName(), parameter.getValue());
        }
        return result;
    }
}

