/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.functions;

import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.StringVector;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

public class FuncId
extends FunctionOneArg {
    static final long serialVersionUID = 8930573966143567310L;

    private StringVector getNodesByID(XPathContext xctxt, int docContext, String refval, StringVector usedrefs, NodeSetDTM nodeSet, boolean mayBeMore) {
        if (null != refval) {
            String ref = null;
            StringTokenizer tokenizer = new StringTokenizer(refval);
            boolean hasMore = tokenizer.hasMoreTokens();
            DTM dtm = xctxt.getDTM(docContext);
            while (hasMore) {
                ref = tokenizer.nextToken();
                hasMore = tokenizer.hasMoreTokens();
                if (null != usedrefs && usedrefs.contains(ref)) {
                    ref = null;
                    continue;
                }
                int node = dtm.getElementById(ref);
                if (-1 != node) {
                    nodeSet.addNodeInDocOrder(node, xctxt);
                }
                if (null == ref || !hasMore && !mayBeMore) continue;
                if (null == usedrefs) {
                    usedrefs = new StringVector();
                }
                usedrefs.addElement(ref);
            }
        }
        return usedrefs;
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        int context = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(context);
        int docContext = dtm.getDocument();
        if (-1 == docContext) {
            this.error(xctxt, "ER_CONTEXT_HAS_NO_OWNERDOC", null);
        }
        XObject arg = this.m_arg0.execute(xctxt);
        int argType = arg.getType();
        XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
        NodeSetDTM nodeSet = nodes.mutableNodeset();
        if (4 == argType) {
            DTMIterator ni = arg.iter();
            StringVector usedrefs = null;
            int pos = ni.nextNode();
            while (-1 != pos) {
                DTM ndtm = ni.getDTM(pos);
                String refval = ndtm.getStringValue(pos).toString();
                pos = ni.nextNode();
                usedrefs = this.getNodesByID(xctxt, docContext, refval, usedrefs, nodeSet, -1 != pos);
            }
        } else {
            if (-1 == argType) {
                return nodes;
            }
            String refval = arg.str();
            this.getNodesByID(xctxt, docContext, refval, null, nodeSet, false);
        }
        return nodes;
    }
}

