/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.TemplateRenderer;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class SQLFunctionTemplate
implements SQLFunction {
    private final Type type;
    private final TemplateRenderer renderer;
    private final boolean hasParenthesesIfNoArgs;

    public SQLFunctionTemplate(Type type, String template) {
        this(type, template, true);
    }

    public SQLFunctionTemplate(Type type, String template, boolean hasParenthesesIfNoArgs) {
        this.type = type;
        this.renderer = new TemplateRenderer(template);
        this.hasParenthesesIfNoArgs = hasParenthesesIfNoArgs;
    }

    @Override
    public String render(Type argumentType, List args, SessionFactoryImplementor factory) {
        return this.renderer.render(args, factory);
    }

    @Override
    public Type getReturnType(Type argumentType, Mapping mapping) throws QueryException {
        return this.type;
    }

    @Override
    public boolean hasArguments() {
        return this.renderer.getAnticipatedNumberOfArguments() > 0;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return this.hasParenthesesIfNoArgs;
    }

    public String toString() {
        return this.renderer.getTemplate();
    }
}

