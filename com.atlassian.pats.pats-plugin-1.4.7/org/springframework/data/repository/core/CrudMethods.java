/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core;

import java.lang.reflect.Method;
import java.util.Optional;

public interface CrudMethods {
    public Optional<Method> getSaveMethod();

    public boolean hasSaveMethod();

    public Optional<Method> getFindAllMethod();

    public boolean hasFindAllMethod();

    public Optional<Method> getFindOneMethod();

    public boolean hasFindOneMethod();

    public Optional<Method> getDeleteMethod();

    public boolean hasDelete();
}

