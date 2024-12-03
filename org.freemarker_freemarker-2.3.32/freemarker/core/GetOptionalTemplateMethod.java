/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._MessageUtil;
import freemarker.core._TemplateModelException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class GetOptionalTemplateMethod
implements TemplateMethodModelEx {
    static final GetOptionalTemplateMethod INSTANCE = new GetOptionalTemplateMethod("get_optional_template");
    static final GetOptionalTemplateMethod INSTANCE_CC = new GetOptionalTemplateMethod("getOptionalTemplate");
    private static final String OPTION_ENCODING = "encoding";
    private static final String OPTION_PARSE = "parse";
    private static final String RESULT_INCLUDE = "include";
    private static final String RESULT_IMPORT = "import";
    private static final String RESULT_EXISTS = "exists";
    private final String methodName;

    private GetOptionalTemplateMethod(String builtInVarName) {
        this.methodName = "." + builtInVarName;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        Template template;
        TemplateHashModelEx options;
        String absTemplateName;
        int argCnt = args.size();
        if (argCnt < 1 || argCnt > 2) {
            throw _MessageUtil.newArgCntError(this.methodName, argCnt, 1, 2);
        }
        final Environment env = Environment.getCurrentEnvironment();
        if (env == null) {
            throw new IllegalStateException("No freemarer.core.Environment is associated to the current thread.");
        }
        TemplateModel arg = (TemplateModel)args.get(0);
        if (!(arg instanceof TemplateScalarModel)) {
            throw _MessageUtil.newMethodArgMustBeStringException(this.methodName, 0, arg);
        }
        String templateName = EvalUtil.modelToString((TemplateScalarModel)arg, null, env);
        try {
            absTemplateName = env.toFullTemplateName(env.getCurrentTemplate().getName(), templateName);
        }
        catch (MalformedTemplateNameException e) {
            throw new _TemplateModelException((Throwable)e, "Failed to convert template path to full path; see cause exception.");
        }
        if (argCnt > 1) {
            TemplateModel arg2 = (TemplateModel)args.get(1);
            if (!(arg2 instanceof TemplateHashModelEx)) {
                throw _MessageUtil.newMethodArgMustBeExtendedHashException(this.methodName, 1, arg2);
            }
            options = (TemplateHashModelEx)arg2;
        } else {
            options = null;
        }
        String encoding = null;
        boolean parse = true;
        if (options != null) {
            TemplateHashModelEx2.KeyValuePairIterator kvpi = TemplateModelUtils.getKeyValuePairIterator(options);
            while (kvpi.hasNext()) {
                TemplateHashModelEx2.KeyValuePair kvp = kvpi.next();
                TemplateModel optNameTM = kvp.getKey();
                if (!(optNameTM instanceof TemplateScalarModel)) {
                    throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "All keys in the options hash must be strings, but found ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(optNameTM)));
                }
                String optName = ((TemplateScalarModel)optNameTM).getAsString();
                TemplateModel optValue = kvp.getValue();
                if (OPTION_ENCODING.equals(optName)) {
                    encoding = this.getStringOption(OPTION_ENCODING, optValue);
                    continue;
                }
                if (OPTION_PARSE.equals(optName)) {
                    parse = this.getBooleanOption(OPTION_PARSE, optValue);
                    continue;
                }
                throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "Unsupported option ", new _DelayedJQuote(optName), "; valid names are: ", new _DelayedJQuote(OPTION_ENCODING), ", ", new _DelayedJQuote(OPTION_PARSE), ".");
            }
        }
        try {
            template = env.getTemplateForInclusion(absTemplateName, encoding, parse, true);
        }
        catch (IOException e) {
            throw new _TemplateModelException((Throwable)e, "I/O error when trying to load optional template ", new _DelayedJQuote(absTemplateName), "; see cause exception");
        }
        SimpleHash result = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        result.put(RESULT_EXISTS, template != null);
        if (template != null) {
            result.put(RESULT_INCLUDE, new TemplateDirectiveModel(){

                @Override
                public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                    if (!params.isEmpty()) {
                        throw new TemplateException("This directive supports no parameters.", env);
                    }
                    if (loopVars.length != 0) {
                        throw new TemplateException("This directive supports no loop variables.", env);
                    }
                    if (body != null) {
                        throw new TemplateException("This directive supports no nested content.", env);
                    }
                    env.include(template);
                }
            });
            result.put(RESULT_IMPORT, new TemplateMethodModelEx(){

                @Override
                public Object exec(List args) throws TemplateModelException {
                    if (!args.isEmpty()) {
                        throw new TemplateModelException("This method supports no parameters.");
                    }
                    try {
                        return env.importLib(template, null);
                    }
                    catch (TemplateException | IOException e) {
                        throw new _TemplateModelException((Throwable)e, "Failed to import loaded template; see cause exception");
                    }
                }
            });
        }
        return result;
    }

    private boolean getBooleanOption(String optionName, TemplateModel value) throws TemplateModelException {
        if (!(value instanceof TemplateBooleanModel)) {
            throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a boolean, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
        }
        return ((TemplateBooleanModel)value).getAsBoolean();
    }

    private String getStringOption(String optionName, TemplateModel value) throws TemplateModelException {
        if (!(value instanceof TemplateScalarModel)) {
            throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a string, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
        }
        return EvalUtil.modelToString((TemplateScalarModel)value, null, null);
    }
}

