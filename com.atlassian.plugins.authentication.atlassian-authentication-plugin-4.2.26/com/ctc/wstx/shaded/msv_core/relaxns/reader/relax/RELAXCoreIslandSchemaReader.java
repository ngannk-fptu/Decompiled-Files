/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalAttributeExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.ExternalElementExp;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.RELAXIslandSchema;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.AnyOtherElementState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.relax.InterfaceStateEx;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class RELAXCoreIslandSchemaReader
extends RELAXCoreReader
implements IslandSchemaReader {
    private final Map externalElementExps = new HashMap();
    protected final Set pendingAnyOtherElements = new HashSet();

    public RELAXCoreIslandSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool, String expectedTargetnamespace) throws SAXException, ParserConfigurationException {
        super(controller, parserFactory, new StateFactory(), pool, expectedTargetnamespace);
    }

    protected RELAXModule getModule() {
        return this.module;
    }

    protected boolean canHaveOccurs(ExpressionState state) {
        return super.canHaveOccurs(state) || state instanceof AnyOtherElementState;
    }

    public final IslandSchema getSchema() {
        RELAXModule m = this.getResult();
        if (m == null) {
            return null;
        }
        return new RELAXIslandSchema(m, this.pendingAnyOtherElements);
    }

    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (!"http://www.xml.gr.jp/xmlns/relaxCore".equals(tag.namespaceURI)) {
            return null;
        }
        if (tag.localName.equals("anyOtherElement")) {
            return new AnyOtherElementState();
        }
        return super.createExpressionChildState(parent, tag);
    }

    private ExternalElementExp getExtElementExp(String namespace, String label) {
        StringPair name = new StringPair(namespace, label);
        ExternalElementExp exp = (ExternalElementExp)this.externalElementExps.get(name);
        if (exp != null) {
            return exp;
        }
        exp = new ExternalElementExp(this.pool, namespace, label, new LocatorImpl(this.getLocator()));
        this.externalElementExps.put(name, exp);
        return exp;
    }

    protected Expression resolveElementRef(String namespace, String label) {
        if (namespace != null) {
            return this.getExtElementExp(namespace, label);
        }
        return super.resolveElementRef(namespace, label);
    }

    protected Expression resolveHedgeRef(String namespace, String label) {
        if (namespace != null) {
            return this.getExtElementExp(namespace, label);
        }
        return super.resolveHedgeRef(namespace, label);
    }

    protected Expression resolveAttPoolRef(String namespace, String label) {
        if (namespace != null) {
            return new ExternalAttributeExp(this.pool, namespace, label, new LocatorImpl(this.getLocator()));
        }
        return super.resolveAttPoolRef(namespace, label);
    }

    private static class StateFactory
    extends RELAXCoreReader.StateFactory {
        private StateFactory() {
        }

        public State interface_(State parent, StartTagInfo tag) {
            return new InterfaceStateEx();
        }
    }
}

