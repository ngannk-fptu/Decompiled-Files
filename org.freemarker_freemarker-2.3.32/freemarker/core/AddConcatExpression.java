/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.CollectionAndSequence;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.NonStringException;
import freemarker.core.NonStringOrTemplateOutputException;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.TemplateObject;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.util.HashSet;
import java.util.Set;

final class AddConcatExpression
extends Expression {
    private final Expression left;
    private final Expression right;

    AddConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return AddConcatExpression._eval(env, this, this.left, this.left.eval(env), this.right, this.right.eval(env));
    }

    static TemplateModel _eval(Environment env, TemplateObject parent, Expression leftExp, TemplateModel leftModel, Expression rightExp, TemplateModel rightModel) throws TemplateModelException, TemplateException, NonStringException {
        if (leftModel instanceof TemplateNumberModel && rightModel instanceof TemplateNumberModel) {
            Number first = EvalUtil.modelToNumber((TemplateNumberModel)leftModel, leftExp);
            Number second = EvalUtil.modelToNumber((TemplateNumberModel)rightModel, rightExp);
            return AddConcatExpression._evalOnNumbers(env, parent, first, second);
        }
        if (leftModel instanceof TemplateSequenceModel && rightModel instanceof TemplateSequenceModel) {
            return new ConcatenatedSequence((TemplateSequenceModel)leftModel, (TemplateSequenceModel)rightModel);
        }
        boolean hashConcatPossible = leftModel instanceof TemplateHashModel && rightModel instanceof TemplateHashModel;
        try {
            Object leftOMOrStr = EvalUtil.coerceModelToStringOrMarkup(leftModel, leftExp, hashConcatPossible, null, env);
            if (leftOMOrStr == null) {
                return AddConcatExpression._eval_concatenateHashes(leftModel, rightModel);
            }
            Object rightOMOrStr = EvalUtil.coerceModelToStringOrMarkup(rightModel, rightExp, hashConcatPossible, null, env);
            if (rightOMOrStr == null) {
                return AddConcatExpression._eval_concatenateHashes(leftModel, rightModel);
            }
            if (leftOMOrStr instanceof String) {
                if (rightOMOrStr instanceof String) {
                    return new SimpleScalar(((String)leftOMOrStr).concat((String)rightOMOrStr));
                }
                TemplateMarkupOutputModel rightMO = (TemplateMarkupOutputModel)rightOMOrStr;
                return EvalUtil.concatMarkupOutputs(parent, rightMO.getOutputFormat().fromPlainTextByEscaping((String)leftOMOrStr), rightMO);
            }
            TemplateMarkupOutputModel leftMO = (TemplateMarkupOutputModel)leftOMOrStr;
            if (rightOMOrStr instanceof String) {
                return EvalUtil.concatMarkupOutputs(parent, leftMO, leftMO.getOutputFormat().fromPlainTextByEscaping((String)rightOMOrStr));
            }
            return EvalUtil.concatMarkupOutputs(parent, leftMO, (TemplateMarkupOutputModel)rightOMOrStr);
        }
        catch (NonStringOrTemplateOutputException e) {
            if (hashConcatPossible) {
                return AddConcatExpression._eval_concatenateHashes(leftModel, rightModel);
            }
            throw e;
        }
    }

    private static TemplateModel _eval_concatenateHashes(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {
        if (leftModel instanceof TemplateHashModelEx && rightModel instanceof TemplateHashModelEx) {
            TemplateHashModelEx leftModelEx = (TemplateHashModelEx)leftModel;
            TemplateHashModelEx rightModelEx = (TemplateHashModelEx)rightModel;
            if (leftModelEx.size() == 0) {
                return rightModelEx;
            }
            if (rightModelEx.size() == 0) {
                return leftModelEx;
            }
            return new ConcatenatedHashEx(leftModelEx, rightModelEx);
        }
        return new ConcatenatedHash((TemplateHashModel)leftModel, (TemplateHashModel)rightModel);
    }

    static TemplateModel _evalOnNumbers(Environment env, TemplateObject parent, Number first, Number second) throws TemplateException {
        ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
        return new SimpleNumber(ae.add(first, second));
    }

    @Override
    boolean isLiteral() {
        return this.constantValue != null || this.left.isLiteral() && this.right.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new AddConcatExpression(this.left.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    public String getCanonicalForm() {
        return this.left.getCanonicalForm() + " + " + this.right.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return "+";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        return idx == 0 ? this.left : this.right;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }

    private static final class ConcatenatedHashEx
    extends ConcatenatedHash
    implements TemplateHashModelEx {
        private CollectionAndSequence keys;
        private CollectionAndSequence values;

        ConcatenatedHashEx(TemplateHashModelEx left, TemplateHashModelEx right) {
            super(left, right);
        }

        @Override
        public int size() throws TemplateModelException {
            this.initKeys();
            return this.keys.size();
        }

        @Override
        public TemplateCollectionModel keys() throws TemplateModelException {
            this.initKeys();
            return this.keys;
        }

        @Override
        public TemplateCollectionModel values() throws TemplateModelException {
            this.initValues();
            return this.values;
        }

        private void initKeys() throws TemplateModelException {
            if (this.keys == null) {
                HashSet keySet = new HashSet();
                SimpleSequence keySeq = new SimpleSequence(32, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                ConcatenatedHashEx.addKeys(keySet, keySeq, (TemplateHashModelEx)this.left);
                ConcatenatedHashEx.addKeys(keySet, keySeq, (TemplateHashModelEx)this.right);
                this.keys = new CollectionAndSequence(keySeq);
            }
        }

        private static void addKeys(Set keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
            TemplateModelIterator it = hash.keys().iterator();
            while (it.hasNext()) {
                TemplateScalarModel tsm = (TemplateScalarModel)it.next();
                if (!keySet.add(tsm.getAsString())) continue;
                keySeq.add(tsm);
            }
        }

        private void initValues() throws TemplateModelException {
            if (this.values == null) {
                SimpleSequence seq = new SimpleSequence(this.size(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                int ln = this.keys.size();
                for (int i = 0; i < ln; ++i) {
                    seq.add(this.get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
                }
                this.values = new CollectionAndSequence(seq);
            }
        }
    }

    private static class ConcatenatedHash
    implements TemplateHashModel {
        protected final TemplateHashModel left;
        protected final TemplateHashModel right;

        ConcatenatedHash(TemplateHashModel left, TemplateHashModel right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel model = this.right.get(key);
            return model != null ? model : this.left.get(key);
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return this.left.isEmpty() && this.right.isEmpty();
        }
    }

    private static final class ConcatenatedSequence
    implements TemplateSequenceModel {
        private final TemplateSequenceModel left;
        private final TemplateSequenceModel right;

        ConcatenatedSequence(TemplateSequenceModel left, TemplateSequenceModel right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.left.size() + this.right.size();
        }

        @Override
        public TemplateModel get(int i) throws TemplateModelException {
            int ls = this.left.size();
            return i < ls ? this.left.get(i) : this.right.get(i - ls);
        }
    }
}

