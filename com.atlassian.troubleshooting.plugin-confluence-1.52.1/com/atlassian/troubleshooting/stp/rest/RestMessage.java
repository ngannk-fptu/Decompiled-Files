/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.action.Message;
import java.io.Serializable;
import java.util.LinkedHashMap;

public class RestMessage
extends LinkedHashMap<String, Object>
implements Serializable {
    private static final long serialVersionUID = 7599389932000859497L;

    public RestMessage(Message message) {
        this.put("name", message.getName());
        this.put("body", message.getBody());
    }
}

