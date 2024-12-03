/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.cache.AndMatcher;
import freemarker.cache.ConditionalTemplateConfigurationFactory;
import freemarker.cache.FileExtensionMatcher;
import freemarker.cache.FileNameGlobMatcher;
import freemarker.cache.FirstMatchTemplateConfigurationFactory;
import freemarker.cache.MergingTemplateConfigurationFactory;
import freemarker.cache.NotMatcher;
import freemarker.cache.OrMatcher;
import freemarker.cache.PathGlobMatcher;
import freemarker.cache.PathRegexMatcher;
import freemarker.core.BugException;
import freemarker.core.DefaultTruncateBuiltinAlgorithm;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.ParseException;
import freemarker.core.PlainTextOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.TemplateConfiguration;
import freemarker.core.UndefinedOutputFormat;
import freemarker.core.XHTMLOutputFormat;
import freemarker.core.XMLOutputFormat;
import freemarker.core._ObjectBuilderSettingEvaluationException;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.WriteProtectable;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class _ObjectBuilderSettingEvaluator {
    private static final String INSTANCE_FIELD_NAME = "INSTANCE";
    private static final String BUILD_METHOD_NAME = "build";
    private static final String BUILDER_CLASS_POSTFIX = "Builder";
    private static Map<String, String> SHORTHANDS;
    private static final Object VOID;
    private final String src;
    private final Class expectedClass;
    private final boolean allowNull;
    private final _SettingEvaluationEnvironment env;
    private int pos;
    private boolean modernMode = false;

    private _ObjectBuilderSettingEvaluator(String src, int pos, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) {
        this.src = src;
        this.pos = pos;
        this.expectedClass = expectedClass;
        this.allowNull = allowNull;
        this.env = env;
    }

    public static Object eval(String src, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new _ObjectBuilderSettingEvaluator(src, 0, expectedClass, allowNull, env).eval();
    }

    public static int configureBean(String argumentListSrc, int posAfterOpenParen, Object bean, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new _ObjectBuilderSettingEvaluator(argumentListSrc, posAfterOpenParen, bean.getClass(), true, env).configureBean(bean);
    }

    private Object eval() throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object value;
        this.skipWS();
        try {
            value = this.ensureEvaled(this.fetchValue(false, true, false, true));
        }
        catch (LegacyExceptionWrapperSettingEvaluationExpression e) {
            e.rethrowLegacy();
            value = null;
        }
        this.skipWS();
        if (this.pos != this.src.length()) {
            throw new _ObjectBuilderSettingEvaluationException("end-of-expression", this.src, this.pos);
        }
        if (value == null && !this.allowNull) {
            throw new _ObjectBuilderSettingEvaluationException("Value can't be null.");
        }
        if (value != null && !this.expectedClass.isInstance(value)) {
            throw new _ObjectBuilderSettingEvaluationException("The resulting object (of class " + value.getClass() + ") is not a(n) " + this.expectedClass.getName() + ".");
        }
        return value;
    }

    private int configureBean(Object bean) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        PropertyAssignmentsExpression propAssignments = new PropertyAssignmentsExpression(bean);
        this.fetchParameterListInto(propAssignments);
        this.skipWS();
        propAssignments.eval();
        return this.pos;
    }

    private Object ensureEvaled(Object value) throws _ObjectBuilderSettingEvaluationException {
        return value instanceof SettingExpression ? ((SettingExpression)value).eval() : value;
    }

    private Object fetchBuilderCall(boolean optional, boolean topLevel) throws _ObjectBuilderSettingEvaluationException {
        int startPos = this.pos;
        BuilderCallExpression exp = new BuilderCallExpression();
        exp.canBeStaticField = true;
        String fetchedClassName = this.fetchClassName(optional);
        if (fetchedClassName == null) {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
            }
            return VOID;
        }
        exp.className = _ObjectBuilderSettingEvaluator.shorthandToFullQualified(fetchedClassName);
        if (!fetchedClassName.equals(exp.className)) {
            this.modernMode = true;
            exp.canBeStaticField = false;
        }
        this.skipWS();
        char openParen = this.fetchOptionalChar("(");
        if (openParen == '\u0000' && !topLevel) {
            if (fetchedClassName.indexOf(46) != -1) {
                exp.mustBeStaticField = true;
            } else {
                this.pos = startPos;
                return VOID;
            }
        }
        if (openParen != '\u0000') {
            this.fetchParameterListInto(exp);
            exp.canBeStaticField = false;
        }
        return exp;
    }

    private void fetchParameterListInto(ExpressionWithParameters exp) throws _ObjectBuilderSettingEvaluationException {
        this.modernMode = true;
        this.skipWS();
        if (this.fetchOptionalChar(")") != ')') {
            do {
                this.skipWS();
                Object paramNameOrValue = this.fetchValue(false, false, true, false);
                if (paramNameOrValue == VOID) continue;
                this.skipWS();
                if (paramNameOrValue instanceof Name) {
                    exp.namedParamNames.add(((Name)paramNameOrValue).name);
                    this.skipWS();
                    this.fetchRequiredChar("=");
                    this.skipWS();
                    Object paramValue = this.fetchValue(false, false, true, true);
                    exp.namedParamValues.add(this.ensureEvaled(paramValue));
                } else {
                    if (!exp.namedParamNames.isEmpty()) {
                        throw new _ObjectBuilderSettingEvaluationException("Positional parameters must precede named parameters");
                    }
                    if (!exp.getAllowPositionalParameters()) {
                        throw new _ObjectBuilderSettingEvaluationException("Positional parameters not supported here");
                    }
                    exp.positionalParamValues.add(this.ensureEvaled(paramNameOrValue));
                }
                this.skipWS();
            } while (this.fetchRequiredChar(",)") == ',');
        }
    }

    private Object fetchValue(boolean optional, boolean topLevel, boolean resultCoerced, boolean resolveVariables) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos < this.src.length()) {
            Object val = this.fetchNumberLike(true, resultCoerced);
            if (val != VOID) {
                return val;
            }
            val = this.fetchStringLiteral(true);
            if (val != VOID) {
                return val;
            }
            val = this.fetchListLiteral(true);
            if (val != VOID) {
                return val;
            }
            val = this.fetchMapLiteral(true);
            if (val != VOID) {
                return val;
            }
            val = this.fetchBuilderCall(true, topLevel);
            if (val != VOID) {
                return val;
            }
            String name = this.fetchSimpleName(true);
            if (name != null) {
                val = this.keywordToValueOrVoid(name);
                if (val != VOID) {
                    return val;
                }
                if (resolveVariables) {
                    throw new _ObjectBuilderSettingEvaluationException("Can't resolve variable reference: " + name);
                }
                return new Name(name);
            }
        }
        if (optional) {
            return VOID;
        }
        throw new _ObjectBuilderSettingEvaluationException("value or name", this.src, this.pos);
    }

    private boolean isKeyword(String name) {
        return this.keywordToValueOrVoid(name) != VOID;
    }

    private Object keywordToValueOrVoid(String name) {
        if (name.equals("true")) {
            return Boolean.TRUE;
        }
        if (name.equals("false")) {
            return Boolean.FALSE;
        }
        if (name.equals("null")) {
            return null;
        }
        return VOID;
    }

    private String fetchSimpleName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        char c;
        char c2 = c = this.pos < this.src.length() ? this.src.charAt(this.pos) : (char)'\u0000';
        if (!this.isIdentifierStart(c)) {
            if (optional) {
                return null;
            }
            throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
        }
        int startPos = this.pos++;
        while (this.pos != this.src.length() && this.isIdentifierMiddle(c = this.src.charAt(this.pos))) {
            ++this.pos;
        }
        return this.src.substring(startPos, this.pos);
    }

    private String fetchClassName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        int startPos = this.pos;
        StringBuilder sb = new StringBuilder();
        while (true) {
            String name;
            if ((name = this.fetchSimpleName(true)) == null) {
                if (!optional) {
                    throw new _ObjectBuilderSettingEvaluationException("name", this.src, this.pos);
                }
                this.pos = startPos;
                return null;
            }
            sb.append(name);
            this.skipWS();
            if (this.pos >= this.src.length() || this.src.charAt(this.pos) != '.') break;
            sb.append('.');
            ++this.pos;
            this.skipWS();
        }
        String className = sb.toString();
        if (this.isKeyword(className)) {
            this.pos = startPos;
            return null;
        }
        return className;
    }

    private Object fetchNumberLike(boolean optional, boolean resultCoerced) throws _ObjectBuilderSettingEvaluationException {
        char c;
        int startPos = this.pos;
        boolean isVersion = false;
        boolean hasDot = false;
        while (this.pos != this.src.length()) {
            char c2 = this.src.charAt(this.pos);
            if (c2 == '.') {
                if (hasDot) {
                    isVersion = true;
                } else {
                    hasDot = true;
                }
            } else if (!this.isASCIIDigit(c2) && c2 != '-') break;
            ++this.pos;
        }
        if (startPos == this.pos) {
            if (optional) {
                return VOID;
            }
            throw new _ObjectBuilderSettingEvaluationException("number-like", this.src, this.pos);
        }
        String numStr = this.src.substring(startPos, this.pos);
        if (isVersion) {
            try {
                return new Version(numStr);
            }
            catch (IllegalArgumentException e) {
                throw new _ObjectBuilderSettingEvaluationException("Malformed version number: " + numStr, e);
            }
        }
        String typePostfix = null;
        while (this.pos != this.src.length() && Character.isLetter(c = this.src.charAt(this.pos))) {
            typePostfix = typePostfix == null ? String.valueOf(c) : typePostfix + c;
            ++this.pos;
        }
        try {
            if (numStr.endsWith(".")) {
                throw new NumberFormatException("A number can't end with a dot");
            }
            if (numStr.startsWith(".") || numStr.startsWith("-.") || numStr.startsWith("+.")) {
                throw new NumberFormatException("A number can't start with a dot");
            }
            if (typePostfix == null) {
                if (numStr.indexOf(46) == -1) {
                    BigInteger biNum = new BigInteger(numStr);
                    int bitLength = biNum.bitLength();
                    if (bitLength <= 31) {
                        return biNum.intValue();
                    }
                    if (bitLength <= 63) {
                        return biNum.longValue();
                    }
                    return biNum;
                }
                if (resultCoerced) {
                    return new BigDecimal(numStr);
                }
                return Double.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("l")) {
                return Long.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("bi")) {
                return new BigInteger(numStr);
            }
            if (typePostfix.equalsIgnoreCase("bd")) {
                return new BigDecimal(numStr);
            }
            if (typePostfix.equalsIgnoreCase("d")) {
                return Double.valueOf(numStr);
            }
            if (typePostfix.equalsIgnoreCase("f")) {
                return Float.valueOf(numStr);
            }
            throw new _ObjectBuilderSettingEvaluationException("Unrecognized number type postfix: " + typePostfix);
        }
        catch (NumberFormatException e) {
            throw new _ObjectBuilderSettingEvaluationException("Malformed number: " + numStr, e);
        }
    }

    private Object fetchStringLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        int startPos = this.pos;
        int q = 0;
        boolean afterEscape = false;
        boolean raw = false;
        while (true) {
            if (this.pos == this.src.length()) {
                if (q == 0) break;
                throw new _ObjectBuilderSettingEvaluationException(String.valueOf((char)q), this.src, this.pos);
            }
            int c = this.src.charAt(this.pos);
            if (q == 0) {
                if (c == 114 && this.pos + 1 < this.src.length()) {
                    raw = true;
                    c = this.src.charAt(this.pos + 1);
                }
                if (c == 39) {
                    q = 39;
                } else {
                    if (c != 34) break;
                    q = 34;
                }
                if (raw) {
                    ++this.pos;
                }
            } else if (!afterEscape) {
                if (c == 92 && !raw) {
                    afterEscape = true;
                } else {
                    char prevC;
                    if (c == q) break;
                    if (c == 123 && ((prevC = this.src.charAt(this.pos - 1)) == '$' || prevC == '#')) {
                        throw new _ObjectBuilderSettingEvaluationException("${...} and #{...} aren't allowed here.");
                    }
                }
            } else {
                afterEscape = false;
            }
            ++this.pos;
        }
        if (startPos == this.pos) {
            if (optional) {
                return VOID;
            }
            throw new _ObjectBuilderSettingEvaluationException("string literal", this.src, this.pos);
        }
        String sInside = this.src.substring(startPos + (raw ? 2 : 1), this.pos);
        try {
            ++this.pos;
            return raw ? sInside : StringUtil.FTLStringLiteralDec(sInside);
        }
        catch (ParseException e) {
            throw new _ObjectBuilderSettingEvaluationException("Malformed string literal: " + sInside, e);
        }
    }

    private Object fetchListLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos == this.src.length() || this.src.charAt(this.pos) != '[') {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException("[", this.src, this.pos);
            }
            return VOID;
        }
        ++this.pos;
        ListExpression listExp = new ListExpression();
        while (true) {
            this.skipWS();
            if (this.fetchOptionalChar("]") != '\u0000') {
                return listExp;
            }
            if (listExp.itemCount() != 0) {
                this.fetchRequiredChar(",");
                this.skipWS();
            }
            listExp.addItem(this.fetchValue(false, false, false, true));
            this.skipWS();
        }
    }

    private Object fetchMapLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
        if (this.pos == this.src.length() || this.src.charAt(this.pos) != '{') {
            if (!optional) {
                throw new _ObjectBuilderSettingEvaluationException("{", this.src, this.pos);
            }
            return VOID;
        }
        ++this.pos;
        MapExpression mapExp = new MapExpression();
        while (true) {
            this.skipWS();
            if (this.fetchOptionalChar("}") != '\u0000') {
                return mapExp;
            }
            if (mapExp.itemCount() != 0) {
                this.fetchRequiredChar(",");
                this.skipWS();
            }
            Object key = this.fetchValue(false, false, false, true);
            this.skipWS();
            this.fetchRequiredChar(":");
            this.skipWS();
            Object value = this.fetchValue(false, false, false, true);
            mapExp.addItem(new KeyValuePair(key, value));
            this.skipWS();
        }
    }

    private void skipWS() {
        while (this.pos != this.src.length()) {
            char c = this.src.charAt(this.pos);
            if (!Character.isWhitespace(c)) {
                return;
            }
            ++this.pos;
        }
        return;
    }

    private char fetchOptionalChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
        return this.fetchChar(expectedChars, true);
    }

    private char fetchRequiredChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
        return this.fetchChar(expectedChars, false);
    }

    private char fetchChar(String expectedChars, boolean optional) throws _ObjectBuilderSettingEvaluationException {
        char c;
        char c2 = c = this.pos < this.src.length() ? this.src.charAt(this.pos) : (char)'\u0000';
        if (expectedChars.indexOf(c) != -1) {
            ++this.pos;
            return c;
        }
        if (optional) {
            return '\u0000';
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expectedChars.length(); ++i) {
            if (i != 0) {
                sb.append(" or ");
            }
            sb.append(StringUtil.jQuote(expectedChars.substring(i, i + 1)));
        }
        throw new _ObjectBuilderSettingEvaluationException(sb.toString(), this.src, this.pos);
    }

    private boolean isASCIIDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_' || c == '$';
    }

    private boolean isIdentifierMiddle(char c) {
        return this.isIdentifierStart(c) || this.isASCIIDigit(c);
    }

    private static synchronized String shorthandToFullQualified(String className) {
        String fullClassName;
        if (SHORTHANDS == null) {
            SHORTHANDS = new HashMap<String, String>();
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, DefaultObjectWrapper.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, BeansWrapper.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, SimpleObjectWrapper.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, TemplateConfiguration.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, PathGlobMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, FileNameGlobMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, FileExtensionMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, PathRegexMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, AndMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, OrMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, NotMatcher.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, ConditionalTemplateConfigurationFactory.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, MergingTemplateConfigurationFactory.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, FirstMatchTemplateConfigurationFactory.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, HTMLOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, XHTMLOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, XMLOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, RTFOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, PlainTextOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, UndefinedOutputFormat.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, DefaultTruncateBuiltinAlgorithm.class);
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, Locale.class);
            SHORTHANDS.put("TimeZone", "freemarker.core._TimeZone");
            SHORTHANDS.put("markup", "freemarker.core._Markup");
            _ObjectBuilderSettingEvaluator.addWithSimpleName(SHORTHANDS, Configuration.class);
        }
        return (fullClassName = SHORTHANDS.get(className)) == null ? className : fullClassName;
    }

    private static void addWithSimpleName(Map map, Class<?> pClass) {
        map.put(pClass.getSimpleName(), pClass.getName());
    }

    private void setJavaBeanProperties(Object bean, List namedParamNames, List namedParamValues) throws _ObjectBuilderSettingEvaluationException {
        int i;
        HashMap<String, Method> beanPropSetters;
        if (namedParamNames.isEmpty()) {
            return;
        }
        Class<?> cl = bean.getClass();
        try {
            PropertyDescriptor[] propDescs = Introspector.getBeanInfo(cl).getPropertyDescriptors();
            beanPropSetters = new HashMap<String, Method>(propDescs.length * 4 / 3, 1.0f);
            for (i = 0; i < propDescs.length; ++i) {
                PropertyDescriptor propDesc = propDescs[i];
                Method writeMethod = propDesc.getWriteMethod();
                if (writeMethod == null) continue;
                beanPropSetters.put(propDesc.getName(), writeMethod);
            }
        }
        catch (Exception e) {
            throw new _ObjectBuilderSettingEvaluationException("Failed to inspect " + cl.getName() + " class", e);
        }
        TemplateHashModel beanTM = null;
        for (i = 0; i < namedParamNames.size(); ++i) {
            String name = (String)namedParamNames.get(i);
            if (!beanPropSetters.containsKey(name)) {
                throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class has no writeable JavaBeans property called " + StringUtil.jQuote(name) + ".");
            }
            Method beanPropSetter = beanPropSetters.put(name, null);
            if (beanPropSetter == null) {
                throw new _ObjectBuilderSettingEvaluationException("JavaBeans property " + StringUtil.jQuote(name) + " is set twice.");
            }
            try {
                TemplateModel m;
                if (beanTM == null) {
                    TemplateModel wrappedObj = this.env.getObjectWrapper().wrap(bean);
                    if (!(wrappedObj instanceof TemplateHashModel)) {
                        throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " class is not a wrapped as TemplateHashModel.");
                    }
                    beanTM = (TemplateHashModel)wrappedObj;
                }
                if ((m = beanTM.get(beanPropSetter.getName())) == null) {
                    throw new _ObjectBuilderSettingEvaluationException("Can't find " + beanPropSetter + " as FreeMarker method.");
                }
                if (!(m instanceof TemplateMethodModelEx)) {
                    throw new _ObjectBuilderSettingEvaluationException(StringUtil.jQuote(beanPropSetter.getName()) + " wasn't a TemplateMethodModelEx.");
                }
                ArrayList<TemplateModel> args = new ArrayList<TemplateModel>();
                args.add(this.env.getObjectWrapper().wrap(namedParamValues.get(i)));
                ((TemplateMethodModelEx)m).exec(args);
                continue;
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to set " + StringUtil.jQuote(name), e);
            }
        }
    }

    static {
        VOID = new Object();
    }

    private static class LegacyExceptionWrapperSettingEvaluationExpression
    extends _ObjectBuilderSettingEvaluationException {
        public LegacyExceptionWrapperSettingEvaluationExpression(Throwable cause) {
            super("Legacy operation failed", cause);
            if (!(cause instanceof ClassNotFoundException || cause instanceof InstantiationException || cause instanceof IllegalAccessException)) {
                throw new IllegalArgumentException();
            }
        }

        public void rethrowLegacy() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Throwable cause = this.getCause();
            if (cause instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)cause;
            }
            if (cause instanceof InstantiationException) {
                throw (InstantiationException)cause;
            }
            if (cause instanceof IllegalAccessException) {
                throw (IllegalAccessException)cause;
            }
            throw new BugException();
        }
    }

    private class PropertyAssignmentsExpression
    extends ExpressionWithParameters {
        private final Object bean;

        public PropertyAssignmentsExpression(Object bean) {
            this.bean = bean;
        }

        @Override
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(this.bean, this.namedParamNames, this.namedParamValues);
            return this.bean;
        }

        @Override
        protected boolean getAllowPositionalParameters() {
            return false;
        }
    }

    private class BuilderCallExpression
    extends ExpressionWithParameters {
        private String className;
        private boolean canBeStaticField;
        private boolean mustBeStaticField;

        private BuilderCallExpression() {
        }

        @Override
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            Object result;
            boolean clIsBuilderClass;
            Class cl;
            if (this.mustBeStaticField) {
                if (!this.canBeStaticField) {
                    throw new BugException();
                }
                return this.getStaticFieldValue(this.className);
            }
            if (!_ObjectBuilderSettingEvaluator.this.modernMode) {
                try {
                    try {
                        return ClassUtil.forName(this.className).newInstance();
                    }
                    catch (InstantiationException e) {
                        throw new LegacyExceptionWrapperSettingEvaluationExpression(e);
                    }
                    catch (IllegalAccessException e) {
                        throw new LegacyExceptionWrapperSettingEvaluationExpression(e);
                    }
                    catch (ClassNotFoundException e) {
                        throw new LegacyExceptionWrapperSettingEvaluationExpression(e);
                    }
                }
                catch (LegacyExceptionWrapperSettingEvaluationExpression e) {
                    if (!this.canBeStaticField || this.className.indexOf(46) == -1) {
                        throw e;
                    }
                    try {
                        return this.getStaticFieldValue(this.className);
                    }
                    catch (_ObjectBuilderSettingEvaluationException e2) {
                        throw e;
                    }
                }
            }
            try {
                cl = ClassUtil.forName(this.className + _ObjectBuilderSettingEvaluator.BUILDER_CLASS_POSTFIX);
                clIsBuilderClass = true;
            }
            catch (ClassNotFoundException e) {
                clIsBuilderClass = false;
                try {
                    cl = ClassUtil.forName(this.className);
                }
                catch (Exception e2) {
                    boolean failedToGetAsStaticField;
                    if (this.canBeStaticField) {
                        try {
                            return this.getStaticFieldValue(this.className);
                        }
                        catch (_ObjectBuilderSettingEvaluationException e3) {
                            failedToGetAsStaticField = true;
                        }
                    } else {
                        failedToGetAsStaticField = false;
                    }
                    throw new _ObjectBuilderSettingEvaluationException("Failed to get class " + StringUtil.jQuote(this.className) + (failedToGetAsStaticField ? " (also failed to resolve name as static field)" : "") + ".", e2);
                }
            }
            if (!clIsBuilderClass && this.hasNoParameters()) {
                try {
                    Field f = cl.getField(_ObjectBuilderSettingEvaluator.INSTANCE_FIELD_NAME);
                    if ((f.getModifiers() & 9) == 9) {
                        return f.get(null);
                    }
                }
                catch (NoSuchFieldException f) {
                }
                catch (Exception e) {
                    throw new _ObjectBuilderSettingEvaluationException("Error when trying to access " + StringUtil.jQuote(this.className) + "." + _ObjectBuilderSettingEvaluator.INSTANCE_FIELD_NAME, e);
                }
            }
            Object constructorResult = this.callConstructor(cl);
            _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(constructorResult, this.namedParamNames, this.namedParamValues);
            if (clIsBuilderClass) {
                result = this.callBuild(constructorResult);
            } else {
                if (constructorResult instanceof WriteProtectable) {
                    ((WriteProtectable)constructorResult).writeProtect();
                }
                result = constructorResult;
            }
            return result;
        }

        private Object getStaticFieldValue(String dottedName) throws _ObjectBuilderSettingEvaluationException {
            Field field;
            Class cl;
            int lastDotIdx = dottedName.lastIndexOf(46);
            if (lastDotIdx == -1) {
                throw new IllegalArgumentException();
            }
            String className = _ObjectBuilderSettingEvaluator.shorthandToFullQualified(dottedName.substring(0, lastDotIdx));
            String fieldName = dottedName.substring(lastDotIdx + 1);
            try {
                cl = ClassUtil.forName(className);
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to get field's parent class, " + StringUtil.jQuote(className) + ".", e);
            }
            try {
                field = cl.getField(fieldName);
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to get field " + StringUtil.jQuote(fieldName) + " from class " + StringUtil.jQuote(className) + ".", e);
            }
            if ((field.getModifiers() & 8) == 0) {
                throw new _ObjectBuilderSettingEvaluationException("Referred field isn't static: " + field);
            }
            if ((field.getModifiers() & 1) == 0) {
                throw new _ObjectBuilderSettingEvaluationException("Referred field isn't public: " + field);
            }
            if (field.getName().equals(_ObjectBuilderSettingEvaluator.INSTANCE_FIELD_NAME)) {
                throw new _ObjectBuilderSettingEvaluationException("The INSTANCE field is only accessible through pseudo-constructor call: " + className + "()");
            }
            try {
                return field.get(null);
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to get field value: " + field, e);
            }
        }

        private Object callConstructor(Class cl) throws _ObjectBuilderSettingEvaluationException {
            if (this.hasNoParameters()) {
                try {
                    return cl.newInstance();
                }
                catch (Exception e) {
                    throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " 0-argument constructor", e);
                }
            }
            BeansWrapper ow = _ObjectBuilderSettingEvaluator.this.env.getObjectWrapper();
            ArrayList<TemplateModel> tmArgs = new ArrayList<TemplateModel>(this.positionalParamValues.size());
            for (int i = 0; i < this.positionalParamValues.size(); ++i) {
                try {
                    tmArgs.add(ow.wrap(this.positionalParamValues.get(i)));
                    continue;
                }
                catch (TemplateModelException e) {
                    throw new _ObjectBuilderSettingEvaluationException("Failed to wrap arg #" + (i + 1), e);
                }
            }
            try {
                return ow.newInstance(cl, tmArgs);
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl.getName() + " constructor", e);
            }
        }

        private Object callBuild(Object constructorResult) throws _ObjectBuilderSettingEvaluationException {
            Method buildMethod;
            Class<?> cl = constructorResult.getClass();
            try {
                buildMethod = constructorResult.getClass().getMethod(_ObjectBuilderSettingEvaluator.BUILD_METHOD_NAME, null);
            }
            catch (NoSuchMethodException e) {
                throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " builder class must have a public " + _ObjectBuilderSettingEvaluator.BUILD_METHOD_NAME + "() method", e);
            }
            catch (Exception e) {
                throw new _ObjectBuilderSettingEvaluationException("Failed to get the build() method of the " + cl.getName() + " builder class", e);
            }
            try {
                return buildMethod.invoke(constructorResult, (Object[])null);
            }
            catch (Exception e) {
                Throwable cause = e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
                throw new _ObjectBuilderSettingEvaluationException("Failed to call build() method on " + cl.getName() + " instance", cause);
            }
        }

        private boolean hasNoParameters() {
            return this.positionalParamValues.isEmpty() && this.namedParamValues.isEmpty();
        }

        @Override
        protected boolean getAllowPositionalParameters() {
            return true;
        }
    }

    private static class KeyValuePair {
        private final Object key;
        private final Object value;

        public KeyValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private class MapExpression
    extends SettingExpression {
        private List<KeyValuePair> items;

        private MapExpression() {
            this.items = new ArrayList<KeyValuePair>();
        }

        void addItem(KeyValuePair item) {
            this.items.add(item);
        }

        public int itemCount() {
            return this.items.size();
        }

        @Override
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            LinkedHashMap<Object, Object> res = new LinkedHashMap<Object, Object>(this.items.size() * 4 / 3, 1.0f);
            for (KeyValuePair item : this.items) {
                Object key = _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.key);
                if (key == null) {
                    throw new _ObjectBuilderSettingEvaluationException("Map can't use null as key.");
                }
                res.put(key, _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.value));
            }
            return res;
        }
    }

    private class ListExpression
    extends SettingExpression {
        private List<Object> items;

        private ListExpression() {
            this.items = new ArrayList<Object>();
        }

        void addItem(Object item) {
            this.items.add(item);
        }

        public int itemCount() {
            return this.items.size();
        }

        @Override
        Object eval() throws _ObjectBuilderSettingEvaluationException {
            ArrayList<Object> res = new ArrayList<Object>(this.items.size());
            for (Object item : this.items) {
                res.add(_ObjectBuilderSettingEvaluator.this.ensureEvaled(item));
            }
            return res;
        }
    }

    private abstract class ExpressionWithParameters
    extends SettingExpression {
        protected List positionalParamValues;
        protected List namedParamNames;
        protected List namedParamValues;

        private ExpressionWithParameters() {
            this.positionalParamValues = new ArrayList();
            this.namedParamNames = new ArrayList();
            this.namedParamValues = new ArrayList();
        }

        protected abstract boolean getAllowPositionalParameters();
    }

    private static abstract class SettingExpression {
        private SettingExpression() {
        }

        abstract Object eval() throws _ObjectBuilderSettingEvaluationException;
    }

    private static class Name {
        private final String name;

        public Name(String name) {
            this.name = name;
        }
    }
}

