/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.badgerfish;

import java.io.OutputStream;
import org.codehaus.jettison.AbstractDOMDocumentSerializer;
import org.codehaus.jettison.badgerfish.BadgerFishXMLOutputFactory;

public class BadgerFishDOMDocumentSerializer
extends AbstractDOMDocumentSerializer {
    public BadgerFishDOMDocumentSerializer(OutputStream output) {
        super(output, new BadgerFishXMLOutputFactory());
    }
}

