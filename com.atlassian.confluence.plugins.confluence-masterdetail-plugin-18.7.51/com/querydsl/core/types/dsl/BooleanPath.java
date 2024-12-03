/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.lang.reflect.AnnotatedElement;

public class BooleanPath
extends BooleanExpression
implements Path<Boolean> {
    private static final long serialVersionUID = 6590516706769430565L;
    private final PathImpl<Boolean> pathMixin;

    protected BooleanPath(PathImpl<Boolean> mixin) {
        super((Expression<Boolean>)mixin);
        this.pathMixin = mixin;
    }

    protected BooleanPath(Path<?> parent, String property) {
        this(PathMetadataFactory.forProperty(parent, property));
    }

    protected BooleanPath(PathMetadata metadata) {
        super((Expression<Boolean>)ExpressionUtils.path(Boolean.class, metadata));
        this.pathMixin = (PathImpl)this.mixin;
    }

    protected BooleanPath(String var) {
        this(PathMetadataFactory.forVariable(var));
    }

    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public PathMetadata getMetadata() {
        return this.pathMixin.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return this.pathMixin.getRoot();
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return this.pathMixin.getAnnotatedElement();
    }
}

