/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.EmptyStringType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.RunAwayExpressionChecker;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDVocabulary;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.AttPoolRefState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.AttPoolState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.AttributeState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.DivInModuleState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleWithHedgeState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleWithTypeState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.HedgeRuleState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.IncludeModuleState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InlineElementState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InlineTagState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InterfaceState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.MixedState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RootModuleState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.TagState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.checker.DblAttrConstraintChecker;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.checker.ExportedHedgeRuleChecker;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.checker.IdAbuseChecker;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.RELAXCoreFactoryImpl;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

public class RELAXCoreReader
extends RELAXReader
implements XSDatatypeResolver {
    protected static Schema relaxCoreSchema4Schema = null;
    protected RELAXModule module;
    protected final ReferenceContainer combinedAttPools = new ReferenceContainer(){

        protected ReferenceExp createReference(String name) {
            return new ReferenceExp(name);
        }
    };
    private final Map userDefinedTypes = new HashMap();
    public static final String ERR_NAMESPACE_NOT_SUPPROTED = "RELAXReader.NamespaceNotSupported";
    public static final String ERR_INCONSISTENT_TARGET_NAMESPACE = "RELAXReader.InconsistentTargetNamespace";
    public static final String ERR_MISSING_TARGET_NAMESPACE = "RELAXReader.MissingTargetNamespace";
    public static final String ERR_MULTIPLE_TAG_DECLARATIONS = "RELAXReader.MultipleTagDeclarations";
    public static final String ERR_MORE_THAN_ONE_INLINE_TAG = "RELAXReader.MoreThanOneInlineTag";
    public static final String ERR_MULTIPLE_ATTPOOL_DECLARATIONS = "RELAXReader.MultipleAttPoolDeclarations";
    public static final String ERR_UNDEFINED_ELEMENTRULE = "RELAXReader.UndefinedElementRule";
    public static final String ERR_UNDEFINED_HEDGERULE = "RELAXReader.UndefinedHedgeRule";
    public static final String ERR_UNDEFINED_TAG = "RELAXReader.UndefinedTag";
    public static final String ERR_UNDEFINED_ATTPOOL = "RELAXReader.UndefinedAttPool";
    public static final String ERR_LABEL_COLLISION = "RELAXReader.LabelCollision";
    public static final String ERR_ROLE_COLLISION = "RELAXReader.RoleCollision";
    public static final String WRN_NO_EXPROTED_LABEL = "RELAXReader.NoExportedLabel";
    public static final String ERR_EXPROTED_HEDGERULE_CONSTRAINT = "RELAXReader.ExportedHedgeRuleConstraint";
    public static final String ERR_MULTIPLE_ATTRIBUTE_CONSTRAINT = "RELAXReader.MultipleAttributeConstraint";
    public static final String ERR_ID_ABUSE = "RELAXReader.IdAbuse";
    public static final String ERR_ID_ABUSE_1 = "RELAXReader.IdAbuse.1";
    public static final String WRN_ILLEGAL_RELAXCORE_VERSION = "RELAXReader.Warning.IllegalRelaxCoreVersion";

    public static RELAXModule parse(String moduleURL, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXCoreReader reader = new RELAXCoreReader(controller, factory, pool);
        reader.parse(moduleURL);
        return reader.getResult();
    }

    public static RELAXModule parse(InputSource module, SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool) {
        RELAXCoreReader reader = new RELAXCoreReader(controller, factory, pool);
        reader.parse(module);
        return reader.getResult();
    }

    public static Schema getRELAXCoreSchema4Schema() {
        if (relaxCoreSchema4Schema == null) {
            try {
                relaxCoreSchema4Schema = new RELAXCoreFactoryImpl().compileSchema(RELAXCoreReader.class.getResourceAsStream("relaxCore.rlx"));
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new Error("unable to load schema-for-schema for RELAX Core");
            }
        }
        return relaxCoreSchema4Schema;
    }

    public RELAXCoreReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        this(controller, parserFactory, new StateFactory(), pool, null);
    }

    public RELAXCoreReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool, String expectedTargetNamespace) {
        super(controller, parserFactory, stateFactory, pool, new RootModuleState(expectedTargetNamespace));
    }

    public final RELAXModule getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.module;
    }

    public final Grammar getResultAsGrammar() {
        return this.getResult();
    }

    protected boolean isGrammarElement(StartTagInfo tag) {
        if (!"http://www.xml.gr.jp/xmlns/relaxCore".equals(tag.namespaceURI)) {
            return false;
        }
        return !tag.localName.equals("annotation");
    }

    public final void addUserDefinedType(XSDatatypeExp exp) {
        this.userDefinedTypes.put(exp.name, exp);
    }

    public XSDatatypeExp resolveXSDatatype(String typeName) {
        try {
            XSDatatypeExp e = (XSDatatypeExp)this.userDefinedTypes.get(typeName);
            if (e != null) {
                return e;
            }
            return new XSDatatypeExp(DatatypeFactory.getTypeByName(typeName), this.pool);
        }
        catch (DatatypeException e) {
            XSDatatype dt = this.getBackwardCompatibleType(typeName);
            if (typeName.equals("none")) {
                dt = NoneType.theInstance;
            }
            if (typeName.equals("emptyString")) {
                dt = EmptyStringType.theInstance;
            }
            if (dt == null) {
                this.reportError("GrammarReader.UndefinedDataType", (Object)typeName);
                dt = NoneType.theInstance;
            }
            return new XSDatatypeExp(dt, this.pool);
        }
    }

    protected final StateFactory getStateFactory() {
        return (StateFactory)this.sfactory;
    }

    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (!"http://www.xml.gr.jp/xmlns/relaxCore".equals(tag.namespaceURI)) {
            return null;
        }
        if (tag.localName.equals("mixed")) {
            return this.getStateFactory().mixed(parent, tag);
        }
        if (tag.localName.equals("element")) {
            return this.getStateFactory().element(parent, tag);
        }
        return super.createExpressionChildState(parent, tag);
    }

    protected boolean canHaveOccurs(ExpressionState state) {
        return super.canHaveOccurs(state) || state instanceof InlineElementState;
    }

    protected Expression resolveElementRef(String namespace, String label) {
        if (namespace != null) {
            this.reportError(ERR_NAMESPACE_NOT_SUPPROTED);
            return Expression.nullSet;
        }
        ElementRules exp = this.module.elementRules.getOrCreate(label);
        this.backwardReference.memorizeLink(exp);
        return exp;
    }

    protected Expression resolveHedgeRef(String namespace, String label) {
        if (namespace != null) {
            this.reportError(ERR_NAMESPACE_NOT_SUPPROTED);
            return Expression.nullSet;
        }
        HedgeRules exp = this.module.hedgeRules.getOrCreate(label);
        this.backwardReference.memorizeLink(exp);
        return exp;
    }

    protected Expression resolveAttPoolRef(String namespace, String role) {
        if (namespace != null) {
            this.reportError(ERR_NAMESPACE_NOT_SUPPROTED);
            return Expression.nullSet;
        }
        AttPoolClause c = this.module.attPools.getOrCreate(role);
        this.backwardReference.memorizeLink(c);
        return c;
    }

    protected void wrapUp() {
        this.runBackPatchJob();
        Iterator itr = this.userDefinedTypes.entrySet().iterator();
        while (itr.hasNext()) {
            XSDatatypeExp e = (XSDatatypeExp)itr.next().getValue();
            this.module.datatypes.add(e.getCreatedType());
        }
        ReferenceExp[] combines = this.combinedAttPools.getAll();
        for (int i = 0; i < combines.length; ++i) {
            AttPoolClause ac = this.module.attPools.get(combines[i].name);
            if (ac != null) {
                if (ac.exp == null) {
                    ac.exp = Expression.epsilon;
                }
                ac.exp = this.pool.createSequence(ac.exp, combines[i].exp);
                continue;
            }
            TagClause tc = this.module.tags.get(combines[i].name);
            if (tc != null && tc.exp != null) {
                tc.exp = this.pool.createSequence(tc.exp, combines[i].exp);
                continue;
            }
            ac = this.module.attPools.getOrCreate(combines[i].name);
            ac.exp = combines[i].exp;
        }
        this.detectCollision(this.module.tags, this.module.attPools, ERR_ROLE_COLLISION);
        this.detectUndefinedOnes(this.module.elementRules, ERR_UNDEFINED_ELEMENTRULE);
        this.detectUndefinedOnes(this.module.hedgeRules, ERR_UNDEFINED_HEDGERULE);
        this.detectUndefinedOnes(this.module.tags, ERR_UNDEFINED_TAG);
        this.detectUndefinedOnes(this.module.attPools, ERR_UNDEFINED_ATTPOOL);
        this.detectCollision(this.module.elementRules, this.module.hedgeRules, ERR_LABEL_COLLISION);
        this.detectDoubleAttributeConstraints(this.module);
        IdAbuseChecker.check(this, this.module);
        Expression exp = this.pool.createChoice(this.choiceOfExported(this.module.elementRules), this.choiceOfExported(this.module.hedgeRules));
        if (exp == Expression.nullSet) {
            this.reportWarning(WRN_NO_EXPROTED_LABEL);
        }
        this.module.topLevel = exp;
        RunAwayExpressionChecker.check(this, this.module.topLevel);
        Iterator jtr = this.module.hedgeRules.iterator();
        while (jtr.hasNext()) {
            ExportedHedgeRuleChecker ehrc;
            HedgeRules hr = (HedgeRules)jtr.next();
            if (!hr.exported || hr.visit(ehrc = new ExportedHedgeRuleChecker(this.module))) continue;
            String dependency = "";
            for (int i = 0; i < ehrc.errorSnapshot.length - 1; ++i) {
                dependency = dependency + ehrc.errorSnapshot[i].name + " > ";
            }
            dependency = dependency + ehrc.errorSnapshot[ehrc.errorSnapshot.length - 1].name;
            this.reportError(ERR_EXPROTED_HEDGERULE_CONSTRAINT, (Object)dependency);
        }
    }

    private Expression choiceOfExported(ReferenceContainer con) {
        Iterator itr = con.iterator();
        Expression r = Expression.nullSet;
        while (itr.hasNext()) {
            Exportable ex = (Exportable)itr.next();
            if (!ex.isExported()) continue;
            r = this.pool.createChoice(r, (Expression)((Object)ex));
        }
        return r;
    }

    private void detectDoubleAttributeConstraints(RELAXModule module) {
        DblAttrConstraintChecker checker = new DblAttrConstraintChecker();
        Iterator itr = module.tags.iterator();
        while (itr.hasNext()) {
            checker.check((TagClause)itr.next(), this);
        }
    }

    private void detectCollision(ReferenceContainer col1, ReferenceContainer col2, String errMsg) {
        Iterator itr = col1.iterator();
        while (itr.hasNext()) {
            ReferenceExp r1 = (ReferenceExp)itr.next();
            ReferenceExp r2 = col2._get(r1.name);
            if (r2 == null || r1.exp == null || r2.exp == null) continue;
            this.reportError(new Locator[]{this.getDeclaredLocationOf(r1), this.getDeclaredLocationOf(r2)}, errMsg, new Object[]{r1.name});
        }
    }

    protected String localizeMessage(String propertyName, Object[] args) {
        return super.localizeMessage(propertyName, args);
    }

    public static class StateFactory
    extends RELAXReader.StateFactory {
        protected XSDVocabulary vocabulary = new XSDVocabulary();

        protected State mixed(State parent, StartTagInfo tag) {
            return new MixedState();
        }

        protected State element(State parent, StartTagInfo tag) {
            return new InlineElementState();
        }

        protected State attribute(State parent, StartTagInfo tag) {
            return new AttributeState();
        }

        protected State refRole(State parent, StartTagInfo tag) {
            return new AttPoolRefState();
        }

        protected State divInModule(State parent, StartTagInfo tag) {
            return new DivInModuleState();
        }

        protected State hedgeRule(State parent, StartTagInfo tag) {
            return new HedgeRuleState();
        }

        protected State tag(State parent, StartTagInfo tag) {
            return new TagState();
        }

        protected State tagInline(State parent, StartTagInfo tag) {
            return new InlineTagState();
        }

        protected State attPool(State parent, StartTagInfo tag) {
            return new AttPoolState();
        }

        protected State include(State parent, StartTagInfo tag) {
            return new IncludeModuleState();
        }

        protected State interface_(State parent, StartTagInfo tag) {
            return new InterfaceState();
        }

        protected State elementRule(State parent, StartTagInfo tag) {
            if (tag.containsAttribute("type")) {
                return new ElementRuleWithTypeState();
            }
            return new ElementRuleWithHedgeState();
        }

        protected State simpleType(State parent, StartTagInfo tag) {
            return this.vocabulary.createTopLevelReaderState(tag);
        }
    }
}

