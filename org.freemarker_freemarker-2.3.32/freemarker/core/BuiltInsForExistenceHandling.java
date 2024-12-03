/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.ParentheticalExpression;
import freemarker.core._MessageUtil;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

class BuiltInsForExistenceHandling {
    private BuiltInsForExistenceHandling() {
    }

    static class if_existsBI
    extends ExistenceBuiltIn {
        if_existsBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.evalMaybeNonexistentTarget(env);
            return model == null ? TemplateModel.NOTHING : model;
        }
    }

    static class has_contentBI
    extends ExistenceBuiltIn {
        has_contentBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            return Expression.isEmpty(this.evalMaybeNonexistentTarget(env)) ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
        }

        @Override
        boolean evalToBoolean(Environment env) throws TemplateException {
            return this._eval(env) == TemplateBooleanModel.TRUE;
        }
    }

    static class existsBI
    extends ExistenceBuiltIn {
        existsBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            return this.evalMaybeNonexistentTarget(env) == null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
        }

        @Override
        boolean evalToBoolean(Environment env) throws TemplateException {
            return this._eval(env) == TemplateBooleanModel.TRUE;
        }
    }

    static class defaultBI
    extends ExistenceBuiltIn {
        private static final TemplateMethodModelEx FIRST_NON_NULL_METHOD = new TemplateMethodModelEx(){

            @Override
            public Object exec(List args) throws TemplateModelException {
                int argCnt = args.size();
                if (argCnt == 0) {
                    throw _MessageUtil.newArgCntError("?default", argCnt, 1, Integer.MAX_VALUE);
                }
                for (int i = 0; i < argCnt; ++i) {
                    TemplateModel result = (TemplateModel)args.get(i);
                    if (result == null) continue;
                    return result;
                }
                return null;
            }
        };

        defaultBI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel model = this.evalMaybeNonexistentTarget(env);
            return model == null ? FIRST_NON_NULL_METHOD : new ConstantMethod(model);
        }

        private static class ConstantMethod
        implements TemplateMethodModelEx {
            private final TemplateModel constant;

            ConstantMethod(TemplateModel constant) {
                this.constant = constant;
            }

            @Override
            public Object exec(List args) {
                return this.constant;
            }
        }
    }

    private static abstract class ExistenceBuiltIn
    extends BuiltIn {
        private ExistenceBuiltIn() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected TemplateModel evalMaybeNonexistentTarget(Environment env) throws TemplateException {
            TemplateModel tm;
            if (this.target instanceof ParentheticalExpression) {
                boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
                try {
                    tm = this.target.eval(env);
                }
                catch (InvalidReferenceException ire) {
                    tm = null;
                }
                finally {
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                }
            } else {
                tm = this.target.eval(env);
            }
            return tm;
        }
    }
}

