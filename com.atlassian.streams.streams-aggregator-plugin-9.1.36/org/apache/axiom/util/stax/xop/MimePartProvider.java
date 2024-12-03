/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import javax.activation.DataHandler;

public interface MimePartProvider {
    public boolean isLoaded(String var1);

    public DataHandler getDataHandler(String var1) throws IOException;
}

