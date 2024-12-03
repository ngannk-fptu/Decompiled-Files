/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.APINotSupportedTemplateException;
import freemarker.core.BackwardCompatibleTemplateNumberFormat;
import freemarker.core.BugException;
import freemarker.core.BuiltIn;
import freemarker.core.CFormat;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.LazilyGeneratedCollectionModel;
import freemarker.core.Macro;
import freemarker.core.NumberLiteral;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._CoreAPI;
import freemarker.core._DelayedJQuote;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NumberUtil;
import java.util.Date;
import java.util.List;

class BuiltInsForMultipleTypes {
    private BuiltInsForMultipleTypes() {
    }

    static class stringBI
    extends BuiltIn {
        stringBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateNumberModel) {
                return new NumberFormatter((TemplateNumberModel)model, env);
            }
            if (model instanceof TemplateDateModel) {
                TemplateDateModel dm = (TemplateDateModel)model;
                return new DateFormatter(dm, env);
            }
            if (model instanceof SimpleScalar) {
                return model;
            }
            if (model instanceof TemplateBooleanModel) {
                return new BooleanFormatter((TemplateBooleanModel)model, env);
            }
            if (model instanceof TemplateScalarModel) {
                return new SimpleScalar(((TemplateScalarModel)model).getAsString());
            }
            if (env.isClassicCompatible() && model instanceof BeanModel) {
                return new SimpleScalar(_BeansAPI.getAsClassicCompatibleString((BeanModel)model));
            }
            throw new UnexpectedTypeException(this.target, model, "number, date, boolean or string", new Class[]{TemplateNumberModel.class, TemplateDateModel.class, TemplateBooleanModel.class, TemplateScalarModel.class}, env);
        }

        private class NumberFormatter
        implements TemplateScalarModel,
        TemplateHashModel,
        TemplateMethodModel {
            private final TemplateNumberModel numberModel;
            private final Number number;
            private final Environment env;
            private final TemplateNumberFormat defaultFormat;
            private String cachedValue;

            NumberFormatter(TemplateNumberModel numberModel, Environment env) throws TemplateException {
                this.env = env;
                this.numberModel = numberModel;
                this.number = EvalUtil.modelToNumber(numberModel, stringBI.this.target);
                try {
                    this.defaultFormat = env.getTemplateNumberFormat(stringBI.this, true);
                }
                catch (TemplateException e) {
                    throw _CoreAPI.ensureIsTemplateModelException("Failed to get default number format", e);
                }
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                stringBI.this.checkMethodArgCount(args, 1);
                return this.get((String)args.get(0));
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                String result;
                TemplateNumberFormat format;
                try {
                    format = this.env.getTemplateNumberFormat(key, stringBI.this, true);
                }
                catch (TemplateException e) {
                    throw _CoreAPI.ensureIsTemplateModelException("Failed to get number format", e);
                }
                try {
                    result = format instanceof BackwardCompatibleTemplateNumberFormat ? this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)format, stringBI.this.target) : this.env.formatNumberToPlainText(this.numberModel, format, stringBI.this.target, true);
                }
                catch (TemplateException e) {
                    throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", e);
                }
                return new SimpleScalar(result);
            }

            @Override
            public String getAsString() throws TemplateModelException {
                if (this.cachedValue == null) {
                    try {
                        this.cachedValue = this.defaultFormat instanceof BackwardCompatibleTemplateNumberFormat ? this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)this.defaultFormat, stringBI.this.target) : this.env.formatNumberToPlainText(this.numberModel, this.defaultFormat, stringBI.this.target, true);
                    }
                    catch (TemplateException e) {
                        throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", e);
                    }
                }
                return this.cachedValue;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        }

        private class DateFormatter
        implements TemplateScalarModel,
        TemplateHashModel,
        TemplateMethodModel {
            private final TemplateDateModel dateModel;
            private final Environment env;
            private final TemplateDateFormat defaultFormat;
            private String cachedValue;

            DateFormatter(TemplateDateModel dateModel, Environment env) throws TemplateException {
                this.dateModel = dateModel;
                this.env = env;
                int dateType = dateModel.getDateType();
                this.defaultFormat = dateType == 0 ? null : env.getTemplateDateFormat(dateType, EvalUtil.modelToDate(dateModel, stringBI.this.target).getClass(), stringBI.this.target, true);
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                stringBI.this.checkMethodArgCount(args, 1);
                return this.formatWith((String)args.get(0));
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                return this.formatWith(key);
            }

            private TemplateModel formatWith(String key) throws TemplateModelException {
                try {
                    return new SimpleScalar(this.env.formatDateToPlainText(this.dateModel, key, stringBI.this.target, stringBI.this, true));
                }
                catch (TemplateException e) {
                    throw _CoreAPI.ensureIsTemplateModelException("Failed to format value", e);
                }
            }

            @Override
            public String getAsString() throws TemplateModelException {
                if (this.cachedValue == null) {
                    if (this.defaultFormat == null) {
                        if (this.dateModel.getDateType() == 0) {
                            throw _MessageUtil.newCantFormatUnknownTypeDateException(stringBI.this.target, null);
                        }
                        throw new BugException();
                    }
                    try {
                        this.cachedValue = EvalUtil.assertFormatResultNotNull(this.defaultFormat.formatToPlainText(this.dateModel));
                    }
                    catch (TemplateValueFormatException e) {
                        try {
                            throw _MessageUtil.newCantFormatDateException(this.defaultFormat, stringBI.this.target, e, true);
                        }
                        catch (TemplateException e2) {
                            throw _CoreAPI.ensureIsTemplateModelException("Failed to format date/time/datetime", e2);
                        }
                    }
                }
                return this.cachedValue;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        }

        private class BooleanFormatter
        implements TemplateScalarModel,
        TemplateMethodModel {
            private final TemplateBooleanModel bool;
            private final Environment env;

            BooleanFormatter(TemplateBooleanModel bool, Environment env) {
                this.bool = bool;
                this.env = env;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                stringBI.this.checkMethodArgCount(args, 2);
                return new SimpleScalar((String)args.get(this.bool.getAsBoolean() ? 0 : 1));
            }

            @Override
            public String getAsString() throws TemplateModelException {
                if (this.bool instanceof TemplateScalarModel) {
                    return ((TemplateScalarModel)((Object)this.bool)).getAsString();
                }
                try {
                    return this.env.formatBoolean(this.bool.getAsBoolean(), true);
                }
                catch (TemplateException e) {
                    throw new TemplateModelException(e);
                }
            }
        }
    }

    static class sizeBI
    extends BuiltIn {
        private int countingLimit;

        sizeBI() {
        }

        @Override
        protected void setTarget(Expression target) {
            super.setTarget(target);
            target.enableLazilyGeneratedResult();
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            int size;
            TemplateModel model = this.target.eval(env);
            if (this.countingLimit == 1 && model instanceof TemplateCollectionModelEx) {
                size = ((TemplateCollectionModelEx)model).isEmpty() ? 0 : 1;
            } else if (model instanceof TemplateSequenceModel) {
                size = ((TemplateSequenceModel)model).size();
            } else if (model instanceof TemplateCollectionModelEx) {
                size = ((TemplateCollectionModelEx)model).size();
            } else if (model instanceof TemplateHashModelEx) {
                size = ((TemplateHashModelEx)model).size();
            } else if (model instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)model).isSequence()) {
                TemplateModelIterator iterator = ((LazilyGeneratedCollectionModel)model).iterator();
                int counter = 0;
                while (iterator.hasNext() && ++counter != this.countingLimit) {
                    iterator.next();
                }
                size = counter;
            } else {
                throw new UnexpectedTypeException(this.target, model, "extended-hash or sequence or extended collection", new Class[]{TemplateHashModelEx.class, TemplateSequenceModel.class, TemplateCollectionModelEx.class}, env);
            }
            return new SimpleNumber(size);
        }

        void setCountingLimit(int cmpOperator, NumberLiteral rightOperand) {
            int cmpInt;
            try {
                cmpInt = NumberUtil.toIntExact(rightOperand.getAsNumber());
            }
            catch (ArithmeticException e) {
                return;
            }
            switch (cmpOperator) {
                case 1: {
                    this.countingLimit = cmpInt + 1;
                    break;
                }
                case 2: {
                    this.countingLimit = cmpInt + 1;
                    break;
                }
                case 3: {
                    this.countingLimit = cmpInt;
                    break;
                }
                case 4: {
                    this.countingLimit = cmpInt + 1;
                    break;
                }
                case 5: {
                    this.countingLimit = cmpInt + 1;
                    break;
                }
                case 6: {
                    this.countingLimit = cmpInt;
                    break;
                }
                default: {
                    throw new BugException("Unsupported comparator operator code: " + cmpOperator);
                }
            }
        }
    }

    static class namespaceBI
    extends BuiltIn {
        namespaceBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            if (!(tm instanceof Macro)) {
                throw new UnexpectedTypeException(this.target, tm, "macro or function", new Class[]{Macro.class}, env);
            }
            return env.getMacroNamespace((Macro)tm);
        }
    }

    static class is_transformBI
    extends BuiltIn {
        is_transformBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateTransformModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_stringBI
    extends BuiltIn {
        is_stringBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateScalarModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_sequenceBI
    extends BuiltIn {
        is_sequenceBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateSequenceModel && (!(tm instanceof OverloadedMethodsModel) && !(tm instanceof SimpleMethodModel) || !env.isIcI2324OrLater()) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_numberBI
    extends BuiltIn {
        is_numberBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateNumberModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_nodeBI
    extends BuiltIn {
        is_nodeBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateNodeModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_methodBI
    extends BuiltIn {
        is_methodBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateMethodModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_markup_outputBI
    extends BuiltIn {
        is_markup_outputBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateMarkupOutputModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_macroBI
    extends BuiltIn {
        is_macroBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof Macro ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_indexableBI
    extends BuiltIn {
        is_indexableBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateSequenceModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_hashBI
    extends BuiltIn {
        is_hashBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateHashModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_hash_exBI
    extends BuiltIn {
        is_hash_exBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateHashModelEx ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_enumerableBI
    extends BuiltIn {
        is_enumerableBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return (tm instanceof TemplateSequenceModel || tm instanceof TemplateCollectionModel) && (_TemplateAPI.getTemplateLanguageVersionAsInt(this) < _VersionInts.V_2_3_21 || !(tm instanceof SimpleMethodModel) && !(tm instanceof OverloadedMethodsModel)) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_directiveBI
    extends BuiltIn {
        is_directiveBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateTransformModel || tm instanceof Macro || tm instanceof TemplateDirectiveModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_dateOfTypeBI
    extends BuiltIn {
        private final int dateType;

        is_dateOfTypeBI(int dateType) {
            this.dateType = dateType;
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateDateModel && ((TemplateDateModel)tm).getDateType() == this.dateType ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_dateLikeBI
    extends BuiltIn {
        is_dateLikeBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateDateModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_collection_exBI
    extends BuiltIn {
        is_collection_exBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateCollectionModelEx ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_collectionBI
    extends BuiltIn {
        is_collectionBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateCollectionModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_booleanBI
    extends BuiltIn {
        is_booleanBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateBooleanModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class has_apiBI
    extends BuiltIn {
        has_apiBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel tm = this.target.eval(env);
            this.target.assertNonNull(tm, env);
            return tm instanceof TemplateModelWithAPISupport ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class apiBI
    extends BuiltIn {
        apiBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            if (!env.isAPIBuiltinEnabled()) {
                throw new _MiscTemplateException((Expression)this, "Can't use ?api, because the \"", "api_builtin_enabled", "\" configuration setting is false. Think twice before you set it to true though. Especially, it shouldn't abused for modifying Map-s and Collection-s.");
            }
            TemplateModel tm = this.target.eval(env);
            if (!(tm instanceof TemplateModelWithAPISupport)) {
                this.target.assertNonNull(tm, env);
                throw new APINotSupportedTemplateException(env, this.target, tm);
            }
            return ((TemplateModelWithAPISupport)tm).getAPI();
        }
    }

    static class dateBI
    extends BuiltIn {
        private final int dateType;

        dateBI(int dateType) {
            this.dateType = dateType;
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateDateModel) {
                TemplateDateModel dmodel = (TemplateDateModel)model;
                int dtype = dmodel.getDateType();
                if (this.dateType == dtype) {
                    return model;
                }
                if (dtype == 0 || dtype == 3) {
                    return new SimpleDate(dmodel.getAsDate(), this.dateType);
                }
                throw new _MiscTemplateException((Expression)this, "Cannot convert ", TemplateDateModel.TYPE_NAMES.get(dtype), " to ", TemplateDateModel.TYPE_NAMES.get(this.dateType));
            }
            String s = this.target.evalAndCoerceToPlainText(env);
            return new DateParser(s, env);
        }

        private class DateParser
        implements TemplateDateModel,
        TemplateMethodModel,
        TemplateHashModel {
            private final String text;
            private final Environment env;
            private final TemplateDateFormat defaultFormat;
            private TemplateDateModel cachedValue;

            DateParser(String text, Environment env) throws TemplateException {
                this.text = text;
                this.env = env;
                this.defaultFormat = env.getTemplateDateFormat(dateBI.this.dateType, Date.class, dateBI.this.target, false);
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                dateBI.this.checkMethodArgCount(args, 0, 1);
                return args.size() == 0 ? this.getAsDateModel() : this.get((String)args.get(0));
            }

            @Override
            public TemplateModel get(String pattern) throws TemplateModelException {
                TemplateDateFormat format;
                try {
                    format = this.env.getTemplateDateFormat(pattern, dateBI.this.dateType, Date.class, dateBI.this.target, dateBI.this, true);
                }
                catch (TemplateException e) {
                    throw _CoreAPI.ensureIsTemplateModelException("Failed to get format", e);
                }
                return this.toTemplateDateModel(this.parse(format));
            }

            private TemplateDateModel toTemplateDateModel(Object date) throws _TemplateModelException {
                if (date instanceof Date) {
                    return new SimpleDate((Date)date, dateBI.this.dateType);
                }
                TemplateDateModel tm = (TemplateDateModel)date;
                if (tm.getDateType() != dateBI.this.dateType) {
                    throw new _TemplateModelException("The result of the parsing was of the wrong date type.");
                }
                return tm;
            }

            private TemplateDateModel getAsDateModel() throws TemplateModelException {
                if (this.cachedValue == null) {
                    this.cachedValue = this.toTemplateDateModel(this.parse(this.defaultFormat));
                }
                return this.cachedValue;
            }

            @Override
            public Date getAsDate() throws TemplateModelException {
                return this.getAsDateModel().getAsDate();
            }

            @Override
            public int getDateType() {
                return dateBI.this.dateType;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            private Object parse(TemplateDateFormat df) throws TemplateModelException {
                try {
                    return df.parse(this.text, dateBI.this.dateType);
                }
                catch (TemplateValueFormatException e) {
                    throw new _TemplateModelException((Throwable)e, "The string doesn't match the expected date/time/date-time format. The string to parse was: ", new _DelayedJQuote(this.text), ". ", "The expected format was: ", new _DelayedJQuote(df.getDescription()), ".", e.getMessage() != null ? "\nThe nested reason given follows:\n" : "", e.getMessage() != null ? e.getMessage() : "");
                }
            }
        }
    }

    private static abstract class AbstractCLikeBI
    extends BuiltIn {
        private AbstractCLikeBI() {
        }

        @Override
        final TemplateModel _eval(Environment env) throws TemplateException {
            String result;
            TemplateModel model = this.target.eval(env);
            if (model instanceof TemplateNumberModel) {
                TemplateNumberFormat cTemplateNumberFormat = env.getCTemplateNumberFormat();
                try {
                    result = cTemplateNumberFormat.formatToPlainText((TemplateNumberModel)model);
                }
                catch (TemplateValueFormatException e) {
                    throw _MessageUtil.newCantFormatNumberException(cTemplateNumberFormat, this.target, e, false);
                }
            } else if (model instanceof TemplateBooleanModel) {
                boolean b = ((TemplateBooleanModel)model).getAsBoolean();
                CFormat cFormat = env.getCFormat();
                result = b ? cFormat.getTrueString() : cFormat.getFalseString();
            } else if (model instanceof TemplateScalarModel) {
                String s = EvalUtil.modelToString((TemplateScalarModel)model, this.target, env);
                result = env.getCFormat().formatString(s, env);
            } else if (model == null) {
                result = this.formatNull(env);
            } else {
                throw new UnexpectedTypeException(this.target, model, "number, boolean, or string", new Class[]{TemplateNumberModel.class, TemplateBooleanModel.class, TemplateScalarModel.class}, env);
            }
            return new SimpleScalar(result);
        }

        protected abstract String formatNull(Environment var1) throws InvalidReferenceException;
    }

    static class cnBI
    extends AbstractCLikeBI {
        cnBI() {
        }

        @Override
        protected final String formatNull(Environment env) {
            return env.getCFormat().getNullString();
        }
    }

    static class cBI
    extends AbstractCLikeBI {
        cBI() {
        }

        @Override
        protected final String formatNull(Environment env) throws InvalidReferenceException {
            throw InvalidReferenceException.getInstance(this.target, env);
        }
    }
}

