/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.IOException;
import software.amazon.ion.impl.PrivateByteTransferSink;

@Deprecated
public interface PrivateByteTransferReader {
    public void transferCurrentValue(PrivateByteTransferSink var1) throws IOException;
}

