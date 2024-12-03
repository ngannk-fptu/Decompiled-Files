/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.TransformStateSetter
 */
package org.apache.xalan.transformer;

import javax.xml.transform.Transformer;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.serializer.TransformStateSetter;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public interface TransformState
extends TransformStateSetter {
    public ElemTemplateElement getCurrentElement();

    public Node getCurrentNode();

    public ElemTemplate getCurrentTemplate();

    public ElemTemplate getMatchedTemplate();

    public Node getMatchedNode();

    public NodeIterator getContextNodeList();

    public Transformer getTransformer();
}

