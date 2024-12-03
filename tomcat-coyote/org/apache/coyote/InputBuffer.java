/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import java.io.IOException;
import org.apache.tomcat.util.net.ApplicationBufferHandler;

public interface InputBuffer {
    public int doRead(ApplicationBufferHandler var1) throws IOException;

    public int available();
}

