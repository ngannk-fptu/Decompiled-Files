/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public interface LowLevelFastInfosetStreamWriter {
    public void initiateLowLevelWriting() throws XMLStreamException;

    public int getNextElementIndex();

    public int getNextAttributeIndex();

    public int getLocalNameIndex();

    public int getNextLocalNameIndex();

    public void writeLowLevelTerminationAndMark() throws IOException;

    public void writeLowLevelStartElementIndexed(int var1, int var2) throws IOException;

    public boolean writeLowLevelStartElement(int var1, String var2, String var3, String var4) throws IOException;

    public void writeLowLevelStartNamespaces() throws IOException;

    public void writeLowLevelNamespace(String var1, String var2) throws IOException;

    public void writeLowLevelEndNamespaces() throws IOException;

    public void writeLowLevelStartAttributes() throws IOException;

    public void writeLowLevelAttributeIndexed(int var1) throws IOException;

    public boolean writeLowLevelAttribute(String var1, String var2, String var3) throws IOException;

    public void writeLowLevelAttributeValue(String var1) throws IOException;

    public void writeLowLevelStartNameLiteral(int var1, String var2, byte[] var3, String var4) throws IOException;

    public void writeLowLevelStartNameLiteral(int var1, String var2, int var3, String var4) throws IOException;

    public void writeLowLevelEndStartElement() throws IOException;

    public void writeLowLevelEndElement() throws IOException;

    public void writeLowLevelText(char[] var1, int var2) throws IOException;

    public void writeLowLevelText(String var1) throws IOException;

    public void writeLowLevelOctets(byte[] var1, int var2) throws IOException;
}

