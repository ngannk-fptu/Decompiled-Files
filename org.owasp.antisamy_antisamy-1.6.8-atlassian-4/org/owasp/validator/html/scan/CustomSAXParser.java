/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sourceforge.htmlunit.cyberneko.HTMLConfiguration
 *  org.apache.xerces.parsers.AbstractSAXParser
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package org.owasp.validator.html.scan;

import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.owasp.validator.html.scan.CustomHtmlConfiguration;
import org.owasp.validator.html.scan.ExtendedParser;

public class CustomSAXParser
extends AbstractSAXParser
implements ExtendedParser {
    public CustomSAXParser() {
        super((XMLParserConfiguration)new CustomHtmlConfiguration());
    }

    @Override
    public HTMLConfiguration getHtmlConfiguration() {
        return (HTMLConfiguration)this.fConfiguration;
    }
}

