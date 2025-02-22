/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.CollectionExpressionBase;
import com.querydsl.core.types.dsl.Constants;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import javax.annotation.Nullable;

public abstract class CollectionPathBase<C extends Collection<E>, E, Q extends SimpleExpression<? super E>>
extends CollectionExpressionBase<C, E>
implements Path<C> {
    private static final long serialVersionUID = -9004995667633601298L;
    @Nullable
    private volatile transient Constructor<?> constructor;
    private volatile transient boolean usePathInits = false;
    private final PathInits inits;

    public CollectionPathBase(PathImpl<C> mixin, PathInits inits) {
        super(mixin);
        this.inits = inits;
    }

    public abstract Q any();

    protected Q newInstance(Class<Q> queryType, PathMetadata pm) {
        try {
            if (this.constructor == null) {
                if (Constants.isTyped(queryType)) {
                    try {
                        this.constructor = queryType.getDeclaredConstructor(Class.class, PathMetadata.class, PathInits.class);
                        this.usePathInits = true;
                    }
                    catch (NoSuchMethodException e) {
                        this.constructor = queryType.getDeclaredConstructor(Class.class, PathMetadata.class);
                    }
                } else {
                    try {
                        this.constructor = queryType.getDeclaredConstructor(PathMetadata.class, PathInits.class);
                        this.usePathInits = true;
                    }
                    catch (NoSuchMethodException e) {
                        this.constructor = queryType.getDeclaredConstructor(PathMetadata.class);
                    }
                }
                this.constructor.setAccessible(true);
            }
            if (Constants.isTyped(queryType)) {
                if (this.usePathInits) {
                    return (Q)((SimpleExpression)this.constructor.newInstance(this.getElementType(), pm, this.inits));
                }
                return (Q)((SimpleExpression)this.constructor.newInstance(this.getElementType(), pm));
            }
            if (this.usePathInits) {
                return (Q)((SimpleExpression)this.constructor.newInstance(pm, this.inits));
            }
            return (Q)((SimpleExpression)this.constructor.newInstance(pm));
        }
        catch (NoSuchMethodException e) {
            throw new ExpressionException(e);
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e);
        }
    }
}

