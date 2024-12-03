/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.parsers;

import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.dom.DocumentImpl;
import org.htmlunit.cyberneko.xerces.parsers.AbstractDOMParser;

public class DOMParser
extends AbstractDOMParser {
    public DOMParser(Class<? extends DocumentImpl> documentClass) {
        super(new HTMLConfiguration(), documentClass);
    }
}

