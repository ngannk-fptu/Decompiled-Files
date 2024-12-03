/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.StringLiteral;
import freemarker.core._DelayedGetMessage;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

final class ListLiteral
extends Expression {
    final ArrayList<Expression> items;

    ListLiteral(ArrayList<Expression> items) {
        this.items = items;
        items.trimToSize();
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        SimpleSequence list = new SimpleSequence(this.items.size(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        for (Expression exp : this.items) {
            TemplateModel tm = exp.eval(env);
            if (env == null || !env.isClassicCompatible()) {
                exp.assertNonNull(tm, env);
            }
            list.add(tm);
        }
        return list;
    }

    List getValueList(Environment env) throws TemplateException {
        int size = this.items.size();
        switch (size) {
            case 0: {
                return Collections.EMPTY_LIST;
            }
            case 1: {
                return Collections.singletonList(this.items.get(0).evalAndCoerceToPlainText(env));
            }
        }
        ArrayList<String> result = new ArrayList<String>(this.items.size());
        ListIterator<Expression> iterator = this.items.listIterator();
        while (iterator.hasNext()) {
            Expression exp = iterator.next();
            result.add(exp.evalAndCoerceToPlainText(env));
        }
        return result;
    }

    List getModelList(Environment env) throws TemplateException {
        int size = this.items.size();
        switch (size) {
            case 0: {
                return Collections.EMPTY_LIST;
            }
            case 1: {
                return Collections.singletonList(this.items.get(0).eval(env));
            }
        }
        ArrayList<TemplateModel> result = new ArrayList<TemplateModel>(this.items.size());
        ListIterator<Expression> iterator = this.items.listIterator();
        while (iterator.hasNext()) {
            Expression exp = iterator.next();
            result.add(exp.eval(env));
        }
        return result;
    }

    @Override
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder("[");
        int size = this.items.size();
        for (int i = 0; i < size; ++i) {
            Expression value = this.items.get(i);
            buf.append(value.getCanonicalForm());
            if (i == size - 1) continue;
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "[...]";
    }

    @Override
    boolean isLiteral() {
        if (this.constantValue != null) {
            return true;
        }
        for (int i = 0; i < this.items.size(); ++i) {
            Expression exp = this.items.get(i);
            if (exp.isLiteral()) continue;
            return false;
        }
        return true;
    }

    TemplateSequenceModel evaluateStringsToNamespaces(Environment env) throws TemplateException {
        TemplateSequenceModel val = (TemplateSequenceModel)this.eval(env);
        SimpleSequence result = new SimpleSequence(val.size(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        for (int i = 0; i < this.items.size(); ++i) {
            Expression itemExpr = this.items.get(i);
            if (itemExpr instanceof StringLiteral) {
                String s = ((StringLiteral)itemExpr).getAsString();
                try {
                    Environment.Namespace ns = env.importLib(s, null);
                    result.add(ns);
                    continue;
                }
                catch (IOException ioe) {
                    throw new _MiscTemplateException((Expression)((StringLiteral)itemExpr), "Couldn't import library ", new _DelayedJQuote(s), ": ", new _DelayedGetMessage(ioe));
                }
            }
            result.add(val.get(i));
        }
        return result;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        ArrayList clonedValues = (ArrayList)this.items.clone();
        ListIterator<Expression> iter = clonedValues.listIterator();
        while (iter.hasNext()) {
            iter.set(((Expression)iter.next()).deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        return new ListLiteral(clonedValues);
    }

    @Override
    int getParameterCount() {
        return this.items != null ? this.items.size() : 0;
    }

    @Override
    Object getParameterValue(int idx) {
        this.checkIndex(idx);
        return this.items.get(idx);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        this.checkIndex(idx);
        return ParameterRole.ITEM_VALUE;
    }

    private void checkIndex(int idx) {
        if (this.items == null || idx >= this.items.size()) {
            throw new IndexOutOfBoundsException();
        }
    }
}

