/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.output;

import org.outerj.daisy.diff.html.dom.TagNode;
import org.xml.sax.SAXException;

public interface DiffOutput {
    public void generateOutput(TagNode var1) throws SAXException;
}

