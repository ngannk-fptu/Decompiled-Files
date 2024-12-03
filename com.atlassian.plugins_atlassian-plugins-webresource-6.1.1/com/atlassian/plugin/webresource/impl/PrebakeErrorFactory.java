/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.baseconditions.BaseCondition
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.web.baseconditions.BaseCondition;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import org.apache.commons.lang3.StringUtils;

public final class PrebakeErrorFactory {
    private static final String DIMENSION_UNAWARE_CONDITION = "Encountered dimension unaware condition";
    private static final String DIMENSION_UNAWARE_TRANSFORMATION = "Encountered dimension unaware transformation";

    public static <E extends BaseCondition> PrebakeError from(E condition) {
        return new DimensionUnawarePrebakeError(DIMENSION_UNAWARE_CONDITION, condition);
    }

    public static <E extends TransformerUrlBuilder> PrebakeError from(E transformer) {
        return new DimensionUnawarePrebakeError(DIMENSION_UNAWARE_TRANSFORMATION, transformer);
    }

    public static PrebakeError from(String message, Throwable cause) {
        return new ExceptionPrebakeError(message, cause);
    }

    public static PrebakeError from(Throwable cause) {
        return new ExceptionPrebakeError(cause);
    }

    public static PrebakeError from(String message) {
        return new StringPrebakeError(message);
    }

    private static final class ExceptionPrebakeError
    implements PrebakeError {
        private final String info;
        private final Throwable cause;

        private ExceptionPrebakeError(String info, Throwable cause) {
            this.info = info;
            this.cause = cause;
        }

        private ExceptionPrebakeError(Throwable cause) {
            this.info = null;
            this.cause = cause;
        }

        public String toString() {
            if (StringUtils.isNotEmpty((CharSequence)this.info)) {
                return this.info + ": " + this.cause.toString();
            }
            return this.cause.toString();
        }
    }

    private static final class StringPrebakeError
    implements PrebakeError {
        private final String message;

        private StringPrebakeError(String message) {
            this.message = message;
        }

        public String toString() {
            return this.message;
        }
    }

    private static final class DimensionUnawarePrebakeError<E>
    implements PrebakeError {
        private final String info;
        private final E source;

        private DimensionUnawarePrebakeError(String info, E source) {
            this.info = info;
            this.source = source;
        }

        public String toString() {
            return this.info + ": " + this.getClassName(this.source.getClass());
        }

        private String getClassName(Class<?> c) {
            String className = c.getCanonicalName();
            if (StringUtils.isEmpty((CharSequence)className)) {
                className = c.getName();
            }
            if (StringUtils.isEmpty((CharSequence)className)) {
                className = c.getSimpleName();
            }
            if (StringUtils.isEmpty((CharSequence)className)) {
                className = "unable to determine class name";
            }
            return className;
        }
    }
}

