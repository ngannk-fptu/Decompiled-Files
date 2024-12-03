/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Macro;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._MessageUtil;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class BuiltInsForCallables {
    BuiltInsForCallables() {
    }

    static final class with_args_lastBI
    extends AbstractWithArgsBI {
        with_args_lastBI() {
        }

        @Override
        protected boolean isOrderLast() {
            return true;
        }
    }

    static final class with_argsBI
    extends AbstractWithArgsBI {
        with_argsBI() {
        }

        @Override
        protected boolean isOrderLast() {
            return false;
        }
    }

    static abstract class AbstractWithArgsBI
    extends BuiltIn {
        AbstractWithArgsBI() {
        }

        protected abstract boolean isOrderLast();

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.target.eval(env);
            if (model instanceof Macro) {
                return new BIMethodForMacroAndFunction((Macro)model);
            }
            if (model instanceof TemplateDirectiveModel) {
                return new BIMethodForDirective((TemplateDirectiveModel)model);
            }
            if (model instanceof TemplateMethodModel) {
                return new BIMethodForMethod((TemplateMethodModel)model);
            }
            throw new UnexpectedTypeException(this.target, model, "macro, function, directive, or method", new Class[]{Macro.class, TemplateDirectiveModel.class, TemplateMethodModel.class}, env);
        }

        private class BIMethodForDirective
        implements TemplateMethodModelEx {
            private final TemplateDirectiveModel directive;

            public BIMethodForDirective(TemplateDirectiveModel directive) {
                this.directive = directive;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel)args.get(0);
                if (argTM instanceof TemplateHashModelEx) {
                    final TemplateHashModelEx withArgs = (TemplateHashModelEx)argTM;
                    return new TemplateDirectiveModel(){

                        @Override
                        public void execute(Environment env, Map origArgs, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                            int withArgsSize = withArgs.size();
                            LinkedHashMap<String, TemplateModel> newArgs = new LinkedHashMap<String, TemplateModel>((withArgsSize + origArgs.size()) * 4 / 3, 1.0f);
                            TemplateHashModelEx2.KeyValuePairIterator withArgsIter = TemplateModelUtils.getKeyValuePairIterator(withArgs);
                            if (AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.putAll(origArgs);
                                while (withArgsIter.hasNext()) {
                                    TemplateHashModelEx2.KeyValuePair withArgsKVP = withArgsIter.next();
                                    String argName = this.getArgumentName(withArgsKVP);
                                    if (newArgs.containsKey(argName)) continue;
                                    newArgs.put(argName, withArgsKVP.getValue());
                                }
                            } else {
                                while (withArgsIter.hasNext()) {
                                    TemplateHashModelEx2.KeyValuePair withArgsKVP = withArgsIter.next();
                                    newArgs.put(this.getArgumentName(withArgsKVP), withArgsKVP.getValue());
                                }
                                newArgs.putAll(origArgs);
                            }
                            BIMethodForDirective.this.directive.execute(env, newArgs, loopVars, body);
                        }

                        private String getArgumentName(TemplateHashModelEx2.KeyValuePair withArgsKVP) throws TemplateModelException {
                            TemplateModel argNameTM = withArgsKVP.getKey();
                            if (!(argNameTM instanceof TemplateScalarModel)) {
                                throw new _TemplateModelException("Expected string keys in the ?", AbstractWithArgsBI.this.key, "(...) arguments, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), ".");
                            }
                            return EvalUtil.modelToString((TemplateScalarModel)argNameTM, null, null);
                        }
                    };
                }
                if (argTM instanceof TemplateSequenceModel) {
                    throw new _TemplateModelException("When applied on a directive, ?", AbstractWithArgsBI.this.key, "(...) can't have a sequence argument. Use a hash argument.");
                }
                throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
            }
        }

        private class BIMethodForMethod
        implements TemplateMethodModelEx {
            private final TemplateMethodModel method;

            public BIMethodForMethod(TemplateMethodModel method) {
                this.method = method;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel)args.get(0);
                if (argTM instanceof TemplateSequenceModel) {
                    final TemplateSequenceModel withArgs = (TemplateSequenceModel)argTM;
                    if (this.method instanceof TemplateMethodModelEx) {
                        return new TemplateMethodModelEx(){

                            @Override
                            public Object exec(List origArgs) throws TemplateModelException {
                                int withArgsSize = withArgs.size();
                                ArrayList<TemplateModel> newArgs = new ArrayList<TemplateModel>(withArgsSize + origArgs.size());
                                if (AbstractWithArgsBI.this.isOrderLast()) {
                                    newArgs.addAll(origArgs);
                                }
                                for (int i = 0; i < withArgsSize; ++i) {
                                    newArgs.add(withArgs.get(i));
                                }
                                if (!AbstractWithArgsBI.this.isOrderLast()) {
                                    newArgs.addAll(origArgs);
                                }
                                return BIMethodForMethod.this.method.exec(newArgs);
                            }
                        };
                    }
                    return new TemplateMethodModel(){

                        @Override
                        public Object exec(List origArgs) throws TemplateModelException {
                            int withArgsSize = withArgs.size();
                            ArrayList<String> newArgs = new ArrayList<String>(withArgsSize + origArgs.size());
                            if (AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.addAll(origArgs);
                            }
                            for (int i = 0; i < withArgsSize; ++i) {
                                TemplateModel argVal = withArgs.get(i);
                                newArgs.add(this.argValueToString(argVal));
                            }
                            if (!AbstractWithArgsBI.this.isOrderLast()) {
                                newArgs.addAll(origArgs);
                            }
                            return BIMethodForMethod.this.method.exec(newArgs);
                        }

                        private String argValueToString(TemplateModel argVal) throws TemplateModelException {
                            String argValStr;
                            if (argVal instanceof TemplateScalarModel) {
                                argValStr = ((TemplateScalarModel)argVal).getAsString();
                            } else if (argVal == null) {
                                argValStr = null;
                            } else {
                                try {
                                    argValStr = EvalUtil.coerceModelToPlainText(argVal, null, null, Environment.getCurrentEnvironment());
                                }
                                catch (TemplateException e) {
                                    throw new _TemplateModelException((Throwable)e, "Failed to convert method argument to string. Argument type was: ", new _DelayedFTLTypeDescription(argVal));
                                }
                            }
                            return argValStr;
                        }
                    };
                }
                if (argTM instanceof TemplateHashModelEx) {
                    throw new _TemplateModelException("When applied on a method, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument.");
                }
                throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
            }
        }

        private class BIMethodForMacroAndFunction
        implements TemplateMethodModelEx {
            private final Macro macroOrFunction;

            private BIMethodForMacroAndFunction(Macro macroOrFunction) {
                this.macroOrFunction = macroOrFunction;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                Macro.WithArgs withArgs;
                AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
                TemplateModel argTM = (TemplateModel)args.get(0);
                if (argTM instanceof TemplateSequenceModel) {
                    withArgs = new Macro.WithArgs((TemplateSequenceModel)argTM, AbstractWithArgsBI.this.isOrderLast());
                } else if (argTM instanceof TemplateHashModelEx) {
                    if (this.macroOrFunction.isFunction()) {
                        throw new _TemplateModelException("When applied on a function, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument.");
                    }
                    withArgs = new Macro.WithArgs((TemplateHashModelEx)argTM, AbstractWithArgsBI.this.isOrderLast());
                } else {
                    throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
                }
                return new Macro(this.macroOrFunction, withArgs);
            }
        }
    }
}

