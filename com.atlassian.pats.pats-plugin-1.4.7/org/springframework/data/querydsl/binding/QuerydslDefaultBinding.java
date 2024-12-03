/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.util.Assert;

class QuerydslDefaultBinding
implements MultiValueBinding<Path<? extends Object>, Object> {
    QuerydslDefaultBinding() {
    }

    @Override
    public Optional<Predicate> bind(Path<?> path, Collection<? extends Object> value) {
        Assert.notNull(path, (String)"Path must not be null!");
        Assert.notNull(value, (String)"Value must not be null!");
        if (value.isEmpty()) {
            return Optional.empty();
        }
        if (path instanceof CollectionPathBase) {
            BooleanBuilder builder = new BooleanBuilder();
            for (Object object : value) {
                builder.and(((CollectionPathBase)path).contains(object));
            }
            return Optional.of(builder.getValue());
        }
        if (path instanceof SimpleExpression) {
            SimpleExpression expression = (SimpleExpression)((Object)path);
            if (value.size() > 1) {
                return Optional.of(expression.in(value));
            }
            Object object = value.iterator().next();
            return Optional.of(object == null ? expression.isNull() : expression.eq(object));
        }
        throw new IllegalArgumentException(String.format("Cannot create predicate for path '%s' with type '%s'.", path, path.getMetadata().getPathType()));
    }
}

