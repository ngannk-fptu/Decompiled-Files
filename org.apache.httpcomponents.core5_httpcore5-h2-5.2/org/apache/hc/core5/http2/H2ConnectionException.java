/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2;

import java.io.IOException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.util.Args;

public class H2ConnectionException
extends IOException {
    private static final long serialVersionUID = -2014204317155428658L;
    private final int code;

    public H2ConnectionException(H2Error error, String message) {
        super(message);
        Args.notNull((Object)((Object)error), (String)"H2 Error code");
        this.code = error.getCode();
    }

    public H2ConnectionException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

