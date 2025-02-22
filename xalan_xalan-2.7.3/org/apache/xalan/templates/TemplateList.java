/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateSubPatternAssociation;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.patterns.UnionPattern;

public class TemplateList
implements Serializable {
    static final long serialVersionUID = 5803675288911728791L;
    static final boolean DEBUG = false;
    private Hashtable m_namedTemplates = new Hashtable(89);
    private Hashtable m_patternTable = new Hashtable(89);
    private TemplateSubPatternAssociation m_wildCardPatterns = null;
    private TemplateSubPatternAssociation m_textPatterns = null;
    private TemplateSubPatternAssociation m_docPatterns = null;
    private TemplateSubPatternAssociation m_commentPatterns = null;

    public void setTemplate(ElemTemplate template) {
        XPath matchXPath = template.getMatch();
        if (null == template.getName() && null == matchXPath) {
            template.error("ER_NEED_NAME_OR_MATCH_ATTRIB", new Object[]{"xsl:template"});
        }
        if (null != template.getName()) {
            ElemTemplate existingTemplate = (ElemTemplate)this.m_namedTemplates.get(template.getName());
            if (null == existingTemplate) {
                this.m_namedTemplates.put(template.getName(), template);
            } else {
                int existingPrecedence = existingTemplate.getStylesheetComposed().getImportCountComposed();
                int newPrecedence = template.getStylesheetComposed().getImportCountComposed();
                if (newPrecedence > existingPrecedence) {
                    this.m_namedTemplates.put(template.getName(), template);
                } else if (newPrecedence == existingPrecedence) {
                    template.error("ER_DUPLICATE_NAMED_TEMPLATE", new Object[]{template.getName()});
                }
            }
        }
        if (null != matchXPath) {
            Expression matchExpr = matchXPath.getExpression();
            if (matchExpr instanceof StepPattern) {
                this.insertPatternInTable((StepPattern)matchExpr, template);
            } else if (matchExpr instanceof UnionPattern) {
                UnionPattern upat = (UnionPattern)matchExpr;
                StepPattern[] pats = upat.getPatterns();
                int n = pats.length;
                for (int i = 0; i < n; ++i) {
                    this.insertPatternInTable(pats[i], template);
                }
            }
        }
    }

    void dumpAssociationTables() {
        TemplateSubPatternAssociation head;
        Enumeration associations = this.m_patternTable.elements();
        while (associations.hasMoreElements()) {
            for (head = (TemplateSubPatternAssociation)associations.nextElement(); null != head; head = head.getNext()) {
                System.out.print("(" + head.getTargetString() + ", " + head.getPattern() + ")");
            }
            System.out.println("\n.....");
        }
        System.out.print("wild card list: ");
        for (head = this.m_wildCardPatterns; null != head; head = head.getNext()) {
            System.out.print("(" + head.getTargetString() + ", " + head.getPattern() + ")");
        }
        System.out.println("\n.....");
    }

    public void compose(StylesheetRoot sroot) {
        if (null != this.m_wildCardPatterns) {
            Enumeration associations = this.m_patternTable.elements();
            while (associations.hasMoreElements()) {
                TemplateSubPatternAssociation head = (TemplateSubPatternAssociation)associations.nextElement();
                for (TemplateSubPatternAssociation wild = this.m_wildCardPatterns; null != wild; wild = wild.getNext()) {
                    try {
                        head = this.insertAssociationIntoList(head, (TemplateSubPatternAssociation)wild.clone(), true);
                        continue;
                    }
                    catch (CloneNotSupportedException cloneNotSupportedException) {
                        // empty catch block
                    }
                }
            }
        }
    }

    private TemplateSubPatternAssociation insertAssociationIntoList(TemplateSubPatternAssociation head, TemplateSubPatternAssociation item, boolean isWildCardInsert) {
        boolean insertBefore;
        double workPriority;
        TemplateSubPatternAssociation next;
        double priority = this.getPriorityOrScore(item);
        int importLevel = item.getImportLevel();
        int docOrder = item.getDocOrderPos();
        TemplateSubPatternAssociation insertPoint = head;
        while (null != (next = insertPoint.getNext())) {
            workPriority = this.getPriorityOrScore(next);
            if (importLevel > next.getImportLevel()) break;
            if (importLevel < next.getImportLevel()) {
                insertPoint = next;
                continue;
            }
            if (priority > workPriority) break;
            if (priority < workPriority) {
                insertPoint = next;
                continue;
            }
            if (docOrder >= next.getDocOrderPos()) break;
            insertPoint = next;
        }
        if (null == next || insertPoint == head) {
            workPriority = this.getPriorityOrScore(insertPoint);
            insertBefore = importLevel > insertPoint.getImportLevel() ? true : (importLevel < insertPoint.getImportLevel() ? false : (priority > workPriority ? true : (priority < workPriority ? false : docOrder >= insertPoint.getDocOrderPos())));
        } else {
            insertBefore = false;
        }
        if (isWildCardInsert) {
            if (insertBefore) {
                item.setNext(insertPoint);
                String key = insertPoint.getTargetString();
                item.setTargetString(key);
                this.putHead(key, item);
                return item;
            }
            item.setNext(next);
            insertPoint.setNext(item);
            return head;
        }
        if (insertBefore) {
            item.setNext(insertPoint);
            if (insertPoint.isWild() || item.isWild()) {
                this.m_wildCardPatterns = item;
            } else {
                this.putHead(item.getTargetString(), item);
            }
            return item;
        }
        item.setNext(next);
        insertPoint.setNext(item);
        return head;
    }

    private void insertPatternInTable(StepPattern pattern, ElemTemplate template) {
        String target = pattern.getTargetString();
        if (null != target) {
            TemplateSubPatternAssociation head;
            String pstring = template.getMatch().getPatternString();
            TemplateSubPatternAssociation association = new TemplateSubPatternAssociation(template, pattern, pstring);
            boolean isWildCard = association.isWild();
            TemplateSubPatternAssociation templateSubPatternAssociation = head = isWildCard ? this.m_wildCardPatterns : this.getHead(target);
            if (null == head) {
                if (isWildCard) {
                    this.m_wildCardPatterns = association;
                } else {
                    this.putHead(target, association);
                }
            } else {
                this.insertAssociationIntoList(head, association, false);
            }
        }
    }

    private double getPriorityOrScore(TemplateSubPatternAssociation matchPat) {
        StepPattern ex;
        double priority = matchPat.getTemplate().getPriority();
        if (priority == Double.NEGATIVE_INFINITY && (ex = matchPat.getStepPattern()) instanceof NodeTest) {
            return ((NodeTest)ex).getDefaultScore();
        }
        return priority;
    }

    public ElemTemplate getTemplate(QName qname) {
        return (ElemTemplate)this.m_namedTemplates.get(qname);
    }

    public TemplateSubPatternAssociation getHead(XPathContext xctxt, int targetNode, DTM dtm) {
        TemplateSubPatternAssociation head;
        short targetNodeType = dtm.getNodeType(targetNode);
        switch (targetNodeType) {
            case 1: 
            case 2: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getLocalName(targetNode));
                break;
            }
            case 3: 
            case 4: {
                head = this.m_textPatterns;
                break;
            }
            case 5: 
            case 6: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getNodeName(targetNode));
                break;
            }
            case 7: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getLocalName(targetNode));
                break;
            }
            case 8: {
                head = this.m_commentPatterns;
                break;
            }
            case 9: 
            case 11: {
                head = this.m_docPatterns;
                break;
            }
            default: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getNodeName(targetNode));
            }
        }
        return null == head ? this.m_wildCardPatterns : head;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ElemTemplate getTemplateFast(XPathContext xctxt, int targetNode, int expTypeID, QName mode, int maxImportLevel, boolean quietConflictWarnings, DTM dtm) throws TransformerException {
        TemplateSubPatternAssociation head;
        switch (dtm.getNodeType(targetNode)) {
            case 1: 
            case 2: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getLocalNameFromExpandedNameID(expTypeID));
                break;
            }
            case 3: 
            case 4: {
                head = this.m_textPatterns;
                break;
            }
            case 5: 
            case 6: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getNodeName(targetNode));
                break;
            }
            case 7: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getLocalName(targetNode));
                break;
            }
            case 8: {
                head = this.m_commentPatterns;
                break;
            }
            case 9: 
            case 11: {
                head = this.m_docPatterns;
                break;
            }
            default: {
                head = (TemplateSubPatternAssociation)this.m_patternTable.get(dtm.getNodeName(targetNode));
            }
        }
        if (null == head && null == (head = this.m_wildCardPatterns)) {
            return null;
        }
        xctxt.pushNamespaceContextNull();
        try {
            do {
                if (maxImportLevel > -1 && head.getImportLevel() > maxImportLevel) continue;
                ElemTemplate template = head.getTemplate();
                xctxt.setNamespaceContext(template);
                if (head.m_stepPattern.execute(xctxt, targetNode, dtm, expTypeID) == NodeTest.SCORE_NONE || !head.matchMode(mode)) continue;
                if (quietConflictWarnings) {
                    this.checkConflicts(head, xctxt, targetNode, mode);
                }
                ElemTemplate elemTemplate = template;
                return elemTemplate;
            } while (null != (head = head.getNext()));
        }
        finally {
            xctxt.popNamespaceContext();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ElemTemplate getTemplate(XPathContext xctxt, int targetNode, QName mode, boolean quietConflictWarnings, DTM dtm) throws TransformerException {
        TemplateSubPatternAssociation head = this.getHead(xctxt, targetNode, dtm);
        if (null != head) {
            xctxt.pushNamespaceContextNull();
            xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);
            try {
                do {
                    ElemTemplate template = head.getTemplate();
                    xctxt.setNamespaceContext(template);
                    if (head.m_stepPattern.execute(xctxt, targetNode) == NodeTest.SCORE_NONE || !head.matchMode(mode)) continue;
                    if (quietConflictWarnings) {
                        this.checkConflicts(head, xctxt, targetNode, mode);
                    }
                    ElemTemplate elemTemplate = template;
                    return elemTemplate;
                } while (null != (head = head.getNext()));
            }
            finally {
                xctxt.popCurrentNodeAndExpression();
                xctxt.popNamespaceContext();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ElemTemplate getTemplate(XPathContext xctxt, int targetNode, QName mode, int maxImportLevel, int endImportLevel, boolean quietConflictWarnings, DTM dtm) throws TransformerException {
        TemplateSubPatternAssociation head = this.getHead(xctxt, targetNode, dtm);
        if (null != head) {
            xctxt.pushNamespaceContextNull();
            xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);
            try {
                do {
                    if (maxImportLevel > -1 && head.getImportLevel() > maxImportLevel) continue;
                    if (head.getImportLevel() <= maxImportLevel - endImportLevel) {
                        ElemTemplate elemTemplate = null;
                        return elemTemplate;
                    }
                    ElemTemplate template = head.getTemplate();
                    xctxt.setNamespaceContext(template);
                    if (head.m_stepPattern.execute(xctxt, targetNode) == NodeTest.SCORE_NONE || !head.matchMode(mode)) continue;
                    if (quietConflictWarnings) {
                        this.checkConflicts(head, xctxt, targetNode, mode);
                    }
                    ElemTemplate elemTemplate = template;
                    return elemTemplate;
                } while (null != (head = head.getNext()));
            }
            finally {
                xctxt.popCurrentNodeAndExpression();
                xctxt.popNamespaceContext();
            }
        }
        return null;
    }

    public TemplateWalker getWalker() {
        return new TemplateWalker();
    }

    private void checkConflicts(TemplateSubPatternAssociation head, XPathContext xctxt, int targetNode, QName mode) {
    }

    private void addObjectIfNotFound(Object obj, Vector v) {
        int n = v.size();
        boolean addIt = true;
        for (int i = 0; i < n; ++i) {
            if (v.elementAt(i) != obj) continue;
            addIt = false;
            break;
        }
        if (addIt) {
            v.addElement(obj);
        }
    }

    private Hashtable getNamedTemplates() {
        return this.m_namedTemplates;
    }

    private void setNamedTemplates(Hashtable v) {
        this.m_namedTemplates = v;
    }

    private TemplateSubPatternAssociation getHead(String key) {
        return (TemplateSubPatternAssociation)this.m_patternTable.get(key);
    }

    private void putHead(String key, TemplateSubPatternAssociation assoc) {
        if (key.equals("#text")) {
            this.m_textPatterns = assoc;
        } else if (key.equals("/")) {
            this.m_docPatterns = assoc;
        } else if (key.equals("#comment")) {
            this.m_commentPatterns = assoc;
        }
        this.m_patternTable.put(key, assoc);
    }

    public class TemplateWalker {
        private Enumeration hashIterator;
        private boolean inPatterns;
        private TemplateSubPatternAssociation curPattern;
        private Hashtable m_compilerCache = new Hashtable();

        private TemplateWalker() {
            this.hashIterator = TemplateList.this.m_patternTable.elements();
            this.inPatterns = true;
            this.curPattern = null;
        }

        public ElemTemplate next() {
            ElemTemplate ct;
            ElemTemplateElement retValue = null;
            do {
                if (this.inPatterns) {
                    if (null != this.curPattern) {
                        this.curPattern = this.curPattern.getNext();
                    }
                    if (null != this.curPattern) {
                        retValue = this.curPattern.getTemplate();
                    } else if (this.hashIterator.hasMoreElements()) {
                        this.curPattern = (TemplateSubPatternAssociation)this.hashIterator.nextElement();
                        retValue = this.curPattern.getTemplate();
                    } else {
                        this.inPatterns = false;
                        this.hashIterator = TemplateList.this.m_namedTemplates.elements();
                    }
                }
                if (this.inPatterns) continue;
                if (this.hashIterator.hasMoreElements()) {
                    retValue = (ElemTemplate)this.hashIterator.nextElement();
                    continue;
                }
                return null;
            } while (null != (ct = (ElemTemplate)this.m_compilerCache.get(new Integer(retValue.getUid()))));
            this.m_compilerCache.put(new Integer(retValue.getUid()), retValue);
            return retValue;
        }
    }
}

