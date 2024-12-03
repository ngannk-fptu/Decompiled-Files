/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.jdbc.config;

import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.jdbc.config.SortedResourcesFactoryBean;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

abstract class DatabasePopulatorConfigUtils {
    DatabasePopulatorConfigUtils() {
    }

    public static void setDatabasePopulator(Element element, BeanDefinitionBuilder builder) {
        List scripts = DomUtils.getChildElementsByTagName((Element)element, (String)"script");
        if (!scripts.isEmpty()) {
            builder.addPropertyValue("databasePopulator", (Object)DatabasePopulatorConfigUtils.createDatabasePopulator(element, scripts, "INIT"));
            builder.addPropertyValue("databaseCleaner", (Object)DatabasePopulatorConfigUtils.createDatabasePopulator(element, scripts, "DESTROY"));
        }
    }

    private static BeanDefinition createDatabasePopulator(Element element, List<Element> scripts, String execution) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CompositeDatabasePopulator.class);
        boolean ignoreFailedDrops = element.getAttribute("ignore-failures").equals("DROPS");
        boolean continueOnError = element.getAttribute("ignore-failures").equals("ALL");
        ManagedList delegates = new ManagedList();
        for (Element scriptElement : scripts) {
            String separator;
            String executionAttr = scriptElement.getAttribute("execution");
            if (!StringUtils.hasText((String)executionAttr)) {
                executionAttr = "INIT";
            }
            if (!execution.equals(executionAttr)) continue;
            BeanDefinitionBuilder delegate = BeanDefinitionBuilder.genericBeanDefinition(ResourceDatabasePopulator.class);
            delegate.addPropertyValue("ignoreFailedDrops", (Object)ignoreFailedDrops);
            delegate.addPropertyValue("continueOnError", (Object)continueOnError);
            BeanDefinitionBuilder resourcesFactory = BeanDefinitionBuilder.genericBeanDefinition(SortedResourcesFactoryBean.class);
            resourcesFactory.addConstructorArgValue((Object)new TypedStringValue(scriptElement.getAttribute("location")));
            delegate.addPropertyValue("scripts", (Object)resourcesFactory.getBeanDefinition());
            if (StringUtils.hasLength((String)scriptElement.getAttribute("encoding"))) {
                delegate.addPropertyValue("sqlScriptEncoding", (Object)new TypedStringValue(scriptElement.getAttribute("encoding")));
            }
            if ((separator = DatabasePopulatorConfigUtils.getSeparator(element, scriptElement)) != null) {
                delegate.addPropertyValue("separator", (Object)new TypedStringValue(separator));
            }
            delegates.add((Object)delegate.getBeanDefinition());
        }
        builder.addPropertyValue("populators", (Object)delegates);
        return builder.getBeanDefinition();
    }

    @Nullable
    private static String getSeparator(Element element, Element scriptElement) {
        String scriptSeparator = scriptElement.getAttribute("separator");
        if (StringUtils.hasLength((String)scriptSeparator)) {
            return scriptSeparator;
        }
        String elementSeparator = element.getAttribute("separator");
        if (StringUtils.hasLength((String)elementSeparator)) {
            return elementSeparator;
        }
        return null;
    }
}

