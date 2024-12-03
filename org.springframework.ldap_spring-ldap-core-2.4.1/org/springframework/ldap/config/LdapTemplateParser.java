/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.ldap.config.ParserUtils;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.SearchScope;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class LdapTemplateParser
implements BeanDefinitionParser {
    private static final String ATT_COUNT_LIMIT = "count-limit";
    private static final String ATT_TIME_LIMIT = "time-limit";
    private static final String ATT_SEARCH_SCOPE = "search-scope";
    private static final String ATT_IGNORE_PARTIAL_RESULT = "ignore-partial-result";
    private static final String ATT_IGNORE_NAME_NOT_FOUND = "ignore-name-not-found";
    private static final String ATT_ODM_REF = "odm-ref";
    private static final String ATT_CONTEXT_SOURCE_REF = "context-source-ref";
    private static final String DEFAULT_ID = "ldapTemplate";
    private static final int DEFAULT_COUNT_LIMIT = 0;
    private static final int DEFAULT_TIME_LIMIT = 0;

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(LdapTemplate.class);
        String contextSourceRef = ParserUtils.getString(element, ATT_CONTEXT_SOURCE_REF, "contextSource");
        builder.addPropertyReference("contextSource", contextSourceRef);
        builder.addPropertyValue("defaultCountLimit", (Object)ParserUtils.getInt(element, ATT_COUNT_LIMIT, 0));
        builder.addPropertyValue("defaultTimeLimit", (Object)ParserUtils.getInt(element, ATT_TIME_LIMIT, 0));
        String searchScope = ParserUtils.getString(element, ATT_SEARCH_SCOPE, SearchScope.SUBTREE.toString());
        builder.addPropertyValue("defaultSearchScope", (Object)SearchScope.valueOf(searchScope).getId());
        builder.addPropertyValue("ignorePartialResultException", (Object)ParserUtils.getBoolean(element, ATT_IGNORE_PARTIAL_RESULT, false));
        builder.addPropertyValue("ignoreNameNotFoundException", (Object)ParserUtils.getBoolean(element, ATT_IGNORE_NAME_NOT_FOUND, false));
        String odmRef = element.getAttribute(ATT_ODM_REF);
        if (StringUtils.hasText((String)odmRef)) {
            builder.addPropertyReference("objectDirectoryMapper", odmRef);
        }
        String id = ParserUtils.getString(element, "id", DEFAULT_ID);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)beanDefinition, id));
        return beanDefinition;
    }
}

