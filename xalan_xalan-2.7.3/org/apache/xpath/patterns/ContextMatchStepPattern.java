/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.patterns;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;

public class ContextMatchStepPattern
extends StepPattern {
    static final long serialVersionUID = -1888092779313211942L;

    public ContextMatchStepPattern(int axis, int paxis) {
        super(-1, axis, paxis);
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        if (xctxt.getIteratorRoot() == xctxt.getCurrentNode()) {
            return this.getStaticScore();
        }
        return SCORE_NONE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XObject executeRelativePathPattern(XPathContext xctxt, StepPattern prevStep) throws TransformerException {
        XObject score = NodeTest.SCORE_NONE;
        int context = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(context);
        if (null != dtm) {
            boolean iterRootIsAttr;
            int predContext = xctxt.getCurrentNode();
            int axis = this.m_axis;
            boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
            boolean bl = iterRootIsAttr = dtm.getNodeType(xctxt.getIteratorRoot()) == 2;
            if (11 == axis && iterRootIsAttr) {
                axis = 15;
            }
            DTMAxisTraverser traverser = dtm.getAxisTraverser(axis);
            int relative = traverser.first(context);
            while (-1 != relative) {
                block16: {
                    try {
                        xctxt.pushCurrentNode(relative);
                        score = this.execute(xctxt);
                        if (score != NodeTest.SCORE_NONE) {
                            if (this.executePredicates(xctxt, dtm, context)) {
                                XObject xObject = score;
                                return xObject;
                            }
                            score = NodeTest.SCORE_NONE;
                        }
                        if (!needToTraverseAttrs || !iterRootIsAttr || 1 != dtm.getNodeType(relative)) break block16;
                        int xaxis = 2;
                        for (int i = 0; i < 2; ++i) {
                            DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);
                            int arelative = atraverser.first(relative);
                            while (-1 != arelative) {
                                try {
                                    xctxt.pushCurrentNode(arelative);
                                    score = this.execute(xctxt);
                                    if (score != NodeTest.SCORE_NONE && score != NodeTest.SCORE_NONE) {
                                        XObject xObject = score;
                                        return xObject;
                                    }
                                }
                                finally {
                                    xctxt.popCurrentNode();
                                }
                                arelative = atraverser.next(relative, arelative);
                            }
                            xaxis = 9;
                        }
                    }
                    finally {
                        xctxt.popCurrentNode();
                    }
                }
                relative = traverser.next(context, relative);
            }
        }
        return score;
    }
}

