/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.io.OutputStream;
import org.codehaus.jettison.AbstractDOMDocumentSerializer;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;

public class MappedDOMDocumentSerializer
extends AbstractDOMDocumentSerializer {
    public MappedDOMDocumentSerializer(OutputStream output, Configuration con) {
        super(output, new MappedXMLOutputFactory(con));
    }
}

