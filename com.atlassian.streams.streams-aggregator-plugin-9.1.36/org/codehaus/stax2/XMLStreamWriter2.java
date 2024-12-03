/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.typed.TypedXMLStreamWriter;
import org.codehaus.stax2.validation.Validatable;

public interface XMLStreamWriter2
extends TypedXMLStreamWriter,
Validatable {
    public boolean isPropertySupported(String var1);

    public boolean setProperty(String var1, Object var2);

    public XMLStreamLocation2 getLocation();

    public String getEncoding();

    public void writeCData(char[] var1, int var2, int var3) throws XMLStreamException;

    public void writeDTD(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public void writeFullEndElement() throws XMLStreamException;

    public void writeStartDocument(String var1, String var2, boolean var3) throws XMLStreamException;

    public void writeSpace(String var1) throws XMLStreamException;

    public void writeSpace(char[] var1, int var2, int var3) throws XMLStreamException;

    public void writeRaw(String var1) throws XMLStreamException;

    public void writeRaw(String var1, int var2, int var3) throws XMLStreamException;

    public void writeRaw(char[] var1, int var2, int var3) throws XMLStreamException;

    public void copyEventFromReader(XMLStreamReader2 var1, boolean var2) throws XMLStreamException;

    public void closeCompletely() throws XMLStreamException;
}

