/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.activation.DataHandler;

public interface MultipartWriter {
    public OutputStream writePart(String var1, String var2, String var3) throws IOException;

    public OutputStream writePart(String var1, String var2, String var3, List var4) throws IOException;

    public void writePart(DataHandler var1, String var2, String var3) throws IOException;

    public void writePart(DataHandler var1, String var2, String var3, List var4) throws IOException;

    public void complete() throws IOException;
}

