/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.UniqueElements;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.util.CollectionHelper;

public class UniqueElementsValidator
implements ConstraintValidator<UniqueElements, Collection> {
    public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
        if (collection == null || collection.size() < 2) {
            return true;
        }
        List<Object> duplicates = this.findDuplicates(collection);
        if (duplicates.isEmpty()) {
            return true;
        }
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext)constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("duplicates", duplicates.stream().map(String::valueOf).collect(Collectors.joining(", "))).withDynamicPayload(CollectionHelper.toImmutableList(duplicates));
        }
        return false;
    }

    private List<Object> findDuplicates(Collection<?> collection) {
        HashSet uniqueElements = CollectionHelper.newHashSet(collection.size());
        return collection.stream().filter(o -> !uniqueElements.add(o)).collect(Collectors.toList());
    }
}

