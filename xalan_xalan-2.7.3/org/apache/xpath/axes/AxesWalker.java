/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.axes.PredicatedNodeTest;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.axes.WalkingIterator;
import org.apache.xpath.compiler.Compiler;

public class AxesWalker
extends PredicatedNodeTest
implements Cloneable,
PathComponent,
ExpressionOwner {
    static final long serialVersionUID = -2966031951306601247L;
    private DTM m_dtm;
    transient int m_root = -1;
    private transient int m_currentNode = -1;
    transient boolean m_isFresh;
    protected AxesWalker m_nextWalker;
    AxesWalker m_prevWalker;
    protected int m_axis = -1;
    protected DTMAxisTraverser m_traverser;

    public AxesWalker(LocPathIterator locPathIterator, int axis) {
        super(locPathIterator);
        this.m_axis = axis;
    }

    public final WalkingIterator wi() {
        return (WalkingIterator)this.m_lpi;
    }

    public void init(Compiler compiler, int opPos, int stepType) throws TransformerException {
        this.initPredicateInfo(compiler, opPos);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AxesWalker clone = (AxesWalker)super.clone();
        return clone;
    }

    AxesWalker cloneDeep(WalkingIterator cloneOwner, Vector cloneList) throws CloneNotSupportedException {
        AxesWalker clone = AxesWalker.findClone(this, cloneList);
        if (null != clone) {
            return clone;
        }
        clone = (AxesWalker)this.clone();
        clone.setLocPathIterator(cloneOwner);
        if (null != cloneList) {
            cloneList.addElement(this);
            cloneList.addElement(clone);
        }
        if (this.wi().m_lastUsedWalker == this) {
            cloneOwner.m_lastUsedWalker = clone;
        }
        if (null != this.m_nextWalker) {
            clone.m_nextWalker = this.m_nextWalker.cloneDeep(cloneOwner, cloneList);
        }
        if (null != cloneList) {
            if (null != this.m_prevWalker) {
                clone.m_prevWalker = this.m_prevWalker.cloneDeep(cloneOwner, cloneList);
            }
        } else if (null != this.m_nextWalker) {
            clone.m_nextWalker.m_prevWalker = clone;
        }
        return clone;
    }

    static AxesWalker findClone(AxesWalker key, Vector cloneList) {
        if (null != cloneList) {
            int n = cloneList.size();
            for (int i = 0; i < n; i += 2) {
                if (key != cloneList.elementAt(i)) continue;
                return (AxesWalker)cloneList.elementAt(i + 1);
            }
        }
        return null;
    }

    public void detach() {
        this.m_currentNode = -1;
        this.m_dtm = null;
        this.m_traverser = null;
        this.m_isFresh = true;
        this.m_root = -1;
    }

    public int getRoot() {
        return this.m_root;
    }

    @Override
    public int getAnalysisBits() {
        int axis = this.getAxis();
        int bit = WalkerFactory.getAnalysisBitFromAxes(axis);
        return bit;
    }

    public void setRoot(int root) {
        XPathContext xctxt = this.wi().getXPathContext();
        this.m_dtm = xctxt.getDTM(root);
        this.m_traverser = this.m_dtm.getAxisTraverser(this.m_axis);
        this.m_isFresh = true;
        this.m_foundLast = false;
        this.m_root = root;
        this.m_currentNode = root;
        if (-1 == root) {
            throw new RuntimeException(XSLMessages.createXPATHMessage("ER_SETTING_WALKER_ROOT_TO_NULL", null));
        }
        this.resetProximityPositions();
    }

    public final int getCurrentNode() {
        return this.m_currentNode;
    }

    public void setNextWalker(AxesWalker walker) {
        this.m_nextWalker = walker;
    }

    public AxesWalker getNextWalker() {
        return this.m_nextWalker;
    }

    public void setPrevWalker(AxesWalker walker) {
        this.m_prevWalker = walker;
    }

    public AxesWalker getPrevWalker() {
        return this.m_prevWalker;
    }

    private int returnNextNode(int n) {
        return n;
    }

    protected int getNextNode() {
        if (this.m_foundLast) {
            return -1;
        }
        if (this.m_isFresh) {
            this.m_currentNode = this.m_traverser.first(this.m_root);
            this.m_isFresh = false;
        } else if (-1 != this.m_currentNode) {
            this.m_currentNode = this.m_traverser.next(this.m_root, this.m_currentNode);
        }
        if (-1 == this.m_currentNode) {
            this.m_foundLast = true;
        }
        return this.m_currentNode;
    }

    public int nextNode() {
        int nextNode = -1;
        AxesWalker walker = this.wi().getLastUsedWalker();
        while (null != walker) {
            nextNode = walker.getNextNode();
            if (-1 == nextNode) {
                walker = walker.m_prevWalker;
                continue;
            }
            if (walker.acceptNode(nextNode) != 1) continue;
            if (null == walker.m_nextWalker) {
                this.wi().setLastUsedWalker(walker);
                break;
            }
            AxesWalker prev = walker;
            walker = walker.m_nextWalker;
            walker.setRoot(nextNode);
            walker.m_prevWalker = prev;
        }
        return nextNode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getLastPos(XPathContext xctxt) {
        AxesWalker walker;
        int pos = this.getProximityPosition();
        try {
            walker = (AxesWalker)this.clone();
        }
        catch (CloneNotSupportedException cnse) {
            return -1;
        }
        walker.setPredicateCount(this.m_predicateIndex);
        walker.setNextWalker(null);
        walker.setPrevWalker(null);
        WalkingIterator lpi = this.wi();
        AxesWalker savedWalker = lpi.getLastUsedWalker();
        try {
            int next;
            lpi.setLastUsedWalker(walker);
            while (-1 != (next = walker.nextNode())) {
                ++pos;
            }
        }
        finally {
            lpi.setLastUsedWalker(savedWalker);
        }
        return pos;
    }

    public void setDefaultDTM(DTM dtm) {
        this.m_dtm = dtm;
    }

    public DTM getDTM(int node) {
        return this.wi().getXPathContext().getDTM(node);
    }

    public boolean isDocOrdered() {
        return true;
    }

    public int getAxis() {
        return this.m_axis;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        if (visitor.visitStep(owner, this)) {
            this.callPredicateVisitors(visitor);
            if (null != this.m_nextWalker) {
                this.m_nextWalker.callVisitors(this, visitor);
            }
        }
    }

    @Override
    public Expression getExpression() {
        return this.m_nextWalker;
    }

    @Override
    public void setExpression(Expression exp) {
        exp.exprSetParent(this);
        this.m_nextWalker = (AxesWalker)exp;
    }

    @Override
    public boolean deepEquals(Expression expr) {
        if (!super.deepEquals(expr)) {
            return false;
        }
        AxesWalker walker = (AxesWalker)expr;
        return this.m_axis == walker.m_axis;
    }
}

