/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.MiscUtil;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._MessageUtil;
import freemarker.template.EmptyMap;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TransformBlock
extends TemplateElement {
    private Expression transformExpression;
    Map namedArgs;
    private volatile transient SoftReference sortedNamedArgsCache;

    TransformBlock(Expression transformExpression, Map namedArgs, TemplateElements children) {
        this.transformExpression = transformExpression;
        this.namedArgs = namedArgs;
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Map args;
        TemplateTransformModel ttm = env.getTransform(this.transformExpression);
        if (ttm != null) {
            if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
                args = new HashMap();
                for (Map.Entry entry : this.namedArgs.entrySet()) {
                    String key = (String)entry.getKey();
                    Expression valueExp = (Expression)entry.getValue();
                    TemplateModel value = valueExp.eval(env);
                    args.put(key, value);
                }
            } else {
                args = EmptyMap.instance;
            }
        } else {
            TemplateModel tm = this.transformExpression.eval(env);
            throw new UnexpectedTypeException(this.transformExpression, tm, "transform", new Class[]{TemplateTransformModel.class}, env);
        }
        env.visitAndTransform(this.getChildBuffer(), ttm, args);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        sb.append(' ');
        sb.append(this.transformExpression);
        if (this.namedArgs != null) {
            for (Map.Entry entry : this.getSortedNamedArgs()) {
                sb.append(' ');
                sb.append(entry.getKey());
                sb.append('=');
                _MessageUtil.appendExpressionAsUntearable(sb, (Expression)entry.getValue());
            }
        }
        if (canonical) {
            sb.append(">");
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</").append(this.getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#transform";
    }

    @Override
    int getParameterCount() {
        return 1 + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0);
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.transformExpression;
        }
        if (this.namedArgs != null && idx - 1 < this.namedArgs.size() * 2) {
            Map.Entry namedArg = (Map.Entry)this.getSortedNamedArgs().get((idx - 1) / 2);
            return (idx - 1) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.CALLEE;
        }
        if (idx - 1 < this.namedArgs.size() * 2) {
            return (idx - 1) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
        }
        throw new IndexOutOfBoundsException();
    }

    private List getSortedNamedArgs() {
        List res;
        SoftReference ref = this.sortedNamedArgsCache;
        if (ref != null && (res = (List)((Reference)ref).get()) != null) {
            return res;
        }
        res = MiscUtil.sortMapOfExpressions(this.namedArgs);
        this.sortedNamedArgsCache = new SoftReference<List>(res);
        return res;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}

