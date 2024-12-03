/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.WithParam;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.utils.XML11Char;

final class CallTemplate
extends Instruction {
    private QName _name;
    private Object[] _parameters = null;
    private Template _calleeTemplate = null;

    CallTemplate() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        System.out.print("CallTemplate");
        Util.println(" name " + this._name);
        this.displayContents(indent + 4);
    }

    public boolean hasWithParams() {
        return this.elementCount() > 0;
    }

    @Override
    public void parseContents(Parser parser) {
        String name = this.getAttribute("name");
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)name, this);
                parser.reportError(3, err);
            }
            this._name = parser.getQNameIgnoreDefaultNs(name);
        } else {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "name");
        }
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Template template = stable.lookupTemplate(this._name);
        if (template == null) {
            ErrorMsg err = new ErrorMsg("TEMPLATE_UNDEF_ERR", (Object)this._name, this);
            throw new TypeCheckError(err);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Stylesheet stylesheet = classGen.getStylesheet();
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (stylesheet.hasLocalParams() || this.hasContents()) {
            this._calleeTemplate = this.getCalleeTemplate();
            if (this._calleeTemplate != null) {
                this.buildParameterList();
            } else {
                int push = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
                il.append(classGen.loadTranslet());
                il.append(new INVOKEVIRTUAL(push));
                this.translateContents(classGen, methodGen);
            }
        }
        String className = stylesheet.getClassName();
        String methodName = Util.escape(this._name.toString());
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadHandler());
        il.append(methodGen.loadCurrentNode());
        StringBuffer methodSig = new StringBuffer("(Lorg/apache/xalan/xsltc/DOM;Lorg/apache/xml/dtm/DTMAxisIterator;" + TRANSLET_OUTPUT_SIG + "I");
        if (this._calleeTemplate != null) {
            Vector calleeParams = this._calleeTemplate.getParameters();
            int numParams = this._parameters.length;
            for (int i = 0; i < numParams; ++i) {
                SyntaxTreeNode node = (SyntaxTreeNode)this._parameters[i];
                methodSig.append("Ljava/lang/Object;");
                if (node instanceof Param) {
                    il.append(ACONST_NULL);
                    continue;
                }
                node.translate(classGen, methodGen);
            }
        }
        methodSig.append(")V");
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(className, methodName, methodSig.toString())));
        if (this._calleeTemplate == null && (stylesheet.hasLocalParams() || this.hasContents())) {
            int pop = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
            il.append(classGen.loadTranslet());
            il.append(new INVOKEVIRTUAL(pop));
        }
    }

    public Template getCalleeTemplate() {
        Template foundTemplate = this.getXSLTC().getParser().getSymbolTable().lookupTemplate(this._name);
        return foundTemplate.isSimpleNamedTemplate() ? foundTemplate : null;
    }

    private void buildParameterList() {
        Vector defaultParams = this._calleeTemplate.getParameters();
        int numParams = defaultParams.size();
        this._parameters = new Object[numParams];
        for (int i = 0; i < numParams; ++i) {
            this._parameters[i] = defaultParams.elementAt(i);
        }
        int count = this.elementCount();
        block1: for (int i = 0; i < count; ++i) {
            Object node = this.elementAt(i);
            if (!(node instanceof WithParam)) continue;
            WithParam withParam = (WithParam)node;
            QName name = withParam.getName();
            for (int k = 0; k < numParams; ++k) {
                Object object = this._parameters[k];
                if (object instanceof Param && ((Param)object).getName().equals(name)) {
                    withParam.setDoParameterOptimization(true);
                    this._parameters[k] = withParam;
                    continue block1;
                }
                if (!(object instanceof WithParam) || !((WithParam)object).getName().equals(name)) continue;
                withParam.setDoParameterOptimization(true);
                this._parameters[k] = withParam;
                continue block1;
            }
        }
    }
}

