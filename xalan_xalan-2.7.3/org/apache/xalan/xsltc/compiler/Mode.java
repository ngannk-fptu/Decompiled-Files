/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.SWITCH;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;
import org.apache.xalan.xsltc.compiler.AlternativePattern;
import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.compiler.IdKeyPattern;
import org.apache.xalan.xsltc.compiler.LocationPathPattern;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.TestSeq;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NamedMethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Mode
implements Constants {
    private final QName _name;
    private final Stylesheet _stylesheet;
    private final String _methodName;
    private Vector _templates;
    private Vector _childNodeGroup = null;
    private TestSeq _childNodeTestSeq = null;
    private Vector _attribNodeGroup = null;
    private TestSeq _attribNodeTestSeq = null;
    private Vector _idxGroup = null;
    private TestSeq _idxTestSeq = null;
    private Vector[] _patternGroups;
    private TestSeq[] _testSeq;
    private Hashtable _neededTemplates = new Hashtable();
    private Hashtable _namedTemplates = new Hashtable();
    private Hashtable _templateIHs = new Hashtable();
    private Hashtable _templateILs = new Hashtable();
    private LocationPathPattern _rootPattern = null;
    private Hashtable _importLevels = null;
    private Hashtable _keys = null;
    private int _currentIndex;

    public Mode(QName name, Stylesheet stylesheet, String suffix) {
        this._name = name;
        this._stylesheet = stylesheet;
        this._methodName = "applyTemplates" + suffix;
        this._templates = new Vector();
        this._patternGroups = new Vector[32];
    }

    public String functionName() {
        return this._methodName;
    }

    public String functionName(int min, int max) {
        if (this._importLevels == null) {
            this._importLevels = new Hashtable();
        }
        this._importLevels.put(new Integer(max), new Integer(min));
        return this._methodName + '_' + max;
    }

    private String getClassName() {
        return this._stylesheet.getClassName();
    }

    public Stylesheet getStylesheet() {
        return this._stylesheet;
    }

    public void addTemplate(Template template) {
        this._templates.addElement(template);
    }

    private Vector quicksort(Vector templates, int p, int r) {
        if (p < r) {
            int q = this.partition(templates, p, r);
            this.quicksort(templates, p, q);
            this.quicksort(templates, q + 1, r);
        }
        return templates;
    }

    private int partition(Vector templates, int p, int r) {
        Template x = (Template)templates.elementAt(p);
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x.compareTo((Template)templates.elementAt(--j)) > 0) {
                continue;
            }
            while (x.compareTo((Template)templates.elementAt(++i)) < 0) {
            }
            if (i >= j) break;
            templates.set(j, templates.set(i, templates.elementAt(j)));
        }
        return j;
    }

    public void processPatterns(Hashtable keys) {
        this._keys = keys;
        this._templates = this.quicksort(this._templates, 0, this._templates.size() - 1);
        Enumeration templates = this._templates.elements();
        while (templates.hasMoreElements()) {
            Pattern pattern;
            Template template = (Template)templates.nextElement();
            if (template.isNamed() && !template.disabled()) {
                this._namedTemplates.put(template, this);
            }
            if ((pattern = template.getPattern()) == null) continue;
            this.flattenAlternative(pattern, template, keys);
        }
        this.prepareTestSequences();
    }

    private void flattenAlternative(Pattern pattern, Template template, Hashtable keys) {
        if (pattern instanceof IdKeyPattern) {
            IdKeyPattern idkey = (IdKeyPattern)pattern;
            idkey.setTemplate(template);
            if (this._idxGroup == null) {
                this._idxGroup = new Vector();
            }
            this._idxGroup.add(pattern);
        } else if (pattern instanceof AlternativePattern) {
            AlternativePattern alt = (AlternativePattern)pattern;
            this.flattenAlternative(alt.getLeft(), template, keys);
            this.flattenAlternative(alt.getRight(), template, keys);
        } else if (pattern instanceof LocationPathPattern) {
            LocationPathPattern lpp = (LocationPathPattern)pattern;
            lpp.setTemplate(template);
            this.addPatternToGroup(lpp);
        }
    }

    private void addPatternToGroup(LocationPathPattern lpp) {
        if (lpp instanceof IdKeyPattern) {
            this.addPattern(-1, lpp);
        } else {
            StepPattern kernel = lpp.getKernelPattern();
            if (kernel != null) {
                this.addPattern(kernel.getNodeType(), lpp);
            } else if (this._rootPattern == null || lpp.noSmallerThan(this._rootPattern)) {
                this._rootPattern = lpp;
            }
        }
    }

    private void addPattern(int kernelType, LocationPathPattern pattern) {
        Vector<LocationPathPattern> patterns;
        int oldLength = this._patternGroups.length;
        if (kernelType >= oldLength) {
            Vector[] newGroups = new Vector[kernelType * 2];
            System.arraycopy(this._patternGroups, 0, newGroups, 0, oldLength);
            this._patternGroups = newGroups;
        }
        if (kernelType == -1) {
            patterns = pattern.getAxis() == 2 ? (this._attribNodeGroup == null ? (this._attribNodeGroup = new Vector<LocationPathPattern>(2)) : this._attribNodeGroup) : (this._childNodeGroup == null ? (this._childNodeGroup = new Vector(2)) : this._childNodeGroup);
        } else {
            Vector<Object> vector = patterns = this._patternGroups[kernelType] == null ? new Vector(2) : this._patternGroups[kernelType];
        }
        if (patterns.size() == 0) {
            patterns.addElement(pattern);
        } else {
            boolean inserted = false;
            for (int i = 0; i < patterns.size(); ++i) {
                LocationPathPattern lppToCompare = (LocationPathPattern)patterns.elementAt(i);
                if (!pattern.noSmallerThan(lppToCompare)) continue;
                inserted = true;
                patterns.insertElementAt(pattern, i);
                break;
            }
            if (!inserted) {
                patterns.addElement(pattern);
            }
        }
    }

    private void completeTestSequences(int nodeType, Vector patterns) {
        if (patterns != null) {
            if (this._patternGroups[nodeType] == null) {
                this._patternGroups[nodeType] = patterns;
            } else {
                int m = patterns.size();
                for (int j = 0; j < m; ++j) {
                    this.addPattern(nodeType, (LocationPathPattern)patterns.elementAt(j));
                }
            }
        }
    }

    private void prepareTestSequences() {
        int i;
        int n;
        Vector starGroup = this._patternGroups[1];
        Vector atStarGroup = this._patternGroups[2];
        this.completeTestSequences(3, this._childNodeGroup);
        this.completeTestSequences(1, this._childNodeGroup);
        this.completeTestSequences(7, this._childNodeGroup);
        this.completeTestSequences(8, this._childNodeGroup);
        this.completeTestSequences(2, this._attribNodeGroup);
        Vector names = this._stylesheet.getXSLTC().getNamesIndex();
        if (starGroup != null || atStarGroup != null || this._childNodeGroup != null || this._attribNodeGroup != null) {
            n = this._patternGroups.length;
            for (i = 14; i < n; ++i) {
                if (this._patternGroups[i] == null) continue;
                String name = (String)names.elementAt(i - 14);
                if (Mode.isAttributeName(name)) {
                    this.completeTestSequences(i, atStarGroup);
                    this.completeTestSequences(i, this._attribNodeGroup);
                    continue;
                }
                this.completeTestSequences(i, starGroup);
                this.completeTestSequences(i, this._childNodeGroup);
            }
        }
        this._testSeq = new TestSeq[14 + names.size()];
        n = this._patternGroups.length;
        for (i = 0; i < n; ++i) {
            Vector patterns = this._patternGroups[i];
            if (patterns == null) continue;
            TestSeq testSeq = new TestSeq(patterns, i, this);
            testSeq.reduce();
            this._testSeq[i] = testSeq;
            testSeq.findTemplates(this._neededTemplates);
        }
        if (this._childNodeGroup != null && this._childNodeGroup.size() > 0) {
            this._childNodeTestSeq = new TestSeq(this._childNodeGroup, -1, this);
            this._childNodeTestSeq.reduce();
            this._childNodeTestSeq.findTemplates(this._neededTemplates);
        }
        if (this._idxGroup != null && this._idxGroup.size() > 0) {
            this._idxTestSeq = new TestSeq(this._idxGroup, this);
            this._idxTestSeq.reduce();
            this._idxTestSeq.findTemplates(this._neededTemplates);
        }
        if (this._rootPattern != null) {
            this._neededTemplates.put(this._rootPattern.getTemplate(), this);
        }
    }

    private void compileNamedTemplate(Template template, ClassGenerator classGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        String methodName = Util.escape(template.getName().toString());
        int numParams = 0;
        if (template.isSimpleNamedTemplate()) {
            Vector parameters = template.getParameters();
            numParams = parameters.size();
        }
        Type[] types = new Type[4 + numParams];
        String[] names = new String[4 + numParams];
        types[0] = Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;");
        types[1] = Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;");
        types[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);
        types[3] = Type.INT;
        names[0] = "document";
        names[1] = "iterator";
        names[2] = "handler";
        names[3] = "node";
        for (int i = 4; i < 4 + numParams; ++i) {
            types[i] = Util.getJCRefType("Ljava/lang/Object;");
            names[i] = "param" + String.valueOf(i - 4);
        }
        NamedMethodGenerator methodGen = new NamedMethodGenerator(1, Type.VOID, types, names, methodName, this.getClassName(), il, cpg);
        il.append(template.compile(classGen, methodGen));
        il.append(RETURN);
        classGen.addMethod(methodGen);
    }

    private void compileTemplates(ClassGenerator classGen, MethodGenerator methodGen, InstructionHandle next) {
        Template template;
        Enumeration templates = this._namedTemplates.keys();
        while (templates.hasMoreElements()) {
            template = (Template)templates.nextElement();
            this.compileNamedTemplate(template, classGen);
        }
        templates = this._neededTemplates.keys();
        while (templates.hasMoreElements()) {
            template = (Template)templates.nextElement();
            if (template.hasContents()) {
                InstructionList til = template.compile(classGen, methodGen);
                til.append(new GOTO_W(next));
                this._templateILs.put(template, til);
                this._templateIHs.put(template, til.getStart());
                continue;
            }
            this._templateIHs.put(template, next);
        }
    }

    private void appendTemplateCode(InstructionList body) {
        Enumeration templates = this._neededTemplates.keys();
        while (templates.hasMoreElements()) {
            Object iList = this._templateILs.get(templates.nextElement());
            if (iList == null) continue;
            body.append((InstructionList)iList);
        }
    }

    private void appendTestSequences(InstructionList body) {
        for (TestSeq testSeq : this._testSeq) {
            InstructionList il;
            if (testSeq == null || (il = testSeq.getInstructionList()) == null) continue;
            body.append(il);
        }
    }

    public static void compileGetChildren(ClassGenerator classGen, MethodGenerator methodGen, int node) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int git = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getChildren", "(I)Lorg/apache/xml/dtm/DTMAxisIterator;");
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(node));
        il.append(new INVOKEINTERFACE(git, 2));
    }

    private InstructionList compileDefaultRecursion(ClassGenerator classGen, MethodGenerator methodGen, InstructionHandle next) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        String applyTemplatesSig = classGen.getApplyTemplatesSig();
        int git = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getChildren", "(I)Lorg/apache/xml/dtm/DTMAxisIterator;");
        int applyTemplates = cpg.addMethodref(this.getClassName(), this.functionName(), applyTemplatesSig);
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(this._currentIndex));
        il.append(new INVOKEINTERFACE(git, 2));
        il.append(methodGen.loadHandler());
        il.append(new INVOKEVIRTUAL(applyTemplates));
        il.append(new GOTO_W(next));
        return il;
    }

    private InstructionList compileDefaultText(ClassGenerator classGen, MethodGenerator methodGen, InstructionHandle next) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = new InstructionList();
        int chars = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "characters", CHARACTERS_SIG);
        il.append(methodGen.loadDOM());
        il.append(new ILOAD(this._currentIndex));
        il.append(methodGen.loadHandler());
        il.append(new INVOKEINTERFACE(chars, 3));
        il.append(new GOTO_W(next));
        return il;
    }

    private InstructionList compileNamespaces(ClassGenerator classGen, MethodGenerator methodGen, boolean[] isNamespace, boolean[] isAttribute, boolean attrFlag, InstructionHandle defaultTarget) {
        XSLTC xsltc = classGen.getParser().getXSLTC();
        ConstantPoolGen cpg = classGen.getConstantPool();
        Vector namespaces = xsltc.getNamespaceIndex();
        Vector names = xsltc.getNamesIndex();
        int namespaceCount = namespaces.size() + 1;
        int namesCount = names.size();
        InstructionList il = new InstructionList();
        int[] types = new int[namespaceCount];
        InstructionHandle[] targets = new InstructionHandle[types.length];
        if (namespaceCount > 0) {
            int i;
            boolean compiled = false;
            for (i = 0; i < namespaceCount; ++i) {
                targets[i] = defaultTarget;
                types[i] = i;
            }
            for (i = 14; i < 14 + namesCount; ++i) {
                if (!isNamespace[i] || isAttribute[i] != attrFlag) continue;
                String name = (String)names.elementAt(i - 14);
                String namespace = name.substring(0, name.lastIndexOf(58));
                int type = xsltc.registerNamespace(namespace);
                if (i >= this._testSeq.length || this._testSeq[i] == null) continue;
                targets[type] = this._testSeq[i].compile(classGen, methodGen, defaultTarget);
                compiled = true;
            }
            if (!compiled) {
                return null;
            }
            int getNS = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getNamespaceType", "(I)I");
            il.append(methodGen.loadDOM());
            il.append(new ILOAD(this._currentIndex));
            il.append(new INVOKEINTERFACE(getNS, 2));
            il.append(new SWITCH(types, targets, defaultTarget));
            return il;
        }
        return null;
    }

    public void compileApplyTemplates(ClassGenerator classGen) {
        XSLTC xsltc = classGen.getParser().getXSLTC();
        ConstantPoolGen cpg = classGen.getConstantPool();
        Vector names = xsltc.getNamesIndex();
        Type[] argTypes = new Type[]{Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), Util.getJCRefType(TRANSLET_OUTPUT_SIG)};
        String[] argNames = new String[]{"document", "iterator", "handler"};
        InstructionList mainIL = new InstructionList();
        MethodGenerator methodGen = new MethodGenerator(17, Type.VOID, argTypes, argNames, this.functionName(), this.getClassName(), mainIL, classGen.getConstantPool());
        methodGen.addException("org.apache.xalan.xsltc.TransletException");
        mainIL.append(NOP);
        LocalVariableGen current = methodGen.addLocalVariable2("current", Type.INT, null);
        this._currentIndex = current.getIndex();
        InstructionList body = new InstructionList();
        body.append(NOP);
        InstructionList ilLoop = new InstructionList();
        ilLoop.append(methodGen.loadIterator());
        ilLoop.append(methodGen.nextNode());
        ilLoop.append(DUP);
        ilLoop.append(new ISTORE(this._currentIndex));
        BranchHandle ifeq = ilLoop.append(new IFLT(null));
        BranchHandle loop = ilLoop.append(new GOTO_W(null));
        ifeq.setTarget(ilLoop.append(RETURN));
        InstructionHandle ihLoop = ilLoop.getStart();
        current.setStart(mainIL.append(new GOTO_W(ihLoop)));
        current.setEnd(loop);
        InstructionList ilRecurse = this.compileDefaultRecursion(classGen, methodGen, ihLoop);
        InstructionHandle ihRecurse = ilRecurse.getStart();
        InstructionList ilText = this.compileDefaultText(classGen, methodGen, ihLoop);
        InstructionHandle ihText = ilText.getStart();
        int[] types = new int[14 + names.size()];
        for (int i = 0; i < types.length; ++i) {
            types[i] = i;
        }
        boolean[] isAttribute = new boolean[types.length];
        boolean[] isNamespace = new boolean[types.length];
        for (int i = 0; i < names.size(); ++i) {
            String name = (String)names.elementAt(i);
            isAttribute[i + 14] = Mode.isAttributeName(name);
            isNamespace[i + 14] = Mode.isNamespaceName(name);
        }
        this.compileTemplates(classGen, methodGen, ihLoop);
        TestSeq elemTest = this._testSeq[1];
        InstructionHandle ihElem = ihRecurse;
        if (elemTest != null) {
            ihElem = elemTest.compile(classGen, methodGen, ihRecurse);
        }
        TestSeq attrTest = this._testSeq[2];
        InstructionHandle ihAttr = ihText;
        if (attrTest != null) {
            ihAttr = attrTest.compile(classGen, methodGen, ihAttr);
        }
        InstructionList ilKey = null;
        if (this._idxTestSeq != null) {
            loop.setTarget(this._idxTestSeq.compile(classGen, methodGen, body.getStart()));
            ilKey = this._idxTestSeq.getInstructionList();
        } else {
            loop.setTarget(body.getStart());
        }
        if (this._childNodeTestSeq != null) {
            double nodePrio = this._childNodeTestSeq.getPriority();
            int nodePos = this._childNodeTestSeq.getPosition();
            double elemPrio = -1.7976931348623157E308;
            int elemPos = Integer.MIN_VALUE;
            if (elemTest != null) {
                elemPrio = elemTest.getPriority();
                elemPos = elemTest.getPosition();
            }
            if (Double.isNaN(elemPrio) || elemPrio < nodePrio || elemPrio == nodePrio && elemPos < nodePos) {
                ihElem = this._childNodeTestSeq.compile(classGen, methodGen, ihLoop);
            }
            TestSeq textTest = this._testSeq[3];
            double textPrio = -1.7976931348623157E308;
            int textPos = Integer.MIN_VALUE;
            if (textTest != null) {
                textPrio = textTest.getPriority();
                textPos = textTest.getPosition();
            }
            if (Double.isNaN(textPrio) || textPrio < nodePrio || textPrio == nodePrio && textPos < nodePos) {
                ihText = this._childNodeTestSeq.compile(classGen, methodGen, ihLoop);
                this._testSeq[3] = this._childNodeTestSeq;
            }
        }
        InstructionHandle elemNamespaceHandle = ihElem;
        InstructionList nsElem = this.compileNamespaces(classGen, methodGen, isNamespace, isAttribute, false, ihElem);
        if (nsElem != null) {
            elemNamespaceHandle = nsElem.getStart();
        }
        InstructionHandle attrNamespaceHandle = ihAttr;
        InstructionList nsAttr = this.compileNamespaces(classGen, methodGen, isNamespace, isAttribute, true, ihAttr);
        if (nsAttr != null) {
            attrNamespaceHandle = nsAttr.getStart();
        }
        InstructionHandle[] targets = new InstructionHandle[types.length];
        for (int i = 14; i < targets.length; ++i) {
            TestSeq testSeq = this._testSeq[i];
            if (isNamespace[i]) {
                if (isAttribute[i]) {
                    targets[i] = attrNamespaceHandle;
                    continue;
                }
                targets[i] = elemNamespaceHandle;
                continue;
            }
            if (testSeq != null) {
                if (isAttribute[i]) {
                    targets[i] = testSeq.compile(classGen, methodGen, attrNamespaceHandle);
                    continue;
                }
                targets[i] = testSeq.compile(classGen, methodGen, elemNamespaceHandle);
                continue;
            }
            targets[i] = ihLoop;
        }
        targets[0] = this._rootPattern != null ? this.getTemplateInstructionHandle(this._rootPattern.getTemplate()) : ihRecurse;
        targets[9] = this._rootPattern != null ? this.getTemplateInstructionHandle(this._rootPattern.getTemplate()) : ihRecurse;
        targets[3] = this._testSeq[3] != null ? this._testSeq[3].compile(classGen, methodGen, ihText) : ihText;
        targets[13] = ihLoop;
        targets[1] = elemNamespaceHandle;
        targets[2] = attrNamespaceHandle;
        InstructionHandle ihPI = ihLoop;
        if (this._childNodeTestSeq != null) {
            ihPI = ihElem;
        }
        targets[7] = this._testSeq[7] != null ? this._testSeq[7].compile(classGen, methodGen, ihPI) : ihPI;
        InstructionHandle ihComment = ihLoop;
        if (this._childNodeTestSeq != null) {
            ihComment = ihElem;
        }
        targets[8] = this._testSeq[8] != null ? this._testSeq[8].compile(classGen, methodGen, ihComment) : ihComment;
        targets[4] = ihLoop;
        targets[11] = ihLoop;
        targets[10] = ihLoop;
        targets[6] = ihLoop;
        targets[5] = ihLoop;
        targets[12] = ihLoop;
        for (int i = 14; i < targets.length; ++i) {
            TestSeq testSeq = this._testSeq[i];
            if (testSeq == null || isNamespace[i]) {
                if (isAttribute[i]) {
                    targets[i] = attrNamespaceHandle;
                    continue;
                }
                targets[i] = elemNamespaceHandle;
                continue;
            }
            targets[i] = isAttribute[i] ? testSeq.compile(classGen, methodGen, attrNamespaceHandle) : testSeq.compile(classGen, methodGen, elemNamespaceHandle);
        }
        if (ilKey != null) {
            body.insert(ilKey);
        }
        int getType = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getExpandedTypeID", "(I)I");
        body.append(methodGen.loadDOM());
        body.append(new ILOAD(this._currentIndex));
        body.append(new INVOKEINTERFACE(getType, 2));
        InstructionHandle disp = body.append(new SWITCH(types, targets, ihLoop));
        this.appendTestSequences(body);
        this.appendTemplateCode(body);
        if (nsElem != null) {
            body.append(nsElem);
        }
        if (nsAttr != null) {
            body.append(nsAttr);
        }
        body.append(ilRecurse);
        body.append(ilText);
        mainIL.append(body);
        mainIL.append(ilLoop);
        this.peepHoleOptimization(methodGen);
        classGen.addMethod(methodGen);
        if (this._importLevels != null) {
            Enumeration levels = this._importLevels.keys();
            while (levels.hasMoreElements()) {
                Integer max = (Integer)levels.nextElement();
                Integer min = (Integer)this._importLevels.get(max);
                this.compileApplyImports(classGen, min, max);
            }
        }
    }

    private void compileTemplateCalls(ClassGenerator classGen, MethodGenerator methodGen, InstructionHandle next, int min, int max) {
        Enumeration templates = this._neededTemplates.keys();
        while (templates.hasMoreElements()) {
            Template template = (Template)templates.nextElement();
            int prec = template.getImportPrecedence();
            if (prec < min || prec >= max) continue;
            if (template.hasContents()) {
                InstructionList til = template.compile(classGen, methodGen);
                til.append(new GOTO_W(next));
                this._templateILs.put(template, til);
                this._templateIHs.put(template, til.getStart());
                continue;
            }
            this._templateIHs.put(template, next);
        }
    }

    public void compileApplyImports(ClassGenerator classGen, int min, int max) {
        XSLTC xsltc = classGen.getParser().getXSLTC();
        ConstantPoolGen cpg = classGen.getConstantPool();
        Vector names = xsltc.getNamesIndex();
        this._namedTemplates = new Hashtable();
        this._neededTemplates = new Hashtable();
        this._templateIHs = new Hashtable();
        this._templateILs = new Hashtable();
        this._patternGroups = new Vector[32];
        this._rootPattern = null;
        Vector oldTemplates = this._templates;
        this._templates = new Vector();
        Enumeration templates = oldTemplates.elements();
        while (templates.hasMoreElements()) {
            Template template = (Template)templates.nextElement();
            int prec = template.getImportPrecedence();
            if (prec < min || prec >= max) continue;
            this.addTemplate(template);
        }
        this.processPatterns(this._keys);
        Type[] argTypes = new Type[]{Util.getJCRefType("Lorg/apache/xalan/xsltc/DOM;"), Util.getJCRefType("Lorg/apache/xml/dtm/DTMAxisIterator;"), Util.getJCRefType(TRANSLET_OUTPUT_SIG), Type.INT};
        String[] argNames = new String[]{"document", "iterator", "handler", "node"};
        InstructionList mainIL = new InstructionList();
        MethodGenerator methodGen = new MethodGenerator(17, Type.VOID, argTypes, argNames, this.functionName() + '_' + max, this.getClassName(), mainIL, classGen.getConstantPool());
        methodGen.addException("org.apache.xalan.xsltc.TransletException");
        LocalVariableGen current = methodGen.addLocalVariable2("current", Type.INT, null);
        this._currentIndex = current.getIndex();
        mainIL.append(new ILOAD(methodGen.getLocalIndex("node")));
        current.setStart(mainIL.append(new ISTORE(this._currentIndex)));
        InstructionList body = new InstructionList();
        body.append(NOP);
        InstructionList ilLoop = new InstructionList();
        ilLoop.append(RETURN);
        InstructionHandle ihLoop = ilLoop.getStart();
        InstructionList ilRecurse = this.compileDefaultRecursion(classGen, methodGen, ihLoop);
        InstructionHandle ihRecurse = ilRecurse.getStart();
        InstructionList ilText = this.compileDefaultText(classGen, methodGen, ihLoop);
        InstructionHandle ihText = ilText.getStart();
        int[] types = new int[14 + names.size()];
        for (int i = 0; i < types.length; ++i) {
            types[i] = i;
        }
        boolean[] isAttribute = new boolean[types.length];
        boolean[] isNamespace = new boolean[types.length];
        for (int i = 0; i < names.size(); ++i) {
            String name = (String)names.elementAt(i);
            isAttribute[i + 14] = Mode.isAttributeName(name);
            isNamespace[i + 14] = Mode.isNamespaceName(name);
        }
        this.compileTemplateCalls(classGen, methodGen, ihLoop, min, max);
        TestSeq elemTest = this._testSeq[1];
        InstructionHandle ihElem = ihRecurse;
        if (elemTest != null) {
            ihElem = elemTest.compile(classGen, methodGen, ihLoop);
        }
        TestSeq attrTest = this._testSeq[2];
        InstructionHandle ihAttr = ihLoop;
        if (attrTest != null) {
            ihAttr = attrTest.compile(classGen, methodGen, ihAttr);
        }
        InstructionList ilKey = null;
        if (this._idxTestSeq != null) {
            ilKey = this._idxTestSeq.getInstructionList();
        }
        if (this._childNodeTestSeq != null) {
            double nodePrio = this._childNodeTestSeq.getPriority();
            int nodePos = this._childNodeTestSeq.getPosition();
            double elemPrio = -1.7976931348623157E308;
            int elemPos = Integer.MIN_VALUE;
            if (elemTest != null) {
                elemPrio = elemTest.getPriority();
                elemPos = elemTest.getPosition();
            }
            if (Double.isNaN(elemPrio) || elemPrio < nodePrio || elemPrio == nodePrio && elemPos < nodePos) {
                ihElem = this._childNodeTestSeq.compile(classGen, methodGen, ihLoop);
            }
            TestSeq textTest = this._testSeq[3];
            double textPrio = -1.7976931348623157E308;
            int textPos = Integer.MIN_VALUE;
            if (textTest != null) {
                textPrio = textTest.getPriority();
                textPos = textTest.getPosition();
            }
            if (Double.isNaN(textPrio) || textPrio < nodePrio || textPrio == nodePrio && textPos < nodePos) {
                ihText = this._childNodeTestSeq.compile(classGen, methodGen, ihLoop);
                this._testSeq[3] = this._childNodeTestSeq;
            }
        }
        InstructionHandle elemNamespaceHandle = ihElem;
        InstructionList nsElem = this.compileNamespaces(classGen, methodGen, isNamespace, isAttribute, false, ihElem);
        if (nsElem != null) {
            elemNamespaceHandle = nsElem.getStart();
        }
        InstructionList nsAttr = this.compileNamespaces(classGen, methodGen, isNamespace, isAttribute, true, ihAttr);
        InstructionHandle attrNamespaceHandle = ihAttr;
        if (nsAttr != null) {
            attrNamespaceHandle = nsAttr.getStart();
        }
        InstructionHandle[] targets = new InstructionHandle[types.length];
        for (int i = 14; i < targets.length; ++i) {
            TestSeq testSeq = this._testSeq[i];
            if (isNamespace[i]) {
                if (isAttribute[i]) {
                    targets[i] = attrNamespaceHandle;
                    continue;
                }
                targets[i] = elemNamespaceHandle;
                continue;
            }
            if (testSeq != null) {
                if (isAttribute[i]) {
                    targets[i] = testSeq.compile(classGen, methodGen, attrNamespaceHandle);
                    continue;
                }
                targets[i] = testSeq.compile(classGen, methodGen, elemNamespaceHandle);
                continue;
            }
            targets[i] = ihLoop;
        }
        targets[0] = this._rootPattern != null ? this.getTemplateInstructionHandle(this._rootPattern.getTemplate()) : ihRecurse;
        targets[9] = this._rootPattern != null ? this.getTemplateInstructionHandle(this._rootPattern.getTemplate()) : ihRecurse;
        targets[3] = this._testSeq[3] != null ? this._testSeq[3].compile(classGen, methodGen, ihText) : ihText;
        targets[13] = ihLoop;
        targets[1] = elemNamespaceHandle;
        targets[2] = attrNamespaceHandle;
        InstructionHandle ihPI = ihLoop;
        if (this._childNodeTestSeq != null) {
            ihPI = ihElem;
        }
        targets[7] = this._testSeq[7] != null ? this._testSeq[7].compile(classGen, methodGen, ihPI) : ihPI;
        InstructionHandle ihComment = ihLoop;
        if (this._childNodeTestSeq != null) {
            ihComment = ihElem;
        }
        targets[8] = this._testSeq[8] != null ? this._testSeq[8].compile(classGen, methodGen, ihComment) : ihComment;
        targets[4] = ihLoop;
        targets[11] = ihLoop;
        targets[10] = ihLoop;
        targets[6] = ihLoop;
        targets[5] = ihLoop;
        targets[12] = ihLoop;
        for (int i = 14; i < targets.length; ++i) {
            TestSeq testSeq = this._testSeq[i];
            if (testSeq == null || isNamespace[i]) {
                if (isAttribute[i]) {
                    targets[i] = attrNamespaceHandle;
                    continue;
                }
                targets[i] = elemNamespaceHandle;
                continue;
            }
            targets[i] = isAttribute[i] ? testSeq.compile(classGen, methodGen, attrNamespaceHandle) : testSeq.compile(classGen, methodGen, elemNamespaceHandle);
        }
        if (ilKey != null) {
            body.insert(ilKey);
        }
        int getType = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getExpandedTypeID", "(I)I");
        body.append(methodGen.loadDOM());
        body.append(new ILOAD(this._currentIndex));
        body.append(new INVOKEINTERFACE(getType, 2));
        InstructionHandle disp = body.append(new SWITCH(types, targets, ihLoop));
        this.appendTestSequences(body);
        this.appendTemplateCode(body);
        if (nsElem != null) {
            body.append(nsElem);
        }
        if (nsAttr != null) {
            body.append(nsAttr);
        }
        body.append(ilRecurse);
        body.append(ilText);
        mainIL.append(body);
        current.setEnd(body.getEnd());
        mainIL.append(ilLoop);
        this.peepHoleOptimization(methodGen);
        classGen.addMethod(methodGen);
        this._templates = oldTemplates;
    }

    private void peepHoleOptimization(MethodGenerator methodGen) {
        InstructionHandle[] match;
        InstructionList il = methodGen.getInstructionList();
        InstructionFinder find = new InstructionFinder(il);
        String pattern = "LoadInstruction POP";
        Iterator<InstructionHandle[]> iter = find.search(pattern);
        while (iter.hasNext()) {
            match = iter.next();
            try {
                if (match[0].hasTargeters() || match[1].hasTargeters()) continue;
                il.delete(match[0], match[1]);
            }
            catch (TargetLostException targetLostException) {}
        }
        pattern = "ILOAD ILOAD SWAP ISTORE";
        iter = find.search(pattern);
        while (iter.hasNext()) {
            match = iter.next();
            try {
                ILOAD iload1 = (ILOAD)match[0].getInstruction();
                ILOAD iload2 = (ILOAD)match[1].getInstruction();
                ISTORE istore = (ISTORE)match[3].getInstruction();
                if (match[1].hasTargeters() || match[2].hasTargeters() || match[3].hasTargeters() || iload1.getIndex() != iload2.getIndex() || iload2.getIndex() != istore.getIndex()) continue;
                il.delete(match[1], match[3]);
            }
            catch (TargetLostException iload1) {}
        }
        pattern = "LoadInstruction LoadInstruction SWAP";
        iter = find.search(pattern);
        while (iter.hasNext()) {
            match = iter.next();
            try {
                if (match[0].hasTargeters() || match[1].hasTargeters() || match[2].hasTargeters()) continue;
                Instruction load_m = match[1].getInstruction();
                il.insert(match[0], load_m);
                il.delete(match[1], match[2]);
            }
            catch (TargetLostException load_m) {}
        }
        pattern = "ALOAD ALOAD";
        iter = find.search(pattern);
        while (iter.hasNext()) {
            match = iter.next();
            try {
                if (match[1].hasTargeters()) continue;
                ALOAD aload1 = (ALOAD)match[0].getInstruction();
                ALOAD aload2 = (ALOAD)match[1].getInstruction();
                if (aload1.getIndex() != aload2.getIndex()) continue;
                il.insert(match[1], (Instruction)new DUP());
                il.delete(match[1]);
            }
            catch (TargetLostException targetLostException) {}
        }
    }

    public InstructionHandle getTemplateInstructionHandle(Template template) {
        return (InstructionHandle)this._templateIHs.get(template);
    }

    private static boolean isAttributeName(String qname) {
        int col = qname.lastIndexOf(58) + 1;
        return qname.charAt(col) == '@';
    }

    private static boolean isNamespaceName(String qname) {
        int col = qname.lastIndexOf(58);
        return col > -1 && qname.charAt(qname.length() - 1) == '*';
    }
}

