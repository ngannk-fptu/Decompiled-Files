/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.RELAXGrammar;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RootGrammarState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.RELAXCoreIslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.trex.TREXIslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class RELAXNSReader
extends RELAXReader {
    public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";
    public final RELAXGrammar grammar;
    protected SchemaProvider schemaProvider;
    public static final String WRN_ILLEGAL_RELAXNAMESPACE_VERSION = "RELAXNSReader.Warning.IllegalRelaxNamespaceVersion";
    public static final String ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE = "RELAXNSReader.TopLevelParticleMustBeRelaxCore";
    public static final String ERR_INLINEMODULE_NOT_FOUND = "RELAXNSReader.InlineModuleNotFound";
    public static final String ERR_UNKNOWN_LANGUAGE = "RELAXNSReader.UnknownLanguage";
    public static final String ERR_NAMESPACE_COLLISION = "RELAXNSReader.NamespaceCollision";

    public static RELAXGrammar parse(String moduleURL, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXNSReader reader = new RELAXNSReader(controller, factory, pool);
        reader.parse(moduleURL);
        return reader.getResult();
    }

    public static RELAXGrammar parse(InputSource module, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXNSReader reader = new RELAXNSReader(controller, factory, pool);
        reader.parse(module);
        return reader.getResult();
    }

    public RELAXNSReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        super(controller, parserFactory, new RELAXReader.StateFactory(), pool, new RootGrammarState());
        this.grammar = new RELAXGrammar(pool);
    }

    public final RELAXGrammar getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.grammar;
    }

    public Grammar getResultAsGrammar() {
        return this.getResult();
    }

    public final SchemaProvider getSchemaProvider() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.schemaProvider;
    }

    public IslandSchemaReader getIslandSchemaReader(String language, String expectedTargetNamespace) {
        try {
            if (language.equals("http://www.xml.gr.jp/xmlns/relaxCore")) {
                return new RELAXCoreIslandSchemaReader((GrammarReaderController)this.controller, this.parserFactory, this.pool, expectedTargetNamespace);
            }
            if (language.equals("http://www.thaiopensource.com/trex")) {
                return new TREXIslandSchemaReader(new TREXGrammarReader((GrammarReaderController)this.controller, this.parserFactory, new TREXGrammarReader.StateFactory(), this.pool));
            }
        }
        catch (ParserConfigurationException e) {
            this.controller.error(e, null);
        }
        catch (SAXException e) {
            this.controller.error(e, null);
        }
        return null;
    }

    public Datatype resolveDataType(String typeName) {
        throw new Error();
    }

    protected boolean isGrammarElement(StartTagInfo tag) {
        if (!RELAXNamespaceNamespace.equals(tag.namespaceURI)) {
            return false;
        }
        return !tag.localName.equals("annotation");
    }

    protected Expression resolveElementRef(String namespace, String label) {
        return this.resolveRef(namespace, label, "ref");
    }

    protected Expression resolveHedgeRef(String namespace, String label) {
        return this.resolveRef(namespace, label, "hedgeRef");
    }

    private Expression resolveRef(String namespace, String label, String tagName) {
        if (namespace == null) {
            this.reportError("GrammarReader.MissingAttribute", (Object)tagName, (Object)"namespace");
            return Expression.nullSet;
        }
        return new ExternalElementExp(this.pool, namespace, label, new LocatorImpl(this.getLocator()));
    }

    protected String localizeMessage(String propertyName, Object[] args) {
        return super.localizeMessage(propertyName, args);
    }
}

