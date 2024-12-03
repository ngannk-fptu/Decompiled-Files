/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.ext.stax.datahandler;

import java.io.IOException;
import javax.activation.DataHandler;

public interface DataHandlerProvider {
    public boolean isLoaded();

    public DataHandler getDataHandler() throws IOException;
}

