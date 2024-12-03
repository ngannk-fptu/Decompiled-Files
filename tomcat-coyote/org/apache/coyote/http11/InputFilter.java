/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 */
package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.tomcat.util.buf.ByteChunk;

public interface InputFilter
extends InputBuffer {
    public void setRequest(Request var1);

    public void recycle();

    public ByteChunk getEncodingName();

    public void setBuffer(InputBuffer var1);

    public long end() throws IOException;

    public boolean isFinished();
}

