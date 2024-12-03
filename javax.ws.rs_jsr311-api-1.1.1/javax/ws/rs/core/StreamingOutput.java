/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;

public interface StreamingOutput {
    public void write(OutputStream var1) throws IOException, WebApplicationException;
}

