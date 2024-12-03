/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator$Context
 *  javax.validation.Path
 */
package org.hibernate.validator.messageinterpolation;

import java.util.Map;
import javax.validation.MessageInterpolator;
import javax.validation.Path;

public interface HibernateMessageInterpolatorContext
extends MessageInterpolator.Context {
    public Class<?> getRootBeanType();

    public Map<String, Object> getMessageParameters();

    public Map<String, Object> getExpressionVariables();

    public Path getPropertyPath();
}

