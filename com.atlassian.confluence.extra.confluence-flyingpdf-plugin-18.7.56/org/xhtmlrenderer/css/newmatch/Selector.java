/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.newmatch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.css.extend.TreeResolver;
import org.xhtmlrenderer.css.newmatch.Condition;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.util.XRLog;

public class Selector {
    private Ruleset _parent;
    private Selector chainedSelector = null;
    private Selector siblingSelector = null;
    private int _axis;
    private String _name;
    private String _namespaceURI;
    private int _pc = 0;
    private String _pe;
    private int _specificityB;
    private int _specificityC;
    private int _specificityD;
    private int _pos;
    private List conditions;
    public static final int DESCENDANT_AXIS = 0;
    public static final int CHILD_AXIS = 1;
    public static final int IMMEDIATE_SIBLING_AXIS = 2;
    public static final int VISITED_PSEUDOCLASS = 2;
    public static final int HOVER_PSEUDOCLASS = 4;
    public static final int ACTIVE_PSEUDOCLASS = 8;
    public static final int FOCUS_PSEUDOCLASS = 16;
    private int selectorID = selectorCount++;
    private static int selectorCount = 0;

    public boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
        if (this.siblingSelector != null) {
            Object sib = this.siblingSelector.getAppropriateSibling(e, treeRes);
            if (sib == null) {
                return false;
            }
            if (!this.siblingSelector.matches(sib, attRes, treeRes)) {
                return false;
            }
        }
        if (this._name == null || treeRes.matchesElement(e, this._namespaceURI, this._name)) {
            if (this.conditions != null) {
                for (Condition c : this.conditions) {
                    if (c.matches(e, attRes, treeRes)) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean matchesDynamic(Object e, AttributeResolver attRes, TreeResolver treeRes) {
        if (this.siblingSelector != null) {
            Object sib = this.siblingSelector.getAppropriateSibling(e, treeRes);
            if (sib == null) {
                return false;
            }
            if (!this.siblingSelector.matchesDynamic(sib, attRes, treeRes)) {
                return false;
            }
        }
        if (this.isPseudoClass(2) && (attRes == null || !attRes.isVisited(e))) {
            return false;
        }
        if (this.isPseudoClass(8) && (attRes == null || !attRes.isActive(e))) {
            return false;
        }
        if (this.isPseudoClass(4) && (attRes == null || !attRes.isHover(e))) {
            return false;
        }
        return !this.isPseudoClass(16) || attRes != null && attRes.isFocus(e);
    }

    public void addUnsupportedCondition() {
        this.addCondition(Condition.createUnsupportedCondition());
    }

    public void addLinkCondition() {
        ++this._specificityC;
        this.addCondition(Condition.createLinkCondition());
    }

    public void addFirstChildCondition() {
        ++this._specificityC;
        this.addCondition(Condition.createFirstChildCondition());
    }

    public void addLastChildCondition() {
        ++this._specificityC;
        this.addCondition(Condition.createLastChildCondition());
    }

    public void addNthChildCondition(String number) {
        ++this._specificityC;
        this.addCondition(Condition.createNthChildCondition(number));
    }

    public void addEvenChildCondition() {
        ++this._specificityC;
        this.addCondition(Condition.createEvenChildCondition());
    }

    public void addOddChildCondition() {
        ++this._specificityC;
        this.addCondition(Condition.createOddChildCondition());
    }

    public void addLangCondition(String lang) {
        ++this._specificityC;
        this.addCondition(Condition.createLangCondition(lang));
    }

    public void addIDCondition(String id) {
        ++this._specificityB;
        this.addCondition(Condition.createIDCondition(id));
    }

    public void addClassCondition(String className) {
        ++this._specificityC;
        this.addCondition(Condition.createClassCondition(className));
    }

    public void addAttributeExistsCondition(String namespaceURI, String name) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeExistsCondition(namespaceURI, name));
    }

    public void addAttributeEqualsCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeEqualsCondition(namespaceURI, name, value));
    }

    public void addAttributePrefixCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributePrefixCondition(namespaceURI, name, value));
    }

    public void addAttributeSuffixCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeSuffixCondition(namespaceURI, name, value));
    }

    public void addAttributeSubstringCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeSubstringCondition(namespaceURI, name, value));
    }

    public void addAttributeMatchesListCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeMatchesListCondition(namespaceURI, name, value));
    }

    public void addAttributeMatchesFirstPartCondition(String namespaceURI, String name, String value) {
        ++this._specificityC;
        this.addCondition(Condition.createAttributeMatchesFirstPartCondition(namespaceURI, name, value));
    }

    public void setPseudoClass(int pc) {
        if (!this.isPseudoClass(pc)) {
            ++this._specificityC;
        }
        this._pc |= pc;
    }

    public void setPseudoElement(String pseudoElement) {
        if (this._pe != null) {
            this.addUnsupportedCondition();
            XRLog.match(Level.WARNING, "Trying to set more than one pseudo-element");
        } else {
            ++this._specificityD;
            this._pe = pseudoElement;
        }
    }

    public boolean isPseudoClass(int pc) {
        return (this._pc & pc) != 0;
    }

    public String getPseudoElement() {
        return this._pe;
    }

    public Selector getChainedSelector() {
        return this.chainedSelector;
    }

    public Ruleset getRuleset() {
        return this._parent;
    }

    public int getAxis() {
        return this._axis;
    }

    public int getSpecificityB() {
        return this._specificityB;
    }

    public int getSpecificityD() {
        return this._specificityD;
    }

    public int getSpecificityC() {
        return this._specificityC;
    }

    String getOrder() {
        if (this.chainedSelector != null) {
            return this.chainedSelector.getOrder();
        }
        String b = "000" + this.getSpecificityB();
        String c = "000" + this.getSpecificityC();
        String d = "000" + this.getSpecificityD();
        String p = "00000" + this._pos;
        return "0" + b.substring(b.length() - 3) + c.substring(c.length() - 3) + d.substring(d.length() - 3) + p.substring(p.length() - 5);
    }

    Object getAppropriateSibling(Object e, TreeResolver treeRes) {
        Object sibling = null;
        switch (this._axis) {
            case 2: {
                sibling = treeRes.getPreviousSiblingElement(e);
                break;
            }
            default: {
                XRLog.exception("Bad sibling axis");
            }
        }
        return sibling;
    }

    private void addCondition(Condition c) {
        if (this.conditions == null) {
            this.conditions = new ArrayList();
        }
        if (this._pe != null) {
            this.conditions.add(Condition.createUnsupportedCondition());
            XRLog.match(Level.WARNING, "Trying to append conditions to pseudoElement " + this._pe);
        }
        this.conditions.add(c);
    }

    static String getElementStylingOrder() {
        return "100000000000000";
    }

    public int getSelectorID() {
        return this.selectorID;
    }

    public void setName(String name) {
        this._name = name;
        ++this._specificityD;
    }

    public void setPos(int pos) {
        this._pos = pos;
        if (this.siblingSelector != null) {
            this.siblingSelector.setPos(pos);
        }
        if (this.chainedSelector != null) {
            this.chainedSelector.setPos(pos);
        }
    }

    public void setParent(Ruleset ruleset) {
        this._parent = ruleset;
    }

    public void setAxis(int axis) {
        this._axis = axis;
    }

    public void setSpecificityB(int b) {
        this._specificityB = b;
    }

    public void setSpecificityC(int c) {
        this._specificityC = c;
    }

    public void setSpecificityD(int d) {
        this._specificityD = d;
    }

    public void setChainedSelector(Selector selector) {
        this.chainedSelector = selector;
    }

    public void setSiblingSelector(Selector selector) {
        this.siblingSelector = selector;
    }

    public void setNamespaceURI(String namespaceURI) {
        this._namespaceURI = namespaceURI;
    }
}

