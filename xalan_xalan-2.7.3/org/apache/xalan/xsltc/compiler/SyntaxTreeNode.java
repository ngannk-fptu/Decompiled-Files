/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DUP_X1;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.AbsolutePathPattern;
import org.apache.xalan.xsltc.compiler.ApplyTemplates;
import org.apache.xalan.xsltc.compiler.CallTemplate;
import org.apache.xalan.xsltc.compiler.Choose;
import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.compiler.If;
import org.apache.xalan.xsltc.compiler.Number;
import org.apache.xalan.xsltc.compiler.Otherwise;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.Text;
import org.apache.xalan.xsltc.compiler.ValueOf;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.VariableBase;
import org.apache.xalan.xsltc.compiler.When;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.runtime.AttributeList;
import org.xml.sax.Attributes;

public abstract class SyntaxTreeNode
implements Constants {
    private Parser _parser;
    protected SyntaxTreeNode _parent;
    private Stylesheet _stylesheet;
    private Template _template;
    private final Vector _contents = new Vector(2);
    protected QName _qname;
    private int _line;
    protected AttributeList _attributes = null;
    private Hashtable _prefixMapping = null;
    public static final int UNKNOWN_STYLESHEET_NODE_ID = -1;
    private int _nodeIDForStylesheetNSLookup = -1;
    static final SyntaxTreeNode Dummy = new AbsolutePathPattern(null);
    protected static final int IndentIncrement = 4;
    private static final char[] _spaces = "                                                       ".toCharArray();

    public SyntaxTreeNode() {
        this._line = 0;
        this._qname = null;
    }

    public SyntaxTreeNode(int line) {
        this._line = line;
        this._qname = null;
    }

    public SyntaxTreeNode(String uri, String prefix, String local) {
        this._line = 0;
        this.setQName(uri, prefix, local);
    }

    protected final void setLineNumber(int line) {
        this._line = line;
    }

    public final int getLineNumber() {
        if (this._line > 0) {
            return this._line;
        }
        SyntaxTreeNode parent = this.getParent();
        return parent != null ? parent.getLineNumber() : 0;
    }

    protected void setQName(QName qname) {
        this._qname = qname;
    }

    protected void setQName(String uri, String prefix, String localname) {
        this._qname = new QName(uri, prefix, localname);
    }

    protected QName getQName() {
        return this._qname;
    }

    protected void setAttributes(AttributeList attributes) {
        this._attributes = attributes;
    }

    protected String getAttribute(String qname) {
        if (this._attributes == null) {
            return "";
        }
        String value = this._attributes.getValue(qname);
        return value == null || value.equals("") ? "" : value;
    }

    protected String getAttribute(String prefix, String localName) {
        return this.getAttribute(prefix + ':' + localName);
    }

    protected boolean hasAttribute(String qname) {
        return this._attributes != null && this._attributes.getValue(qname) != null;
    }

    protected void addAttribute(String qname, String value) {
        this._attributes.add(qname, value);
    }

    protected Attributes getAttributes() {
        return this._attributes;
    }

    protected void setPrefixMapping(Hashtable mapping) {
        this._prefixMapping = mapping;
    }

    protected Hashtable getPrefixMapping() {
        return this._prefixMapping;
    }

    protected void addPrefixMapping(String prefix, String uri) {
        if (this._prefixMapping == null) {
            this._prefixMapping = new Hashtable();
        }
        this._prefixMapping.put(prefix, uri);
    }

    protected String lookupNamespace(String prefix) {
        String uri = null;
        if (this._prefixMapping != null) {
            uri = (String)this._prefixMapping.get(prefix);
        }
        if (uri == null && this._parent != null) {
            uri = this._parent.lookupNamespace(prefix);
            if (prefix == "" && uri == null) {
                uri = "";
            }
        }
        return uri;
    }

    protected String lookupPrefix(String uri) {
        String prefix = null;
        if (this._prefixMapping != null && this._prefixMapping.contains(uri)) {
            Enumeration prefixes = this._prefixMapping.keys();
            while (prefixes.hasMoreElements()) {
                prefix = (String)prefixes.nextElement();
                String mapsTo = (String)this._prefixMapping.get(prefix);
                if (!mapsTo.equals(uri)) continue;
                return prefix;
            }
        } else if (this._parent != null) {
            prefix = this._parent.lookupPrefix(uri);
            if (uri == "" && prefix == null) {
                prefix = "";
            }
        }
        return prefix;
    }

    protected void setParser(Parser parser) {
        this._parser = parser;
    }

    public final Parser getParser() {
        return this._parser;
    }

    protected void setParent(SyntaxTreeNode parent) {
        if (this._parent == null) {
            this._parent = parent;
        }
    }

    protected final SyntaxTreeNode getParent() {
        return this._parent;
    }

    protected final boolean isDummy() {
        return this == Dummy;
    }

    protected int getImportPrecedence() {
        Stylesheet stylesheet = this.getStylesheet();
        if (stylesheet == null) {
            return Integer.MIN_VALUE;
        }
        return stylesheet.getImportPrecedence();
    }

    public Stylesheet getStylesheet() {
        if (this._stylesheet == null) {
            SyntaxTreeNode parent;
            for (parent = this; parent != null; parent = parent.getParent()) {
                if (!(parent instanceof Stylesheet)) continue;
                return (Stylesheet)parent;
            }
            this._stylesheet = (Stylesheet)parent;
        }
        return this._stylesheet;
    }

    protected Template getTemplate() {
        if (this._template == null) {
            SyntaxTreeNode parent;
            for (parent = this; parent != null && !(parent instanceof Template); parent = parent.getParent()) {
            }
            this._template = (Template)parent;
        }
        return this._template;
    }

    protected final XSLTC getXSLTC() {
        return this._parser.getXSLTC();
    }

    protected final SymbolTable getSymbolTable() {
        return this._parser == null ? null : this._parser.getSymbolTable();
    }

    public void parseContents(Parser parser) {
        this.parseChildren(parser);
    }

    protected final void parseChildren(Parser parser) {
        Vector<QName> locals = null;
        int count = this._contents.size();
        for (int i = 0; i < count; ++i) {
            SyntaxTreeNode child = (SyntaxTreeNode)this._contents.elementAt(i);
            parser.getSymbolTable().setCurrentNode(child);
            child.parseContents(parser);
            QName varOrParamName = this.updateScope(parser, child);
            if (varOrParamName == null) continue;
            if (locals == null) {
                locals = new Vector<QName>(2);
            }
            locals.addElement(varOrParamName);
        }
        parser.getSymbolTable().setCurrentNode(this);
        if (locals != null) {
            int nLocals = locals.size();
            for (int i = 0; i < nLocals; ++i) {
                parser.removeVariable((QName)locals.elementAt(i));
            }
        }
    }

    protected QName updateScope(Parser parser, SyntaxTreeNode node) {
        if (node instanceof Variable) {
            Variable var = (Variable)node;
            parser.addVariable(var);
            return var.getName();
        }
        if (node instanceof Param) {
            Param param = (Param)node;
            parser.addParameter(param);
            return param.getName();
        }
        return null;
    }

    public abstract Type typeCheck(SymbolTable var1) throws TypeCheckError;

    protected Type typeCheckContents(SymbolTable stable) throws TypeCheckError {
        int n = this.elementCount();
        for (int i = 0; i < n; ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)this._contents.elementAt(i);
            item.typeCheck(stable);
        }
        return Type.Void;
    }

    public abstract void translate(ClassGenerator var1, MethodGenerator var2);

    protected void translateContents(ClassGenerator classGen, MethodGenerator methodGen) {
        int i;
        int n = this.elementCount();
        for (i = 0; i < n; ++i) {
            methodGen.markChunkStart();
            SyntaxTreeNode item = (SyntaxTreeNode)this._contents.elementAt(i);
            item.translate(classGen, methodGen);
            methodGen.markChunkEnd();
        }
        for (i = 0; i < n; ++i) {
            if (!(this._contents.elementAt(i) instanceof VariableBase)) continue;
            VariableBase var = (VariableBase)this._contents.elementAt(i);
            var.unmapRegister(methodGen);
        }
    }

    private boolean isSimpleRTF(SyntaxTreeNode node) {
        Vector contents = node.getContents();
        for (int i = 0; i < contents.size(); ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)contents.elementAt(i);
            if (this.isTextElement(item, false)) continue;
            return false;
        }
        return true;
    }

    private boolean isAdaptiveRTF(SyntaxTreeNode node) {
        Vector contents = node.getContents();
        for (int i = 0; i < contents.size(); ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)contents.elementAt(i);
            if (this.isTextElement(item, true)) continue;
            return false;
        }
        return true;
    }

    private boolean isTextElement(SyntaxTreeNode node, boolean doExtendedCheck) {
        if (node instanceof ValueOf || node instanceof Number || node instanceof Text) {
            return true;
        }
        if (node instanceof If) {
            return doExtendedCheck ? this.isAdaptiveRTF(node) : this.isSimpleRTF(node);
        }
        if (node instanceof Choose) {
            Vector contents = node.getContents();
            for (int i = 0; i < contents.size(); ++i) {
                SyntaxTreeNode item = (SyntaxTreeNode)contents.elementAt(i);
                if (item instanceof Text || (item instanceof When || item instanceof Otherwise) && (doExtendedCheck && this.isAdaptiveRTF(item) || !doExtendedCheck && this.isSimpleRTF(item))) continue;
                return false;
            }
            return true;
        }
        return doExtendedCheck && (node instanceof CallTemplate || node instanceof ApplyTemplates);
    }

    protected void compileResultTree(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        Stylesheet stylesheet = classGen.getStylesheet();
        boolean isSimple = this.isSimpleRTF(this);
        boolean isAdaptive = false;
        if (!isSimple) {
            isAdaptive = this.isAdaptiveRTF(this);
        }
        int rtfType = isSimple ? 0 : (isAdaptive ? 1 : 2);
        il.append(methodGen.loadHandler());
        String DOM_CLASS = classGen.getDOMClass();
        il.append(methodGen.loadDOM());
        int index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getResultTreeFrag", "(IIZ)Lorg/apache/xalan/xsltc/DOM;");
        il.append(new PUSH(cpg, 32));
        il.append(new PUSH(cpg, rtfType));
        il.append(new PUSH(cpg, stylesheet.callsNodeset()));
        il.append(new INVOKEINTERFACE(index, 4));
        il.append(DUP);
        index = cpg.addInterfaceMethodref("org.apache.xalan.xsltc.DOM", "getOutputDomBuilder", "()" + TRANSLET_OUTPUT_SIG);
        il.append(new INVOKEINTERFACE(index, 1));
        il.append(DUP);
        il.append(methodGen.storeHandler());
        il.append(methodGen.startDocument());
        this.translateContents(classGen, methodGen);
        il.append(methodGen.loadHandler());
        il.append(methodGen.endDocument());
        if (stylesheet.callsNodeset() && !DOM_CLASS.equals("org/apache/xalan/xsltc/DOM")) {
            index = cpg.addMethodref("org/apache/xalan/xsltc/dom/DOMAdapter", "<init>", "(Lorg/apache/xalan/xsltc/DOM;[Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
            il.append(new NEW(cpg.addClass("org/apache/xalan/xsltc/dom/DOMAdapter")));
            il.append(new DUP_X1());
            il.append(SWAP);
            if (!stylesheet.callsNodeset()) {
                il.append(new ICONST(0));
                il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
                il.append(DUP);
                il.append(DUP);
                il.append(new ICONST(0));
                il.append(new NEWARRAY(BasicType.INT));
                il.append(SWAP);
                il.append(new INVOKESPECIAL(index));
            } else {
                il.append(ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
                il.append(ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
                il.append(ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
                il.append(ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
                il.append(new INVOKESPECIAL(index));
                il.append(DUP);
                il.append(methodGen.loadDOM());
                il.append(new CHECKCAST(cpg.addClass(classGen.getDOMClass())));
                il.append(SWAP);
                index = cpg.addMethodref("org.apache.xalan.xsltc.dom.MultiDOM", "addDOMAdapter", "(Lorg/apache/xalan/xsltc/dom/DOMAdapter;)I");
                il.append(new INVOKEVIRTUAL(index));
                il.append(POP);
            }
        }
        il.append(SWAP);
        il.append(methodGen.storeHandler());
    }

    protected final int getNodeIDForStylesheetNSLookup() {
        if (this._nodeIDForStylesheetNSLookup == -1) {
            Hashtable prefixMapping = this.getPrefixMapping();
            int parentNodeID = this._parent != null ? this._parent.getNodeIDForStylesheetNSLookup() : -1;
            this._nodeIDForStylesheetNSLookup = prefixMapping == null ? parentNodeID : this.getXSLTC().registerStylesheetPrefixMappingForRuntime(prefixMapping, parentNodeID);
        }
        return this._nodeIDForStylesheetNSLookup;
    }

    protected boolean contextDependent() {
        return true;
    }

    protected boolean dependentContents() {
        int n = this.elementCount();
        for (int i = 0; i < n; ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)this._contents.elementAt(i);
            if (!item.contextDependent()) continue;
            return true;
        }
        return false;
    }

    protected final void addElement(SyntaxTreeNode element) {
        this._contents.addElement(element);
        element.setParent(this);
    }

    protected final void setFirstElement(SyntaxTreeNode element) {
        this._contents.insertElementAt(element, 0);
        element.setParent(this);
    }

    protected final void removeElement(SyntaxTreeNode element) {
        this._contents.remove(element);
        element.setParent(null);
    }

    protected final Vector getContents() {
        return this._contents;
    }

    protected final boolean hasContents() {
        return this.elementCount() > 0;
    }

    protected final int elementCount() {
        return this._contents.size();
    }

    protected final Enumeration elements() {
        return this._contents.elements();
    }

    protected final Object elementAt(int pos) {
        return this._contents.elementAt(pos);
    }

    protected final SyntaxTreeNode lastChild() {
        if (this._contents.size() == 0) {
            return null;
        }
        return (SyntaxTreeNode)this._contents.lastElement();
    }

    public void display(int indent) {
        this.displayContents(indent);
    }

    protected void displayContents(int indent) {
        int n = this.elementCount();
        for (int i = 0; i < n; ++i) {
            SyntaxTreeNode item = (SyntaxTreeNode)this._contents.elementAt(i);
            item.display(indent);
        }
    }

    protected final void indent(int indent) {
        System.out.print(new String(_spaces, 0, indent));
    }

    protected void reportError(SyntaxTreeNode element, Parser parser, String errorCode, String message) {
        ErrorMsg error = new ErrorMsg(errorCode, (Object)message, element);
        parser.reportError(3, error);
    }

    protected void reportWarning(SyntaxTreeNode element, Parser parser, String errorCode, String message) {
        ErrorMsg error = new ErrorMsg(errorCode, (Object)message, element);
        parser.reportError(4, error);
    }
}

