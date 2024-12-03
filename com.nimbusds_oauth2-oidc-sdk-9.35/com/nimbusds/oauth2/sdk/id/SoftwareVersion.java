/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class SoftwareVersion
extends Identifier {
    private static final long serialVersionUID = -7983464258144627949L;

    public SoftwareVersion(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SoftwareVersion && this.toString().equals(object.toString());
    }
}

