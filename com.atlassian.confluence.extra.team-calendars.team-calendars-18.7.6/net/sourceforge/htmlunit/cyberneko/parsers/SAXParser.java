/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.AbstractSAXParser
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package net.sourceforge.htmlunit.cyberneko.parsers;

import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class SAXParser
extends AbstractSAXParser {
    public SAXParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
    }
}

