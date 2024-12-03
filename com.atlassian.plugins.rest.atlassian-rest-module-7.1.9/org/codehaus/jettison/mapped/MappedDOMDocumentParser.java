/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import org.codehaus.jettison.AbstractDOMDocumentParser;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;

public class MappedDOMDocumentParser
extends AbstractDOMDocumentParser {
    public MappedDOMDocumentParser(Configuration con) {
        super(new MappedXMLInputFactory(con));
    }
}

