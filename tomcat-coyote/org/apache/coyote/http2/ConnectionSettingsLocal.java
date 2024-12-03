/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.util.Map;
import org.apache.coyote.http2.ByteUtil;
import org.apache.coyote.http2.ConnectionSettingsBase;
import org.apache.coyote.http2.FrameType;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Setting;

class ConnectionSettingsLocal
extends ConnectionSettingsBase<IllegalArgumentException> {
    private static final String ENDPOINT_NAME = "Local(client->server)";
    private boolean sendInProgress = false;

    ConnectionSettingsLocal(String connectionId) {
        super(connectionId);
    }

    @Override
    final synchronized void set(Setting setting, Long value) {
        this.checkSend();
        if (((Long)this.current.get((Object)setting)).longValue() == value.longValue()) {
            this.pending.remove((Object)setting);
        } else {
            this.pending.put(setting, value);
        }
    }

    final synchronized byte[] getSettingsFrameForPending() {
        this.checkSend();
        int payloadSize = this.pending.size() * 6;
        byte[] result = new byte[9 + payloadSize];
        ByteUtil.setThreeBytes(result, 0, payloadSize);
        result[3] = FrameType.SETTINGS.getIdByte();
        int pos = 9;
        for (Map.Entry setting : this.pending.entrySet()) {
            ByteUtil.setTwoBytes(result, pos, ((Setting)((Object)setting.getKey())).getId());
            ByteUtil.setFourBytes(result, pos += 2, (Long)setting.getValue());
            pos += 4;
        }
        this.sendInProgress = true;
        return result;
    }

    final synchronized boolean ack() {
        if (this.sendInProgress) {
            this.sendInProgress = false;
            this.current.putAll(this.pending);
            this.pending.clear();
            return true;
        }
        return false;
    }

    private void checkSend() {
        if (this.sendInProgress) {
            throw new IllegalStateException();
        }
    }

    @Override
    final void throwException(String msg, Http2Error error) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }

    @Override
    final String getEndpointName() {
        return ENDPOINT_NAME;
    }
}

