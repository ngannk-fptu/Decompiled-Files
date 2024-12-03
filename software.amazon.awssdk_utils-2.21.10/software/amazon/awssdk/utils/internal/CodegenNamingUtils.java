/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class CodegenNamingUtils {
    private CodegenNamingUtils() {
    }

    public static String[] splitOnWordBoundaries(String toSplit) {
        String result = toSplit;
        result = result.replaceAll("[^A-Za-z0-9]+", " ");
        result = result.replaceAll("([^a-z]{2,})v([0-9]+)", "$1 v$2 ").replaceAll("([^A-Z]{2,})V([0-9]+)", "$1 V$2 ");
        result = String.join((CharSequence)" ", result.split("(?<=[a-z])(?=[A-Z]([a-zA-Z]|[0-9]))"));
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
        result = result.replaceAll("([0-9])([a-zA-Z])", "$1 $2");
        result = result.replaceAll(" +", " ").trim();
        return result.split(" ");
    }

    public static String pascalCase(String word) {
        return Stream.of(CodegenNamingUtils.splitOnWordBoundaries(word)).map(StringUtils::lowerCase).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    public static String pascalCase(String ... words) {
        return Stream.of(words).map(StringUtils::lowerCase).map(StringUtils::capitalize).collect(Collectors.joining());
    }

    public static String lowercaseFirstChar(String word) {
        char[] chars = word.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return String.valueOf(chars);
    }
}

