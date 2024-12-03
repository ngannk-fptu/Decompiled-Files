/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.Writer;
import javanet.staxutils.BaseXMLOutputFactory;
import javanet.staxutils.io.StAXStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class OutputFactory
extends BaseXMLOutputFactory {
    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return new StAXStreamWriter(stream);
    }
}

