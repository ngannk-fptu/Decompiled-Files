/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.UseAttributeSets;
import org.apache.xalan.xsltc.compiler.XslAttribute;
import org.apache.xalan.xsltc.compiler.util.AttributeSetMethodGenerator;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.utils.XML11Char;

final class AttributeSet
extends TopLevelElement {
    private static final String AttributeSetPrefix = "$as$";
    private QName _name;
    private UseAttributeSets _useSets;
    private AttributeSet _mergeSet;
    private String _method;
    private boolean _ignore = false;

    AttributeSet() {
    }

    public QName getName() {
        return this._name;
    }

    public String getMethodName() {
        return this._method;
    }

    public void ignore() {
        this._ignore = true;
    }

    @Override
    public void parseContents(Parser parser) {
        String useSets;
        String name = this.getAttribute("name");
        if (!XML11Char.isXML11ValidQName(name)) {
            ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)name, this);
            parser.reportError(3, err);
        }
        this._name = parser.getQNameIgnoreDefaultNs(name);
        if (this._name == null || this._name.equals("")) {
            ErrorMsg msg = new ErrorMsg("UNNAMED_ATTRIBSET_ERR", this);
            parser.reportError(3, msg);
        }
        if ((useSets = this.getAttribute("use-attribute-sets")).length() > 0) {
            if (!Util.isValidQNames(useSets)) {
                ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)useSets, this);
                parser.reportError(3, err);
            }
            this._useSets = new UseAttributeSets(useSets, parser);
        }
        Vector contents = this.getContents();
        int count = contents.size();
        for (int i = 0; i < count; ++i) {
            SyntaxTreeNode child = (SyntaxTreeNode)contents.elementAt(i);
            if (child instanceof XslAttribute) {
                parser.getSymbolTable().setCurrentNode(child);
                child.parseContents(parser);
                continue;
            }
            if (child instanceof Text) continue;
            ErrorMsg msg = new ErrorMsg("ILLEGAL_CHILD_ERR", this);
            parser.reportError(3, msg);
        }
        parser.getSymbolTable().setCurrentNode(this);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._ignore) {
            return Type.Void;
        }
        this._mergeSet = stable.addAttributeSet(this);
        this._method = AttributeSetPrefix + this.getXSLTC().nextAttributeSetSerial();
        if (this._useSets != null) {
            this._useSets.typeCheck(stable);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il;
        if (this._ignore) {
            return;
        }
        methodGen = new AttributeSetMethodGenerator(this._method, classGen);
        if (this._mergeSet != null) {
            ConstantPoolGen cpg = classGen.getConstantPool();
            il = methodGen.getInstructionList();
            String methodName = this._mergeSet.getMethodName();
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadIterator());
            il.append(methodGen.loadHandler());
            int method = cpg.addMethodref(classGen.getClassName(), methodName, ATTR_SET_SIG);
            il.append(new INVOKESPECIAL(method));
        }
        if (this._useSets != null) {
            this._useSets.translate(classGen, methodGen);
        }
        Enumeration attributes = this.elements();
        while (attributes.hasMoreElements()) {
            SyntaxTreeNode element = (SyntaxTreeNode)attributes.nextElement();
            if (!(element instanceof XslAttribute)) continue;
            XslAttribute attribute = (XslAttribute)element;
            attribute.translate(classGen, methodGen);
        }
        il = methodGen.getInstructionList();
        il.append(RETURN);
        classGen.addMethod(methodGen);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("attribute-set: ");
        Enumeration attributes = this.elements();
        while (attributes.hasMoreElements()) {
            XslAttribute attribute = (XslAttribute)attributes.nextElement();
            buf.append(attribute);
        }
        return buf.toString();
    }
}

