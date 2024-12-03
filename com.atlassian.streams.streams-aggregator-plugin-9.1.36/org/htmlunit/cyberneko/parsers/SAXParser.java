/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.parsers;

import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.parsers.AbstractSAXParser;

public class SAXParser
extends AbstractSAXParser {
    public SAXParser() {
        super(new HTMLConfiguration());
    }
}

