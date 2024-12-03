/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.ParameterRole;
import freemarker.core.ParentheticalExpression;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.Constants;
import java.util.ArrayList;

class DefaultToExpression
extends Expression {
    private static final TemplateCollectionModel EMPTY_COLLECTION = new SimpleCollection(new ArrayList(0));
    static final TemplateModel EMPTY_STRING_AND_SEQUENCE_AND_HASH = new EmptyStringAndSequenceAndHash();
    private final Expression lho;
    private final Expression rho;

    DefaultToExpression(Expression lho, Expression rho) {
        this.lho = lho;
        this.rho = rho;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel left;
        if (this.lho instanceof ParentheticalExpression) {
            boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
            try {
                left = this.lho.eval(env);
            }
            catch (InvalidReferenceException ire) {
                left = null;
            }
            finally {
                env.setFastInvalidReferenceExceptions(lastFIRE);
            }
        } else {
            left = this.lho.eval(env);
        }
        if (left != null) {
            return left;
        }
        if (this.rho == null) {
            return EMPTY_STRING_AND_SEQUENCE_AND_HASH;
        }
        return this.rho.eval(env);
    }

    @Override
    boolean isLiteral() {
        return false;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new DefaultToExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho != null ? this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState) : null);
    }

    @Override
    public String getCanonicalForm() {
        if (this.rho == null) {
            return this.lho.getCanonicalForm() + '!';
        }
        return this.lho.getCanonicalForm() + '!' + this.rho.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return "...!...";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.lho;
            }
            case 1: {
                return this.rho;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }

    private static class EmptyStringAndSequenceAndHash
    implements TemplateScalarModel,
    TemplateSequenceModel,
    TemplateHashModelEx2 {
        private EmptyStringAndSequenceAndHash() {
        }

        @Override
        public String getAsString() {
            return "";
        }

        @Override
        public TemplateModel get(int i) {
            return null;
        }

        @Override
        public TemplateModel get(String s) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public TemplateCollectionModel keys() {
            return EMPTY_COLLECTION;
        }

        @Override
        public TemplateCollectionModel values() {
            return EMPTY_COLLECTION;
        }

        @Override
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
            return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
        }
    }
}

