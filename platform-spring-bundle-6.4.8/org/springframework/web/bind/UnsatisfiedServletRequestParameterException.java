/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;

public class UnsatisfiedServletRequestParameterException
extends ServletRequestBindingException {
    private final List<String[]> paramConditions;
    private final Map<String, String[]> actualParams;

    public UnsatisfiedServletRequestParameterException(String[] paramConditions, Map<String, String[]> actualParams) {
        super("");
        this.paramConditions = Arrays.asList(new String[][]{paramConditions});
        this.actualParams = actualParams;
    }

    public UnsatisfiedServletRequestParameterException(List<String[]> paramConditions, Map<String, String[]> actualParams) {
        super("");
        Assert.notEmpty(paramConditions, "Parameter conditions must not be empty");
        this.paramConditions = paramConditions;
        this.actualParams = actualParams;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("Parameter conditions ");
        int i2 = 0;
        for (Object[] objectArray : this.paramConditions) {
            if (i2 > 0) {
                sb.append(" OR ");
            }
            sb.append('\"');
            sb.append(StringUtils.arrayToDelimitedString(objectArray, ", "));
            sb.append('\"');
            ++i2;
        }
        sb.append(" not met for actual request parameters: ");
        sb.append(UnsatisfiedServletRequestParameterException.requestParameterMapToString(this.actualParams));
        return sb.toString();
    }

    public final String[] getParamConditions() {
        return this.paramConditions.get(0);
    }

    public final List<String[]> getParamConditionGroups() {
        return this.paramConditions;
    }

    public final Map<String, String[]> getActualParams() {
        return this.actualParams;
    }

    private static String requestParameterMapToString(Map<String, String[]> actualParams) {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, String[]>> it = actualParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            result.append(entry.getKey()).append('=').append(ObjectUtils.nullSafeToString(entry.getValue()));
            if (!it.hasNext()) continue;
            result.append(", ");
        }
        return result.toString();
    }
}

