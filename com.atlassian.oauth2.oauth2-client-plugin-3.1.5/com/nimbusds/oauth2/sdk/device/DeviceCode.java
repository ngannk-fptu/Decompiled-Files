/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class DeviceCode
extends Identifier {
    private static final long serialVersionUID = 5165818656410539254L;

    public DeviceCode(String value) {
        super(value);
    }

    public DeviceCode(int byteLength) {
        super(byteLength);
    }

    public DeviceCode() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof DeviceCode && this.toString().equals(object.toString());
    }
}

