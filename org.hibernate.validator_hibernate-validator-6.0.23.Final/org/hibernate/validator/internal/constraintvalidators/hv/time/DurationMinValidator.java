/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators.hv.time;

import java.time.Duration;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.time.DurationMin;

public class DurationMinValidator
implements ConstraintValidator<DurationMin, Duration> {
    private Duration minDuration;
    private boolean inclusive;

    public void initialize(DurationMin constraintAnnotation) {
        this.minDuration = Duration.ofNanos(constraintAnnotation.nanos()).plusMillis(constraintAnnotation.millis()).plusSeconds(constraintAnnotation.seconds()).plusMinutes(constraintAnnotation.minutes()).plusHours(constraintAnnotation.hours()).plusDays(constraintAnnotation.days());
        this.inclusive = constraintAnnotation.inclusive();
    }

    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int comparisonResult = this.minDuration.compareTo(value);
        return this.inclusive ? comparisonResult <= 0 : comparisonResult < 0;
    }
}

