/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.w3c.dom.DocumentFragment;

public class DocumentFragmentImpl
extends ParentNode
implements DocumentFragment {
    public DocumentFragmentImpl(CoreDocumentImpl ownerDoc) {
        super(ownerDoc);
    }

    @Override
    public short getNodeType() {
        return 11;
    }

    @Override
    public String getNodeName() {
        return "#document-fragment";
    }
}

