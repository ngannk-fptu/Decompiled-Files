/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.jmx.support.WebSphereMBeanServerFactoryBean;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

class MBeanServerBeanDefinitionParser
extends AbstractBeanDefinitionParser {
    private static final String MBEAN_SERVER_BEAN_NAME = "mbeanServer";
    private static final String AGENT_ID_ATTRIBUTE = "agent-id";
    private static final boolean weblogicPresent;
    private static final boolean webspherePresent;

    MBeanServerBeanDefinitionParser() {
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        String id = element.getAttribute("id");
        return StringUtils.hasText((String)id) ? id : MBEAN_SERVER_BEAN_NAME;
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        String agentId = element.getAttribute(AGENT_ID_ATTRIBUTE);
        if (StringUtils.hasText((String)agentId)) {
            RootBeanDefinition bd = new RootBeanDefinition(MBeanServerFactoryBean.class);
            bd.getPropertyValues().add("agentId", (Object)agentId);
            return bd;
        }
        AbstractBeanDefinition specialServer = MBeanServerBeanDefinitionParser.findServerForSpecialEnvironment();
        if (specialServer != null) {
            return specialServer;
        }
        RootBeanDefinition bd = new RootBeanDefinition(MBeanServerFactoryBean.class);
        bd.getPropertyValues().add("locateExistingServerIfPossible", (Object)Boolean.TRUE);
        bd.setRole(2);
        bd.setSource(parserContext.extractSource((Object)element));
        return bd;
    }

    @Nullable
    static AbstractBeanDefinition findServerForSpecialEnvironment() {
        if (weblogicPresent) {
            RootBeanDefinition bd = new RootBeanDefinition(JndiObjectFactoryBean.class);
            bd.getPropertyValues().add("jndiName", (Object)"java:comp/env/jmx/runtime");
            return bd;
        }
        if (webspherePresent) {
            return new RootBeanDefinition(WebSphereMBeanServerFactoryBean.class);
        }
        return null;
    }

    static {
        ClassLoader classLoader = MBeanServerBeanDefinitionParser.class.getClassLoader();
        weblogicPresent = ClassUtils.isPresent((String)"weblogic.management.Helper", (ClassLoader)classLoader);
        webspherePresent = ClassUtils.isPresent((String)"com.ibm.websphere.management.AdminServiceFactory", (ClassLoader)classLoader);
    }
}

