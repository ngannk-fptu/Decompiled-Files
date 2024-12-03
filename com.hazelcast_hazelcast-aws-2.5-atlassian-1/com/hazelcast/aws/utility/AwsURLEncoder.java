/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastException
 */
package com.hazelcast.aws.utility;

import com.hazelcast.core.HazelcastException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class AwsURLEncoder {
    private AwsURLEncoder() {
    }

    public static String urlEncode(String string) {
        String encoded;
        try {
            encoded = URLEncoder.encode(string, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e) {
            throw new HazelcastException((Throwable)e);
        }
        return encoded;
    }
}

