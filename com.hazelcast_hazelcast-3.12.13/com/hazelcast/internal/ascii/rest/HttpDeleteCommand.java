/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.rest.HttpCommand;

public class HttpDeleteCommand
extends HttpCommand {
    public HttpDeleteCommand(String uri) {
        super(TextCommandConstants.TextCommandType.HTTP_DELETE, uri);
    }
}

