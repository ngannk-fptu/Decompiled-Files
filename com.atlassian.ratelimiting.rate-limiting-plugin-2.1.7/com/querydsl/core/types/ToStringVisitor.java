/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.Visitor;
import java.util.Arrays;
import java.util.List;

public final class ToStringVisitor
implements Visitor<String, Templates> {
    public static final ToStringVisitor DEFAULT = new ToStringVisitor();

    private ToStringVisitor() {
    }

    @Override
    public String visit(Constant<?> e, Templates templates) {
        return e.getConstant().toString();
    }

    @Override
    public String visit(FactoryExpression<?> e, Templates templates) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ").append(e.getType().getSimpleName()).append("(");
        boolean first = true;
        for (Expression<?> arg : e.getArgs()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(arg.accept(this, templates));
            first = false;
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(Operation<?> o, Templates templates) {
        Template template = templates.getTemplate(o.getOperator());
        if (template != null) {
            int precedence = templates.getPrecedence(o.getOperator());
            StringBuilder builder = new StringBuilder();
            for (Template.Element element : template.getElements()) {
                Object rv = element.convert(o.getArgs());
                if (rv instanceof Expression) {
                    if (precedence > -1 && rv instanceof Operation && precedence < templates.getPrecedence(((Operation)rv).getOperator())) {
                        builder.append("(");
                        builder.append(((Expression)rv).accept(this, templates));
                        builder.append(")");
                        continue;
                    }
                    builder.append(((Expression)rv).accept(this, templates));
                    continue;
                }
                builder.append(rv.toString());
            }
            return builder.toString();
        }
        return "unknown operation with operator " + o.getOperator().name() + " and args " + o.getArgs();
    }

    @Override
    public String visit(ParamExpression<?> param, Templates templates) {
        return "{" + param.getName() + "}";
    }

    @Override
    public String visit(Path<?> p, Templates templates) {
        Path<?> parent = p.getMetadata().getParent();
        Object elem = p.getMetadata().getElement();
        if (parent != null) {
            Template pattern = templates.getTemplate(p.getMetadata().getPathType());
            if (pattern != null) {
                List<Object> args = Arrays.asList(parent, elem);
                StringBuilder builder = new StringBuilder();
                for (Template.Element element : pattern.getElements()) {
                    Object rv = element.convert(args);
                    if (rv instanceof Expression) {
                        builder.append(((Expression)rv).accept(this, templates));
                        continue;
                    }
                    builder.append(rv.toString());
                }
                return builder.toString();
            }
            throw new IllegalArgumentException("No pattern for " + p.getMetadata().getPathType());
        }
        return elem.toString();
    }

    @Override
    public String visit(SubQueryExpression<?> expr, Templates templates) {
        return expr.getMetadata().toString();
    }

    @Override
    public String visit(TemplateExpression<?> expr, Templates templates) {
        StringBuilder builder = new StringBuilder();
        for (Template.Element element : expr.getTemplate().getElements()) {
            Object rv = element.convert(expr.getArgs());
            if (rv instanceof Expression) {
                builder.append(((Expression)rv).accept(this, templates));
                continue;
            }
            builder.append(rv.toString());
        }
        return builder.toString();
    }
}

