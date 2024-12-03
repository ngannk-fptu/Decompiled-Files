/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.badgerfish;

import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.jettison.AbstractXMLOutputFactory;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;

public class BadgerFishXMLOutputFactory
extends AbstractXMLOutputFactory {
    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return new BadgerFishXMLStreamWriter(writer);
    }
}

