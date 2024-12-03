/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyConstants;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.math.NumberUtils;

public class ConnectionPoolPropertyUtil {
    public static final String VALUES_LIST_DELIMITER = " ";

    public static String millisToSeconds(String millis) {
        return ConnectionPoolPropertyUtil.convertStringTimeUnit(millis, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    }

    public static String secondsToMillis(String seconds) {
        return ConnectionPoolPropertyUtil.convertStringTimeUnit(seconds, TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
    }

    private static String convertStringTimeUnit(String value, TimeUnit fromTimeUnit, TimeUnit toTimeUnit) {
        return Long.toString(toTimeUnit.convert(NumberUtils.toLong((String)value), fromTimeUnit));
    }

    public static boolean isValidProtocol(String userInput) {
        return ConnectionPoolPropertyUtil.isValidEntry(userInput, ConnectionPoolPropertyConstants.VALID_PROTOCOL_TYPES);
    }

    public static boolean isValidAuthentication(String userInput) {
        return ConnectionPoolPropertyUtil.isValidEntry(userInput, ConnectionPoolPropertyConstants.VALID_AUTHENTICATION_TYPES);
    }

    private static boolean isValidEntry(String userInput, Set<String> validValues) {
        return validValues.containsAll((Collection<?>)ImmutableList.copyOf((Object[])userInput.split(VALUES_LIST_DELIMITER)));
    }
}

