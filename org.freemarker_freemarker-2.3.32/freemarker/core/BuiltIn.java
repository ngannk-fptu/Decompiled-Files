/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInsForCallables;
import freemarker.core.BuiltInsForDates;
import freemarker.core.BuiltInsForExistenceHandling;
import freemarker.core.BuiltInsForHashes;
import freemarker.core.BuiltInsForLoopVariables;
import freemarker.core.BuiltInsForMarkupOutputs;
import freemarker.core.BuiltInsForMultipleTypes;
import freemarker.core.BuiltInsForNodes;
import freemarker.core.BuiltInsForNumbers;
import freemarker.core.BuiltInsForOutputFormatRelated;
import freemarker.core.BuiltInsForSequences;
import freemarker.core.BuiltInsForStringsBasic;
import freemarker.core.BuiltInsForStringsEncoding;
import freemarker.core.BuiltInsForStringsMisc;
import freemarker.core.BuiltInsForStringsRegexp;
import freemarker.core.BuiltInsWithLazyConditionals;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.FMParserTokenManager;
import freemarker.core.ICIChainMember;
import freemarker.core.Interpret;
import freemarker.core.NewBI;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.Token;
import freemarker.core._CoreStringUtils;
import freemarker.core._MessageUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

abstract class BuiltIn
extends Expression
implements Cloneable {
    protected Expression target;
    protected String key;
    static final Set<String> CAMEL_CASE_NAMES = new TreeSet<String>();
    static final Set<String> SNAKE_CASE_NAMES = new TreeSet<String>();
    static final int NUMBER_OF_BIS = 296;
    static final HashMap<String, BuiltIn> BUILT_INS_BY_NAME = new HashMap(445, 1.0f);
    static final String BI_NAME_SNAKE_CASE_WITH_ARGS = "with_args";
    static final String BI_NAME_CAMEL_CASE_WITH_ARGS = "withArgs";
    static final String BI_NAME_SNAKE_CASE_WITH_ARGS_LAST = "with_args_last";
    static final String BI_NAME_CAMEL_CASE_WITH_ARGS_LAST = "withArgsLast";

    BuiltIn() {
    }

    private static void putBI(String name, BuiltIn bi) {
        BUILT_INS_BY_NAME.put(name, bi);
        SNAKE_CASE_NAMES.add(name);
        CAMEL_CASE_NAMES.add(name);
    }

    private static void putBI(String nameSnakeCase, String nameCamelCase, BuiltIn bi) {
        BUILT_INS_BY_NAME.put(nameSnakeCase, bi);
        BUILT_INS_BY_NAME.put(nameCamelCase, bi);
        SNAKE_CASE_NAMES.add(nameSnakeCase);
        CAMEL_CASE_NAMES.add(nameCamelCase);
    }

    static BuiltIn newBuiltIn(int incompatibleImprovements, Expression target, Token keyTk, FMParserTokenManager tokenManager) throws ParseException {
        String key = keyTk.image;
        BuiltIn bi = BUILT_INS_BY_NAME.get(key);
        if (bi == null) {
            StringBuilder buf = new StringBuilder("Unknown built-in: ").append(StringUtil.jQuote(key)).append(". ");
            buf.append("Help (latest version): https://freemarker.apache.org/docs/ref_builtins.html; you're using FreeMarker ").append(Configuration.getVersion()).append(".\nThe alphabetical list of built-ins:");
            ArrayList<String> names = new ArrayList<String>(BUILT_INS_BY_NAME.keySet().size());
            names.addAll(BUILT_INS_BY_NAME.keySet());
            Collections.sort(names);
            char lastLetter = '\u0000';
            int namingConvention = tokenManager.namingConvention;
            int shownNamingConvention = namingConvention != 10 ? namingConvention : 11;
            boolean first = true;
            for (String correctName : names) {
                int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
                if (!(shownNamingConvention == 12 ? correctNameNamingConvetion != 11 : correctNameNamingConvetion != 12)) continue;
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                char firstChar = correctName.charAt(0);
                if (firstChar != lastLetter) {
                    lastLetter = firstChar;
                    buf.append('\n');
                }
                buf.append(correctName);
            }
            throw new ParseException(buf.toString(), null, keyTk);
        }
        while (bi instanceof ICIChainMember && incompatibleImprovements < ((ICIChainMember)((Object)bi)).getMinimumICIVersion()) {
            bi = (BuiltIn)((ICIChainMember)((Object)bi)).getPreviousICIChainMember();
        }
        try {
            bi = (BuiltIn)bi.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        bi.key = key;
        bi.setTarget(target);
        return bi;
    }

    protected void setTarget(Expression target) {
        this.target = target;
    }

    @Override
    public String getCanonicalForm() {
        return this.target.getCanonicalForm() + "?" + this.key;
    }

    @Override
    String getNodeTypeSymbol() {
        return "?" + this.key;
    }

    @Override
    boolean isLiteral() {
        return false;
    }

    protected final void checkMethodArgCount(List args, int expectedCnt) throws TemplateModelException {
        this.checkMethodArgCount(args.size(), expectedCnt);
    }

    protected final void checkMethodArgCount(int argCnt, int expectedCnt) throws TemplateModelException {
        if (argCnt != expectedCnt) {
            throw _MessageUtil.newArgCntError("?" + this.key, argCnt, expectedCnt);
        }
    }

    protected final void checkMethodArgCount(List args, int minCnt, int maxCnt) throws TemplateModelException {
        this.checkMethodArgCount(args.size(), minCnt, maxCnt);
    }

    protected final void checkMethodArgCount(int argCnt, int minCnt, int maxCnt) throws TemplateModelException {
        if (argCnt < minCnt || argCnt > maxCnt) {
            throw _MessageUtil.newArgCntError("?" + this.key, argCnt, minCnt, maxCnt);
        }
    }

    protected final String getOptStringMethodArg(List args, int argIdx) throws TemplateModelException {
        return args.size() > argIdx ? this.getStringMethodArg(args, argIdx) : null;
    }

    protected final String getStringMethodArg(List args, int argIdx) throws TemplateModelException {
        TemplateModel arg = (TemplateModel)args.get(argIdx);
        if (!(arg instanceof TemplateScalarModel)) {
            throw _MessageUtil.newMethodArgMustBeStringException("?" + this.key, argIdx, arg);
        }
        return EvalUtil.modelToString((TemplateScalarModel)arg, null, null);
    }

    protected final Number getOptNumberMethodArg(List args, int argIdx) throws TemplateModelException {
        return args.size() > argIdx ? (Number)this.getNumberMethodArg(args, argIdx) : (Number)null;
    }

    protected final Number getNumberMethodArg(List args, int argIdx) throws TemplateModelException {
        TemplateModel arg = (TemplateModel)args.get(argIdx);
        if (!(arg instanceof TemplateNumberModel)) {
            throw _MessageUtil.newMethodArgMustBeNumberException("?" + this.key, argIdx, arg);
        }
        return EvalUtil.modelToNumber((TemplateNumberModel)arg, null);
    }

    protected final TemplateModelException newMethodArgInvalidValueException(int argIdx, Object[] details) {
        return _MessageUtil.newMethodArgInvalidValueException("?" + this.key, argIdx, details);
    }

    protected final TemplateModelException newMethodArgsInvalidValueException(Object[] details) {
        return _MessageUtil.newMethodArgsInvalidValueException("?" + this.key, details);
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        try {
            BuiltIn clone = (BuiltIn)this.clone();
            clone.target = this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Internal error: " + e);
        }
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.target;
            }
            case 1: {
                return this.key;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.LEFT_HAND_OPERAND;
            }
            case 1: {
                return ParameterRole.RIGHT_HAND_OPERAND;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    static {
        BuiltIn.putBI("abs", new BuiltInsForNumbers.absBI());
        BuiltIn.putBI("absolute_template_name", "absoluteTemplateName", new BuiltInsForStringsMisc.absolute_template_nameBI());
        BuiltIn.putBI("ancestors", new BuiltInsForNodes.ancestorsBI());
        BuiltIn.putBI("api", new BuiltInsForMultipleTypes.apiBI());
        BuiltIn.putBI("boolean", new BuiltInsForStringsMisc.booleanBI());
        BuiltIn.putBI("byte", new BuiltInsForNumbers.byteBI());
        BuiltIn.putBI("c", new BuiltInsForMultipleTypes.cBI());
        BuiltIn.putBI("cn", new BuiltInsForMultipleTypes.cnBI());
        BuiltIn.putBI("cap_first", "capFirst", new BuiltInsForStringsBasic.cap_firstBI());
        BuiltIn.putBI("capitalize", new BuiltInsForStringsBasic.capitalizeBI());
        BuiltIn.putBI("ceiling", new BuiltInsForNumbers.ceilingBI());
        BuiltIn.putBI("children", new BuiltInsForNodes.childrenBI());
        BuiltIn.putBI("chop_linebreak", "chopLinebreak", new BuiltInsForStringsBasic.chop_linebreakBI());
        BuiltIn.putBI("contains", new BuiltInsForStringsBasic.containsBI());
        BuiltIn.putBI("date", new BuiltInsForMultipleTypes.dateBI(2));
        BuiltIn.putBI("date_if_unknown", "dateIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(2));
        BuiltIn.putBI("datetime", new BuiltInsForMultipleTypes.dateBI(3));
        BuiltIn.putBI("datetime_if_unknown", "datetimeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(3));
        BuiltIn.putBI("default", new BuiltInsForExistenceHandling.defaultBI());
        BuiltIn.putBI("double", new BuiltInsForNumbers.doubleBI());
        BuiltIn.putBI("drop_while", "dropWhile", new BuiltInsForSequences.drop_whileBI());
        BuiltIn.putBI("ends_with", "endsWith", new BuiltInsForStringsBasic.ends_withBI());
        BuiltIn.putBI("ensure_ends_with", "ensureEndsWith", new BuiltInsForStringsBasic.ensure_ends_withBI());
        BuiltIn.putBI("ensure_starts_with", "ensureStartsWith", new BuiltInsForStringsBasic.ensure_starts_withBI());
        BuiltIn.putBI("esc", new BuiltInsForOutputFormatRelated.escBI());
        BuiltIn.putBI("eval", new BuiltInsForStringsMisc.evalBI());
        BuiltIn.putBI("eval_json", "evalJson", new BuiltInsForStringsMisc.evalJsonBI());
        BuiltIn.putBI("exists", new BuiltInsForExistenceHandling.existsBI());
        BuiltIn.putBI("filter", new BuiltInsForSequences.filterBI());
        BuiltIn.putBI("first", new BuiltInsForSequences.firstBI());
        BuiltIn.putBI("float", new BuiltInsForNumbers.floatBI());
        BuiltIn.putBI("floor", new BuiltInsForNumbers.floorBI());
        BuiltIn.putBI("chunk", new BuiltInsForSequences.chunkBI());
        BuiltIn.putBI("counter", new BuiltInsForLoopVariables.counterBI());
        BuiltIn.putBI("item_cycle", "itemCycle", new BuiltInsForLoopVariables.item_cycleBI());
        BuiltIn.putBI("has_api", "hasApi", new BuiltInsForMultipleTypes.has_apiBI());
        BuiltIn.putBI("has_content", "hasContent", new BuiltInsForExistenceHandling.has_contentBI());
        BuiltIn.putBI("has_next", "hasNext", new BuiltInsForLoopVariables.has_nextBI());
        BuiltIn.putBI("html", new BuiltInsForStringsEncoding.htmlBI());
        BuiltIn.putBI("if_exists", "ifExists", new BuiltInsForExistenceHandling.if_existsBI());
        BuiltIn.putBI("index", new BuiltInsForLoopVariables.indexBI());
        BuiltIn.putBI("index_of", "indexOf", new BuiltInsForStringsBasic.index_ofBI(false));
        BuiltIn.putBI("int", new BuiltInsForNumbers.intBI());
        BuiltIn.putBI("interpret", new Interpret());
        BuiltIn.putBI("is_boolean", "isBoolean", new BuiltInsForMultipleTypes.is_booleanBI());
        BuiltIn.putBI("is_collection", "isCollection", new BuiltInsForMultipleTypes.is_collectionBI());
        BuiltIn.putBI("is_collection_ex", "isCollectionEx", new BuiltInsForMultipleTypes.is_collection_exBI());
        BuiltInsForMultipleTypes.is_dateLikeBI bi = new BuiltInsForMultipleTypes.is_dateLikeBI();
        BuiltIn.putBI("is_date", "isDate", bi);
        BuiltIn.putBI("is_date_like", "isDateLike", bi);
        BuiltIn.putBI("is_date_only", "isDateOnly", new BuiltInsForMultipleTypes.is_dateOfTypeBI(2));
        BuiltIn.putBI("is_even_item", "isEvenItem", new BuiltInsForLoopVariables.is_even_itemBI());
        BuiltIn.putBI("is_first", "isFirst", new BuiltInsForLoopVariables.is_firstBI());
        BuiltIn.putBI("is_last", "isLast", new BuiltInsForLoopVariables.is_lastBI());
        BuiltIn.putBI("is_unknown_date_like", "isUnknownDateLike", new BuiltInsForMultipleTypes.is_dateOfTypeBI(0));
        BuiltIn.putBI("is_datetime", "isDatetime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(3));
        BuiltIn.putBI("is_directive", "isDirective", new BuiltInsForMultipleTypes.is_directiveBI());
        BuiltIn.putBI("is_enumerable", "isEnumerable", new BuiltInsForMultipleTypes.is_enumerableBI());
        BuiltIn.putBI("is_hash_ex", "isHashEx", new BuiltInsForMultipleTypes.is_hash_exBI());
        BuiltIn.putBI("is_hash", "isHash", new BuiltInsForMultipleTypes.is_hashBI());
        BuiltIn.putBI("is_infinite", "isInfinite", new BuiltInsForNumbers.is_infiniteBI());
        BuiltIn.putBI("is_indexable", "isIndexable", new BuiltInsForMultipleTypes.is_indexableBI());
        BuiltIn.putBI("is_macro", "isMacro", new BuiltInsForMultipleTypes.is_macroBI());
        BuiltIn.putBI("is_markup_output", "isMarkupOutput", new BuiltInsForMultipleTypes.is_markup_outputBI());
        BuiltIn.putBI("is_method", "isMethod", new BuiltInsForMultipleTypes.is_methodBI());
        BuiltIn.putBI("is_nan", "isNan", new BuiltInsForNumbers.is_nanBI());
        BuiltIn.putBI("is_node", "isNode", new BuiltInsForMultipleTypes.is_nodeBI());
        BuiltIn.putBI("is_number", "isNumber", new BuiltInsForMultipleTypes.is_numberBI());
        BuiltIn.putBI("is_odd_item", "isOddItem", new BuiltInsForLoopVariables.is_odd_itemBI());
        BuiltIn.putBI("is_sequence", "isSequence", new BuiltInsForMultipleTypes.is_sequenceBI());
        BuiltIn.putBI("is_string", "isString", new BuiltInsForMultipleTypes.is_stringBI());
        BuiltIn.putBI("is_time", "isTime", new BuiltInsForMultipleTypes.is_dateOfTypeBI(1));
        BuiltIn.putBI("is_transform", "isTransform", new BuiltInsForMultipleTypes.is_transformBI());
        BuiltIn.putBI("iso_utc", "isoUtc", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, true));
        BuiltIn.putBI("iso_utc_fz", "isoUtcFZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.TRUE, 6, true));
        BuiltIn.putBI("iso_utc_nz", "isoUtcNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, true));
        BuiltIn.putBI("iso_utc_ms", "isoUtcMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, true));
        BuiltIn.putBI("iso_utc_ms_nz", "isoUtcMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, true));
        BuiltIn.putBI("iso_utc_m", "isoUtcM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, true));
        BuiltIn.putBI("iso_utc_m_nz", "isoUtcMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, true));
        BuiltIn.putBI("iso_utc_h", "isoUtcH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, true));
        BuiltIn.putBI("iso_utc_h_nz", "isoUtcHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, true));
        BuiltIn.putBI("iso_local", "isoLocal", new BuiltInsForDates.iso_utc_or_local_BI(null, 6, false));
        BuiltIn.putBI("iso_local_nz", "isoLocalNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 6, false));
        BuiltIn.putBI("iso_local_ms", "isoLocalMs", new BuiltInsForDates.iso_utc_or_local_BI(null, 7, false));
        BuiltIn.putBI("iso_local_ms_nz", "isoLocalMsNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 7, false));
        BuiltIn.putBI("iso_local_m", "isoLocalM", new BuiltInsForDates.iso_utc_or_local_BI(null, 5, false));
        BuiltIn.putBI("iso_local_m_nz", "isoLocalMNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 5, false));
        BuiltIn.putBI("iso_local_h", "isoLocalH", new BuiltInsForDates.iso_utc_or_local_BI(null, 4, false));
        BuiltIn.putBI("iso_local_h_nz", "isoLocalHNZ", new BuiltInsForDates.iso_utc_or_local_BI(Boolean.FALSE, 4, false));
        BuiltIn.putBI("iso", new BuiltInsForDates.iso_BI(null, 6));
        BuiltIn.putBI("iso_nz", "isoNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 6));
        BuiltIn.putBI("iso_ms", "isoMs", new BuiltInsForDates.iso_BI(null, 7));
        BuiltIn.putBI("iso_ms_nz", "isoMsNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 7));
        BuiltIn.putBI("iso_m", "isoM", new BuiltInsForDates.iso_BI(null, 5));
        BuiltIn.putBI("iso_m_nz", "isoMNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 5));
        BuiltIn.putBI("iso_h", "isoH", new BuiltInsForDates.iso_BI(null, 4));
        BuiltIn.putBI("iso_h_nz", "isoHNZ", new BuiltInsForDates.iso_BI(Boolean.FALSE, 4));
        BuiltIn.putBI("j_string", "jString", new BuiltInsForStringsEncoding.j_stringBI());
        BuiltIn.putBI("join", new BuiltInsForSequences.joinBI());
        BuiltIn.putBI("js_string", "jsString", new BuiltInsForStringsEncoding.js_stringBI());
        BuiltIn.putBI("json_string", "jsonString", new BuiltInsForStringsEncoding.json_stringBI());
        BuiltIn.putBI("keep_after", "keepAfter", new BuiltInsForStringsBasic.keep_afterBI());
        BuiltIn.putBI("keep_before", "keepBefore", new BuiltInsForStringsBasic.keep_beforeBI());
        BuiltIn.putBI("keep_after_last", "keepAfterLast", new BuiltInsForStringsBasic.keep_after_lastBI());
        BuiltIn.putBI("keep_before_last", "keepBeforeLast", new BuiltInsForStringsBasic.keep_before_lastBI());
        BuiltIn.putBI("keys", new BuiltInsForHashes.keysBI());
        BuiltIn.putBI("last_index_of", "lastIndexOf", new BuiltInsForStringsBasic.index_ofBI(true));
        BuiltIn.putBI("last", new BuiltInsForSequences.lastBI());
        BuiltIn.putBI("left_pad", "leftPad", new BuiltInsForStringsBasic.padBI(true));
        BuiltIn.putBI("length", new BuiltInsForStringsBasic.lengthBI());
        BuiltIn.putBI("long", new BuiltInsForNumbers.longBI());
        BuiltIn.putBI("lower_abc", "lowerAbc", new BuiltInsForNumbers.lower_abcBI());
        BuiltIn.putBI("lower_case", "lowerCase", new BuiltInsForStringsBasic.lower_caseBI());
        BuiltIn.putBI("c_lower_case", "cLowerCase", new BuiltInsForStringsBasic.c_lower_caseBI());
        BuiltIn.putBI("map", new BuiltInsForSequences.mapBI());
        BuiltIn.putBI("namespace", new BuiltInsForMultipleTypes.namespaceBI());
        BuiltIn.putBI("new", new NewBI());
        BuiltIn.putBI("markup_string", "markupString", new BuiltInsForMarkupOutputs.markup_stringBI());
        BuiltIn.putBI("node_name", "nodeName", new BuiltInsForNodes.node_nameBI());
        BuiltIn.putBI("node_namespace", "nodeNamespace", new BuiltInsForNodes.node_namespaceBI());
        BuiltIn.putBI("node_type", "nodeType", new BuiltInsForNodes.node_typeBI());
        BuiltIn.putBI("no_esc", "noEsc", new BuiltInsForOutputFormatRelated.no_escBI());
        BuiltIn.putBI("max", new BuiltInsForSequences.maxBI());
        BuiltIn.putBI("min", new BuiltInsForSequences.minBI());
        BuiltIn.putBI("number", new BuiltInsForStringsMisc.numberBI());
        BuiltIn.putBI("number_to_date", "numberToDate", new BuiltInsForNumbers.number_to_dateBI(2));
        BuiltIn.putBI("number_to_time", "numberToTime", new BuiltInsForNumbers.number_to_dateBI(1));
        BuiltIn.putBI("number_to_datetime", "numberToDatetime", new BuiltInsForNumbers.number_to_dateBI(3));
        BuiltIn.putBI("parent", new BuiltInsForNodes.parentBI());
        BuiltIn.putBI("previous_sibling", "previousSibling", new BuiltInsForNodes.previousSiblingBI());
        BuiltIn.putBI("next_sibling", "nextSibling", new BuiltInsForNodes.nextSiblingBI());
        BuiltIn.putBI("item_parity", "itemParity", new BuiltInsForLoopVariables.item_parityBI());
        BuiltIn.putBI("item_parity_cap", "itemParityCap", new BuiltInsForLoopVariables.item_parity_capBI());
        BuiltIn.putBI("reverse", new BuiltInsForSequences.reverseBI());
        BuiltIn.putBI("right_pad", "rightPad", new BuiltInsForStringsBasic.padBI(false));
        BuiltIn.putBI("root", new BuiltInsForNodes.rootBI());
        BuiltIn.putBI("round", new BuiltInsForNumbers.roundBI());
        BuiltIn.putBI("remove_ending", "removeEnding", new BuiltInsForStringsBasic.remove_endingBI());
        BuiltIn.putBI("remove_beginning", "removeBeginning", new BuiltInsForStringsBasic.remove_beginningBI());
        BuiltIn.putBI("rtf", new BuiltInsForStringsEncoding.rtfBI());
        BuiltIn.putBI("seq_contains", "seqContains", new BuiltInsForSequences.seq_containsBI());
        BuiltIn.putBI("seq_index_of", "seqIndexOf", new BuiltInsForSequences.seq_index_ofBI(true));
        BuiltIn.putBI("seq_last_index_of", "seqLastIndexOf", new BuiltInsForSequences.seq_index_ofBI(false));
        BuiltIn.putBI("sequence", new BuiltInsForSequences.sequenceBI());
        BuiltIn.putBI("short", new BuiltInsForNumbers.shortBI());
        BuiltIn.putBI("size", new BuiltInsForMultipleTypes.sizeBI());
        BuiltIn.putBI("sort_by", "sortBy", new BuiltInsForSequences.sort_byBI());
        BuiltIn.putBI("sort", new BuiltInsForSequences.sortBI());
        BuiltIn.putBI("split", new BuiltInsForStringsBasic.split_BI());
        BuiltIn.putBI("switch", new BuiltInsWithLazyConditionals.switch_BI());
        BuiltIn.putBI("starts_with", "startsWith", new BuiltInsForStringsBasic.starts_withBI());
        BuiltIn.putBI("string", new BuiltInsForMultipleTypes.stringBI());
        BuiltIn.putBI("substring", new BuiltInsForStringsBasic.substringBI());
        BuiltIn.putBI("take_while", "takeWhile", new BuiltInsForSequences.take_whileBI());
        BuiltIn.putBI("then", new BuiltInsWithLazyConditionals.then_BI());
        BuiltIn.putBI("time", new BuiltInsForMultipleTypes.dateBI(1));
        BuiltIn.putBI("time_if_unknown", "timeIfUnknown", new BuiltInsForDates.dateType_if_unknownBI(1));
        BuiltIn.putBI("trim", new BuiltInsForStringsBasic.trimBI());
        BuiltIn.putBI("truncate", new BuiltInsForStringsBasic.truncateBI());
        BuiltIn.putBI("truncate_w", "truncateW", new BuiltInsForStringsBasic.truncate_wBI());
        BuiltIn.putBI("truncate_c", "truncateC", new BuiltInsForStringsBasic.truncate_cBI());
        BuiltIn.putBI("truncate_m", "truncateM", new BuiltInsForStringsBasic.truncate_mBI());
        BuiltIn.putBI("truncate_w_m", "truncateWM", new BuiltInsForStringsBasic.truncate_w_mBI());
        BuiltIn.putBI("truncate_c_m", "truncateCM", new BuiltInsForStringsBasic.truncate_c_mBI());
        BuiltIn.putBI("uncap_first", "uncapFirst", new BuiltInsForStringsBasic.uncap_firstBI());
        BuiltIn.putBI("upper_abc", "upperAbc", new BuiltInsForNumbers.upper_abcBI());
        BuiltIn.putBI("upper_case", "upperCase", new BuiltInsForStringsBasic.upper_caseBI());
        BuiltIn.putBI("c_upper_case", "cUpperCase", new BuiltInsForStringsBasic.c_upper_caseBI());
        BuiltIn.putBI("url", new BuiltInsForStringsEncoding.urlBI());
        BuiltIn.putBI("url_path", "urlPath", new BuiltInsForStringsEncoding.urlPathBI());
        BuiltIn.putBI("values", new BuiltInsForHashes.valuesBI());
        BuiltIn.putBI("web_safe", "webSafe", BUILT_INS_BY_NAME.get("html"));
        BuiltIn.putBI(BI_NAME_SNAKE_CASE_WITH_ARGS, BI_NAME_CAMEL_CASE_WITH_ARGS, new BuiltInsForCallables.with_argsBI());
        BuiltIn.putBI(BI_NAME_SNAKE_CASE_WITH_ARGS_LAST, BI_NAME_CAMEL_CASE_WITH_ARGS_LAST, new BuiltInsForCallables.with_args_lastBI());
        BuiltIn.putBI("word_list", "wordList", new BuiltInsForStringsBasic.word_listBI());
        BuiltIn.putBI("xhtml", new BuiltInsForStringsEncoding.xhtmlBI());
        BuiltIn.putBI("xml", new BuiltInsForStringsEncoding.xmlBI());
        BuiltIn.putBI("matches", new BuiltInsForStringsRegexp.matchesBI());
        BuiltIn.putBI("groups", new BuiltInsForStringsRegexp.groupsBI());
        BuiltIn.putBI("replace", new BuiltInsForStringsRegexp.replace_reBI());
        if (296 < BUILT_INS_BY_NAME.size()) {
            throw new AssertionError((Object)("Update NUMBER_OF_BIS! Should be: " + BUILT_INS_BY_NAME.size()));
        }
    }
}

