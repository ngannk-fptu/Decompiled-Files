/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.auditing.utils;

import com.atlassian.audit.entity.ChangedValue;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

public class ChangedValuesCalculator<T> {
    private static final int MAX_STRING_LENGTH = 250;
    private final T oldObject;
    private final T newObject;

    public ChangedValuesCalculator(@Nullable T oldObject, @Nullable T newObject) {
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    public Optional<ChangedValue> getDifference(String key, Function<T, Object> function) {
        Object oldValue;
        if (this.oldObject != null && this.newObject != null) {
            Object newValue;
            Object oldValue2 = function.apply(this.oldObject);
            if (!Objects.equals(oldValue2, newValue = function.apply(this.newObject))) {
                return Optional.of(ChangedValue.fromI18nKeys((String)key).from(this.convertToString(oldValue2)).to(this.convertToString(newValue)).build());
            }
        } else if (this.newObject != null) {
            Object newValue = function.apply(this.newObject);
            if (newValue != null) {
                return Optional.of(ChangedValue.fromI18nKeys((String)key).to(this.convertToString(newValue)).build());
            }
        } else if (this.oldObject != null && (oldValue = function.apply(this.oldObject)) != null) {
            return Optional.of(ChangedValue.fromI18nKeys((String)key).from(this.convertToString(oldValue)).build());
        }
        return Optional.empty();
    }

    private String convertToString(Object obj) {
        Object stringRepresentation = Objects.toString(obj, "");
        if (((String)stringRepresentation).length() > 250) {
            stringRepresentation = ((String)stringRepresentation).substring(0, 250) + "...";
        }
        return stringRepresentation;
    }
}

