/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.ext.stax.datahandler;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

public interface DataHandlerReader {
    public static final String PROPERTY = DataHandlerReader.class.getName();

    public boolean isBinary();

    public boolean isOptimized();

    public boolean isDeferred();

    public String getContentID();

    public DataHandler getDataHandler() throws XMLStreamException;

    public DataHandlerProvider getDataHandlerProvider();
}

