/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.Collection;
import java.util.Map;
import org.hibernate.criterion.BetweenExpression;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.EmptyExpression;
import org.hibernate.criterion.ForeignKeyExpression;
import org.hibernate.criterion.ForeignKeyNullExpression;
import org.hibernate.criterion.IdentifierEqExpression;
import org.hibernate.criterion.InExpression;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.NaturalIdentifier;
import org.hibernate.criterion.NotEmptyExpression;
import org.hibernate.criterion.NotExpression;
import org.hibernate.criterion.NotNullExpression;
import org.hibernate.criterion.NullExpression;
import org.hibernate.criterion.PropertyExpression;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.SizeExpression;
import org.hibernate.type.Type;

public class Restrictions {
    public static Criterion idEq(Object value) {
        return new IdentifierEqExpression(value);
    }

    public static Criterion fkEq(String associationPropertyName, Object value) {
        return new ForeignKeyExpression(associationPropertyName, value, "=");
    }

    public static Criterion fkNe(String associationPropertyName, Object value) {
        return new ForeignKeyExpression(associationPropertyName, value, "<>");
    }

    public static Criterion fkIsNotNull(String associationPropertyName) {
        return new ForeignKeyNullExpression(associationPropertyName, true);
    }

    public static Criterion fkIsNull(String associationPropertyName) {
        return new ForeignKeyNullExpression(associationPropertyName);
    }

    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "=");
    }

    public static Criterion eqOrIsNull(String propertyName, Object value) {
        return value == null ? Restrictions.isNull(propertyName) : Restrictions.eq(propertyName, value);
    }

    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<>");
    }

    public static Criterion neOrIsNotNull(String propertyName, Object value) {
        return value == null ? Restrictions.isNotNull(propertyName) : Restrictions.ne(propertyName, value);
    }

    public static SimpleExpression like(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, " like ");
    }

    public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
        return new SimpleExpression(propertyName, matchMode.toMatchString(value), " like ");
    }

    public static Criterion ilike(String propertyName, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return Restrictions.ilike(propertyName, value.toString(), MatchMode.EXACT);
    }

    public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
        if (value == null) {
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new LikeExpression(propertyName, value, matchMode, null, true);
    }

    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">");
    }

    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<");
    }

    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<=");
    }

    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">=");
    }

    public static Criterion between(String propertyName, Object low, Object high) {
        return new BetweenExpression(propertyName, low, high);
    }

    public static Criterion in(String propertyName, Object ... values) {
        return new InExpression(propertyName, values);
    }

    public static Criterion in(String propertyName, Collection values) {
        return new InExpression(propertyName, values.toArray());
    }

    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName);
    }

    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "=");
    }

    public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<>");
    }

    public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<");
    }

    public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, "<=");
    }

    public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">");
    }

    public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
        return new PropertyExpression(propertyName, otherPropertyName, ">=");
    }

    public static LogicalExpression and(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "and");
    }

    public static Conjunction and(Criterion ... predicates) {
        return Restrictions.conjunction(predicates);
    }

    public static LogicalExpression or(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "or");
    }

    public static Disjunction or(Criterion ... predicates) {
        return Restrictions.disjunction(predicates);
    }

    public static Criterion not(Criterion expression) {
        return new NotExpression(expression);
    }

    public static Criterion sqlRestriction(String sql, Object[] values, Type[] types) {
        return new SQLCriterion(sql, values, types);
    }

    public static Criterion sqlRestriction(String sql, Object value, Type type) {
        return new SQLCriterion(sql, value, type);
    }

    public static Criterion sqlRestriction(String sql) {
        return new SQLCriterion(sql);
    }

    public static Conjunction conjunction() {
        return new Conjunction();
    }

    public static Conjunction conjunction(Criterion ... conditions) {
        return new Conjunction(conditions);
    }

    public static Disjunction disjunction() {
        return new Disjunction();
    }

    public static Disjunction disjunction(Criterion ... conditions) {
        return new Disjunction(conditions);
    }

    public static Criterion allEq(Map<String, ?> propertyNameValues) {
        Conjunction conj = Restrictions.conjunction();
        for (Map.Entry<String, ?> entry : propertyNameValues.entrySet()) {
            conj.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        return conj;
    }

    public static Criterion isEmpty(String propertyName) {
        return new EmptyExpression(propertyName);
    }

    public static Criterion isNotEmpty(String propertyName) {
        return new NotEmptyExpression(propertyName);
    }

    public static Criterion sizeEq(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "=");
    }

    public static Criterion sizeNe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<>");
    }

    public static Criterion sizeGt(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<");
    }

    public static Criterion sizeLt(String propertyName, int size) {
        return new SizeExpression(propertyName, size, ">");
    }

    public static Criterion sizeGe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, "<=");
    }

    public static Criterion sizeLe(String propertyName, int size) {
        return new SizeExpression(propertyName, size, ">=");
    }

    public static NaturalIdentifier naturalId() {
        return new NaturalIdentifier();
    }

    protected Restrictions() {
    }
}

