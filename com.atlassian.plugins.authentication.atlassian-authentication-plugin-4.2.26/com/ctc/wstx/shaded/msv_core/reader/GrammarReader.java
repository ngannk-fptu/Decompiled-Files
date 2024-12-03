/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.AnyURIType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DurationType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NormalizedStringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.Controller;
import com.ctc.wstx.shaded.msv_core.reader.DOMLSInput;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.Uri;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class GrammarReader
extends XMLFilterImpl
implements IDContextProvider2 {
    private Locator locator;
    public final Controller controller;
    public final SAXParserFactory parserFactory;
    public final ExpressionPool pool;
    public static final PrefixResolver basePrefixResolver = new PrefixResolver(){

        public String resolve(String prefix) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            return null;
        }
    };
    public PrefixResolver prefixResolver = basePrefixResolver;
    private InclusionContext pendingIncludes;
    public final BackwardReferenceMap backwardReference = new BackwardReferenceMap();
    private final Map declaredLocations = new HashMap();
    private final Vector backPatchJobs = new Vector();
    private final Vector delayedBackPatchJobs = new Vector();
    public static final String ERR_MALPLACED_ELEMENT = "GrammarReader.MalplacedElement";
    public static final String ERR_CHARACTERS = "GrammarReader.Characters";
    public static final String ERR_DISALLOWED_ATTRIBUTE = "GrammarReader.DisallowedAttribute";
    public static final String ERR_MISSING_ATTRIBUTE = "GrammarReader.MissingAttribute";
    public static final String ERR_BAD_ATTRIBUTE_VALUE = "GrammarReader.BadAttributeValue";
    public static final String ERR_MISSING_ATTRIBUTE_2 = "GrammarReader.MissingAttribute.2";
    public static final String ERR_CONFLICTING_ATTRIBUTES = "GrammarReader.ConflictingAttribute";
    public static final String ERR_RECURSIVE_INCLUDE = "GrammarReader.RecursiveInclude";
    public static final String ERR_FRAGMENT_IDENTIFIER = "GrammarReader.FragmentIdentifier";
    public static final String ERR_UNDEFINED_DATATYPE = "GrammarReader.UndefinedDataType";
    public static final String ERR_DATATYPE_ALREADY_DEFINED = "GrammarReader.DataTypeAlreadyDefined";
    public static final String ERR_MISSING_CHILD_EXPRESSION = "GrammarReader.Abstract.MissingChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_EXPRESSION = "GrammarReader.Abstract.MoreThanOneChildExpression";
    public static final String ERR_MORE_THAN_ONE_CHILD_TYPE = "GrammarReader.Abstract.MoreThanOneChildType";
    public static final String ERR_MISSING_CHILD_TYPE = "GrammarReader.Abstract.MissingChildType";
    public static final String ERR_ILLEGAL_FINAL_VALUE = "GrammarReader.IllegalFinalValue";
    public static final String ERR_RUNAWAY_EXPRESSION = "GrammarReader.Abstract.RunAwayExpression";
    public static final String ERR_MISSING_TOPLEVEL = "GrammarReader.Abstract.MissingTopLevel";
    public static final String WRN_MAYBE_WRONG_NAMESPACE = "GrammarReader.Warning.MaybeWrongNamespace";
    public static final String WRN_DEPRECATED_TYPENAME = "GrammarReader.Warning.DeprecatedTypeName";
    public static final String ERR_BAD_TYPE = "GrammarReader.BadType";
    public static final String ERR_RECURSIVE_DATATYPE = "GrammarReader.RecursiveDatatypeDefinition";

    protected static SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    protected GrammarReader(GrammarReaderController _controller, SAXParserFactory parserFactory, ExpressionPool pool, State initialState) {
        this.controller = new Controller(_controller);
        this.parserFactory = parserFactory;
        if (parserFactory != null && !parserFactory.isNamespaceAware()) {
            throw new IllegalArgumentException("parser factory must be namespace-aware");
        }
        this.pool = pool;
        this.pushState(initialState, null, null);
    }

    public abstract Grammar getResultAsGrammar();

    protected abstract boolean isGrammarElement(StartTagInfo var1);

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.prefixResolver = new ChainPrefixResolver(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        this.prefixResolver = ((ChainPrefixResolver)this.prefixResolver).previous;
        super.endPrefixMapping(prefix);
    }

    public Iterator iterateInscopeNamespaces() {
        return new Iterator(){
            private PrefixResolver resolver;
            {
                this.resolver = this.proceed(GrammarReader.this.prefixResolver);
            }

            public Object next() {
                final ChainPrefixResolver cpr = (ChainPrefixResolver)this.resolver;
                this.resolver = this.proceed(cpr.previous);
                return new Map.Entry(){

                    public Object getKey() {
                        return cpr.prefix;
                    }

                    public Object getValue() {
                        return cpr.uri;
                    }

                    public Object setValue(Object o) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public boolean hasNext() {
                return this.resolver instanceof ChainPrefixResolver;
            }

            private PrefixResolver proceed(PrefixResolver resolver) {
                while (resolver instanceof ChainPrefixResolver) {
                    ChainPrefixResolver cpr = (ChainPrefixResolver)resolver;
                    if (GrammarReader.this.resolveNamespacePrefix(cpr.prefix) == cpr.uri) {
                        return resolver;
                    }
                    resolver = cpr.previous;
                }
                return resolver;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String[] splitQName(String qName) {
        int idx = qName.indexOf(58);
        if (idx < 0) {
            String ns = this.prefixResolver.resolve("");
            if (ns == null) {
                ns = "";
            }
            return new String[]{ns, qName, qName};
        }
        String uri = this.prefixResolver.resolve(qName.substring(0, idx));
        if (uri == null) {
            return null;
        }
        return new String[]{uri, qName.substring(idx + 1), qName};
    }

    protected Expression interceptExpression(State state, Expression exp) {
        return exp;
    }

    public XSDatatype getBackwardCompatibleType(String typeName) {
        BuiltinAtomicType dt = null;
        if (typeName.equals("uriReference")) {
            dt = AnyURIType.theInstance;
        } else if (typeName.equals("number")) {
            dt = NumberType.theInstance;
        } else if (typeName.equals("timeDuration")) {
            dt = DurationType.theInstance;
        } else if (typeName.equals("CDATA")) {
            dt = NormalizedStringType.theInstance;
        } else if (typeName.equals("year")) {
            dt = GYearType.theInstance;
        } else if (typeName.equals("yearMonth")) {
            dt = GYearMonthType.theInstance;
        } else if (typeName.equals("month")) {
            dt = GMonthType.theInstance;
        } else if (typeName.equals("monthDay")) {
            dt = GMonthDayType.theInstance;
        } else if (typeName.equals("day")) {
            dt = GDayType.theInstance;
        }
        if (dt != null) {
            this.reportWarning(WRN_DEPRECATED_TYPENAME, typeName, dt.displayName());
        }
        return dt;
    }

    private void pushInclusionContext() {
        this.pendingIncludes = new InclusionContext(this.prefixResolver, this.getLocator(), this.getLocator().getSystemId(), this.pendingIncludes);
        this.prefixResolver = basePrefixResolver;
        this.setLocator(null);
    }

    private void popInclusionContext() {
        this.prefixResolver = this.pendingIncludes.prefixResolver;
        this.setLocator(this.pendingIncludes.locator);
        this.pendingIncludes = this.pendingIncludes.previousContext;
    }

    public final InputSource resolveLocation(State sourceState, String uri) throws AbortException {
        try {
            uri = this.combineURI(sourceState.getBaseURI(), uri);
            InputSource source = this.controller.resolveEntity(null, uri);
            if (source == null) {
                return new InputSource(uri);
            }
            return source;
        }
        catch (IOException e) {
            this.controller.error(e, this.getLocator());
        }
        catch (SAXException e) {
            this.controller.error(e, this.getLocator());
        }
        throw AbortException.theInstance;
    }

    public final String combineURI(String baseURI, String relativeURI) {
        return Uri.resolve(baseURI, relativeURI);
    }

    public final String combineURL(String baseURI, String relativeURI) {
        return Uri.resolve(baseURI, relativeURI);
    }

    public void switchSource(State sourceState, String url, State newState) throws AbortException {
        if (url.indexOf(35) >= 0) {
            this.reportError(ERR_FRAGMENT_IDENTIFIER, (Object)url);
            throw AbortException.theInstance;
        }
        this.switchSource(this.resolveLocation(sourceState, url), newState);
    }

    public void switchSource(InputSource source, State newState) {
        this.switchSource(new SAXSource(source), newState);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void switchSource(Source source, State newState) {
        String url = source.getSystemId();
        InclusionContext ic = this.pendingIncludes;
        while (ic != null) {
            if (ic.systemId != null && ic.systemId.equals(url)) {
                String s = "";
                InclusionContext i = this.pendingIncludes;
                while (i != ic) {
                    s = i.systemId + " > " + s;
                    i = i.previousContext;
                }
                s = url + " > " + s + url;
                this.reportError(ERR_RECURSIVE_INCLUDE, (Object)s);
                return;
            }
            ic = ic.previousContext;
        }
        this.pushInclusionContext();
        State currentState = this.getCurrentState();
        try {
            this.pushState(newState, null, null);
            try {
                this.parse(source);
            }
            catch (TransformerConfigurationException e) {
                this.controller.error("transform error", e);
            }
            catch (TransformerException e) {
                this.controller.error("transform error", e);
            }
        }
        finally {
            super.setContentHandler(currentState);
            this.popInclusionContext();
        }
    }

    public final void parse(String source) {
        this._parse(source, null);
    }

    public final void parse(InputSource source) {
        this._parse(source, null);
    }

    public void parse(Source source) throws TransformerConfigurationException, TransformerException {
        InputSource saxSource = SAXSource.sourceToInputSource(source);
        if (saxSource != null) {
            this.parse(saxSource);
        } else {
            LocatorImpl sourceLocator = new LocatorImpl();
            sourceLocator.setSystemId(source.getSystemId());
            this.setLocator(sourceLocator);
            TransformerFactory factory = TransformerFactory.newInstance();
            SAXResult result = new SAXResult(this);
            factory.newTransformer().transform(source, result);
        }
    }

    public final void _parse(Object source, Locator errorSource) {
        try {
            XMLReader reader = this.parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(this);
            reader.setErrorHandler(this.controller);
            reader.setEntityResolver(this.controller);
            if (source instanceof InputSource) {
                reader.parse((InputSource)source);
            }
            if (source instanceof String) {
                reader.parse((String)source);
            }
        }
        catch (ParserConfigurationException e) {
            this.controller.error(e, errorSource);
        }
        catch (IOException e) {
            this.controller.error(e, errorSource);
        }
        catch (SAXParseException e) {
            this.controller.error(e);
        }
        catch (SAXException e) {
            this.controller.error(e, errorSource);
        }
    }

    public void setDeclaredLocationOf(Object o) {
        this.declaredLocations.put(o, new LocatorImpl(this.getLocator()));
    }

    public Locator getDeclaredLocationOf(Object o) {
        return (Locator)this.declaredLocations.get(o);
    }

    public void detectUndefinedOnes(ReferenceContainer container, String errMsg) {
        Iterator itr = container.iterator();
        while (itr.hasNext()) {
            ReferenceExp ref = (ReferenceExp)itr.next();
            if (ref.isDefined()) continue;
            this.reportError(this.backwardReference.getReferer(ref), errMsg, new Object[]{ref.name});
            ref.exp = Expression.nullSet;
        }
    }

    public void pushState(State newState, State parentState, StartTagInfo startTag) {
        super.setContentHandler(newState);
        newState.init(this, parentState, startTag);
    }

    public void popState() {
        State currentState = this.getCurrentState();
        if (currentState.parentState != null) {
            super.setContentHandler(currentState.parentState);
        } else {
            super.setContentHandler(new DefaultHandler());
        }
    }

    public final State getCurrentState() {
        return (State)super.getContentHandler();
    }

    public abstract State createExpressionChildState(State var1, StartTagInfo var2);

    public void setDocumentLocator(Locator loc) {
        super.setDocumentLocator(loc);
        this.setLocator(loc);
    }

    public String resolveNamespacePrefix(String prefix) {
        return this.prefixResolver.resolve(prefix);
    }

    public boolean isUnparsedEntity(String entityName) {
        return true;
    }

    public boolean isNotation(String notationName) {
        return true;
    }

    public String getBaseUri() {
        return this.getCurrentState().getBaseURI();
    }

    public final void onID(Datatype dt, StringToken token) {
    }

    public final void addBackPatchJob(BackPatch job) {
        this.backPatchJobs.add(job);
    }

    public final void addBackPatchJob(XSDatatypeExp job) {
        this.delayedBackPatchJobs.add(job);
    }

    public final void runBackPatchJob() {
        Locator oldLoc = this.getLocator();
        this.runBackPatchJob(this.backPatchJobs);
        this.runBackPatchJob(this.delayedBackPatchJobs);
        this.setLocator(oldLoc);
    }

    private final void runBackPatchJob(Vector vec) {
        for (BackPatch job : vec) {
            this.setLocator(job.getOwnerState().getLocation());
            job.patch();
        }
    }

    public final void reportError(String propertyName) {
        this.reportError(propertyName, null, null, null);
    }

    public final void reportError(String propertyName, Object arg1) {
        this.reportError(propertyName, new Object[]{arg1}, null, null);
    }

    public final void reportError(String propertyName, Object arg1, Object arg2) {
        this.reportError(propertyName, new Object[]{arg1, arg2}, null, null);
    }

    public final void reportError(String propertyName, Object arg1, Object arg2, Object arg3) {
        this.reportError(propertyName, new Object[]{arg1, arg2, arg3}, null, null);
    }

    public final void reportError(Exception nestedException, String propertyName) {
        this.reportError(propertyName, null, nestedException, null);
    }

    public final void reportError(Exception nestedException, String propertyName, Object arg1) {
        this.reportError(propertyName, new Object[]{arg1}, nestedException, null);
    }

    public final void reportError(Locator[] locs, String propertyName, Object[] args) {
        this.reportError(propertyName, args, null, locs);
    }

    public final void reportWarning(String propertyName) {
        this.reportWarning(propertyName, null, null);
    }

    public final void reportWarning(String propertyName, Object arg1) {
        this.reportWarning(propertyName, new Object[]{arg1}, null);
    }

    public final void reportWarning(String propertyName, Object arg1, Object arg2) {
        this.reportWarning(propertyName, new Object[]{arg1, arg2}, null);
    }

    private Locator[] prepareLocation(Locator[] param) {
        if (param != null) {
            int cnt = 0;
            for (int i = 0; i < param.length; ++i) {
                if (param[i] == null) continue;
                ++cnt;
            }
            if (param.length == cnt) {
                return param;
            }
            Locator[] locs = new Locator[cnt];
            cnt = 0;
            for (int i = 0; i < param.length; ++i) {
                if (param[i] == null) continue;
                locs[cnt++] = param[i];
            }
            return locs;
        }
        if (this.getLocator() != null) {
            return new Locator[]{this.getLocator()};
        }
        return new Locator[0];
    }

    public final void reportError(String propertyName, Object[] args, Exception nestedException, Locator[] errorLocations) {
        this.controller.error(this.prepareLocation(errorLocations), this.localizeMessage(propertyName, args), nestedException);
    }

    public final void reportWarning(String propertyName, Object[] args, Locator[] locations) {
        this.controller.warning(this.prepareLocation(locations), this.localizeMessage(propertyName, args));
    }

    public static Source inputSourceFromLSInput(LSInput input) {
        Source source;
        if (input instanceof DOMLSInput) {
            DOMLSInput domLSInput = (DOMLSInput)((Object)input);
            source = new DOMSource(domLSInput.getElement());
        } else {
            InputSource inputSource = new InputSource();
            if (input.getCharacterStream() != null) {
                inputSource.setCharacterStream(input.getCharacterStream());
            }
            if (input.getByteStream() != null) {
                inputSource.setByteStream(input.getByteStream());
            }
            if (input.getStringData() != null) {
                inputSource.setCharacterStream(new StringReader(input.getStringData()));
            }
            if (input.getPublicId() != null) {
                inputSource.setPublicId(input.getPublicId());
            }
            source = new SAXSource(inputSource);
        }
        if (input.getSystemId() != null) {
            source.setSystemId(input.getSystemId());
        }
        return source;
    }

    protected abstract String localizeMessage(String var1, Object[] var2);

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getLocator() {
        return this.locator;
    }

    public static interface BackPatch {
        public void patch();

        public State getOwnerState();
    }

    public class BackwardReferenceMap {
        private final Map impl = new HashMap();

        public void memorizeLink(Object target) {
            ArrayList list;
            if (this.impl.containsKey(target)) {
                list = (ArrayList)this.impl.get(target);
            } else {
                list = new ArrayList();
                this.impl.put(target, list);
            }
            list.add(new LocatorImpl(GrammarReader.this.getLocator()));
        }

        public Locator[] getReferer(Object target) {
            if (this.impl.containsKey(target)) {
                ArrayList lst = (ArrayList)this.impl.get(target);
                Locator[] locs = new Locator[lst.size()];
                lst.toArray(locs);
                return locs;
            }
            return null;
        }
    }

    private class InclusionContext {
        final PrefixResolver prefixResolver;
        final Locator locator;
        final String systemId;
        final InclusionContext previousContext;

        InclusionContext(PrefixResolver prefix, Locator loc, String sysId, InclusionContext prev) {
            this.prefixResolver = prefix;
            this.locator = loc;
            this.systemId = sysId;
            this.previousContext = prev;
        }
    }

    public class ChainPrefixResolver
    implements PrefixResolver {
        public final PrefixResolver previous;
        public final String prefix;
        public final String uri;

        public ChainPrefixResolver(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
            this.previous = GrammarReader.this.prefixResolver;
        }

        public String resolve(String p) {
            if (p.equals(this.prefix)) {
                return this.uri;
            }
            return this.previous.resolve(p);
        }
    }

    public static interface PrefixResolver {
        public String resolve(String var1);
    }
}

