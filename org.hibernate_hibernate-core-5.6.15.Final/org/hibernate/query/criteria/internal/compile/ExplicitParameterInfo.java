/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query.criteria.internal.compile;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Parameter;

public class ExplicitParameterInfo<T>
implements Parameter<T> {
    private final String name;
    private final Integer position;
    private final Class<T> type;

    public ExplicitParameterInfo(String name, Integer position, Class<T> type) {
        if (name == null && position == null) {
            throw new IllegalStateException("Both name and position were null; caller should have generated parameter name");
        }
        if (name != null && position != null) {
            throw new IllegalStateException("Both name and position were specified");
        }
        this.name = name;
        this.position = position;
        this.type = type;
    }

    public boolean isNamed() {
        return this.name != null;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPosition() {
        return this.position;
    }

    public Class<T> getParameterType() {
        return this.type;
    }

    public String render() {
        return this.isNamed() ? ":" + this.name : "?" + this.position.toString();
    }

    public void validateBindValue(Object value) {
        if (value == null) {
            return;
        }
        if (!this.getParameterType().isInstance(value)) {
            if (this.isNamed()) {
                throw new IllegalArgumentException(String.format("Named parameter [%s] type mismatch; expecting [%s] but found [%s]", this.getName(), this.getParameterType().getSimpleName(), value.getClass().getSimpleName()));
            }
            throw new IllegalArgumentException(String.format("Positional parameter [%s] type mismatch; expecting [%s] but found [%s]", this.getPosition(), this.getParameterType().getSimpleName(), value.getClass().getSimpleName()));
        }
    }

    public void validateCalendarBind() {
        if (!Calendar.class.isAssignableFrom(this.getParameterType())) {
            if (this.isNamed()) {
                throw new IllegalArgumentException(String.format("Named parameter [%s] type mismatch; Calendar was passed, but parameter defined as [%s]", this.getName(), this.getParameterType().getSimpleName()));
            }
            throw new IllegalArgumentException(String.format("Positional parameter [%s] type mismatch; Calendar was passed, but parameter defined as [%s]", this.getPosition(), this.getParameterType().getSimpleName()));
        }
    }

    public void validateDateBind() {
        if (!Date.class.isAssignableFrom(this.getParameterType())) {
            if (this.isNamed()) {
                throw new IllegalArgumentException(String.format("Named parameter [%s] type mismatch; Date was passed, but parameter defined as [%s]", this.getName(), this.getParameterType().getSimpleName()));
            }
            throw new IllegalArgumentException(String.format("Positional parameter [%s] type mismatch; Date was passed, but parameter defined as [%s]", this.getPosition(), this.getParameterType().getSimpleName()));
        }
    }
}

