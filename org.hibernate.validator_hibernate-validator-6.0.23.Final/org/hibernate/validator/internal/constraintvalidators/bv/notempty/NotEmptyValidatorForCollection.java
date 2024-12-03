/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.constraints.NotEmpty
 */
package org.hibernate.validator.internal.constraintvalidators.bv.notempty;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotEmpty;

public class NotEmptyValidatorForCollection
implements ConstraintValidator<NotEmpty, Collection> {
    public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
        if (collection == null) {
            return false;
        }
        return collection.size() > 0;
    }
}

