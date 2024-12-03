/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.RunAwayExpressionChecker;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.TerminalState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.reader.trex.IncludePatternState;
import com.ctc.wstx.shaded.msv_core.reader.trex.RootState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXSequencedStringChecker;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.ConcurState;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.DataState;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.DefineState;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.StringState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

public class TREXGrammarReader
extends TREXBaseReader
implements XSDatatypeResolver {
    protected String currentGrammarURI;
    public static final String TREXNamespace = "http://www.thaiopensource.com/trex";
    private boolean issueObsoletedXMLSchemaNamespace = false;

    public static TREXGrammar parse(String grammarURL, SAXParserFactory factory, GrammarReaderController controller) {
        TREXGrammarReader reader = new TREXGrammarReader(controller, factory, new ExpressionPool());
        reader.parse(grammarURL);
        return reader.getResult();
    }

    public static TREXGrammar parse(InputSource grammar, SAXParserFactory factory, GrammarReaderController controller) {
        TREXGrammarReader reader = new TREXGrammarReader(controller, factory, new ExpressionPool());
        reader.parse(grammar);
        return reader.getResult();
    }

    public TREXGrammarReader(GrammarReaderController controller) {
        this(controller, TREXGrammarReader.createParserFactory(), new ExpressionPool());
    }

    public TREXGrammarReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        this(controller, parserFactory, new StateFactory(), pool);
    }

    public TREXGrammarReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool) {
        super(controller, parserFactory, pool, stateFactory, new RootState());
    }

    protected String localizeMessage(String propertyName, Object[] args) {
        String format;
        try {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.trex.classic.Messages").getString(propertyName);
        }
        catch (Exception e) {
            return super.localizeMessage(propertyName, args);
        }
        return MessageFormat.format(format, args);
    }

    protected TREXGrammar getGrammar() {
        return this.grammar;
    }

    protected boolean isGrammarElement(StartTagInfo tag) {
        if (this.currentGrammarURI == null) {
            if (tag.namespaceURI.equals(TREXNamespace)) {
                this.currentGrammarURI = TREXNamespace;
                return true;
            }
            if (tag.namespaceURI.equals("")) {
                this.currentGrammarURI = "";
                return true;
            }
            return false;
        }
        if (this.currentGrammarURI.equals(tag.namespaceURI)) {
            return true;
        }
        return tag.containsAttribute(TREXNamespace, "role");
    }

    protected StateFactory getStateFactory() {
        return (StateFactory)this.sfactory;
    }

    private String mapNamespace(String namespace) {
        if (namespace.equals("http://www.w3.org/2000/10/XMLSchema") || namespace.equals("http://www.w3.org/2000/10/XMLSchema-datatypes")) {
            if (!this.issueObsoletedXMLSchemaNamespace) {
                this.reportWarning("TREXGrammarReader.Warning.ObsoletedXMLSchemaNamespace", namespace);
            }
            this.issueObsoletedXMLSchemaNamespace = true;
            return "http://www.w3.org/2001/XMLSchema-datatypes";
        }
        return namespace;
    }

    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("concur")) {
            return this.getStateFactory().concur(parent, tag);
        }
        if (tag.localName.equals("anyString")) {
            return this.getStateFactory().anyString(parent, tag);
        }
        if (tag.localName.equals("string")) {
            return this.getStateFactory().string(parent, tag);
        }
        if (tag.localName.equals("data")) {
            return this.getStateFactory().data(parent, tag);
        }
        if (tag.localName.equals("include")) {
            return this.getStateFactory().includePattern(parent, tag);
        }
        String role = tag.getAttribute(TREXNamespace, "role");
        if ("datatype".equals(role)) {
            String namespaceURI = this.mapNamespace(tag.namespaceURI);
            DataTypeVocabulary v = this.grammar.dataTypes.get(namespaceURI);
            if (v == null) {
                this.reportError("TREXGrammarReader.UnknownDataTypeVocabulary", (Object)tag.namespaceURI);
                this.grammar.dataTypes.put(tag.namespaceURI, new UndefinedDataTypeVocabulary());
                return new IgnoreState();
            }
            return v.createTopLevelReaderState(tag);
        }
        return super.createExpressionChildState(parent, tag);
    }

    public XSDatatypeExp resolveXSDatatype(String qName) {
        return new XSDatatypeExp((XSDatatype)this.resolveDatatype(qName), this.pool);
    }

    public Datatype resolveDatatype(String qName) {
        String[] s = this.splitQName(qName);
        if (s == null) {
            this.reportError("TREXGrammarReader.UndeclaredPrefix", (Object)qName);
            return StringType.theInstance;
        }
        s[0] = this.mapNamespace(s[0]);
        DataTypeVocabulary v = this.grammar.dataTypes.get(s[0]);
        if (v == null) {
            this.reportError("TREXGrammarReader.UnknownDataTypeVocabulary", (Object)s[0]);
            this.grammar.dataTypes.put(s[0], new UndefinedDataTypeVocabulary());
        } else {
            try {
                return v.getType(s[1]);
            }
            catch (DatatypeException e) {
                this.reportError("GrammarReader.UndefinedDataType", (Object)qName);
            }
        }
        return StringType.theInstance;
    }

    public void wrapUp() {
        RunAwayExpressionChecker.check(this, this.grammar);
        if (!this.controller.hadError()) {
            this.grammar.visit(new TREXSequencedStringChecker(this, false));
        }
    }

    private static class UndefinedDataTypeVocabulary
    implements DataTypeVocabulary {
        private UndefinedDataTypeVocabulary() {
        }

        public State createTopLevelReaderState(StartTagInfo tag) {
            return new IgnoreState();
        }

        public Datatype getType(String localTypeName) {
            return StringType.theInstance;
        }
    }

    public static class StateFactory
    extends TREXBaseReader.StateFactory {
        public State concur(State parent, StartTagInfo tag) {
            return new ConcurState();
        }

        public State anyString(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.anyString);
        }

        public State string(State parent, StartTagInfo tag) {
            return new StringState();
        }

        public State data(State parent, StartTagInfo tag) {
            return new DataState();
        }

        public State define(State parent, StartTagInfo tag) {
            return new DefineState();
        }

        public State includePattern(State parent, StartTagInfo tag) {
            return new IncludePatternState();
        }
    }
}

