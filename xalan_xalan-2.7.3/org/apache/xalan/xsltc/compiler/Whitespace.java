/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Whitespace
extends TopLevelElement {
    public static final int USE_PREDICATE = 0;
    public static final int STRIP_SPACE = 1;
    public static final int PRESERVE_SPACE = 2;
    public static final int RULE_NONE = 0;
    public static final int RULE_ELEMENT = 1;
    public static final int RULE_NAMESPACE = 2;
    public static final int RULE_ALL = 3;
    private String _elementList;
    private int _action;
    private int _importPrecedence;

    Whitespace() {
    }

    @Override
    public void parseContents(Parser parser) {
        this._action = this._qname.getLocalPart().endsWith("strip-space") ? 1 : 2;
        this._importPrecedence = parser.getCurrentImportPrecedence();
        this._elementList = this.getAttribute("elements");
        if (this._elementList == null || this._elementList.length() == 0) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "elements");
            return;
        }
        SymbolTable stable = parser.getSymbolTable();
        StringTokenizer list = new StringTokenizer(this._elementList);
        StringBuffer elements = new StringBuffer("");
        while (list.hasMoreElements()) {
            String token = list.nextToken();
            int col = token.indexOf(58);
            if (col != -1) {
                String namespace = this.lookupNamespace(token.substring(0, col));
                if (namespace != null) {
                    elements.append(namespace + ":" + token.substring(col + 1, token.length()));
                } else {
                    elements.append(token);
                }
            } else {
                elements.append(token);
            }
            if (!list.hasMoreElements()) continue;
            elements.append(" ");
        }
        this._elementList = elements.toString();
    }

    public Vector getRules() {
        Vector<WhitespaceRule> rules = new Vector<WhitespaceRule>();
        StringTokenizer list = new StringTokenizer(this._elementList);
        while (list.hasMoreElements()) {
            rules.add(new WhitespaceRule(this._action, list.nextToken(), this._importPrecedence));
        }
        return rules;
    }

    private static WhitespaceRule findContradictingRule(Vector rules, WhitespaceRule rule) {
        block5: for (int i = 0; i < rules.size(); ++i) {
            WhitespaceRule currentRule = (WhitespaceRule)rules.elementAt(i);
            if (currentRule == rule) {
                return null;
            }
            switch (currentRule.getStrength()) {
                case 3: {
                    return currentRule;
                }
                case 1: {
                    if (!rule.getElement().equals(currentRule.getElement())) continue block5;
                }
                case 2: {
                    if (!rule.getNamespace().equals(currentRule.getNamespace())) continue block5;
                    return currentRule;
                }
            }
        }
        return null;
    }

    private static int prioritizeRules(Vector rules) {
        WhitespaceRule currentRule;
        int defaultAction = 2;
        Whitespace.quicksort(rules, 0, rules.size() - 1);
        boolean strip = false;
        for (int i = 0; i < rules.size(); ++i) {
            currentRule = (WhitespaceRule)rules.elementAt(i);
            if (currentRule.getAction() != 1) continue;
            strip = true;
        }
        if (!strip) {
            rules.removeAllElements();
            return 2;
        }
        int idx = 0;
        while (idx < rules.size()) {
            currentRule = (WhitespaceRule)rules.elementAt(idx);
            if (Whitespace.findContradictingRule(rules, currentRule) != null) {
                rules.remove(idx);
                continue;
            }
            if (currentRule.getStrength() == 3) {
                defaultAction = currentRule.getAction();
                for (int i = idx; i < rules.size(); ++i) {
                    rules.removeElementAt(i);
                }
            }
            ++idx;
        }
        if (rules.size() == 0) {
            return defaultAction;
        }
        while ((currentRule = (WhitespaceRule)rules.lastElement()).getAction() == defaultAction) {
            rules.removeElementAt(rules.size() - 1);
            if (rules.size() > 0) continue;
        }
        return defaultAction;
    }

    public static void compileStripSpace(BranchHandle[] strip, int sCount, InstructionList il) {
        InstructionHandle target = il.append(ICONST_1);
        il.append(IRETURN);
        for (int i = 0; i < sCount; ++i) {
            strip[i].setTarget(target);
        }
    }

    public static void compilePreserveSpace(BranchHandle[] preserve, int pCount, InstructionList il) {
        InstructionHandle target = il.append(ICONST_0);
        il.append(IRETURN);
        for (int i = 0; i < pCount; ++i) {
            preserve[i].setTarget(target);
        }
    }

    private static void compilePredicate(Vector rules, int defaultAction, ClassGenerator classGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        XSLTC xsltc = classGen.getParser().getXSLTC();
        MethodGenerator stripSpace = new MethodGenerator(17, org.apache.bcel.generic.Type.BOOLEAN, new org.apache.bcel.generic.Type[]{Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), org.apache.bcel.generic.Type.INT, org.apache.bcel.generic.Type.INT}, new String[]{"dom", "node", "type"}, "stripSpace", classGen.getClassName(), il, cpg);
        classGen.addInterface("org/apache/xalan/xsltc/StripFilter");
        int paramDom = stripSpace.getLocalIndex("dom");
        int paramCurrent = stripSpace.getLocalIndex("node");
        int paramType = stripSpace.getLocalIndex("type");
        BranchHandle[] strip = new BranchHandle[rules.size()];
        BranchHandle[] preserve = new BranchHandle[rules.size()];
        int sCount = 0;
        int pCount = 0;
        for (int i = 0; i < rules.size(); ++i) {
            WhitespaceRule rule = (WhitespaceRule)rules.elementAt(i);
            int gns = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getNamespaceName", "(I)Ljava/lang/String;");
            int strcmp = cpg.addMethodref("java/lang/String", "compareTo", "(Ljava/lang/String;)I");
            if (rule.getStrength() == 2) {
                il.append(new ALOAD(paramDom));
                il.append(new ILOAD(paramCurrent));
                il.append(new INVOKEINTERFACE(gns, 2));
                il.append(new PUSH(cpg, rule.getNamespace()));
                il.append(new INVOKEVIRTUAL(strcmp));
                il.append(ICONST_0);
                if (rule.getAction() == 1) {
                    strip[sCount++] = il.append(new IF_ICMPEQ(null));
                    continue;
                }
                preserve[pCount++] = il.append(new IF_ICMPEQ(null));
                continue;
            }
            if (rule.getStrength() != 1) continue;
            Parser parser = classGen.getParser();
            QName qname = rule.getNamespace() != "" ? parser.getQName(rule.getNamespace(), null, rule.getElement()) : parser.getQName(rule.getElement());
            int elementType = xsltc.registerElement(qname);
            il.append(new ILOAD(paramType));
            il.append(new PUSH(cpg, elementType));
            if (rule.getAction() == 1) {
                strip[sCount++] = il.append(new IF_ICMPEQ(null));
                continue;
            }
            preserve[pCount++] = il.append(new IF_ICMPEQ(null));
        }
        if (defaultAction == 1) {
            Whitespace.compileStripSpace(strip, sCount, il);
            Whitespace.compilePreserveSpace(preserve, pCount, il);
        } else {
            Whitespace.compilePreserveSpace(preserve, pCount, il);
            Whitespace.compileStripSpace(strip, sCount, il);
        }
        classGen.addMethod(stripSpace);
    }

    private static void compileDefault(int defaultAction, ClassGenerator classGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        XSLTC xsltc = classGen.getParser().getXSLTC();
        MethodGenerator stripSpace = new MethodGenerator(17, org.apache.bcel.generic.Type.BOOLEAN, new org.apache.bcel.generic.Type[]{Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), org.apache.bcel.generic.Type.INT, org.apache.bcel.generic.Type.INT}, new String[]{"dom", "node", "type"}, "stripSpace", classGen.getClassName(), il, cpg);
        classGen.addInterface("org/apache/xalan/xsltc/StripFilter");
        if (defaultAction == 1) {
            il.append(ICONST_1);
        } else {
            il.append(ICONST_0);
        }
        il.append(IRETURN);
        classGen.addMethod(stripSpace);
    }

    public static int translateRules(Vector rules, ClassGenerator classGen) {
        int defaultAction = Whitespace.prioritizeRules(rules);
        if (rules.size() == 0) {
            Whitespace.compileDefault(defaultAction, classGen);
            return defaultAction;
        }
        Whitespace.compilePredicate(rules, defaultAction, classGen);
        return 0;
    }

    private static void quicksort(Vector rules, int p, int r) {
        while (p < r) {
            int q = Whitespace.partition(rules, p, r);
            Whitespace.quicksort(rules, p, q);
            p = q + 1;
        }
    }

    private static int partition(Vector rules, int p, int r) {
        WhitespaceRule x = (WhitespaceRule)rules.elementAt(p + r >>> 1);
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x.compareTo((WhitespaceRule)rules.elementAt(--j)) < 0) {
                continue;
            }
            while (x.compareTo((WhitespaceRule)rules.elementAt(++i)) > 0) {
            }
            if (i >= j) break;
            WhitespaceRule tmp = (WhitespaceRule)rules.elementAt(i);
            rules.setElementAt(rules.elementAt(j), i);
            rules.setElementAt(tmp, j);
        }
        return j;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
    }

    private static final class WhitespaceRule {
        private final int _action;
        private String _namespace;
        private String _element;
        private int _type;
        private int _priority;

        public WhitespaceRule(int action, String element, int precedence) {
            this._action = action;
            int colon = element.lastIndexOf(58);
            if (colon >= 0) {
                this._namespace = element.substring(0, colon);
                this._element = element.substring(colon + 1, element.length());
            } else {
                this._namespace = "";
                this._element = element;
            }
            this._priority = precedence << 2;
            if (this._element.equals("*")) {
                if (this._namespace == "") {
                    this._type = 3;
                    this._priority += 2;
                } else {
                    this._type = 2;
                    ++this._priority;
                }
            } else {
                this._type = 1;
            }
        }

        public int compareTo(WhitespaceRule other) {
            return this._priority < other._priority ? -1 : (this._priority > other._priority ? 1 : 0);
        }

        public int getAction() {
            return this._action;
        }

        public int getStrength() {
            return this._type;
        }

        public int getPriority() {
            return this._priority;
        }

        public String getElement() {
            return this._element;
        }

        public String getNamespace() {
            return this._namespace;
        }
    }
}

