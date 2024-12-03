/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class BackChannelTokenDeliveryMode
extends Identifier {
    private static final long serialVersionUID = -7661605920720830935L;
    public static final BackChannelTokenDeliveryMode PUSH = new BackChannelTokenDeliveryMode("push");
    public static final BackChannelTokenDeliveryMode POLL = new BackChannelTokenDeliveryMode("poll");
    public static final BackChannelTokenDeliveryMode PING = new BackChannelTokenDeliveryMode("ping");

    public BackChannelTokenDeliveryMode(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BackChannelTokenDeliveryMode && this.toString().equals(object.toString());
    }

    public static BackChannelTokenDeliveryMode parse(String value) throws ParseException {
        if (PING.getValue().equals(value)) {
            return PING;
        }
        if (POLL.getValue().equals(value)) {
            return POLL;
        }
        if (PUSH.getValue().equals(value)) {
            return PUSH;
        }
        throw new ParseException("Invalid CIBA token delivery mode: " + value);
    }
}

