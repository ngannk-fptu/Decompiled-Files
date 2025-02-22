/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.AttributeValueTemplate;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.UseAttributeSets;
import org.apache.xalan.xsltc.compiler.XslAttribute;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.utils.XML11Char;

final class XslElement
extends Instruction {
    private String _prefix;
    private boolean _ignore = false;
    private boolean _isLiteralName = true;
    private AttributeValueTemplate _name;
    private AttributeValueTemplate _namespace;

    XslElement() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("Element " + this._name);
        this.displayContents(indent + 4);
    }

    public boolean declaresDefaultNS() {
        return false;
    }

    @Override
    public void parseContents(Parser parser) {
        SymbolTable stable = parser.getSymbolTable();
        String name = this.getAttribute("name");
        if (name == "") {
            ErrorMsg msg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", (Object)name, this);
            parser.reportError(4, msg);
            this.parseChildren(parser);
            this._ignore = true;
            return;
        }
        String namespace = this.getAttribute("namespace");
        this._isLiteralName = Util.isLiteral(name);
        if (this._isLiteralName) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg msg = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", (Object)name, this);
                parser.reportError(4, msg);
                this.parseChildren(parser);
                this._ignore = true;
                return;
            }
            QName qname = parser.getQNameSafe(name);
            String prefix = qname.getPrefix();
            String local = qname.getLocalPart();
            if (prefix == null) {
                prefix = "";
            }
            if (!this.hasAttribute("namespace")) {
                namespace = this.lookupNamespace(prefix);
                if (namespace == null) {
                    ErrorMsg err = new ErrorMsg("NAMESPACE_UNDEF_ERR", (Object)prefix, this);
                    parser.reportError(4, err);
                    this.parseChildren(parser);
                    this._ignore = true;
                    return;
                }
                this._prefix = prefix;
                this._namespace = new AttributeValueTemplate(namespace, parser, this);
            } else {
                if (prefix == "") {
                    if (Util.isLiteral(namespace) && (prefix = this.lookupPrefix(namespace)) == null) {
                        prefix = stable.generateNamespacePrefix();
                    }
                    StringBuffer newName = new StringBuffer(prefix);
                    if (prefix != "") {
                        newName.append(':');
                    }
                    name = newName.append(local).toString();
                }
                this._prefix = prefix;
                this._namespace = new AttributeValueTemplate(namespace, parser, this);
            }
        } else {
            this._namespace = namespace == "" ? null : new AttributeValueTemplate(namespace, parser, this);
        }
        this._name = new AttributeValueTemplate(name, parser, this);
        String useSets = this.getAttribute("use-attribute-sets");
        if (useSets.length() > 0) {
            if (!Util.isValidQNames(useSets)) {
                ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)useSets, this);
                parser.reportError(3, err);
            }
            this.setFirstElement(new UseAttributeSets(useSets, parser));
        }
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (!this._ignore) {
            this._name.typeCheck(stable);
            if (this._namespace != null) {
                this._namespace.typeCheck(stable);
            }
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }

    public void translateLiteral(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (!this._ignore) {
            il.append(methodGen.loadHandler());
            this._name.translate(classGen, methodGen);
            il.append(DUP2);
            il.append(methodGen.startElement());
            if (this._namespace != null) {
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, this._prefix));
                this._namespace.translate(classGen, methodGen);
                il.append(methodGen.namespace());
            }
        }
        this.translateContents(classGen, methodGen);
        if (!this._ignore) {
            il.append(methodGen.endElement());
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Object local = null;
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._isLiteralName) {
            this.translateLiteral(classGen, methodGen);
            return;
        }
        if (!this._ignore) {
            LocalVariableGen nameValue = methodGen.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
            this._name.translate(classGen, methodGen);
            nameValue.setStart(il.append(new ASTORE(nameValue.getIndex())));
            il.append(new ALOAD(nameValue.getIndex()));
            int check = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "checkQName", "(Ljava/lang/String;)V");
            il.append(new INVOKESTATIC(check));
            il.append(methodGen.loadHandler());
            nameValue.setEnd(il.append(new ALOAD(nameValue.getIndex())));
            if (this._namespace != null) {
                this._namespace.translate(classGen, methodGen);
            } else {
                String transletClassName = this.getXSLTC().getClassName();
                il.append(DUP);
                il.append(new PUSH(cpg, this.getNodeIDForStylesheetNSLookup()));
                il.append(new GETSTATIC(cpg.addFieldref(transletClassName, "_sNamespaceAncestorsArray", "[I")));
                il.append(new GETSTATIC(cpg.addFieldref(transletClassName, "_sPrefixURIsIdxArray", "[I")));
                il.append(new GETSTATIC(cpg.addFieldref(transletClassName, "_sPrefixURIPairsArray", "[Ljava/lang/String;")));
                il.append(ICONST_0);
                il.append(new INVOKESTATIC(cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "lookupStylesheetQNameNamespace", "(Ljava/lang/String;I[I[I[Ljava/lang/String;Z)Ljava/lang/String;")));
            }
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKESTATIC(cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "startXslElement", "(Ljava/lang/String;Ljava/lang/String;" + TRANSLET_OUTPUT_SIG + "Lorg/apache/xalan/xsltc/DOM;" + "I)" + "Ljava/lang/String;")));
        }
        this.translateContents(classGen, methodGen);
        if (!this._ignore) {
            il.append(methodGen.endElement());
        }
    }

    @Override
    public void translateContents(ClassGenerator classGen, MethodGenerator methodGen) {
        int n = this.elementCount();
        for (int i = 0; i < n; ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)this.getContents().elementAt(i);
            if (this._ignore && item instanceof XslAttribute) continue;
            item.translate(classGen, methodGen);
        }
    }
}

