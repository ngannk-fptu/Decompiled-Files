/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.rest.HttpCommand;

public class HttpGetCommand
extends HttpCommand {
    public HttpGetCommand(String uri) {
        super(TextCommandConstants.TextCommandType.HTTP_GET, uri);
    }
}

