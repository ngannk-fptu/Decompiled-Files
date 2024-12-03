/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.AnyPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.ExceptionPredicate;
import org.apache.commons.collections.functors.FalsePredicate;
import org.apache.commons.collections.functors.IdentityPredicate;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.functors.NonePredicate;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.NullIsExceptionPredicate;
import org.apache.commons.collections.functors.NullIsFalsePredicate;
import org.apache.commons.collections.functors.NullIsTruePredicate;
import org.apache.commons.collections.functors.NullPredicate;
import org.apache.commons.collections.functors.OnePredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.apache.commons.collections.functors.TransformedPredicate;
import org.apache.commons.collections.functors.TransformerPredicate;
import org.apache.commons.collections.functors.TruePredicate;
import org.apache.commons.collections.functors.UniquePredicate;

public class PredicateUtils {
    public static Predicate exceptionPredicate() {
        return ExceptionPredicate.INSTANCE;
    }

    public static Predicate truePredicate() {
        return TruePredicate.INSTANCE;
    }

    public static Predicate falsePredicate() {
        return FalsePredicate.INSTANCE;
    }

    public static Predicate nullPredicate() {
        return NullPredicate.INSTANCE;
    }

    public static Predicate notNullPredicate() {
        return NotNullPredicate.INSTANCE;
    }

    public static Predicate equalPredicate(Object value) {
        return EqualPredicate.getInstance(value);
    }

    public static Predicate identityPredicate(Object value) {
        return IdentityPredicate.getInstance(value);
    }

    public static Predicate instanceofPredicate(Class type) {
        return InstanceofPredicate.getInstance(type);
    }

    public static Predicate uniquePredicate() {
        return UniquePredicate.getInstance();
    }

    public static Predicate invokerPredicate(String methodName) {
        return PredicateUtils.asPredicate(InvokerTransformer.getInstance(methodName));
    }

    public static Predicate invokerPredicate(String methodName, Class[] paramTypes, Object[] args) {
        return PredicateUtils.asPredicate(InvokerTransformer.getInstance(methodName, paramTypes, args));
    }

    public static Predicate andPredicate(Predicate predicate1, Predicate predicate2) {
        return AndPredicate.getInstance(predicate1, predicate2);
    }

    public static Predicate allPredicate(Predicate[] predicates) {
        return AllPredicate.getInstance(predicates);
    }

    public static Predicate allPredicate(Collection predicates) {
        return AllPredicate.getInstance(predicates);
    }

    public static Predicate orPredicate(Predicate predicate1, Predicate predicate2) {
        return OrPredicate.getInstance(predicate1, predicate2);
    }

    public static Predicate anyPredicate(Predicate[] predicates) {
        return AnyPredicate.getInstance(predicates);
    }

    public static Predicate anyPredicate(Collection predicates) {
        return AnyPredicate.getInstance(predicates);
    }

    public static Predicate eitherPredicate(Predicate predicate1, Predicate predicate2) {
        return PredicateUtils.onePredicate(new Predicate[]{predicate1, predicate2});
    }

    public static Predicate onePredicate(Predicate[] predicates) {
        return OnePredicate.getInstance(predicates);
    }

    public static Predicate onePredicate(Collection predicates) {
        return OnePredicate.getInstance(predicates);
    }

    public static Predicate neitherPredicate(Predicate predicate1, Predicate predicate2) {
        return PredicateUtils.nonePredicate(new Predicate[]{predicate1, predicate2});
    }

    public static Predicate nonePredicate(Predicate[] predicates) {
        return NonePredicate.getInstance(predicates);
    }

    public static Predicate nonePredicate(Collection predicates) {
        return NonePredicate.getInstance(predicates);
    }

    public static Predicate notPredicate(Predicate predicate) {
        return NotPredicate.getInstance(predicate);
    }

    public static Predicate asPredicate(Transformer transformer) {
        return TransformerPredicate.getInstance(transformer);
    }

    public static Predicate nullIsExceptionPredicate(Predicate predicate) {
        return NullIsExceptionPredicate.getInstance(predicate);
    }

    public static Predicate nullIsFalsePredicate(Predicate predicate) {
        return NullIsFalsePredicate.getInstance(predicate);
    }

    public static Predicate nullIsTruePredicate(Predicate predicate) {
        return NullIsTruePredicate.getInstance(predicate);
    }

    public static Predicate transformedPredicate(Transformer transformer, Predicate predicate) {
        return TransformedPredicate.getInstance(transformer, predicate);
    }
}

