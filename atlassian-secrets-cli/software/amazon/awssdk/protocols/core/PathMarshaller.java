/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkProtectedApi
public abstract class PathMarshaller {
    public static final PathMarshaller NON_GREEDY = new NonGreedyPathMarshaller();
    public static final PathMarshaller GREEDY = new GreedyPathMarshaller();
    public static final PathMarshaller GREEDY_WITH_SLASHES = new GreedyLeadingSlashPathMarshaller();

    private PathMarshaller() {
    }

    private static String trimLeadingSlash(String value) {
        if (value.startsWith("/")) {
            return value.replaceFirst("/", "");
        }
        return value;
    }

    public abstract String marshall(String var1, String var2, String var3);

    private static class GreedyLeadingSlashPathMarshaller
    extends PathMarshaller {
        private GreedyLeadingSlashPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            Validate.notEmpty(pathValue, "%s cannot be empty.", paramName);
            return StringUtils.replace(resourcePath, "{" + paramName + "+}", SdkHttpUtils.urlEncodeIgnoreSlashes(pathValue));
        }
    }

    private static class GreedyPathMarshaller
    extends PathMarshaller {
        private GreedyPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            Validate.notEmpty(pathValue, "%s cannot be empty.", paramName);
            return StringUtils.replace(resourcePath, "{" + paramName + "+}", SdkHttpUtils.urlEncodeIgnoreSlashes(PathMarshaller.trimLeadingSlash(pathValue)));
        }
    }

    private static class NonGreedyPathMarshaller
    extends PathMarshaller {
        private NonGreedyPathMarshaller() {
        }

        @Override
        public String marshall(String resourcePath, String paramName, String pathValue) {
            Validate.notEmpty(pathValue, "%s cannot be empty.", paramName);
            return StringUtils.replace(resourcePath, "{" + paramName + "}", SdkHttpUtils.urlEncode(pathValue));
        }
    }
}

