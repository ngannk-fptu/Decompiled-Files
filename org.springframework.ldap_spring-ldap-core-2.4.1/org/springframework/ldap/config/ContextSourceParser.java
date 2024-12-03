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
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.springframework.ldap.config;

import java.util.HashSet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.config.ParserUtils;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.PoolExhaustedAction;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.pool.validation.DefaultDirContextValidator;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class ContextSourceParser
implements BeanDefinitionParser {
    private static final String ATT_ANONYMOUS_READ_ONLY = "anonymous-read-only";
    private static final String ATT_AUTHENTICATION_SOURCE_REF = "authentication-source-ref";
    private static final String ATT_AUTHENTICATION_STRATEGY_REF = "authentication-strategy-ref";
    private static final String ATT_BASE = "base";
    private static final String ATT_PASSWORD = "password";
    private static final String ATT_NATIVE_POOLING = "native-pooling";
    private static final String ATT_REFERRAL = "referral";
    private static final String ATT_URL = "url";
    private static final String ATT_BASE_ENV_PROPS_REF = "base-env-props-ref";
    private static final String ATT_MAX_ACTIVE = "max-active";
    private static final String ATT_MAX_TOTAL = "max-total";
    private static final String ATT_MAX_IDLE = "max-idle";
    private static final String ATT_MIN_IDLE = "min-idle";
    private static final String ATT_MAX_WAIT = "max-wait";
    private static final String ATT_WHEN_EXHAUSTED = "when-exhausted";
    private static final String ATT_TEST_ON_BORROW = "test-on-borrow";
    private static final String ATT_TEST_ON_RETURN = "test-on-return";
    private static final String ATT_TEST_WHILE_IDLE = "test-while-idle";
    private static final String ATT_EVICTION_RUN_MILLIS = "eviction-run-interval-millis";
    private static final String ATT_TESTS_PER_EVICTION_RUN = "tests-per-eviction-run";
    private static final String ATT_EVICTABLE_TIME_MILLIS = "min-evictable-time-millis";
    private static final String ATT_VALIDATION_QUERY_BASE = "validation-query-base";
    private static final String ATT_VALIDATION_QUERY_FILTER = "validation-query-filter";
    private static final String ATT_VALIDATION_QUERY_SEARCH_CONTROLS_REF = "validation-query-search-controls-ref";
    private static final String ATT_NON_TRANSIENT_EXCEPTIONS = "non-transient-exceptions";
    private static final String ATT_MAX_IDLE_PER_KEY = "max-idle-per-key";
    private static final String ATT_MIN_IDLE_PER_KEY = "min-idle-per-key";
    private static final String ATT_MAX_TOTAL_PER_KEY = "max-total-per-key";
    private static final String ATT_EVICTION_POLICY_CLASS = "eviction-policy-class";
    private static final String ATT_FAIRNESS = "fairness";
    private static final String ATT_JMX_ENABLE = "jmx-enable";
    private static final String ATT_JMX_NAME_BASE = "jmx-name-base";
    private static final String ATT_JMX_NAME_PREFIX = "jmx-name-prefix";
    private static final String ATT_LIFO = "lifo";
    private static final String ATT_BLOCK_WHEN_EXHAUSTED = "block-when-exhausted";
    private static final String ATT_TEST_ON_CREATE = "test-on-create";
    private static final String ATT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "soft-min-evictable-idle-time-millis";
    private static final String ATT_USERNAME = "username";
    static final String DEFAULT_ID = "contextSource";
    private static final int DEFAULT_MAX_ACTIVE = 8;
    private static final int DEFAULT_MAX_TOTAL = -1;
    private static final int DEFAULT_MAX_IDLE = 8;
    private static final int DEFAULT_MIN_IDLE = 0;
    private static final int DEFAULT_MAX_WAIT = -1;
    private static final int DEFAULT_EVICTION_RUN_MILLIS = -1;
    private static final int DEFAULT_TESTS_PER_EVICTION_RUN = 3;
    private static final int DEFAULT_EVICTABLE_MILLIS = 1800000;
    private static final int DEFAULT_MAX_TOTAL_PER_KEY = 8;
    private static final int DEFAULT_MAX_IDLE_PER_KEY = 8;
    private static final int DEFAULT_MIN_IDLE_PER_KEY = 0;
    private static final String DEFAULT_EVICTION_POLICY_CLASS_NAME = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    private static final boolean DEFAULT_FAIRNESS = false;
    private static final boolean DEFAULT_JMX_ENABLE = true;
    private static final String DEFAULT_JMX_NAME_BASE = null;
    private static final String DEFAULT_JMX_NAME_PREFIX = "ldap-pool";
    private static final boolean DEFAULT_LIFO = true;
    private static final int DEFAULT_MAX_WAIT_MILLIS = -1;
    private static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
    private static final int DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String authSourceRef;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(LdapContextSource.class);
        String username = element.getAttribute(ATT_USERNAME);
        String password = element.getAttribute(ATT_PASSWORD);
        String url = element.getAttribute(ATT_URL);
        Assert.hasText((String)url, (String)"url attribute must be specified");
        builder.addPropertyValue("userDn", (Object)username);
        builder.addPropertyValue(ATT_PASSWORD, (Object)password);
        BeanDefinitionBuilder urlsBuilder = BeanDefinitionBuilder.rootBeanDefinition(UrlsFactory.class).setFactoryMethod("urls").addConstructorArgValue((Object)url);
        builder.addPropertyValue("urls", (Object)urlsBuilder.getBeanDefinition());
        builder.addPropertyValue(ATT_BASE, (Object)ParserUtils.getString(element, ATT_BASE, ""));
        builder.addPropertyValue(ATT_REFERRAL, (Object)ParserUtils.getString(element, ATT_REFERRAL, null));
        boolean anonymousReadOnly = ParserUtils.getBoolean(element, ATT_ANONYMOUS_READ_ONLY, false);
        builder.addPropertyValue("anonymousReadOnly", (Object)anonymousReadOnly);
        boolean nativePooling = ParserUtils.getBoolean(element, ATT_NATIVE_POOLING, false);
        builder.addPropertyValue("pooled", (Object)nativePooling);
        String authStrategyRef = element.getAttribute(ATT_AUTHENTICATION_STRATEGY_REF);
        if (StringUtils.hasText((String)authStrategyRef)) {
            builder.addPropertyReference("authenticationStrategy", authStrategyRef);
        }
        if (StringUtils.hasText((String)(authSourceRef = element.getAttribute(ATT_AUTHENTICATION_SOURCE_REF)))) {
            builder.addPropertyReference("authenticationSource", authSourceRef);
        } else {
            Assert.hasText((String)username, (String)"username attribute must be specified unless an authentication-source-ref explicitly configured");
            Assert.hasText((String)password, (String)"password attribute must be specified unless an authentication-source-ref explicitly configured");
        }
        String baseEnvPropsRef = element.getAttribute(ATT_BASE_ENV_PROPS_REF);
        if (StringUtils.hasText((String)baseEnvPropsRef)) {
            builder.addPropertyReference("baseEnvironmentProperties", baseEnvPropsRef);
        }
        AbstractBeanDefinition targetContextSourceDefinition = builder.getBeanDefinition();
        AbstractBeanDefinition actualContextSourceDefinition = targetContextSourceDefinition = this.applyPoolingIfApplicable((BeanDefinition)targetContextSourceDefinition, element, nativePooling);
        if (!anonymousReadOnly) {
            BeanDefinitionBuilder proxyBuilder = BeanDefinitionBuilder.rootBeanDefinition(TransactionAwareContextSourceProxy.class);
            proxyBuilder.addConstructorArgValue((Object)targetContextSourceDefinition);
            actualContextSourceDefinition = proxyBuilder.getBeanDefinition();
        }
        String id = ParserUtils.getString(element, "id", DEFAULT_ID);
        parserContext.registerBeanComponent(new BeanComponentDefinition((BeanDefinition)actualContextSourceDefinition, id));
        return actualContextSourceDefinition;
    }

    private BeanDefinition applyPoolingIfApplicable(BeanDefinition targetContextSourceDefinition, Element element, boolean nativePooling) {
        Element poolingElement = DomUtils.getChildElementByTagName((Element)element, (String)"pooling");
        Element pooling2Element = DomUtils.getChildElementByTagName((Element)element, (String)"pooling2");
        if (pooling2Element != null && poolingElement != null) {
            throw new IllegalArgumentException(String.format("%s cannot be enabled together with %s.", "pooling2", "pooling"));
        }
        if (poolingElement == null && pooling2Element == null) {
            return targetContextSourceDefinition;
        }
        if (nativePooling) {
            throw new IllegalArgumentException(String.format("%s cannot be enabled together with %s", ATT_NATIVE_POOLING, "pooling"));
        }
        if (pooling2Element != null) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PooledContextSource.class);
            builder.addPropertyValue(DEFAULT_ID, (Object)targetContextSourceDefinition);
            this.populatePoolConfigProperties(builder, pooling2Element);
            boolean testOnBorrow = ParserUtils.getBoolean(pooling2Element, ATT_TEST_ON_BORROW, false);
            boolean testOnReturn = ParserUtils.getBoolean(pooling2Element, ATT_TEST_ON_RETURN, false);
            boolean testWhileIdle = ParserUtils.getBoolean(pooling2Element, ATT_TEST_WHILE_IDLE, false);
            boolean testOnCreate = ParserUtils.getBoolean(pooling2Element, ATT_TEST_ON_CREATE, false);
            if (testOnBorrow || testOnCreate || testWhileIdle || testOnReturn) {
                this.populatePoolValidationProperties(builder, pooling2Element);
            }
            return builder.getBeanDefinition();
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(PoolingContextSource.class);
        builder.addPropertyValue(DEFAULT_ID, (Object)targetContextSourceDefinition);
        builder.addPropertyValue("maxActive", (Object)ParserUtils.getString(poolingElement, ATT_MAX_ACTIVE, String.valueOf(8)));
        builder.addPropertyValue("maxTotal", (Object)ParserUtils.getString(poolingElement, ATT_MAX_TOTAL, String.valueOf(-1)));
        builder.addPropertyValue("maxIdle", (Object)ParserUtils.getString(poolingElement, ATT_MAX_IDLE, String.valueOf(8)));
        builder.addPropertyValue("minIdle", (Object)ParserUtils.getString(poolingElement, ATT_MIN_IDLE, String.valueOf(0)));
        builder.addPropertyValue("maxWait", (Object)ParserUtils.getString(poolingElement, ATT_MAX_WAIT, String.valueOf(-1)));
        String whenExhausted = ParserUtils.getString(poolingElement, ATT_WHEN_EXHAUSTED, PoolExhaustedAction.BLOCK.name());
        builder.addPropertyValue("whenExhaustedAction", (Object)PoolExhaustedAction.valueOf(whenExhausted).getValue());
        builder.addPropertyValue("timeBetweenEvictionRunsMillis", (Object)ParserUtils.getString(poolingElement, ATT_EVICTION_RUN_MILLIS, String.valueOf(-1)));
        builder.addPropertyValue("minEvictableIdleTimeMillis", (Object)ParserUtils.getString(poolingElement, ATT_EVICTABLE_TIME_MILLIS, String.valueOf(1800000)));
        builder.addPropertyValue("numTestsPerEvictionRun", (Object)ParserUtils.getString(poolingElement, ATT_TESTS_PER_EVICTION_RUN, String.valueOf(3)));
        boolean testOnBorrow = ParserUtils.getBoolean(poolingElement, ATT_TEST_ON_BORROW, false);
        boolean testOnReturn = ParserUtils.getBoolean(poolingElement, ATT_TEST_ON_RETURN, false);
        boolean testWhileIdle = ParserUtils.getBoolean(poolingElement, ATT_TEST_WHILE_IDLE, false);
        if (testOnBorrow || testOnReturn || testWhileIdle) {
            this.populatePoolValidationProperties(builder, poolingElement, testOnBorrow, testOnReturn, testWhileIdle);
        }
        return builder.getBeanDefinition();
    }

    private void populatePoolValidationProperties(BeanDefinitionBuilder builder, Element element, boolean testOnBorrow, boolean testOnReturn, boolean testWhileIdle) {
        builder.addPropertyValue("testOnBorrow", (Object)testOnBorrow);
        builder.addPropertyValue("testOnReturn", (Object)testOnReturn);
        builder.addPropertyValue("testWhileIdle", (Object)testWhileIdle);
        BeanDefinitionBuilder validatorBuilder = BeanDefinitionBuilder.rootBeanDefinition(DefaultDirContextValidator.class);
        validatorBuilder.addPropertyValue(ATT_BASE, (Object)ParserUtils.getString(element, ATT_VALIDATION_QUERY_BASE, ""));
        validatorBuilder.addPropertyValue("filter", (Object)ParserUtils.getString(element, ATT_VALIDATION_QUERY_FILTER, "objectclass=*"));
        String searchControlsRef = element.getAttribute(ATT_VALIDATION_QUERY_SEARCH_CONTROLS_REF);
        if (StringUtils.hasText((String)searchControlsRef)) {
            validatorBuilder.addPropertyReference("searchControls", searchControlsRef);
        }
        builder.addPropertyValue("dirContextValidator", (Object)validatorBuilder.getBeanDefinition());
        builder.addPropertyValue("timeBetweenEvictionRunsMillis", (Object)ParserUtils.getString(element, ATT_EVICTION_RUN_MILLIS, String.valueOf(-1)));
        builder.addPropertyValue("numTestsPerEvictionRun", (Object)ParserUtils.getInt(element, ATT_TESTS_PER_EVICTION_RUN, 3));
        builder.addPropertyValue("minEvictableIdleTimeMillis", (Object)ParserUtils.getString(element, ATT_EVICTABLE_TIME_MILLIS, String.valueOf(1800000)));
        String nonTransientExceptions = ParserUtils.getString(element, ATT_NON_TRANSIENT_EXCEPTIONS, CommunicationException.class.getName());
        String[] strings = StringUtils.commaDelimitedListToStringArray((String)nonTransientExceptions);
        HashSet nonTransientExceptionClasses = new HashSet();
        for (String className : strings) {
            try {
                nonTransientExceptionClasses.add(ClassUtils.getDefaultClassLoader().loadClass(className));
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(String.format("%s is not a valid class name", className), e);
            }
        }
        builder.addPropertyValue("nonTransientExceptions", nonTransientExceptionClasses);
    }

    private void populatePoolValidationProperties(BeanDefinitionBuilder builder, Element element) {
        BeanDefinitionBuilder validatorBuilder = BeanDefinitionBuilder.rootBeanDefinition(org.springframework.ldap.pool2.validation.DefaultDirContextValidator.class);
        validatorBuilder.addPropertyValue(ATT_BASE, (Object)ParserUtils.getString(element, ATT_VALIDATION_QUERY_BASE, ""));
        validatorBuilder.addPropertyValue("filter", (Object)ParserUtils.getString(element, ATT_VALIDATION_QUERY_FILTER, "objectclass=*"));
        String searchControlsRef = element.getAttribute(ATT_VALIDATION_QUERY_SEARCH_CONTROLS_REF);
        if (StringUtils.hasText((String)searchControlsRef)) {
            validatorBuilder.addPropertyReference("searchControls", searchControlsRef);
        }
        builder.addPropertyValue("dirContextValidator", (Object)validatorBuilder.getBeanDefinition());
        String nonTransientExceptions = ParserUtils.getString(element, ATT_NON_TRANSIENT_EXCEPTIONS, CommunicationException.class.getName());
        String[] strings = StringUtils.commaDelimitedListToStringArray((String)nonTransientExceptions);
        HashSet nonTransientExceptionClasses = new HashSet();
        for (String className : strings) {
            try {
                nonTransientExceptionClasses.add(ClassUtils.getDefaultClassLoader().loadClass(className));
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(String.format("%s is not a valid class name", className), e);
            }
        }
        builder.addPropertyValue("nonTransientExceptions", nonTransientExceptionClasses);
    }

    private void populatePoolConfigProperties(BeanDefinitionBuilder builder, Element element) {
        BeanDefinitionBuilder configBuilder = BeanDefinitionBuilder.rootBeanDefinition(PoolConfig.class);
        configBuilder.addPropertyValue("maxTotal", (Object)ParserUtils.getString(element, ATT_MAX_TOTAL, String.valueOf(-1)));
        configBuilder.addPropertyValue("maxTotalPerKey", (Object)ParserUtils.getString(element, ATT_MAX_TOTAL_PER_KEY, String.valueOf(8)));
        configBuilder.addPropertyValue("maxIdlePerKey", (Object)ParserUtils.getString(element, ATT_MAX_IDLE_PER_KEY, String.valueOf(8)));
        configBuilder.addPropertyValue("minIdlePerKey", (Object)ParserUtils.getString(element, ATT_MIN_IDLE_PER_KEY, String.valueOf(0)));
        configBuilder.addPropertyValue("evictionPolicyClassName", (Object)ParserUtils.getString(element, ATT_EVICTION_POLICY_CLASS, DEFAULT_EVICTION_POLICY_CLASS_NAME));
        configBuilder.addPropertyValue(ATT_FAIRNESS, (Object)ParserUtils.getBoolean(element, ATT_FAIRNESS, false));
        configBuilder.addPropertyValue("jmxEnabled", (Object)ParserUtils.getBoolean(element, ATT_JMX_ENABLE, true));
        configBuilder.addPropertyValue("jmxNameBase", (Object)ParserUtils.getString(element, ATT_JMX_NAME_BASE, DEFAULT_JMX_NAME_BASE));
        configBuilder.addPropertyValue("jmxNamePrefix", (Object)ParserUtils.getString(element, ATT_JMX_NAME_PREFIX, DEFAULT_JMX_NAME_PREFIX));
        configBuilder.addPropertyValue(ATT_LIFO, (Object)ParserUtils.getBoolean(element, ATT_LIFO, true));
        configBuilder.addPropertyValue("maxWaitMillis", (Object)ParserUtils.getString(element, ATT_MAX_WAIT, String.valueOf(-1)));
        configBuilder.addPropertyValue("blockWhenExhausted", (Object)Boolean.valueOf(ParserUtils.getString(element, ATT_BLOCK_WHEN_EXHAUSTED, String.valueOf(true))));
        configBuilder.addPropertyValue("testOnBorrow", (Object)ParserUtils.getBoolean(element, ATT_TEST_ON_BORROW, false));
        configBuilder.addPropertyValue("testOnCreate", (Object)ParserUtils.getBoolean(element, ATT_TEST_ON_CREATE, false));
        configBuilder.addPropertyValue("testOnReturn", (Object)ParserUtils.getBoolean(element, ATT_TEST_ON_RETURN, false));
        configBuilder.addPropertyValue("testWhileIdle", (Object)ParserUtils.getBoolean(element, ATT_TEST_WHILE_IDLE, false));
        configBuilder.addPropertyValue("timeBetweenEvictionRunsMillis", (Object)ParserUtils.getString(element, ATT_EVICTION_RUN_MILLIS, String.valueOf(-1)));
        configBuilder.addPropertyValue("numTestsPerEvictionRun", (Object)ParserUtils.getString(element, ATT_TESTS_PER_EVICTION_RUN, String.valueOf(3)));
        configBuilder.addPropertyValue("minEvictableIdleTimeMillis", (Object)ParserUtils.getString(element, ATT_EVICTABLE_TIME_MILLIS, String.valueOf(1800000)));
        configBuilder.addPropertyValue("softMinEvictableIdleTimeMillis", (Object)ParserUtils.getString(element, ATT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS, String.valueOf(-1)));
        builder.addConstructorArgValue((Object)configBuilder.getBeanDefinition());
    }

    static class UrlsFactory {
        UrlsFactory() {
        }

        public static String[] urls(String value) {
            return StringUtils.commaDelimitedListToStringArray((String)value);
        }
    }
}

