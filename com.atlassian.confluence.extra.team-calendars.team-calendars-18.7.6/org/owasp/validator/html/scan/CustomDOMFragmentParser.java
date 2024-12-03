/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.scan;

import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import net.sourceforge.htmlunit.cyberneko.parsers.DOMFragmentParser;
import org.owasp.validator.html.scan.ExtendedParser;

public class CustomDOMFragmentParser
extends DOMFragmentParser
implements ExtendedParser {
    @Override
    public HTMLConfiguration getHtmlConfiguration() {
        return (HTMLConfiguration)this.fParserConfiguration;
    }
}

