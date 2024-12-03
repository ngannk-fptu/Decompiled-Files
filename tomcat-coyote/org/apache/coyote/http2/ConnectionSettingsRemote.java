/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.ConnectionSettingsBase;
import org.apache.coyote.http2.Http2Error;

class ConnectionSettingsRemote
extends ConnectionSettingsBase<ConnectionException> {
    private static final String ENDPOINT_NAME = "Remote(server->client)";

    ConnectionSettingsRemote(String connectionId) {
        super(connectionId);
    }

    @Override
    final void throwException(String msg, Http2Error error) throws ConnectionException {
        throw new ConnectionException(msg, error);
    }

    @Override
    final String getEndpointName() {
        return ENDPOINT_NAME;
    }
}

