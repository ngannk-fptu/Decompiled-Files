/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.boot.jaxb.hbm.spi.ConfigParameterContainer;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterType;
import org.hibernate.internal.util.collections.CollectionHelper;

public class ConfigParameterHelper {
    public static Map<String, String> extractConfigParameters(ConfigParameterContainer container) {
        return ConfigParameterHelper.extractConfigParameters(container.getConfigParameters());
    }

    private static Map<String, String> extractConfigParameters(List<JaxbHbmConfigParameterType> paramElementList) {
        if (CollectionHelper.isEmpty(paramElementList)) {
            return Collections.emptyMap();
        }
        HashMap<String, String> params = new HashMap<String, String>();
        for (JaxbHbmConfigParameterType paramElement : paramElementList) {
            params.put(paramElement.getName(), paramElement.getValue());
        }
        return params;
    }

    private ConfigParameterHelper() {
    }
}

