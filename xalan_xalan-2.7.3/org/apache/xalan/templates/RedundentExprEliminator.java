/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.AbsPathChecker;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemVariablePsuedo;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.VarNameCollector;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.FilterExprIteratorSimple;
import org.apache.xpath.axes.FilterExprWalker;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.axes.WalkingIterator;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.operations.VariableSafeAbsRef;
import org.w3c.dom.DOMException;

public class RedundentExprEliminator
extends XSLTVisitor {
    Vector m_paths = null;
    Vector m_absPaths;
    boolean m_isSameContext = true;
    AbsPathChecker m_absPathChecker = new AbsPathChecker();
    private static int m_uniquePseudoVarID = 1;
    static final String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";
    public static final boolean DEBUG = false;
    public static final boolean DIAGNOSE_NUM_PATHS_REDUCED = false;
    public static final boolean DIAGNOSE_MULTISTEPLIST = false;
    VarNameCollector m_varNameCollector = new VarNameCollector();

    public RedundentExprEliminator() {
        this.m_absPaths = new Vector();
    }

    public void eleminateRedundentLocals(ElemTemplateElement psuedoVarRecipient) {
        this.eleminateRedundent(psuedoVarRecipient, this.m_paths);
    }

    public void eleminateRedundentGlobals(StylesheetRoot stylesheet) {
        this.eleminateRedundent(stylesheet, this.m_absPaths);
    }

    protected void eleminateRedundent(ElemTemplateElement psuedoVarRecipient, Vector paths) {
        int n = paths.size();
        int numPathsEliminated = 0;
        int numUniquePathsEliminated = 0;
        for (int i = 0; i < n; ++i) {
            ExpressionOwner owner = (ExpressionOwner)paths.elementAt(i);
            if (null == owner) continue;
            int found = this.findAndEliminateRedundant(i + 1, i, owner, psuedoVarRecipient, paths);
            if (found > 0) {
                ++numUniquePathsEliminated;
            }
            numPathsEliminated += found;
        }
        this.eleminateSharedPartialPaths(psuedoVarRecipient, paths);
    }

    protected void eleminateSharedPartialPaths(ElemTemplateElement psuedoVarRecipient, Vector paths) {
        MultistepExprHolder list = this.createMultistepExprList(paths);
        if (null != list) {
            boolean isGlobal = paths == this.m_absPaths;
            int longestStepsCount = list.m_stepCount;
            for (int i = longestStepsCount - 1; i >= 1; --i) {
                MultistepExprHolder next = list;
                while (null != next && next.m_stepCount >= i) {
                    list = this.matchAndEliminatePartialPaths(next, list, isGlobal, i, psuedoVarRecipient);
                    next = next.m_next;
                }
            }
        }
    }

    protected MultistepExprHolder matchAndEliminatePartialPaths(MultistepExprHolder testee, MultistepExprHolder head, boolean isGlobal, int lengthToTest, ElemTemplateElement varScope) {
        if (null == testee.m_exprOwner) {
            return head;
        }
        WalkingIterator iter1 = (WalkingIterator)testee.m_exprOwner.getExpression();
        if (this.partialIsVariable(testee, lengthToTest)) {
            return head;
        }
        MultistepExprHolder matchedPaths = null;
        MultistepExprHolder matchedPathsTail = null;
        MultistepExprHolder meh = head;
        while (null != meh) {
            WalkingIterator iter2;
            if (meh != testee && null != meh.m_exprOwner && this.stepsEqual(iter1, iter2 = (WalkingIterator)meh.m_exprOwner.getExpression(), lengthToTest)) {
                if (null == matchedPaths) {
                    try {
                        matchedPaths = (MultistepExprHolder)testee.clone();
                        testee.m_exprOwner = null;
                    }
                    catch (CloneNotSupportedException cloneNotSupportedException) {
                        // empty catch block
                    }
                    matchedPathsTail = matchedPaths;
                    matchedPathsTail.m_next = null;
                }
                try {
                    matchedPathsTail.m_next = (MultistepExprHolder)meh.clone();
                    meh.m_exprOwner = null;
                }
                catch (CloneNotSupportedException cloneNotSupportedException) {
                    // empty catch block
                }
                matchedPathsTail = matchedPathsTail.m_next;
                matchedPathsTail.m_next = null;
            }
            meh = meh.m_next;
        }
        boolean matchCount = false;
        if (null != matchedPaths) {
            ElemTemplateElement root = isGlobal ? varScope : this.findCommonAncestor(matchedPaths);
            WalkingIterator sharedIter = (WalkingIterator)matchedPaths.m_exprOwner.getExpression();
            WalkingIterator newIter = this.createIteratorFromSteps(sharedIter, lengthToTest);
            ElemVariable var = this.createPseudoVarDecl(root, newIter, isGlobal);
            while (null != matchedPaths) {
                ExpressionOwner owner = matchedPaths.m_exprOwner;
                WalkingIterator iter = (WalkingIterator)owner.getExpression();
                LocPathIterator newIter2 = this.changePartToRef(var.getName(), iter, lengthToTest, isGlobal);
                owner.setExpression(newIter2);
                matchedPaths = matchedPaths.m_next;
            }
        }
        return head;
    }

    boolean partialIsVariable(MultistepExprHolder testee, int lengthToTest) {
        WalkingIterator wi;
        return 1 == lengthToTest && (wi = (WalkingIterator)testee.m_exprOwner.getExpression()).getFirstWalker() instanceof FilterExprWalker;
    }

    protected void diagnoseLineNumber(Expression expr) {
        ElemTemplateElement e = this.getElemFromExpression(expr);
        System.err.println("   " + e.getSystemId() + " Line " + e.getLineNumber());
    }

    protected ElemTemplateElement findCommonAncestor(MultistepExprHolder head) {
        int i;
        int numExprs = head.getLength();
        ElemTemplateElement[] elems = new ElemTemplateElement[numExprs];
        int[] ancestorCounts = new int[numExprs];
        MultistepExprHolder next = head;
        int shortestAncestorCount = 10000;
        for (i = 0; i < numExprs; ++i) {
            int numAncestors;
            ElemTemplateElement elem;
            elems[i] = elem = this.getElemFromExpression(next.m_exprOwner.getExpression());
            ancestorCounts[i] = numAncestors = this.countAncestors(elem);
            if (numAncestors < shortestAncestorCount) {
                shortestAncestorCount = numAncestors;
            }
            next = next.m_next;
        }
        for (i = 0; i < numExprs; ++i) {
            if (ancestorCounts[i] <= shortestAncestorCount) continue;
            int numStepCorrection = ancestorCounts[i] - shortestAncestorCount;
            for (int j = 0; j < numStepCorrection; ++j) {
                elems[i] = elems[i].getParentElem();
            }
        }
        ElemTemplateElement first = null;
        while (shortestAncestorCount-- >= 0) {
            int i2;
            boolean areEqual = true;
            first = elems[0];
            for (i2 = 1; i2 < numExprs; ++i2) {
                if (first == elems[i2]) continue;
                areEqual = false;
                break;
            }
            if (areEqual && this.isNotSameAsOwner(head, first) && first.canAcceptVariables()) {
                return first;
            }
            for (i2 = 0; i2 < numExprs; ++i2) {
                elems[i2] = elems[i2].getParentElem();
            }
        }
        RedundentExprEliminator.assertion(false, "Could not find common ancestor!!!");
        return null;
    }

    protected boolean isNotSameAsOwner(MultistepExprHolder head, ElemTemplateElement ete) {
        MultistepExprHolder next = head;
        while (null != next) {
            ElemTemplateElement elemOwner = this.getElemFromExpression(next.m_exprOwner.getExpression());
            if (elemOwner == ete) {
                return false;
            }
            next = next.m_next;
        }
        return true;
    }

    protected int countAncestors(ElemTemplateElement elem) {
        int count = 0;
        while (null != elem) {
            ++count;
            elem = elem.getParentElem();
        }
        return count;
    }

    protected void diagnoseMultistepList(int matchCount, int lengthToTest, boolean isGlobal) {
        if (matchCount > 0) {
            System.err.print("Found multistep matches: " + matchCount + ", " + lengthToTest + " length");
            if (isGlobal) {
                System.err.println(" (global)");
            } else {
                System.err.println();
            }
        }
    }

    protected LocPathIterator changePartToRef(QName uniquePseudoVarName, WalkingIterator wi, int numSteps, boolean isGlobal) {
        Variable var = new Variable();
        var.setQName(uniquePseudoVarName);
        var.setIsGlobal(isGlobal);
        if (isGlobal) {
            ElemTemplateElement elem = this.getElemFromExpression(wi);
            StylesheetRoot root = elem.getStylesheetRoot();
            Vector vars = root.getVariablesAndParamsComposed();
            var.setIndex(vars.size() - 1);
        }
        AxesWalker walker = wi.getFirstWalker();
        for (int i = 0; i < numSteps; ++i) {
            RedundentExprEliminator.assertion(null != walker, "Walker should not be null!");
            walker = walker.getNextWalker();
        }
        if (null != walker) {
            FilterExprWalker few = new FilterExprWalker(wi);
            few.setInnerExpression(var);
            few.exprSetParent(wi);
            few.setNextWalker(walker);
            walker.setPrevWalker(few);
            wi.setFirstWalker(few);
            return wi;
        }
        FilterExprIteratorSimple feis = new FilterExprIteratorSimple(var);
        feis.exprSetParent(wi.exprGetParent());
        return feis;
    }

    protected WalkingIterator createIteratorFromSteps(WalkingIterator wi, int numSteps) {
        WalkingIterator newIter = new WalkingIterator(wi.getPrefixResolver());
        try {
            AxesWalker walker = (AxesWalker)wi.getFirstWalker().clone();
            newIter.setFirstWalker(walker);
            walker.setLocPathIterator(newIter);
            for (int i = 1; i < numSteps; ++i) {
                AxesWalker next = (AxesWalker)walker.getNextWalker().clone();
                walker.setNextWalker(next);
                next.setLocPathIterator(newIter);
                walker = next;
            }
            walker.setNextWalker(null);
        }
        catch (CloneNotSupportedException cnse) {
            throw new WrappedRuntimeException(cnse);
        }
        return newIter;
    }

    protected boolean stepsEqual(WalkingIterator iter1, WalkingIterator iter2, int numSteps) {
        AxesWalker aw1 = iter1.getFirstWalker();
        AxesWalker aw2 = iter2.getFirstWalker();
        for (int i = 0; i < numSteps; ++i) {
            if (null == aw1 || null == aw2) {
                return false;
            }
            if (!aw1.deepEquals(aw2)) {
                return false;
            }
            aw1 = aw1.getNextWalker();
            aw2 = aw2.getNextWalker();
        }
        RedundentExprEliminator.assertion(null != aw1 || null != aw2, "Total match is incorrect!");
        return true;
    }

    protected MultistepExprHolder createMultistepExprList(Vector paths) {
        MultistepExprHolder first = null;
        int n = paths.size();
        for (int i = 0; i < n; ++i) {
            LocPathIterator lpi;
            int numPaths;
            ExpressionOwner eo = (ExpressionOwner)paths.elementAt(i);
            if (null == eo || (numPaths = this.countSteps(lpi = (LocPathIterator)eo.getExpression())) <= 1) continue;
            first = null == first ? new MultistepExprHolder(eo, numPaths, null) : first.addInSortedOrder(eo, numPaths);
        }
        if (null == first || first.getLength() <= 1) {
            return null;
        }
        return first;
    }

    protected int findAndEliminateRedundant(int start, int firstOccuranceIndex, ExpressionOwner firstOccuranceOwner, ElemTemplateElement psuedoVarRecipient, Vector paths) throws DOMException {
        MultistepExprHolder head = null;
        MultistepExprHolder tail = null;
        int numPathsFound = 0;
        int n = paths.size();
        Expression expr1 = firstOccuranceOwner.getExpression();
        boolean isGlobal = paths == this.m_absPaths;
        LocPathIterator lpi = (LocPathIterator)expr1;
        int stepCount = this.countSteps(lpi);
        for (int j = start; j < n; ++j) {
            Expression expr2;
            boolean isEqual;
            ExpressionOwner owner2 = (ExpressionOwner)paths.elementAt(j);
            if (null == owner2 || !(isEqual = (expr2 = owner2.getExpression()).deepEquals(lpi))) continue;
            LocPathIterator lpi2 = (LocPathIterator)expr2;
            if (null == head) {
                tail = head = new MultistepExprHolder(firstOccuranceOwner, stepCount, null);
                ++numPathsFound;
            }
            tail = tail.m_next = new MultistepExprHolder(owner2, stepCount, null);
            paths.setElementAt(null, j);
            ++numPathsFound;
        }
        if (0 == numPathsFound && isGlobal) {
            head = new MultistepExprHolder(firstOccuranceOwner, stepCount, null);
            ++numPathsFound;
        }
        if (null != head) {
            ElemTemplateElement root = isGlobal ? psuedoVarRecipient : this.findCommonAncestor(head);
            LocPathIterator sharedIter = (LocPathIterator)head.m_exprOwner.getExpression();
            ElemVariable var = this.createPseudoVarDecl(root, sharedIter, isGlobal);
            QName uniquePseudoVarName = var.getName();
            while (null != head) {
                ExpressionOwner owner = head.m_exprOwner;
                this.changeToVarRef(uniquePseudoVarName, owner, paths, root);
                head = head.m_next;
            }
            paths.setElementAt(var.getSelect(), firstOccuranceIndex);
        }
        return numPathsFound;
    }

    protected int oldFindAndEliminateRedundant(int start, int firstOccuranceIndex, ExpressionOwner firstOccuranceOwner, ElemTemplateElement psuedoVarRecipient, Vector paths) throws DOMException {
        QName uniquePseudoVarName = null;
        boolean foundFirst = false;
        int numPathsFound = 0;
        int n = paths.size();
        Expression expr1 = firstOccuranceOwner.getExpression();
        boolean isGlobal = paths == this.m_absPaths;
        LocPathIterator lpi = (LocPathIterator)expr1;
        for (int j = start; j < n; ++j) {
            Expression expr2;
            boolean isEqual;
            ExpressionOwner owner2 = (ExpressionOwner)paths.elementAt(j);
            if (null == owner2 || !(isEqual = (expr2 = owner2.getExpression()).deepEquals(lpi))) continue;
            LocPathIterator lpi2 = (LocPathIterator)expr2;
            if (!foundFirst) {
                foundFirst = true;
                ElemVariable var = this.createPseudoVarDecl(psuedoVarRecipient, lpi, isGlobal);
                if (null == var) {
                    return 0;
                }
                uniquePseudoVarName = var.getName();
                this.changeToVarRef(uniquePseudoVarName, firstOccuranceOwner, paths, psuedoVarRecipient);
                paths.setElementAt(var.getSelect(), firstOccuranceIndex);
                ++numPathsFound;
            }
            this.changeToVarRef(uniquePseudoVarName, owner2, paths, psuedoVarRecipient);
            paths.setElementAt(null, j);
            ++numPathsFound;
        }
        if (0 == numPathsFound && paths == this.m_absPaths) {
            ElemVariable var = this.createPseudoVarDecl(psuedoVarRecipient, lpi, true);
            if (null == var) {
                return 0;
            }
            uniquePseudoVarName = var.getName();
            this.changeToVarRef(uniquePseudoVarName, firstOccuranceOwner, paths, psuedoVarRecipient);
            paths.setElementAt(var.getSelect(), firstOccuranceIndex);
            ++numPathsFound;
        }
        return numPathsFound;
    }

    protected int countSteps(LocPathIterator lpi) {
        if (lpi instanceof WalkingIterator) {
            WalkingIterator wi = (WalkingIterator)lpi;
            int count = 0;
            for (AxesWalker aw = wi.getFirstWalker(); null != aw; aw = aw.getNextWalker()) {
                ++count;
            }
            return count;
        }
        return 1;
    }

    protected void changeToVarRef(QName varName, ExpressionOwner owner, Vector paths, ElemTemplateElement psuedoVarRecipient) {
        Variable varRef = paths == this.m_absPaths ? new VariableSafeAbsRef() : new Variable();
        varRef.setQName(varName);
        if (paths == this.m_absPaths) {
            StylesheetRoot root = (StylesheetRoot)psuedoVarRecipient;
            Vector globalVars = root.getVariablesAndParamsComposed();
            varRef.setIndex(globalVars.size() - 1);
            varRef.setIsGlobal(true);
        }
        owner.setExpression(varRef);
    }

    private static synchronized int getPseudoVarID() {
        return m_uniquePseudoVarID++;
    }

    protected ElemVariable createPseudoVarDecl(ElemTemplateElement psuedoVarRecipient, LocPathIterator lpi, boolean isGlobal) throws DOMException {
        QName uniquePseudoVarName = new QName(PSUEDOVARNAMESPACE, "#" + RedundentExprEliminator.getPseudoVarID());
        if (isGlobal) {
            return this.createGlobalPseudoVarDecl(uniquePseudoVarName, (StylesheetRoot)psuedoVarRecipient, lpi);
        }
        return this.createLocalPseudoVarDecl(uniquePseudoVarName, psuedoVarRecipient, lpi);
    }

    protected ElemVariable createGlobalPseudoVarDecl(QName uniquePseudoVarName, StylesheetRoot stylesheetRoot, LocPathIterator lpi) throws DOMException {
        ElemVariable psuedoVar = new ElemVariable();
        psuedoVar.setIsTopLevel(true);
        XPath xpath = new XPath(lpi);
        psuedoVar.setSelect(xpath);
        psuedoVar.setName(uniquePseudoVarName);
        Vector globalVars = stylesheetRoot.getVariablesAndParamsComposed();
        psuedoVar.setIndex(globalVars.size());
        globalVars.addElement(psuedoVar);
        return psuedoVar;
    }

    protected ElemVariable createLocalPseudoVarDecl(QName uniquePseudoVarName, ElemTemplateElement psuedoVarRecipient, LocPathIterator lpi) throws DOMException {
        ElemVariablePsuedo psuedoVar = new ElemVariablePsuedo();
        XPath xpath = new XPath(lpi);
        ((ElemVariable)psuedoVar).setSelect(xpath);
        psuedoVar.setName(uniquePseudoVarName);
        ElemVariable var = this.addVarDeclToElem(psuedoVarRecipient, lpi, psuedoVar);
        lpi.exprSetParent(var);
        return var;
    }

    protected ElemVariable addVarDeclToElem(ElemTemplateElement psuedoVarRecipient, LocPathIterator lpi, ElemVariable psuedoVar) throws DOMException {
        ElemTemplateElement ete = psuedoVarRecipient.getFirstChildElem();
        lpi.callVisitors(null, this.m_varNameCollector);
        if (this.m_varNameCollector.getVarCount() > 0) {
            ElemTemplateElement baseElem = this.getElemFromExpression(lpi);
            ElemVariable varElem = this.getPrevVariableElem(baseElem);
            while (null != varElem) {
                if (this.m_varNameCollector.doesOccur(varElem.getName())) {
                    psuedoVarRecipient = varElem.getParentElem();
                    ete = varElem.getNextSiblingElem();
                    break;
                }
                varElem = this.getPrevVariableElem(varElem);
            }
        }
        if (null != ete && 41 == ete.getXSLToken()) {
            if (this.isParam(lpi)) {
                return null;
            }
            while (null != ete && (null == (ete = ete.getNextSiblingElem()) || 41 == ete.getXSLToken())) {
            }
        }
        psuedoVarRecipient.insertBefore(psuedoVar, ete);
        this.m_varNameCollector.reset();
        return psuedoVar;
    }

    protected boolean isParam(ExpressionNode expr) {
        while (null != expr && !(expr instanceof ElemTemplateElement)) {
            expr = expr.exprGetParent();
        }
        if (null != expr) {
            for (ElemTemplateElement ete = (ElemTemplateElement)expr; null != ete; ete = ete.getParentElem()) {
                int type = ete.getXSLToken();
                switch (type) {
                    case 41: {
                        return true;
                    }
                    case 19: 
                    case 25: {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    protected ElemVariable getPrevVariableElem(ElemTemplateElement elem) {
        while (null != (elem = this.getPrevElementWithinContext(elem))) {
            int type = elem.getXSLToken();
            if (73 != type && 41 != type) continue;
            return (ElemVariable)elem;
        }
        return null;
    }

    protected ElemTemplateElement getPrevElementWithinContext(ElemTemplateElement elem) {
        int type;
        ElemTemplateElement prev = elem.getPreviousSiblingElem();
        if (null == prev) {
            prev = elem.getParentElem();
        }
        if (null != prev && (28 == (type = prev.getXSLToken()) || 19 == type || 25 == type)) {
            prev = null;
        }
        return prev;
    }

    protected ElemTemplateElement getElemFromExpression(Expression expr) {
        for (ExpressionNode parent = expr.exprGetParent(); null != parent; parent = parent.exprGetParent()) {
            if (!(parent instanceof ElemTemplateElement)) continue;
            return (ElemTemplateElement)parent;
        }
        throw new RuntimeException(XSLMessages.createMessage("ER_ASSERT_NO_TEMPLATE_PARENT", null));
    }

    public boolean isAbsolute(LocPathIterator path) {
        boolean isAbs;
        int analysis = path.getAnalysisBits();
        boolean bl = isAbs = WalkerFactory.isSet(analysis, 0x8000000) || WalkerFactory.isSet(analysis, 0x20000000);
        if (isAbs) {
            isAbs = this.m_absPathChecker.checkAbsolute(path);
        }
        return isAbs;
    }

    @Override
    public boolean visitLocationPath(ExpressionOwner owner, LocPathIterator path) {
        FilterExprWalker few;
        Expression exp;
        WalkingIterator wi;
        AxesWalker aw;
        if (path instanceof SelfIteratorNoPredicate) {
            return true;
        }
        if (path instanceof WalkingIterator && (aw = (wi = (WalkingIterator)path).getFirstWalker()) instanceof FilterExprWalker && null == aw.getNextWalker() && (exp = (few = (FilterExprWalker)aw).getInnerExpression()) instanceof Variable) {
            return true;
        }
        if (this.isAbsolute(path) && null != this.m_absPaths) {
            this.m_absPaths.addElement(owner);
        } else if (this.m_isSameContext && null != this.m_paths) {
            this.m_paths.addElement(owner);
        }
        return true;
    }

    @Override
    public boolean visitPredicate(ExpressionOwner owner, Expression pred) {
        boolean savedIsSame = this.m_isSameContext;
        this.m_isSameContext = false;
        pred.callVisitors(owner, this);
        this.m_isSameContext = savedIsSame;
        return false;
    }

    @Override
    public boolean visitTopLevelInstruction(ElemTemplateElement elem) {
        int type = elem.getXSLToken();
        switch (type) {
            case 19: {
                return this.visitInstruction(elem);
            }
        }
        return true;
    }

    @Override
    public boolean visitInstruction(ElemTemplateElement elem) {
        int type = elem.getXSLToken();
        switch (type) {
            case 17: 
            case 19: 
            case 28: {
                if (type == 28) {
                    ElemForEach efe = (ElemForEach)elem;
                    Expression select = efe.getSelect();
                    select.callVisitors(efe, this);
                }
                Vector savedPaths = this.m_paths;
                this.m_paths = new Vector();
                elem.callChildVisitors(this, false);
                this.eleminateRedundentLocals(elem);
                this.m_paths = savedPaths;
                return false;
            }
            case 35: 
            case 64: {
                boolean savedIsSame = this.m_isSameContext;
                this.m_isSameContext = false;
                elem.callChildVisitors(this);
                this.m_isSameContext = savedIsSame;
                return false;
            }
        }
        return true;
    }

    protected void diagnoseNumPaths(Vector paths, int numPathsEliminated, int numUniquePathsEliminated) {
        if (numPathsEliminated > 0) {
            if (paths == this.m_paths) {
                System.err.println("Eliminated " + numPathsEliminated + " total paths!");
                System.err.println("Consolodated " + numUniquePathsEliminated + " redundent paths!");
            } else {
                System.err.println("Eliminated " + numPathsEliminated + " total global paths!");
                System.err.println("Consolodated " + numUniquePathsEliminated + " redundent global paths!");
            }
        }
    }

    private final void assertIsLocPathIterator(Expression expr1, ExpressionOwner eo) throws RuntimeException {
        if (!(expr1 instanceof LocPathIterator)) {
            String errMsg = expr1 instanceof Variable ? "Programmer's assertion: expr1 not an iterator: " + ((Variable)expr1).getQName() : "Programmer's assertion: expr1 not an iterator: " + expr1.getClass().getName();
            throw new RuntimeException(errMsg + ", " + eo.getClass().getName() + " " + expr1.exprGetParent());
        }
    }

    private static void validateNewAddition(Vector paths, ExpressionOwner owner, LocPathIterator path) throws RuntimeException {
        RedundentExprEliminator.assertion(owner.getExpression() == path, "owner.getExpression() != path!!!");
        int n = paths.size();
        for (int i = 0; i < n; ++i) {
            ExpressionOwner ew = (ExpressionOwner)paths.elementAt(i);
            RedundentExprEliminator.assertion(ew != owner, "duplicate owner on the list!!!");
            RedundentExprEliminator.assertion(ew.getExpression() != path, "duplicate expression on the list!!!");
        }
    }

    protected static void assertion(boolean b, String msg) {
        if (!b) {
            throw new RuntimeException(XSLMessages.createMessage("ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR", new Object[]{msg}));
        }
    }

    class MultistepExprHolder
    implements Cloneable {
        ExpressionOwner m_exprOwner;
        final int m_stepCount;
        MultistepExprHolder m_next;

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        MultistepExprHolder(ExpressionOwner exprOwner, int stepCount, MultistepExprHolder next) {
            this.m_exprOwner = exprOwner;
            RedundentExprEliminator.assertion(null != this.m_exprOwner, "exprOwner can not be null!");
            this.m_stepCount = stepCount;
            this.m_next = next;
        }

        MultistepExprHolder addInSortedOrder(ExpressionOwner exprOwner, int stepCount) {
            MultistepExprHolder first = this;
            MultistepExprHolder next = this;
            MultistepExprHolder prev = null;
            while (null != next) {
                if (stepCount >= next.m_stepCount) {
                    MultistepExprHolder newholder = new MultistepExprHolder(exprOwner, stepCount, next);
                    if (null == prev) {
                        first = newholder;
                    } else {
                        prev.m_next = newholder;
                    }
                    return first;
                }
                prev = next;
                next = next.m_next;
            }
            prev.m_next = new MultistepExprHolder(exprOwner, stepCount, null);
            return first;
        }

        MultistepExprHolder unlink(MultistepExprHolder itemToRemove) {
            MultistepExprHolder first = this;
            MultistepExprHolder next = this;
            MultistepExprHolder prev = null;
            while (null != next) {
                if (next == itemToRemove) {
                    if (null == prev) {
                        first = next.m_next;
                    } else {
                        prev.m_next = next.m_next;
                    }
                    next.m_next = null;
                    return first;
                }
                prev = next;
                next = next.m_next;
            }
            RedundentExprEliminator.assertion(false, "unlink failed!!!");
            return null;
        }

        int getLength() {
            int count = 0;
            MultistepExprHolder next = this;
            while (null != next) {
                ++count;
                next = next.m_next;
            }
            return count;
        }

        protected void diagnose() {
            System.err.print("Found multistep iterators: " + this.getLength() + "  ");
            MultistepExprHolder next = this;
            while (null != next) {
                System.err.print("" + next.m_stepCount);
                next = next.m_next;
                if (null == next) continue;
                System.err.print(", ");
            }
            System.err.println();
        }
    }
}

