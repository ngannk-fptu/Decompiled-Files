/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.ElemDesc
 *  org.apache.xml.serializer.ToHTMLStream
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.ApplyTemplates;
import org.apache.xalan.xsltc.compiler.AttributeValue;
import org.apache.xalan.xsltc.compiler.AttributeValueTemplate;
import org.apache.xalan.xsltc.compiler.CallTemplate;
import org.apache.xalan.xsltc.compiler.Choose;
import org.apache.xalan.xsltc.compiler.Comment;
import org.apache.xalan.xsltc.compiler.Copy;
import org.apache.xalan.xsltc.compiler.CopyOf;
import org.apache.xalan.xsltc.compiler.ForEach;
import org.apache.xalan.xsltc.compiler.If;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.LiteralAttribute;
import org.apache.xalan.xsltc.compiler.Number;
import org.apache.xalan.xsltc.compiler.Otherwise;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.ProcessingInstruction;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.SimpleAttributeValue;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.UseAttributeSets;
import org.apache.xalan.xsltc.compiler.ValueOf;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.When;
import org.apache.xalan.xsltc.compiler.XslAttribute;
import org.apache.xalan.xsltc.compiler.XslElement;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.serializer.ElemDesc;
import org.apache.xml.serializer.ToHTMLStream;

final class LiteralElement
extends Instruction {
    private String _name;
    private LiteralElement _literalElemParent = null;
    private Vector _attributeElements = null;
    private Hashtable _accessedPrefixes = null;
    private boolean _allAttributesUnique = false;
    private static final String XMLNS_STRING = "xmlns";

    LiteralElement() {
    }

    public QName getName() {
        return this._qname;
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("LiteralElement name = " + this._name);
        this.displayContents(indent + 4);
    }

    private String accessedNamespace(String prefix) {
        String result;
        if (this._literalElemParent != null && (result = this._literalElemParent.accessedNamespace(prefix)) != null) {
            return result;
        }
        return this._accessedPrefixes != null ? (String)this._accessedPrefixes.get(prefix) : null;
    }

    public void registerNamespace(String prefix, String uri, SymbolTable stable, boolean declared) {
        String old;
        String parentUri;
        if (this._literalElemParent != null && (parentUri = this._literalElemParent.accessedNamespace(prefix)) != null && parentUri.equals(uri)) {
            return;
        }
        if (this._accessedPrefixes == null) {
            this._accessedPrefixes = new Hashtable();
        } else if (!declared && (old = (String)this._accessedPrefixes.get(prefix)) != null) {
            if (old.equals(uri)) {
                return;
            }
            prefix = stable.generateNamespacePrefix();
        }
        if (!prefix.equals("xml")) {
            this._accessedPrefixes.put(prefix, uri);
        }
    }

    private String translateQName(QName qname, SymbolTable stable) {
        String uri;
        String localname = qname.getLocalPart();
        String prefix = qname.getPrefix();
        if (prefix == null) {
            prefix = "";
        } else if (prefix.equals(XMLNS_STRING)) {
            return XMLNS_STRING;
        }
        String alternative = stable.lookupPrefixAlias(prefix);
        if (alternative != null) {
            stable.excludeNamespaces(prefix);
            prefix = alternative;
        }
        if ((uri = this.lookupNamespace(prefix)) == null) {
            return localname;
        }
        this.registerNamespace(prefix, uri, stable, false);
        if (prefix != "") {
            return prefix + ":" + localname;
        }
        return localname;
    }

    public void addAttribute(SyntaxTreeNode attribute) {
        if (this._attributeElements == null) {
            this._attributeElements = new Vector(2);
        }
        this._attributeElements.add(attribute);
    }

    public void setFirstAttribute(SyntaxTreeNode attribute) {
        if (this._attributeElements == null) {
            this._attributeElements = new Vector(2);
        }
        this._attributeElements.insertElementAt(attribute, 0);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._attributeElements != null) {
            int count = this._attributeElements.size();
            for (int i = 0; i < count; ++i) {
                SyntaxTreeNode node = (SyntaxTreeNode)this._attributeElements.elementAt(i);
                node.typeCheck(stable);
            }
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }

    public Enumeration getNamespaceScope(SyntaxTreeNode node) {
        Hashtable all = new Hashtable();
        while (node != null) {
            Hashtable mapping = node.getPrefixMapping();
            if (mapping != null) {
                Enumeration prefixes = mapping.keys();
                while (prefixes.hasMoreElements()) {
                    String prefix = (String)prefixes.nextElement();
                    if (all.containsKey(prefix)) continue;
                    all.put(prefix, mapping.get(prefix));
                }
            }
            node = node.getParent();
        }
        return all.keys();
    }

    @Override
    public void parseContents(Parser parser) {
        String val;
        String uri;
        SymbolTable stable = parser.getSymbolTable();
        stable.setCurrentNode(this);
        SyntaxTreeNode parent = this.getParent();
        if (parent != null && parent instanceof LiteralElement) {
            this._literalElemParent = (LiteralElement)parent;
        }
        this._name = this.translateQName(this._qname, stable);
        int count = this._attributes.getLength();
        for (int i = 0; i < count; ++i) {
            QName qname = parser.getQName(this._attributes.getQName(i));
            uri = qname.getNamespace();
            val = this._attributes.getValue(i);
            if (qname.equals(parser.getUseAttributeSets())) {
                if (!Util.isValidQNames(val)) {
                    ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", (Object)val, this);
                    parser.reportError(3, err);
                }
                this.setFirstAttribute(new UseAttributeSets(val, parser));
                continue;
            }
            if (qname.equals(parser.getExtensionElementPrefixes())) {
                stable.excludeNamespaces(val);
                continue;
            }
            if (qname.equals(parser.getExcludeResultPrefixes())) {
                stable.excludeNamespaces(val);
                continue;
            }
            String prefix = qname.getPrefix();
            if (prefix != null && prefix.equals(XMLNS_STRING) || prefix == null && qname.getLocalPart().equals(XMLNS_STRING) || uri != null && uri.equals("http://www.w3.org/1999/XSL/Transform")) continue;
            String name = this.translateQName(qname, stable);
            LiteralAttribute attr = new LiteralAttribute(name, val, parser, this);
            this.addAttribute(attr);
            attr.setParent(this);
            attr.parseContents(parser);
        }
        Enumeration include = this.getNamespaceScope(this);
        while (include.hasMoreElements()) {
            String prefix = (String)include.nextElement();
            if (prefix.equals("xml") || (uri = this.lookupNamespace(prefix)) == null || stable.isExcludedNamespace(uri)) continue;
            this.registerNamespace(prefix, uri, stable, true);
        }
        this.parseChildren(parser);
        for (int i = 0; i < count; ++i) {
            QName qname = parser.getQName(this._attributes.getQName(i));
            val = this._attributes.getValue(i);
            if (qname.equals(parser.getExtensionElementPrefixes())) {
                stable.unExcludeNamespaces(val);
                continue;
            }
            if (!qname.equals(parser.getExcludeResultPrefixes())) continue;
            stable.unExcludeNamespaces(val);
        }
    }

    @Override
    protected boolean contextDependent() {
        return this.dependentContents();
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        this._allAttributesUnique = this.checkAttributesUnique();
        il.append(methodGen.loadHandler());
        il.append(new PUSH(cpg, this._name));
        il.append(DUP2);
        il.append(methodGen.startElement());
        for (int j = 0; j < this.elementCount(); ++j) {
            SyntaxTreeNode item = (SyntaxTreeNode)this.elementAt(j);
            if (!(item instanceof Variable)) continue;
            item.translate(classGen, methodGen);
        }
        if (this._accessedPrefixes != null) {
            boolean declaresDefaultNS = false;
            Enumeration e = this._accessedPrefixes.keys();
            while (e.hasMoreElements()) {
                String prefix = (String)e.nextElement();
                String uri = (String)this._accessedPrefixes.get(prefix);
                if (uri == "" && prefix == "") continue;
                if (prefix == "") {
                    declaresDefaultNS = true;
                }
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, prefix));
                il.append(new PUSH(cpg, uri));
                il.append(methodGen.namespace());
            }
            if (!declaresDefaultNS && this._parent instanceof XslElement && ((XslElement)this._parent).declaresDefaultNS()) {
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, ""));
                il.append(new PUSH(cpg, ""));
                il.append(methodGen.namespace());
            }
        }
        if (this._attributeElements != null) {
            int count = this._attributeElements.size();
            for (int i = 0; i < count; ++i) {
                SyntaxTreeNode node = (SyntaxTreeNode)this._attributeElements.elementAt(i);
                if (node instanceof XslAttribute) continue;
                node.translate(classGen, methodGen);
            }
        }
        this.translateContents(classGen, methodGen);
        il.append(methodGen.endElement());
    }

    private boolean isHTMLOutput() {
        return this.getStylesheet().getOutputMethod() == 2;
    }

    public ElemDesc getElemDesc() {
        if (this.isHTMLOutput()) {
            return ToHTMLStream.getElemDesc((String)this._name);
        }
        return null;
    }

    public boolean allAttributesUnique() {
        return this._allAttributesUnique;
    }

    private boolean checkAttributesUnique() {
        boolean hasHiddenXslAttribute = this.canProduceAttributeNodes(this, true);
        if (hasHiddenXslAttribute) {
            return false;
        }
        if (this._attributeElements != null) {
            int numAttrs = this._attributeElements.size();
            Hashtable<String, Instruction> attrsTable = null;
            for (int i = 0; i < numAttrs; ++i) {
                XslAttribute xslAttr;
                AttributeValue attrName;
                SyntaxTreeNode node = (SyntaxTreeNode)this._attributeElements.elementAt(i);
                if (node instanceof UseAttributeSets) {
                    return false;
                }
                if (!(node instanceof XslAttribute)) continue;
                if (attrsTable == null) {
                    attrsTable = new Hashtable<String, Instruction>();
                    for (int k = 0; k < i; ++k) {
                        SyntaxTreeNode n = (SyntaxTreeNode)this._attributeElements.elementAt(k);
                        if (!(n instanceof LiteralAttribute)) continue;
                        LiteralAttribute literalAttr = (LiteralAttribute)n;
                        attrsTable.put(literalAttr.getName(), literalAttr);
                    }
                }
                if ((attrName = (xslAttr = (XslAttribute)node).getName()) instanceof AttributeValueTemplate) {
                    return false;
                }
                if (!(attrName instanceof SimpleAttributeValue)) continue;
                SimpleAttributeValue simpleAttr = (SimpleAttributeValue)attrName;
                String name = simpleAttr.toString();
                if (name != null && attrsTable.get(name) != null) {
                    return false;
                }
                if (name == null) continue;
                attrsTable.put(name, xslAttr);
            }
        }
        return true;
    }

    private boolean canProduceAttributeNodes(SyntaxTreeNode node, boolean ignoreXslAttribute) {
        Vector contents = node.getContents();
        int size = contents.size();
        for (int i = 0; i < size; ++i) {
            SyntaxTreeNode child = (SyntaxTreeNode)contents.elementAt(i);
            if (child instanceof Text) {
                Text text = (Text)child;
                if (text.isIgnore()) continue;
                return false;
            }
            if (child instanceof LiteralElement || child instanceof ValueOf || child instanceof XslElement || child instanceof Comment || child instanceof Number || child instanceof ProcessingInstruction) {
                return false;
            }
            if (child instanceof XslAttribute) {
                if (ignoreXslAttribute) continue;
                return true;
            }
            if (child instanceof CallTemplate || child instanceof ApplyTemplates || child instanceof Copy || child instanceof CopyOf) {
                return true;
            }
            if ((child instanceof If || child instanceof ForEach) && this.canProduceAttributeNodes(child, false)) {
                return true;
            }
            if (!(child instanceof Choose)) continue;
            Vector chooseContents = child.getContents();
            int num = chooseContents.size();
            for (int k = 0; k < num; ++k) {
                SyntaxTreeNode chooseChild = (SyntaxTreeNode)chooseContents.elementAt(k);
                if (!(chooseChild instanceof When) && !(chooseChild instanceof Otherwise) || !this.canProduceAttributeNodes(chooseChild, false)) continue;
                return true;
            }
        }
        return false;
    }
}

