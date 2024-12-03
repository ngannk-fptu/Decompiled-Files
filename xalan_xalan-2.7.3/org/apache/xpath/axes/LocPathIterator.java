/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.IteratorPool;
import org.apache.xpath.axes.PathComponent;
import org.apache.xpath.axes.PredicatedNodeTest;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class LocPathIterator
extends PredicatedNodeTest
implements Cloneable,
DTMIterator,
Serializable,
PathComponent {
    static final long serialVersionUID = -4602476357268405754L;
    protected boolean m_allowDetach = true;
    protected transient IteratorPool m_clones = new IteratorPool(this);
    protected transient DTM m_cdtm;
    transient int m_stackFrame = -1;
    private boolean m_isTopLevel = false;
    public transient int m_lastFetched = -1;
    protected transient int m_context = -1;
    protected transient int m_currentContextNode = -1;
    protected transient int m_pos = 0;
    protected transient int m_length = -1;
    private PrefixResolver m_prefixResolver;
    protected transient XPathContext m_execContext;

    protected LocPathIterator() {
    }

    protected LocPathIterator(PrefixResolver nscontext) {
        this.setLocPathIterator(this);
        this.m_prefixResolver = nscontext;
    }

    protected LocPathIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        this(compiler, opPos, analysis, true);
    }

    protected LocPathIterator(Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers) throws TransformerException {
        this.setLocPathIterator(this);
    }

    @Override
    public int getAnalysisBits() {
        int axis = this.getAxis();
        int bit = WalkerFactory.getAnalysisBitFromAxes(axis);
        return bit;
    }

    private void readObject(ObjectInputStream stream) throws IOException, TransformerException {
        try {
            stream.defaultReadObject();
            this.m_clones = new IteratorPool(this);
        }
        catch (ClassNotFoundException cnfe) {
            throw new TransformerException(cnfe);
        }
    }

    public void setEnvironment(Object environment) {
    }

    @Override
    public DTM getDTM(int nodeHandle) {
        return this.m_execContext.getDTM(nodeHandle);
    }

    @Override
    public DTMManager getDTMManager() {
        return this.m_execContext.getDTMManager();
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        XNodeSet iter = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
        iter.setRoot(xctxt.getCurrentNode(), xctxt);
        return iter;
    }

    @Override
    public void executeCharsToContentHandler(XPathContext xctxt, ContentHandler handler) throws TransformerException, SAXException {
        LocPathIterator clone = (LocPathIterator)this.m_clones.getInstance();
        int current = xctxt.getCurrentNode();
        clone.setRoot(current, xctxt);
        int node = clone.nextNode();
        DTM dtm = clone.getDTM(node);
        clone.detach();
        if (node != -1) {
            dtm.dispatchCharactersEvents(node, handler, false);
        }
    }

    @Override
    public DTMIterator asIterator(XPathContext xctxt, int contextNode) throws TransformerException {
        XNodeSet iter = new XNodeSet((LocPathIterator)this.m_clones.getInstance());
        iter.setRoot(contextNode, xctxt);
        return iter;
    }

    @Override
    public boolean isNodesetExpr() {
        return true;
    }

    @Override
    public int asNode(XPathContext xctxt) throws TransformerException {
        DTMIterator iter = this.m_clones.getInstance();
        int current = xctxt.getCurrentNode();
        iter.setRoot(current, xctxt);
        int next = iter.nextNode();
        iter.detach();
        return next;
    }

    @Override
    public boolean bool(XPathContext xctxt) throws TransformerException {
        return this.asNode(xctxt) != -1;
    }

    public void setIsTopLevel(boolean b) {
        this.m_isTopLevel = b;
    }

    public boolean getIsTopLevel() {
        return this.m_isTopLevel;
    }

    @Override
    public void setRoot(int context, Object environment) {
        XPathContext xctxt;
        this.m_context = context;
        this.m_execContext = xctxt = (XPathContext)environment;
        this.m_cdtm = xctxt.getDTM(context);
        this.m_currentContextNode = context;
        if (null == this.m_prefixResolver) {
            this.m_prefixResolver = xctxt.getNamespaceContext();
        }
        this.m_lastFetched = -1;
        this.m_foundLast = false;
        this.m_pos = 0;
        this.m_length = -1;
        if (this.m_isTopLevel) {
            this.m_stackFrame = xctxt.getVarStack().getStackFrame();
        }
    }

    protected void setNextPosition(int next) {
        this.assertion(false, "setNextPosition not supported in this iterator!");
    }

    @Override
    public final int getCurrentPos() {
        return this.m_pos;
    }

    @Override
    public void setShouldCacheNodes(boolean b) {
        this.assertion(false, "setShouldCacheNodes not supported by this iterater!");
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void setCurrentPos(int i) {
        this.assertion(false, "setCurrentPos not supported by this iterator!");
    }

    public void incrementCurrentPos() {
        ++this.m_pos;
    }

    public int size() {
        this.assertion(false, "size() not supported by this iterator!");
        return 0;
    }

    @Override
    public int item(int index) {
        this.assertion(false, "item(int index) not supported by this iterator!");
        return 0;
    }

    @Override
    public void setItem(int node, int index) {
        this.assertion(false, "setItem not supported by this iterator!");
    }

    @Override
    public int getLength() {
        int next;
        LocPathIterator clone;
        boolean isPredicateTest = this == this.m_execContext.getSubContextList();
        int predCount = this.getPredicateCount();
        if (-1 != this.m_length && isPredicateTest && this.m_predicateIndex < 1) {
            return this.m_length;
        }
        if (this.m_foundLast) {
            return this.m_pos;
        }
        int pos = this.m_predicateIndex >= 0 ? this.getProximityPosition() : this.m_pos;
        try {
            clone = (LocPathIterator)this.clone();
        }
        catch (CloneNotSupportedException cnse) {
            return -1;
        }
        if (predCount > 0 && isPredicateTest) {
            clone.m_predCount = this.m_predicateIndex;
        }
        while (-1 != (next = clone.nextNode())) {
            ++pos;
        }
        if (isPredicateTest && this.m_predicateIndex < 1) {
            this.m_length = pos;
        }
        return pos;
    }

    @Override
    public boolean isFresh() {
        return this.m_pos == 0;
    }

    @Override
    public int previousNode() {
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null));
    }

    @Override
    public int getWhatToShow() {
        return -17;
    }

    public DTMFilter getFilter() {
        return null;
    }

    @Override
    public int getRoot() {
        return this.m_context;
    }

    @Override
    public boolean getExpandEntityReferences() {
        return true;
    }

    @Override
    public void allowDetachToRelease(boolean allowRelease) {
        this.m_allowDetach = allowRelease;
    }

    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_execContext = null;
            this.m_cdtm = null;
            this.m_length = -1;
            this.m_pos = 0;
            this.m_lastFetched = -1;
            this.m_context = -1;
            this.m_currentContextNode = -1;
            this.m_clones.freeInstance(this);
        }
    }

    @Override
    public void reset() {
        this.assertion(false, "This iterator can not reset!");
    }

    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        LocPathIterator clone = (LocPathIterator)this.m_clones.getInstanceOrThrow();
        clone.m_execContext = this.m_execContext;
        clone.m_cdtm = this.m_cdtm;
        clone.m_context = this.m_context;
        clone.m_currentContextNode = this.m_currentContextNode;
        clone.m_stackFrame = this.m_stackFrame;
        return clone;
    }

    @Override
    public abstract int nextNode();

    protected int returnNextNode(int nextNode) {
        if (-1 != nextNode) {
            ++this.m_pos;
        }
        this.m_lastFetched = nextNode;
        if (-1 == nextNode) {
            this.m_foundLast = true;
        }
        return nextNode;
    }

    @Override
    public int getCurrentNode() {
        return this.m_lastFetched;
    }

    @Override
    public void runTo(int index) {
        if (this.m_foundLast || index >= 0 && index <= this.getCurrentPos()) {
            return;
        }
        if (-1 == index) {
            int n;
            while (-1 != (n = this.nextNode())) {
            }
        } else {
            int n;
            while (-1 != (n = this.nextNode()) && this.getCurrentPos() < index) {
            }
        }
    }

    public final boolean getFoundLast() {
        return this.m_foundLast;
    }

    public final XPathContext getXPathContext() {
        return this.m_execContext;
    }

    public final int getContext() {
        return this.m_context;
    }

    public final int getCurrentContextNode() {
        return this.m_currentContextNode;
    }

    public final void setCurrentContextNode(int n) {
        this.m_currentContextNode = n;
    }

    public final PrefixResolver getPrefixResolver() {
        if (null == this.m_prefixResolver) {
            this.m_prefixResolver = (PrefixResolver)((Object)this.getExpressionOwner());
        }
        return this.m_prefixResolver;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        if (visitor.visitLocationPath(owner, this)) {
            visitor.visitStep(owner, this);
            this.callPredicateVisitors(visitor);
        }
    }

    @Override
    public boolean isDocOrdered() {
        return true;
    }

    @Override
    public int getAxis() {
        return -1;
    }

    @Override
    public int getLastPos(XPathContext xctxt) {
        return this.getLength();
    }
}

