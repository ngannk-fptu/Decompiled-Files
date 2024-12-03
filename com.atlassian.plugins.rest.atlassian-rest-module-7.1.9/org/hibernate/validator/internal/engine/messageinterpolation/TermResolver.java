/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator$Context
 */
package org.hibernate.validator.internal.engine.messageinterpolation;

import javax.validation.MessageInterpolator;

public interface TermResolver {
    public String interpolate(MessageInterpolator.Context var1, String var2);
}

