/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package com.querydsl.core.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.Visitor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SerializerBase<S extends SerializerBase<S>>
implements Visitor<Void, Void> {
    private static final Set<Operator> SAME_PRECEDENCE = ImmutableSet.of((Object)Ops.CASE, (Object)Ops.CASE_WHEN, (Object)Ops.CASE_ELSE, (Object)Ops.CASE_EQ, (Object)Ops.CASE_EQ_WHEN, (Object)Ops.CASE_EQ_ELSE, (Object[])new Operator[0]);
    private final StringBuilder builder = new StringBuilder(128);
    private String constantPrefix = "a";
    private String paramPrefix = "p";
    private String anonParamPrefix = "_";
    private Map<Object, String> constantToLabel;
    private final S self = this;
    private final Templates templates;
    private boolean strict = true;

    public SerializerBase(Templates templates) {
        this.templates = templates;
    }

    public final S prepend(String str) {
        this.builder.insert(0, str);
        return this.self;
    }

    public final S insert(int position, String str) {
        this.builder.insert(position, str);
        return this.self;
    }

    public final S append(String str) {
        this.builder.append(str);
        return this.self;
    }

    protected String getConstantPrefix() {
        return this.constantPrefix;
    }

    public Map<Object, String> getConstantToLabel() {
        if (this.constantToLabel == null) {
            this.constantToLabel = new HashMap<Object, String>(4);
        }
        return this.constantToLabel;
    }

    protected int getLength() {
        return this.builder.length();
    }

    protected final Template getTemplate(Operator op) {
        return this.templates.getTemplate(op);
    }

    public final S handle(Expression<?> expr) {
        expr.accept(this, null);
        return this.self;
    }

    public final S handle(Object arg) {
        if (arg instanceof Expression) {
            ((Expression)arg).accept(this, null);
        } else {
            this.visitConstant(arg);
        }
        return this.self;
    }

    public final S handle(JoinFlag joinFlag) {
        return this.handle(joinFlag.getFlag());
    }

    public final S handle(String sep, Expression<?>[] expressions) {
        this.handle(sep, Arrays.asList(expressions));
        return this.self;
    }

    public final S handle(String sep, List<? extends Expression<?>> expressions) {
        for (int i = 0; i < expressions.size(); ++i) {
            if (i != 0) {
                this.append(sep);
            }
            this.handle(expressions.get(i));
        }
        return this.self;
    }

    protected void handleTemplate(Template template, List<?> args) {
        for (Template.Element element : template.getElements()) {
            Object rv = element.convert(args);
            if (rv instanceof Expression) {
                ((Expression)rv).accept(this, null);
                continue;
            }
            if (element.isString()) {
                this.builder.append(rv.toString());
                continue;
            }
            this.visitConstant(rv);
        }
    }

    public final boolean serialize(QueryFlag.Position position, Set<QueryFlag> flags) {
        boolean handled = false;
        for (QueryFlag flag : flags) {
            if (flag.getPosition() != position) continue;
            this.handle(flag.getFlag());
            handled = true;
        }
        return handled;
    }

    public final boolean serialize(JoinFlag.Position position, Set<JoinFlag> flags) {
        boolean handled = false;
        for (JoinFlag flag : flags) {
            if (flag.getPosition() != position) continue;
            this.handle(flag.getFlag());
            handled = true;
        }
        return handled;
    }

    public void setConstantPrefix(String prefix) {
        this.constantPrefix = prefix;
    }

    public void setParamPrefix(String prefix) {
        this.paramPrefix = prefix;
    }

    public void setAnonParamPrefix(String prefix) {
        this.anonParamPrefix = prefix;
    }

    @Deprecated
    public void setNormalize(boolean normalize) {
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String toString() {
        return this.builder.toString();
    }

    @Override
    public final Void visit(Constant<?> expr, Void context) {
        this.visitConstant(expr.getConstant());
        return null;
    }

    public void visitConstant(Object constant) {
        if (!this.getConstantToLabel().containsKey(constant)) {
            String constLabel = this.constantPrefix + (this.getConstantToLabel().size() + 1);
            this.getConstantToLabel().put(constant, constLabel);
            this.append(constLabel);
        } else {
            this.append(this.getConstantToLabel().get(constant));
        }
    }

    @Override
    public Void visit(ParamExpression<?> param, Void context) {
        String paramLabel = param.isAnon() ? this.anonParamPrefix + param.getName() : this.paramPrefix + param.getName();
        this.getConstantToLabel().put(param, paramLabel);
        this.append(paramLabel);
        return null;
    }

    @Override
    public Void visit(TemplateExpression<?> expr, Void context) {
        this.handleTemplate(expr.getTemplate(), expr.getArgs());
        return null;
    }

    @Override
    public Void visit(FactoryExpression<?> expr, Void context) {
        this.handle(", ", expr.getArgs());
        return null;
    }

    @Override
    public Void visit(Operation<?> expr, Void context) {
        this.visitOperation(expr.getType(), expr.getOperator(), expr.getArgs());
        return null;
    }

    @Override
    public Void visit(Path<?> path, Void context) {
        PathType pathType = path.getMetadata().getPathType();
        Template template = this.templates.getTemplate(pathType);
        Object element = path.getMetadata().getElement();
        ImmutableList args = path.getMetadata().getParent() != null ? ImmutableList.of(path.getMetadata().getParent(), (Object)element) : ImmutableList.of((Object)element);
        this.handleTemplate(template, (List<?>)args);
        return null;
    }

    protected void visitOperation(Class<?> type, Operator operator, List<? extends Expression<?>> args) {
        Template template = this.templates.getTemplate(operator);
        if (template != null) {
            int precedence = this.templates.getPrecedence(operator);
            boolean first = true;
            for (Template.Element element : template.getElements()) {
                Object rv = element.convert(args);
                if (rv instanceof Expression) {
                    Expression expr = (Expression)rv;
                    if (precedence > -1 && expr instanceof Operation) {
                        Operator op = ((Operation)expr).getOperator();
                        int opPrecedence = this.templates.getPrecedence(op);
                        if (precedence < opPrecedence) {
                            ((SerializerBase)((SerializerBase)this.append("(")).handle(expr)).append(")");
                        } else if (!first && precedence == opPrecedence && !SAME_PRECEDENCE.contains(op)) {
                            ((SerializerBase)((SerializerBase)this.append("(")).handle(expr)).append(")");
                        } else {
                            this.handle(expr);
                        }
                    } else {
                        this.handle(expr);
                    }
                    first = false;
                    continue;
                }
                if (element.isString()) {
                    this.append(rv.toString());
                    continue;
                }
                this.visitConstant(rv);
            }
        } else {
            if (this.strict) {
                throw new IllegalArgumentException("No pattern found for " + operator);
            }
            this.append(operator.toString());
            this.append("(");
            this.handle(", ", args);
            this.append(")");
        }
    }
}

