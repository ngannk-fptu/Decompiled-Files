/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractDetectingUrlHandlerMapping;

public class BeanNameUrlHandlerMapping
extends AbstractDetectingUrlHandlerMapping {
    @Override
    protected String[] determineUrlsForHandler(String beanName) {
        String[] aliases;
        ArrayList<String> urls = new ArrayList<String>();
        if (beanName.startsWith("/")) {
            urls.add(beanName);
        }
        for (String alias : aliases = this.obtainApplicationContext().getAliases(beanName)) {
            if (!alias.startsWith("/")) continue;
            urls.add(alias);
        }
        return StringUtils.toStringArray(urls);
    }
}

