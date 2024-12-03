/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.badgerfish;

import org.codehaus.jettison.AbstractDOMDocumentParser;
import org.codehaus.jettison.badgerfish.BadgerFishXMLInputFactory;

public class BadgerFishDOMDocumentParser
extends AbstractDOMDocumentParser {
    public BadgerFishDOMDocumentParser() {
        super(new BadgerFishXMLInputFactory());
    }
}

