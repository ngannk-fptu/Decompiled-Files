/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.ext.stax.datahandler;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

public interface DataHandlerWriter {
    public static final String PROPERTY = DataHandlerWriter.class.getName();

    public void writeDataHandler(DataHandler var1, String var2, boolean var3) throws IOException, XMLStreamException;

    public void writeDataHandler(DataHandlerProvider var1, String var2, boolean var3) throws IOException, XMLStreamException;
}

