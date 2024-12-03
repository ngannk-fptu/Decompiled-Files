/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.lang.Nullable
 *  org.springframework.web.bind.ServletRequestDataBinder
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Map;
import javax.servlet.ServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.HandlerMapping;

public class ExtendedServletRequestDataBinder
extends ServletRequestDataBinder {
    public ExtendedServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ExtendedServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
        Map uriVars = (Map)request.getAttribute(attr);
        if (uriVars != null) {
            uriVars.forEach((name, value) -> {
                if (mpvs.contains(name)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("URI variable '" + name + "' overridden by request bind value."));
                    }
                } else {
                    mpvs.addPropertyValue(name, value);
                }
            });
        }
    }
}

