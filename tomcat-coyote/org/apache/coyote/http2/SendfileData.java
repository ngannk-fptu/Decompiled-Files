/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import org.apache.coyote.http2.Stream;

class SendfileData {
    Path path;
    Stream stream;
    MappedByteBuffer mappedBuffer;
    long left;
    int streamReservation;
    int connectionReservation;
    long pos;
    long end;

    SendfileData() {
    }
}

