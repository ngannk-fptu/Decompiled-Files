/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.OutputBuffer;

public interface HttpOutputBuffer
extends OutputBuffer {
    public void end() throws IOException;

    public void flush() throws IOException;
}

