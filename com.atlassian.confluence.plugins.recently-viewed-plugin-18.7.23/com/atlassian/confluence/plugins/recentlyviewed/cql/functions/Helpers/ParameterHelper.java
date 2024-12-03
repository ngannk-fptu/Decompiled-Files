/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.exceptions.GenericQueryException
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.functions.Helpers;

import com.atlassian.querylang.exceptions.GenericQueryException;

public class ParameterHelper {
    public static int getIntegerParameter(String param, String name) {
        int value;
        try {
            value = Integer.parseInt(param);
        }
        catch (NumberFormatException e) {
            throw new GenericQueryException("Invalid " + name + " : " + param);
        }
        return value;
    }
}

