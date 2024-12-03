/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.Expression
 *  org.springframework.expression.spel.SpelNode
 *  org.springframework.expression.spel.ast.CompoundExpression
 *  org.springframework.expression.spel.ast.MethodReference
 *  org.springframework.expression.spel.ast.PropertyOrFieldReference
 *  org.springframework.expression.spel.standard.SpelExpression
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.spel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.data.util.Streamable;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ExpressionDependencies
implements Streamable<ExpressionDependency> {
    private static final ExpressionDependencies EMPTY = new ExpressionDependencies(Collections.emptyList());
    private final List<ExpressionDependency> dependencies;

    private ExpressionDependencies(List<ExpressionDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public static ExpressionDependencies none() {
        return EMPTY;
    }

    public static ExpressionDependencies of(Collection<ExpressionDependency> dependencies) {
        if (dependencies.isEmpty()) {
            return EMPTY;
        }
        return new ExpressionDependencies(new ArrayList<ExpressionDependency>(new LinkedHashSet<ExpressionDependency>(dependencies)));
    }

    public static ExpressionDependencies merged(Iterable<ExpressionDependencies> dependencies) {
        if (!dependencies.iterator().hasNext()) {
            return EMPTY;
        }
        ArrayList<ExpressionDependency> dependencySet = new ArrayList<ExpressionDependency>();
        dependencies.forEach(it -> dependencySet.addAll(it.dependencies));
        return ExpressionDependencies.of(dependencySet);
    }

    public static ExpressionDependencies discover(Expression expression) {
        return expression instanceof SpelExpression ? ExpressionDependencies.discover(((SpelExpression)expression).getAST(), true) : ExpressionDependencies.none();
    }

    public static ExpressionDependencies discover(SpelNode root, boolean topLevelOnly) {
        ArrayList<ExpressionDependency> dependencies = new ArrayList<ExpressionDependency>();
        ExpressionDependencies.collectDependencies(root, 0, expressionDependency -> {
            if (!topLevelOnly || expressionDependency.isTopLevel()) {
                dependencies.add((ExpressionDependency)expressionDependency);
            }
        });
        return new ExpressionDependencies(dependencies);
    }

    private static void collectDependencies(SpelNode node, int compoundPosition, Consumer<ExpressionDependency> dependencies) {
        if (node instanceof MethodReference) {
            dependencies.accept(ExpressionDependency.forMethod(((MethodReference)node).getName()).nest(compoundPosition));
        }
        if (node instanceof PropertyOrFieldReference) {
            dependencies.accept(ExpressionDependency.forPropertyOrField(((PropertyOrFieldReference)node).getName()).nest(compoundPosition));
        }
        for (int i = 0; i < node.getChildCount(); ++i) {
            ExpressionDependencies.collectDependencies(node.getChild(i), node instanceof CompoundExpression ? i : 0, dependencies);
        }
    }

    public ExpressionDependencies mergeWith(ExpressionDependencies other) {
        Assert.notNull((Object)other, (String)"Other ExpressionDependencies must not be null");
        LinkedHashSet<ExpressionDependency> dependencySet = new LinkedHashSet<ExpressionDependency>(this.dependencies.size() + other.dependencies.size());
        dependencySet.addAll(this.dependencies);
        dependencySet.addAll(other.dependencies);
        return new ExpressionDependencies(new ArrayList<ExpressionDependency>(dependencySet));
    }

    @Override
    public Iterator<ExpressionDependency> iterator() {
        return this.dependencies.iterator();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExpressionDependencies)) {
            return false;
        }
        ExpressionDependencies that = (ExpressionDependencies)o;
        return ObjectUtils.nullSafeEquals(this.dependencies, that.dependencies);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.dependencies);
    }

    public static class ExpressionDependency {
        private final DependencyType type;
        private final String symbol;
        private final int nestLevel;

        private ExpressionDependency(DependencyType type, String symbol, int nestLevel) {
            this.symbol = symbol;
            this.nestLevel = nestLevel;
            this.type = type;
        }

        public static ExpressionDependency forMethod(String methodName) {
            return new ExpressionDependency(DependencyType.METHOD, methodName, 0);
        }

        public static ExpressionDependency forPropertyOrField(String fieldOrPropertyName) {
            return new ExpressionDependency(DependencyType.PROPERTY, fieldOrPropertyName, 0);
        }

        public ExpressionDependency nest(int level) {
            return this.nestLevel == level ? this : new ExpressionDependency(this.type, this.symbol, level);
        }

        public boolean isNested() {
            return !this.isTopLevel();
        }

        public boolean isTopLevel() {
            return this.nestLevel == 0;
        }

        public boolean isMethod() {
            return this.type == DependencyType.METHOD;
        }

        public boolean isPropertyOrField() {
            return this.type == DependencyType.PROPERTY;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ExpressionDependency)) {
                return false;
            }
            ExpressionDependency that = (ExpressionDependency)o;
            if (this.nestLevel != that.nestLevel) {
                return false;
            }
            if (this.type != that.type) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.symbol, (Object)that.symbol);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)((Object)this.type));
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.symbol);
            result = 31 * result + this.nestLevel;
            return result;
        }

        public String toString() {
            return "ExpressionDependency{type=" + (Object)((Object)this.type) + ", symbol='" + this.symbol + '\'' + ", nestLevel=" + this.nestLevel + '}';
        }

        static enum DependencyType {
            PROPERTY,
            METHOD;

        }
    }
}

