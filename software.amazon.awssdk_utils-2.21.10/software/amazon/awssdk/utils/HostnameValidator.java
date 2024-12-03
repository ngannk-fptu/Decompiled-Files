/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkProtectedApi
public final class HostnameValidator {
    private static final Pattern DEFAULT_HOSTNAME_COMPLIANT_PATTERN = Pattern.compile("[A-Za-z0-9\\-]+");
    private static final int HOSTNAME_MAX_LENGTH = 63;

    private HostnameValidator() {
    }

    public static void validateHostnameCompliant(String hostnameComponent, String paramName, String object) {
        HostnameValidator.validateHostnameCompliant(hostnameComponent, paramName, object, DEFAULT_HOSTNAME_COMPLIANT_PATTERN);
    }

    public static void validateHostnameCompliant(String hostnameComponent, String paramName, String object, Pattern pattern) {
        if (hostnameComponent == null) {
            throw new IllegalArgumentException(String.format("The provided %s is not valid: the required '%s' component is missing.", object, paramName));
        }
        if (StringUtils.isEmpty(hostnameComponent)) {
            throw new IllegalArgumentException(String.format("The provided %s is not valid: the '%s' component is empty.", object, paramName));
        }
        if (StringUtils.isBlank(hostnameComponent)) {
            throw new IllegalArgumentException(String.format("The provided %s is not valid: the '%s' component is blank.", object, paramName));
        }
        if (hostnameComponent.length() > 63) {
            throw new IllegalArgumentException(String.format("The provided %s is not valid: the '%s' component exceeds the maximum length of %d characters.", object, paramName, 63));
        }
        Matcher m = pattern.matcher(hostnameComponent);
        if (!m.matches()) {
            throw new IllegalArgumentException(String.format("The provided %s is not valid: the '%s' component must match the pattern \"%s\".", object, paramName, pattern));
        }
    }
}

