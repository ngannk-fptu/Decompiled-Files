/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.util.IdempotentUtils;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.ValidationUtils;

public class PathMarshallers {
    public static final PathMarshaller NON_GREEDY = new NonGreedyPathMarshaller();
    public static final PathMarshaller GREEDY = new GreedyPathMarshaller();
    public static final PathMarshaller IDEMPOTENCY = new IdempotencyPathMarshaller();

    private static String trimLeadingSlash(String value) {
        if (value.startsWith("/")) {
            return value.replaceFirst("/", "");
        }
        return value;
    }

    private static class IdempotencyPathMarshaller
    implements PathMarshaller {
        private IdempotencyPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            if (pathValue != null && pathValue.isEmpty()) {
                throw new IllegalArgumentException(paramName + " must not be empty. If not set a value will be auto generated");
            }
            return resourcePath.replace(String.format("{%s}", paramName), SdkHttpUtils.urlEncode(IdempotentUtils.resolveString(pathValue), false));
        }

        @Override
        public String marshall(String resourcePath, String paramName, Integer pathValue) {
            throw new UnsupportedOperationException("Integer idempotency tokens not yet supported");
        }

        @Override
        public String marshall(String resourcePath, String paramName, Long pathValue) {
            throw new UnsupportedOperationException("Long idempotency tokens not yet supported");
        }
    }

    private static class GreedyPathMarshaller
    implements PathMarshaller {
        private GreedyPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            ValidationUtils.assertStringNotEmpty(pathValue, paramName);
            return resourcePath.replace(String.format("{%s+}", paramName), PathMarshallers.trimLeadingSlash(pathValue));
        }

        @Override
        public String marshall(String resourcePath, String paramName, Integer pathValue) {
            ValidationUtils.assertNotNull(pathValue, paramName);
            return this.marshall(resourcePath, paramName, StringUtils.fromInteger(pathValue));
        }

        @Override
        public String marshall(String resourcePath, String paramName, Long pathValue) {
            ValidationUtils.assertNotNull(pathValue, paramName);
            return this.marshall(resourcePath, paramName, StringUtils.fromLong(pathValue));
        }
    }

    private static class NonGreedyPathMarshaller
    implements PathMarshaller {
        private NonGreedyPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            ValidationUtils.assertStringNotEmpty(pathValue, paramName);
            return resourcePath.replace(String.format("{%s}", paramName), SdkHttpUtils.urlEncode(pathValue, false));
        }

        @Override
        public String marshall(String resourcePath, String paramName, Integer pathValue) {
            ValidationUtils.assertNotNull(pathValue, paramName);
            return this.marshall(resourcePath, paramName, StringUtils.fromInteger(pathValue));
        }

        @Override
        public String marshall(String resourcePath, String paramName, Long pathValue) {
            ValidationUtils.assertNotNull(pathValue, paramName);
            return this.marshall(resourcePath, paramName, StringUtils.fromLong(pathValue));
        }
    }

    public static interface PathMarshaller {
        public String marshall(String var1, String var2, String var3);

        public String marshall(String var1, String var2, Integer var3);

        public String marshall(String var1, String var2, Long var3);
    }
}

