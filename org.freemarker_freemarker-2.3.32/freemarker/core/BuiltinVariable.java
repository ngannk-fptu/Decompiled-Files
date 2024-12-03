/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.FMParserTokenManager;
import freemarker.core.GetOptionalTemplateMethod;
import freemarker.core.Macro;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.TemplateObject;
import freemarker.core.Token;
import freemarker.core._CoreStringUtils;
import freemarker.core._MiscTemplateException;
import freemarker.template.Configuration;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.util.Arrays;
import java.util.Date;

final class BuiltinVariable
extends Expression {
    static final String TEMPLATE_NAME_CC = "templateName";
    static final String TEMPLATE_NAME = "template_name";
    static final String MAIN_TEMPLATE_NAME_CC = "mainTemplateName";
    static final String MAIN_TEMPLATE_NAME = "main_template_name";
    static final String CURRENT_TEMPLATE_NAME_CC = "currentTemplateName";
    static final String CURRENT_TEMPLATE_NAME = "current_template_name";
    static final String NAMESPACE = "namespace";
    static final String MAIN = "main";
    static final String GLOBALS = "globals";
    static final String LOCALS = "locals";
    static final String DATA_MODEL_CC = "dataModel";
    static final String DATA_MODEL = "data_model";
    static final String LANG = "lang";
    static final String LOCALE = "locale";
    static final String LOCALE_OBJECT_CC = "localeObject";
    static final String LOCALE_OBJECT = "locale_object";
    static final String TIME_ZONE_CC = "timeZone";
    static final String TIME_ZONE = "time_zone";
    static final String CURRENT_NODE_CC = "currentNode";
    static final String CURRENT_NODE = "current_node";
    static final String NODE = "node";
    static final String PASS = "pass";
    static final String VARS = "vars";
    static final String VERSION = "version";
    static final String INCOMPATIBLE_IMPROVEMENTS_CC = "incompatibleImprovements";
    static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
    static final String ERROR = "error";
    static final String OUTPUT_ENCODING_CC = "outputEncoding";
    static final String OUTPUT_ENCODING = "output_encoding";
    static final String OUTPUT_FORMAT_CC = "outputFormat";
    static final String OUTPUT_FORMAT = "output_format";
    static final String AUTO_ESC_CC = "autoEsc";
    static final String AUTO_ESC = "auto_esc";
    static final String URL_ESCAPING_CHARSET_CC = "urlEscapingCharset";
    static final String URL_ESCAPING_CHARSET = "url_escaping_charset";
    static final String NOW = "now";
    static final String GET_OPTIONAL_TEMPLATE = "get_optional_template";
    static final String GET_OPTIONAL_TEMPLATE_CC = "getOptionalTemplate";
    static final String CALLER_TEMPLATE_NAME = "caller_template_name";
    static final String CALLER_TEMPLATE_NAME_CC = "callerTemplateName";
    static final String ARGS = "args";
    static final String[] SPEC_VAR_NAMES = new String[]{"args", "autoEsc", "auto_esc", "callerTemplateName", "caller_template_name", "currentNode", "currentTemplateName", "current_node", "current_template_name", "dataModel", "data_model", "error", "getOptionalTemplate", "get_optional_template", "globals", "incompatibleImprovements", "incompatible_improvements", "lang", "locale", "localeObject", "locale_object", "locals", "main", "mainTemplateName", "main_template_name", "namespace", "node", "now", "outputEncoding", "outputFormat", "output_encoding", "output_format", "pass", "templateName", "template_name", "timeZone", "time_zone", "urlEscapingCharset", "url_escaping_charset", "vars", "version"};
    private final String name;
    private final TemplateModel parseTimeValue;

    BuiltinVariable(Token nameTk, FMParserTokenManager tokenManager, TemplateModel parseTimeValue) throws ParseException {
        String name = nameTk.image;
        this.parseTimeValue = parseTimeValue;
        if (Arrays.binarySearch(SPEC_VAR_NAMES, name) < 0) {
            int shownNamingConvention;
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown special variable name: ");
            sb.append(StringUtil.jQuote(name)).append(".");
            int namingConvention = tokenManager.namingConvention;
            int n = shownNamingConvention = namingConvention != 10 ? namingConvention : 11;
            String correctName = name.equals("auto_escape") || name.equals("auto_escaping") || name.equals("autoesc") ? AUTO_ESC : (name.equals("autoEscape") || name.equals("autoEscaping") ? AUTO_ESC_CC : null);
            if (correctName != null) {
                sb.append(" You may meant: ");
                sb.append(StringUtil.jQuote(correctName)).append(".");
            }
            sb.append("\nThe allowed special variable names are: ");
            boolean first = true;
            for (int i = 0; i < SPEC_VAR_NAMES.length; ++i) {
                String correctName2 = SPEC_VAR_NAMES[i];
                int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName2);
                if (!(shownNamingConvention == 12 ? correctNameNamingConvetion != 11 : correctNameNamingConvetion != 12)) continue;
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(correctName2);
            }
            throw new ParseException(sb.toString(), null, nameTk);
        }
        this.name = name.intern();
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.parseTimeValue != null) {
            return this.parseTimeValue;
        }
        if (this.name == NAMESPACE) {
            return env.getCurrentNamespace();
        }
        if (this.name == MAIN) {
            return env.getMainNamespace();
        }
        if (this.name == GLOBALS) {
            return env.getGlobalVariables();
        }
        if (this.name == LOCALS) {
            Macro.Context ctx = env.getCurrentMacroContext();
            return ctx == null ? null : ctx.getLocals();
        }
        if (this.name == DATA_MODEL || this.name == DATA_MODEL_CC) {
            return env.getDataModel();
        }
        if (this.name == VARS) {
            return new VarsHash(env);
        }
        if (this.name == LOCALE) {
            return new SimpleScalar(env.getLocale().toString());
        }
        if (this.name == LOCALE_OBJECT || this.name == LOCALE_OBJECT_CC) {
            return env.getObjectWrapper().wrap(env.getLocale());
        }
        if (this.name == LANG) {
            return new SimpleScalar(env.getLocale().getLanguage());
        }
        if (this.name == CURRENT_NODE || this.name == NODE || this.name == CURRENT_NODE_CC) {
            return env.getCurrentVisitorNode();
        }
        if (this.name == TEMPLATE_NAME || this.name == TEMPLATE_NAME_CC) {
            return env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_23 ? new SimpleScalar(env.getTemplate230().getName()) : new SimpleScalar(env.getTemplate().getName());
        }
        if (this.name == MAIN_TEMPLATE_NAME || this.name == MAIN_TEMPLATE_NAME_CC) {
            return SimpleScalar.newInstanceOrNull(env.getMainTemplate().getName());
        }
        if (this.name == CURRENT_TEMPLATE_NAME || this.name == CURRENT_TEMPLATE_NAME_CC) {
            return SimpleScalar.newInstanceOrNull(env.getCurrentTemplate().getName());
        }
        if (this.name == PASS) {
            return Macro.DO_NOTHING_MACRO;
        }
        if (this.name == OUTPUT_ENCODING || this.name == OUTPUT_ENCODING_CC) {
            String s = env.getOutputEncoding();
            return SimpleScalar.newInstanceOrNull(s);
        }
        if (this.name == URL_ESCAPING_CHARSET || this.name == URL_ESCAPING_CHARSET_CC) {
            String s = env.getURLEscapingCharset();
            return SimpleScalar.newInstanceOrNull(s);
        }
        if (this.name == ERROR) {
            return new SimpleScalar(env.getCurrentRecoveredErrorMessage());
        }
        if (this.name == NOW) {
            return new SimpleDate(new Date(), 3);
        }
        if (this.name == VERSION) {
            return new SimpleScalar(Configuration.getVersionNumber());
        }
        if (this.name == INCOMPATIBLE_IMPROVEMENTS || this.name == INCOMPATIBLE_IMPROVEMENTS_CC) {
            return new SimpleScalar(env.getConfiguration().getIncompatibleImprovements().toString());
        }
        if (this.name == GET_OPTIONAL_TEMPLATE) {
            return GetOptionalTemplateMethod.INSTANCE;
        }
        if (this.name == GET_OPTIONAL_TEMPLATE_CC) {
            return GetOptionalTemplateMethod.INSTANCE_CC;
        }
        if (this.name == CALLER_TEMPLATE_NAME || this.name == CALLER_TEMPLATE_NAME_CC) {
            TemplateObject callPlace = this.getRequiredMacroContext((Environment)env).callPlace;
            String name = callPlace != null ? callPlace.getTemplate().getName() : null;
            return name != null ? new SimpleScalar(name) : TemplateScalarModel.EMPTY_STRING;
        }
        if (this.name == ARGS) {
            TemplateModel args = this.getRequiredMacroContext(env).getArgsSpecialVariableValue();
            if (args == null) {
                throw new _MiscTemplateException((Expression)this, "The \"", ARGS, "\" special variable wasn't initialized.", this.name);
            }
            return args;
        }
        if (this.name == TIME_ZONE || this.name == TIME_ZONE_CC) {
            return new SimpleScalar(env.getTimeZone().getID());
        }
        throw new _MiscTemplateException((Expression)this, "Invalid special variable: ", this.name);
    }

    private Macro.Context getRequiredMacroContext(Environment env) throws TemplateException {
        Macro.Context ctx = env.getCurrentMacroContext();
        if (ctx == null) {
            throw new TemplateException("Can't get ." + this.name + " here, as there's no macro or function (that's implemented in the template) call in context.", env);
        }
        return ctx;
    }

    @Override
    public String toString() {
        return "." + this.name;
    }

    @Override
    public String getCanonicalForm() {
        return "." + this.name;
    }

    @Override
    String getNodeTypeSymbol() {
        return this.getCanonicalForm();
    }

    @Override
    boolean isLiteral() {
        return false;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return this;
    }

    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }

    static class VarsHash
    implements TemplateHashModel {
        Environment env;

        VarsHash(Environment env) {
            this.env = env;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            return this.env.getVariable(key);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}

