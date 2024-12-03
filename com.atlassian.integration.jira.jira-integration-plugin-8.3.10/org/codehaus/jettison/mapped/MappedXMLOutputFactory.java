/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.io.Writer;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.jettison.AbstractXMLOutputFactory;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class MappedXMLOutputFactory
extends AbstractXMLOutputFactory {
    private MappedNamespaceConvention convention;

    public MappedXMLOutputFactory(Map<?, ?> nstojns) {
        this(new Configuration(nstojns));
    }

    public MappedXMLOutputFactory(Configuration config) {
        this.convention = new MappedNamespaceConvention(config);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return new MappedXMLStreamWriter(this.convention, writer);
    }
}

