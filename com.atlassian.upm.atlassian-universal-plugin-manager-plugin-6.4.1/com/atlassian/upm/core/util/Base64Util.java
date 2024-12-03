/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.util;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Base64Util.class);

    public byte[] tryDecodeBase64(String value) {
        try {
            return Base64.decodeBase64((byte[])value.getBytes(StandardCharsets.UTF_8));
        }
        catch (IllegalArgumentException exception) {
            LOGGER.warn("The {} string is not on Base64 format and will not be decoded.", (Object)value);
            return value.getBytes();
        }
    }
}

