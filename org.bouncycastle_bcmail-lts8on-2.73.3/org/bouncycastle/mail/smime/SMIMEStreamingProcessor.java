/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;

public interface SMIMEStreamingProcessor {
    public void write(OutputStream var1) throws IOException;
}

