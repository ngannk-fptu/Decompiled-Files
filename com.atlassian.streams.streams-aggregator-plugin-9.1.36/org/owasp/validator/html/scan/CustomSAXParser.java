/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.scan;

import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.parsers.AbstractSAXParser;
import org.owasp.validator.html.scan.CustomHtmlConfiguration;
import org.owasp.validator.html.scan.ExtendedParser;

public class CustomSAXParser
extends AbstractSAXParser
implements ExtendedParser {
    public CustomSAXParser() {
        super(new CustomHtmlConfiguration());
    }

    @Override
    public HTMLConfiguration getHtmlConfiguration() {
        return (HTMLConfiguration)this.fConfiguration;
    }
}

