/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

final class ByteArrayOutputStreamToInputStream
extends ByteArrayOutputStream {
    ByteArrayOutputStreamToInputStream() {
    }

    ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }
}

