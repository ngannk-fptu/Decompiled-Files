/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.jvnet.staxex;

import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;

public interface XMLStreamWriterEx
extends XMLStreamWriter {
    public void writeBinary(byte[] var1, int var2, int var3, String var4) throws XMLStreamException;

    public void writeBinary(DataHandler var1) throws XMLStreamException;

    public OutputStream writeBinary(String var1) throws XMLStreamException;

    public void writePCDATA(CharSequence var1) throws XMLStreamException;

    @Override
    public NamespaceContextEx getNamespaceContext();
}

