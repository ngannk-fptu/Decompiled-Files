/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.PredicatedNodeTest;
import org.apache.xpath.axes.SubContextList;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.StepPattern;

public class FuncCurrent
extends Function {
    static final long serialVersionUID = 5715316804877715008L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        SubContextList subContextList = xctxt.getCurrentNodeList();
        int currentNode = -1;
        if (null != subContextList) {
            if (subContextList instanceof PredicatedNodeTest) {
                LocPathIterator iter = ((PredicatedNodeTest)subContextList).getLocPathIterator();
                currentNode = iter.getCurrentContextNode();
            } else if (subContextList instanceof StepPattern) {
                throw new RuntimeException(XSLMessages.createMessage("ER_PROCESSOR_ERROR", null));
            }
        } else {
            currentNode = xctxt.getContextNode();
        }
        return new XNodeSet(currentNode, xctxt.getDTMManager());
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
    }
}

