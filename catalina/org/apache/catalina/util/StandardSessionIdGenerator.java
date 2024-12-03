/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.util;

import org.apache.catalina.util.SessionIdGeneratorBase;

public class StandardSessionIdGenerator
extends SessionIdGeneratorBase {
    @Override
    public String generateSessionId(String route) {
        byte[] random = new byte[16];
        int sessionIdLength = this.getSessionIdLength();
        StringBuilder buffer = new StringBuilder(2 * sessionIdLength + 20);
        int resultLenBytes = 0;
        while (resultLenBytes < sessionIdLength) {
            this.getRandomBytes(random);
            for (int j = 0; j < random.length && resultLenBytes < sessionIdLength; ++resultLenBytes, ++j) {
                byte b1 = (byte)((random[j] & 0xF0) >> 4);
                byte b2 = (byte)(random[j] & 0xF);
                if (b1 < 10) {
                    buffer.append((char)(48 + b1));
                } else {
                    buffer.append((char)(65 + (b1 - 10)));
                }
                if (b2 < 10) {
                    buffer.append((char)(48 + b2));
                    continue;
                }
                buffer.append((char)(65 + (b2 - 10)));
            }
        }
        if (route != null && route.length() > 0) {
            buffer.append('.').append(route);
        } else {
            String jvmRoute = this.getJvmRoute();
            if (jvmRoute != null && jvmRoute.length() > 0) {
                buffer.append('.').append(jvmRoute);
            }
        }
        return buffer.toString();
    }
}

