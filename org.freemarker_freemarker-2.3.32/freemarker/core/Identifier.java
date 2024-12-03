/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core._CoreStringUtils;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class Identifier
extends Expression {
    private final String name;

    Identifier(String name) {
        this.name = name;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        try {
            return env.getVariable(this.name);
        }
        catch (NullPointerException e) {
            if (env == null) {
                throw new _MiscTemplateException("Variables are not available (certainly you are in a parse-time executed directive). The name of the variable you tried to read: ", this.name);
            }
            throw e;
        }
    }

    @Override
    public String getCanonicalForm() {
        return _CoreStringUtils.toFTLTopLevelIdentifierReference(this.name);
    }

    String getName() {
        return this.name;
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

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        if (this.name.equals(replacedIdentifier)) {
            if (replacementState.replacementAlreadyInUse) {
                Expression clone = replacement.deepCloneWithIdentifierReplaced(null, null, replacementState);
                clone.copyLocationFrom(replacement);
                return clone;
            }
            replacementState.replacementAlreadyInUse = true;
            return replacement;
        }
        return new Identifier(this.name);
    }
}

