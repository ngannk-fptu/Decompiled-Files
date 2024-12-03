/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import java.net.UnknownHostException;

public class BannedHostException
extends UnknownHostException {
    public BannedHostException(String desc) {
        super(desc);
    }
}

