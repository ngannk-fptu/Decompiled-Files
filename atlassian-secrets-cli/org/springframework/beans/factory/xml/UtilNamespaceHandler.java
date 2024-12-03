/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPathFactoryBean;
import org.springframework.beans.factory.config.SetFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class UtilNamespaceHandler
extends NamespaceHandlerSupport {
    private static final String SCOPE_ATTRIBUTE = "scope";

    @Override
    public void init() {
        this.registerBeanDefinitionParser("constant", new ConstantBeanDefinitionParser());
        this.registerBeanDefinitionParser("property-path", new PropertyPathBeanDefinitionParser());
        this.registerBeanDefinitionParser("list", new ListBeanDefinitionParser());
        this.registerBeanDefinitionParser("set", new SetBeanDefinitionParser());
        this.registerBeanDefinitionParser("map", new MapBeanDefinitionParser());
        this.registerBeanDefinitionParser("properties", new PropertiesBeanDefinitionParser());
    }

    private static class PropertiesBeanDefinitionParser
    extends AbstractSingleBeanDefinitionParser {
        private PropertiesBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return PropertiesFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            Properties parsedProps = parserContext.getDelegate().parsePropsElement(element);
            builder.addPropertyValue("properties", parsedProps);
            String location = element.getAttribute("location");
            if (StringUtils.hasLength(location)) {
                location = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location);
                String[] locations = StringUtils.commaDelimitedListToStringArray(location);
                builder.addPropertyValue("locations", locations);
            }
            builder.addPropertyValue("ignoreResourceNotFound", Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));
            builder.addPropertyValue("localOverride", Boolean.valueOf(element.getAttribute("local-override")));
            String scope = element.getAttribute(UtilNamespaceHandler.SCOPE_ATTRIBUTE);
            if (StringUtils.hasLength(scope)) {
                builder.setScope(scope);
            }
        }
    }

    private static class MapBeanDefinitionParser
    extends AbstractSingleBeanDefinitionParser {
        private MapBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return MapFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            String scope;
            Map<Object, Object> parsedMap = parserContext.getDelegate().parseMapElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceMap", parsedMap);
            String mapClass = element.getAttribute("map-class");
            if (StringUtils.hasText(mapClass)) {
                builder.addPropertyValue("targetMapClass", mapClass);
            }
            if (StringUtils.hasLength(scope = element.getAttribute(UtilNamespaceHandler.SCOPE_ATTRIBUTE))) {
                builder.setScope(scope);
            }
        }
    }

    private static class SetBeanDefinitionParser
    extends AbstractSingleBeanDefinitionParser {
        private SetBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return SetFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            String scope;
            Set<Object> parsedSet = parserContext.getDelegate().parseSetElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceSet", parsedSet);
            String setClass = element.getAttribute("set-class");
            if (StringUtils.hasText(setClass)) {
                builder.addPropertyValue("targetSetClass", setClass);
            }
            if (StringUtils.hasLength(scope = element.getAttribute(UtilNamespaceHandler.SCOPE_ATTRIBUTE))) {
                builder.setScope(scope);
            }
        }
    }

    private static class ListBeanDefinitionParser
    extends AbstractSingleBeanDefinitionParser {
        private ListBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return ListFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            String scope;
            List<Object> parsedList = parserContext.getDelegate().parseListElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceList", parsedList);
            String listClass = element.getAttribute("list-class");
            if (StringUtils.hasText(listClass)) {
                builder.addPropertyValue("targetListClass", listClass);
            }
            if (StringUtils.hasLength(scope = element.getAttribute(UtilNamespaceHandler.SCOPE_ATTRIBUTE))) {
                builder.setScope(scope);
            }
        }
    }

    private static class PropertyPathBeanDefinitionParser
    extends AbstractSingleBeanDefinitionParser {
        private PropertyPathBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return PropertyPathFactoryBean.class;
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            String path = element.getAttribute("path");
            if (!StringUtils.hasText(path)) {
                parserContext.getReaderContext().error("Attribute 'path' must not be empty", element);
                return;
            }
            int dotIndex = path.indexOf(46);
            if (dotIndex == -1) {
                parserContext.getReaderContext().error("Attribute 'path' must follow pattern 'beanName.propertyName'", element);
                return;
            }
            String beanName = path.substring(0, dotIndex);
            String propertyPath = path.substring(dotIndex + 1);
            builder.addPropertyValue("targetBeanName", beanName);
            builder.addPropertyValue("propertyPath", propertyPath);
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            if (!StringUtils.hasText(id)) {
                id = element.getAttribute("path");
            }
            return id;
        }
    }

    private static class ConstantBeanDefinitionParser
    extends AbstractSimpleBeanDefinitionParser {
        private ConstantBeanDefinitionParser() {
        }

        @Override
        protected Class<?> getBeanClass(Element element) {
            return FieldRetrievingFactoryBean.class;
        }

        @Override
        protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            if (!StringUtils.hasText(id)) {
                id = element.getAttribute("static-field");
            }
            return id;
        }
    }
}

