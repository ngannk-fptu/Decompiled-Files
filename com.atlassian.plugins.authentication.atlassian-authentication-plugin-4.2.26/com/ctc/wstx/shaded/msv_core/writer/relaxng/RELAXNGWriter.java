/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import com.ctc.wstx.shaded.msv_core.writer.GrammarWriter;
import com.ctc.wstx.shaded.msv_core.writer.SAXRuntimeException;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import com.ctc.wstx.shaded.msv_core.writer.relaxng.Context;
import com.ctc.wstx.shaded.msv_core.writer.relaxng.NameClassWriter;
import com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class RELAXNGWriter
implements GrammarWriter,
Context {
    protected XMLWriter writer = new XMLWriter();
    protected Grammar grammar;
    protected Map exp2name;
    protected String defaultNs;
    protected NameClassVisitor nameClassWriter;
    protected SmartPatternWriter patternWriter = new SmartPatternWriter(this);

    public XMLWriter getWriter() {
        return this.writer;
    }

    public void setDocumentHandler(DocumentHandler handler) {
        this.writer.setDocumentHandler(handler);
    }

    public void write(Grammar g) throws SAXException {
        this.write(g, this.sniffDefaultNs(g.getTopLevel()));
    }

    public void write(Grammar g, String _defaultNs) throws SAXException {
        this.defaultNs = _defaultNs;
        this.grammar = g;
        final HashSet nodes = new HashSet();
        final HashSet heads = new HashSet();
        g.getTopLevel().visit(new ExpressionWalker(){

            public void onElement(ElementExp exp) {
                if (nodes.contains(exp)) {
                    heads.add(exp);
                    return;
                }
                nodes.add(exp);
                super.onElement(exp);
            }

            public void onRef(ReferenceExp exp) {
                if (nodes.contains(exp)) {
                    heads.add(exp);
                    return;
                }
                nodes.add(exp);
                super.onRef(exp);
            }
        });
        HashMap<String, Expression> name2exp = new HashMap<String, Expression>();
        int cnt = 0;
        for (Expression exp : heads) {
            if (exp instanceof ReferenceExp) {
                ReferenceExp rexp = (ReferenceExp)exp;
                if (rexp.name == null) {
                    while (name2exp.containsKey("anonymous" + cnt)) {
                        ++cnt;
                    }
                    name2exp.put("anonymous" + cnt, exp);
                    continue;
                }
                if (name2exp.containsKey(rexp.name)) {
                    int i = 2;
                    while (name2exp.containsKey(rexp.name + i)) {
                        ++i;
                    }
                    name2exp.put(rexp.name + i, exp);
                    continue;
                }
                name2exp.put(rexp.name, exp);
                continue;
            }
            if (exp instanceof ElementExp) {
                ElementExp eexp = (ElementExp)exp;
                NameClass nc = eexp.getNameClass();
                if (nc instanceof SimpleNameClass && !name2exp.containsKey(((SimpleNameClass)nc).localName)) {
                    name2exp.put(((SimpleNameClass)nc).localName, exp);
                    continue;
                }
                while (name2exp.containsKey("element" + cnt)) {
                    ++cnt;
                }
                name2exp.put("element" + cnt, exp);
                continue;
            }
            throw new Error();
        }
        this.exp2name = new HashMap();
        for (String name : name2exp.keySet()) {
            this.exp2name.put(name2exp.get(name), name);
        }
        this.nameClassWriter = this.createNameClassWriter();
        try {
            DocumentHandler handler = this.writer.getDocumentHandler();
            handler.setDocumentLocator(new LocatorImpl());
            handler.startDocument();
            if (this.defaultNs != null) {
                this.writer.start("grammar", new String[]{"ns", this.defaultNs, "xmlns", "http://relaxng.org/ns/structure/1.0", "datatypeLibrary", "http://www.w3.org/2001/XMLSchema-datatypes"});
            } else {
                this.writer.start("grammar", new String[]{"xmlns", "http://relaxng.org/ns/structure/1.0", "datatypeLibrary", "http://www.w3.org/2001/XMLSchema-datatypes"});
            }
            this.writer.start("start");
            this.writeIsland(g.getTopLevel());
            this.writer.end("start");
            for (Expression exp : this.exp2name.keySet()) {
                String name = (String)this.exp2name.get(exp);
                if (exp instanceof ReferenceExp) {
                    exp = ((ReferenceExp)exp).exp;
                }
                this.writer.start("define", new String[]{"name", name});
                this.writeIsland(exp);
                this.writer.end("define");
            }
            this.writer.end("grammar");
            handler.endDocument();
        }
        catch (SAXRuntimeException sw) {
            throw sw.e;
        }
    }

    protected void writeIsland(Expression exp) {
        if (exp instanceof ElementExp) {
            this.patternWriter.writeElement((ElementExp)exp);
        } else {
            this.patternWriter.visitUnary(exp);
        }
    }

    protected String sniffDefaultNs(Expression exp) {
        return (String)exp.visit(new ExpressionVisitor(){

            public Object onElement(ElementExp exp) {
                return this.sniff(exp.getNameClass());
            }

            public Object onAttribute(AttributeExp exp) {
                return this.sniff(exp.nameClass);
            }

            protected String sniff(NameClass nc) {
                if (nc instanceof SimpleNameClass) {
                    return ((SimpleNameClass)nc).namespaceURI;
                }
                return null;
            }

            public Object onChoice(ChoiceExp exp) {
                return this.onBinExp(exp);
            }

            public Object onSequence(SequenceExp exp) {
                return this.onBinExp(exp);
            }

            public Object onInterleave(InterleaveExp exp) {
                return this.onBinExp(exp);
            }

            public Object onConcur(ConcurExp exp) {
                return this.onBinExp(exp);
            }

            public Object onBinExp(BinaryExp exp) {
                Object o = exp.exp1.visit(this);
                if (o == null) {
                    o = exp.exp2.visit(this);
                }
                return o;
            }

            public Object onMixed(MixedExp exp) {
                return exp.exp.visit(this);
            }

            public Object onOneOrMore(OneOrMoreExp exp) {
                return exp.exp.visit(this);
            }

            public Object onRef(ReferenceExp exp) {
                return exp.exp.visit(this);
            }

            public Object onOther(OtherExp exp) {
                return exp.exp.visit(this);
            }

            public Object onNullSet() {
                return null;
            }

            public Object onEpsilon() {
                return null;
            }

            public Object onAnyString() {
                return null;
            }

            public Object onData(DataExp exp) {
                return null;
            }

            public Object onValue(ValueExp exp) {
                return null;
            }

            public Object onList(ListExp exp) {
                return null;
            }
        });
    }

    public String getTargetNamespace() {
        return this.defaultNs;
    }

    public void writeNameClass(NameClass src) {
        String MAGIC = "\u0000";
        Set names = PossibleNamesCollector.calc(src);
        StringPair[] values = names.toArray(new StringPair[names.size()]);
        HashSet<String> uriset = new HashSet<String>();
        for (int i = 0; i < values.length; ++i) {
            uriset.add(values[i].namespaceURI);
        }
        NameClass r = null;
        String[] uris = uriset.toArray(new String[uriset.size()]);
        for (int i = 0; i < uris.length; ++i) {
            if (uris[i] == "\u0000") continue;
            NameClass tmp = null;
            for (int j = 0; j < values.length; ++j) {
                if (!values[j].namespaceURI.equals(uris[i]) || values[j].localName == "\u0000" || src.accepts(values[j]) == src.accepts(uris[i], "\u0000")) continue;
                tmp = tmp == null ? new SimpleNameClass(values[j]) : new ChoiceNameClass(tmp, new SimpleNameClass(values[j]));
            }
            if (src.accepts(uris[i], "\u0000") != src.accepts("\u0000", "\u0000")) {
                tmp = tmp == null ? new NamespaceNameClass(uris[i]) : new DifferenceNameClass(new NamespaceNameClass(uris[i]), tmp);
            }
            r = r == null ? tmp : new ChoiceNameClass(r, tmp);
        }
        if (src.accepts("\u0000", "\u0000")) {
            r = r == null ? NameClass.ALL : new DifferenceNameClass(NameClass.ALL, r);
        } else if (r == null) {
            this.writer.element("anyName");
            this.writer.element("notAllowed");
            return;
        }
        r.visit(this.nameClassWriter);
    }

    protected NameClassVisitor createNameClassWriter() {
        return new NameClassWriter(this);
    }

    class SmartPatternWriter
    extends PatternWriter {
        SmartPatternWriter(Context context) {
            super(context);
        }

        public void onOther(OtherExp exp) {
            exp.exp.visit(this);
        }

        public void onRef(ReferenceExp exp) {
            String uniqueName = (String)RELAXNGWriter.this.exp2name.get(exp);
            if (uniqueName != null) {
                this.writer.element("ref", new String[]{"name", uniqueName});
            } else {
                exp.exp.visit(this);
            }
        }

        public void onElement(ElementExp exp) {
            String uniqueName = (String)RELAXNGWriter.this.exp2name.get(exp);
            if (uniqueName != null) {
                this.writer.element("ref", new String[]{"name", uniqueName});
                return;
            }
            this.writeElement(exp);
        }

        public void onAttribute(AttributeExp exp) {
            if (exp.nameClass instanceof SimpleNameClass && ((SimpleNameClass)exp.nameClass).namespaceURI.equals("")) {
                this.writer.start("attribute", new String[]{"name", ((SimpleNameClass)exp.nameClass).localName});
            } else {
                this.writer.start("attribute");
                this.context.writeNameClass(exp.nameClass);
            }
            if (exp.exp != Expression.anyString) {
                this.visitUnary(exp.exp);
            }
            this.writer.end("attribute");
        }

        protected void writeElement(ElementExp exp) {
            NameClass nc = exp.getNameClass();
            if (nc instanceof SimpleNameClass && ((SimpleNameClass)nc).namespaceURI.equals(RELAXNGWriter.this.defaultNs)) {
                this.writer.start("element", new String[]{"name", ((SimpleNameClass)nc).localName});
            } else {
                this.writer.start("element");
                RELAXNGWriter.this.writeNameClass(exp.getNameClass());
            }
            this.visitUnary(this.simplify(exp.contentModel));
            this.writer.end("element");
        }

        public Expression simplify(Expression exp) {
            return exp.visit(new ExpressionCloner(RELAXNGWriter.this.grammar.getPool()){

                public Expression onRef(ReferenceExp exp) {
                    if (RELAXNGWriter.this.exp2name.containsKey(exp)) {
                        return exp;
                    }
                    return exp.exp.visit(this);
                }

                public Expression onOther(OtherExp exp) {
                    return exp.exp.visit(this);
                }

                public Expression onElement(ElementExp exp) {
                    return exp;
                }

                public Expression onAttribute(AttributeExp exp) {
                    return exp;
                }
            });
        }
    }
}

