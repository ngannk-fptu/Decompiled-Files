/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public class Gender
extends Identifier {
    public static final Gender FEMALE = new Gender("female");
    public static final Gender MALE = new Gender("male");

    public Gender(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Gender && this.toString().equals(object.toString());
    }
}

