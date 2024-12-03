/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xml.utils.XML11Char;

final class DecimalFormatting
extends TopLevelElement {
    private static final String DFS_CLASS = "java.text.DecimalFormatSymbols";
    private static final String DFS_SIG = "Ljava/text/DecimalFormatSymbols;";
    private QName _name = null;

    DecimalFormatting() {
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    @Override
    public void parseContents(Parser parser) {
        SymbolTable stable;
        String name = this.getAttribute("name");
        if (name.length() > 0 && !XML11Char.isXML11ValidQName(name)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)name, this);
            parser.reportError(3, err);
        }
        this._name = parser.getQNameIgnoreDefaultNs(name);
        if (this._name == null) {
            this._name = parser.getQNameIgnoreDefaultNs("");
        }
        if ((stable = parser.getSymbolTable()).getDecimalFormatting(this._name) != null) {
            this.reportWarning(this, parser, "SYMBOLS_REDEF_ERR", this._name.toString());
        } else {
            stable.addDecimalFormatting(this._name, this);
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int init = cpg.addMethodref(DFS_CLASS, "<init>", "(Ljava/util/Locale;)V");
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, this._name.toString()));
        il.append(new NEW(cpg.addClass(DFS_CLASS)));
        il.append(DUP);
        il.append(new GETSTATIC(cpg.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
        il.append(new INVOKESPECIAL(init));
        String tmp = this.getAttribute("NaN");
        if (tmp == null || tmp.equals("")) {
            int nan = cpg.addMethodref(DFS_CLASS, "setNaN", "(Ljava/lang/String;)V");
            il.append(DUP);
            il.append(new PUSH(cpg, "NaN"));
            il.append(new INVOKEVIRTUAL(nan));
        }
        if ((tmp = this.getAttribute("infinity")) == null || tmp.equals("")) {
            int inf = cpg.addMethodref(DFS_CLASS, "setInfinity", "(Ljava/lang/String;)V");
            il.append(DUP);
            il.append(new PUSH(cpg, "Infinity"));
            il.append(new INVOKEVIRTUAL(inf));
        }
        int nAttributes = this._attributes.getLength();
        for (int i = 0; i < nAttributes; ++i) {
            String name = this._attributes.getQName(i);
            String value = this._attributes.getValue(i);
            boolean valid = true;
            int method = 0;
            if (name.equals("decimal-separator")) {
                method = cpg.addMethodref(DFS_CLASS, "setDecimalSeparator", "(C)V");
            } else if (name.equals("grouping-separator")) {
                method = cpg.addMethodref(DFS_CLASS, "setGroupingSeparator", "(C)V");
            } else if (name.equals("minus-sign")) {
                method = cpg.addMethodref(DFS_CLASS, "setMinusSign", "(C)V");
            } else if (name.equals("percent")) {
                method = cpg.addMethodref(DFS_CLASS, "setPercent", "(C)V");
            } else if (name.equals("per-mille")) {
                method = cpg.addMethodref(DFS_CLASS, "setPerMill", "(C)V");
            } else if (name.equals("zero-digit")) {
                method = cpg.addMethodref(DFS_CLASS, "setZeroDigit", "(C)V");
            } else if (name.equals("digit")) {
                method = cpg.addMethodref(DFS_CLASS, "setDigit", "(C)V");
            } else if (name.equals("pattern-separator")) {
                method = cpg.addMethodref(DFS_CLASS, "setPatternSeparator", "(C)V");
            } else if (name.equals("NaN")) {
                method = cpg.addMethodref(DFS_CLASS, "setNaN", "(Ljava/lang/String;)V");
                il.append(DUP);
                il.append(new PUSH(cpg, value));
                il.append(new INVOKEVIRTUAL(method));
                valid = false;
            } else if (name.equals("infinity")) {
                method = cpg.addMethodref(DFS_CLASS, "setInfinity", "(Ljava/lang/String;)V");
                il.append(DUP);
                il.append(new PUSH(cpg, value));
                il.append(new INVOKEVIRTUAL(method));
                valid = false;
            } else {
                valid = false;
            }
            if (!valid) continue;
            il.append(DUP);
            il.append(new PUSH(cpg, (int)value.charAt(0)));
            il.append(new INVOKEVIRTUAL(method));
        }
        int put = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
        il.append(new INVOKEVIRTUAL(put));
    }

    public static void translateDefaultDFS(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        int init = cpg.addMethodref(DFS_CLASS, "<init>", "(Ljava/util/Locale;)V");
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, ""));
        il.append(new NEW(cpg.addClass(DFS_CLASS)));
        il.append(DUP);
        il.append(new GETSTATIC(cpg.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
        il.append(new INVOKESPECIAL(init));
        int nan = cpg.addMethodref(DFS_CLASS, "setNaN", "(Ljava/lang/String;)V");
        il.append(DUP);
        il.append(new PUSH(cpg, "NaN"));
        il.append(new INVOKEVIRTUAL(nan));
        int inf = cpg.addMethodref(DFS_CLASS, "setInfinity", "(Ljava/lang/String;)V");
        il.append(DUP);
        il.append(new PUSH(cpg, "Infinity"));
        il.append(new INVOKEVIRTUAL(inf));
        int put = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
        il.append(new INVOKEVIRTUAL(put));
    }
}

