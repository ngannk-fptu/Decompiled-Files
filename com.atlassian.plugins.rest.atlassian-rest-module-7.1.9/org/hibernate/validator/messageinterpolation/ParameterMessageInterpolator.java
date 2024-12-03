/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator$Context
 */
package org.hibernate.validator.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.InterpolationTerm;
import org.hibernate.validator.internal.engine.messageinterpolation.ParameterTermResolver;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator;

public class ParameterMessageInterpolator
extends AbstractMessageInterpolator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    @Override
    public String interpolate(MessageInterpolator.Context context, Locale locale, String term) {
        if (InterpolationTerm.isElExpression(term)) {
            LOG.warnElIsUnsupported(term);
            return term;
        }
        ParameterTermResolver parameterTermResolver = new ParameterTermResolver();
        return parameterTermResolver.interpolate(context, term);
    }
}

