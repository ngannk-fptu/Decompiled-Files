/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.html.dom.HTMLDocumentImpl;
import org.htmlunit.cyberneko.html.dom.HTMLElementImpl;
import org.htmlunit.cyberneko.html.dom.HTMLFormControl;
import org.w3c.dom.html.HTMLFieldSetElement;

public class HTMLFieldSetElementImpl
extends HTMLElementImpl
implements HTMLFieldSetElement,
HTMLFormControl {
    public HTMLFieldSetElementImpl(HTMLDocumentImpl owner, String name) {
        super(owner, name);
    }
}

