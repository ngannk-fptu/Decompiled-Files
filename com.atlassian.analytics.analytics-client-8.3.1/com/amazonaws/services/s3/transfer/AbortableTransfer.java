/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.transfer.Transfer;
import java.io.IOException;

public interface AbortableTransfer
extends Transfer {
    public void abort() throws IOException;
}

