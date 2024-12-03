/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.AbstractSAXParser
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package org.cyberneko.html.parsers;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

public class SAXParser
extends AbstractSAXParser {
    public SAXParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
    }
}

