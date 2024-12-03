/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInWithParseTimeParameters;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.ParseException;
import freemarker.core.Token;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.List;

final class BuiltInsWithLazyConditionals {
    private BuiltInsWithLazyConditionals() {
    }

    static class switch_BI
    extends BuiltInWithParseTimeParameters {
        private List<Expression> parameters;

        switch_BI() {
        }

        @Override
        void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
            if (parameters.size() < 2) {
                throw this.newArgumentCountException("must have at least 2", openParen, closeParen);
            }
            this.parameters = parameters;
        }

        @Override
        protected List<Expression> getArgumentsAsList() {
            return this.parameters;
        }

        @Override
        protected int getArgumentsCount() {
            return this.parameters.size();
        }

        @Override
        protected Expression getArgumentParameterValue(int argIdx) {
            return this.parameters.get(argIdx);
        }

        @Override
        protected void cloneArguments(Expression clone, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
            ArrayList<Expression> parametersClone = new ArrayList<Expression>(this.parameters.size());
            for (Expression parameter : this.parameters) {
                parametersClone.add(parameter.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
            }
            ((switch_BI)clone).parameters = parametersClone;
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            TemplateModel targetValue = this.target.evalToNonMissing(env);
            List<Expression> parameters = this.parameters;
            int paramCnt = parameters.size();
            int i = 0;
            while (i + 1 < paramCnt) {
                Expression caseExp = parameters.get(i);
                TemplateModel caseValue = caseExp.evalToNonMissing(env);
                if (EvalUtil.compare(targetValue, this.target, 1, "==", caseValue, caseExp, this, true, false, false, false, env)) {
                    return parameters.get(i + 1).evalToNonMissing(env);
                }
                i += 2;
            }
            if (paramCnt % 2 == 0) {
                throw new _MiscTemplateException(this.target, "The value before ?", this.key, "(case1, value1, case2, value2, ...) didn't match any of the case parameters, and there was no default value parameter (an additional last parameter) eithter. ");
            }
            return parameters.get(paramCnt - 1).evalToNonMissing(env);
        }
    }

    static class then_BI
    extends BuiltInWithParseTimeParameters {
        private Expression whenTrueExp;
        private Expression whenFalseExp;

        then_BI() {
        }

        @Override
        TemplateModel _eval(Environment env) throws TemplateException {
            boolean lho = this.target.evalToBoolean(env);
            return (lho ? this.whenTrueExp : this.whenFalseExp).evalToNonMissing(env);
        }

        @Override
        void bindToParameters(List<Expression> parameters, Token openParen, Token closeParen) throws ParseException {
            if (parameters.size() != 2) {
                throw this.newArgumentCountException("requires exactly 2", openParen, closeParen);
            }
            this.whenTrueExp = parameters.get(0);
            this.whenFalseExp = parameters.get(1);
        }

        @Override
        protected Expression getArgumentParameterValue(int argIdx) {
            switch (argIdx) {
                case 0: {
                    return this.whenTrueExp;
                }
                case 1: {
                    return this.whenFalseExp;
                }
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        protected int getArgumentsCount() {
            return 2;
        }

        @Override
        protected List<Expression> getArgumentsAsList() {
            ArrayList<Expression> args = new ArrayList<Expression>(2);
            args.add(this.whenTrueExp);
            args.add(this.whenFalseExp);
            return args;
        }

        @Override
        protected void cloneArguments(Expression cloneExp, String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
            then_BI clone = (then_BI)cloneExp;
            clone.whenTrueExp = this.whenTrueExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
            clone.whenFalseExp = this.whenFalseExp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState);
        }
    }
}

