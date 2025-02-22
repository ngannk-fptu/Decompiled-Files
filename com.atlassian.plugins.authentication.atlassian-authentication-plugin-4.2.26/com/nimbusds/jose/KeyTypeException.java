/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.KeyException;
import java.security.Key;
import java.util.Arrays;

public class KeyTypeException
extends KeyException {
    public KeyTypeException(Class<? extends Key> expectedKeyClass) {
        super("Invalid key: Must be an instance of " + expectedKeyClass);
    }

    public KeyTypeException(Class<? extends Key> expectedKeyInterface, Class<?> ... additionalInterfaces) {
        super("Invalid key: Must be an instance of " + expectedKeyInterface + " and implement all of the following interfaces " + Arrays.toString(additionalInterfaces));
    }
}

