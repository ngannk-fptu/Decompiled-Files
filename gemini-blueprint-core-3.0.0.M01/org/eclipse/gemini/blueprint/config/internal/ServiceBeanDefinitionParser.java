/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.core.Conventions
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.config.internal;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.adapter.OsgiServiceRegistrationListenerAdapter;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    private static final String TARGET_BEAN_NAME_PROP = "targetBeanName";
    private static final String TARGET_PROP = "target";
    private static final String LISTENERS_PROP = "listeners";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String AUTOEXPORT_PROP = "interfaceDetector";
    private static final String CCL_PROP = "exportContextClassLoader";
    private static final String INTERFACES_ID = "interfaces";
    private static final String INTERFACE = "interface";
    private static final String PROPS_ID = "service-properties";
    private static final String LISTENER = "registration-listener";
    private static final String REF = "ref";
    private static final String AUTOEXPORT = "auto-export";
    private static final String CONTEXT_CLASSLOADER = "context-class-loader";

    protected Class getBeanClass(Element element) {
        return OsgiServiceFactoryBean.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.setRole(2);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource((Object)element));
        OsgiDefaultsDefinition defaults = this.resolveDefaults(element.getOwnerDocument(), parserContext);
        this.applyDefaults(parserContext, defaults, builder);
        ServiceAttributeCallback callback = new ServiceAttributeCallback();
        this.parseAttributes(element, builder, new AttributeCallback[]{callback}, defaults);
        Object target = null;
        if (element.hasAttribute(REF)) {
            target = new RuntimeBeanReference(element.getAttribute(REF));
        }
        NodeList nl = element.getChildNodes();
        ManagedList listeners = new ManagedList();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            Element subElement = (Element)node;
            String name = subElement.getLocalName();
            if (this.parseInterfaces(element, subElement, parserContext, builder) || this.parseServiceProperties(element, subElement, parserContext, builder)) continue;
            if (LISTENER.equals(name)) {
                BeanDefinition listenerDef = this.parseListener(parserContext, subElement, builder);
                this.postProcessListenerDefinition(listenerDef);
                listeners.add((Object)listenerDef);
                continue;
            }
            if ("description".equals(name)) {
                builder.getRawBeanDefinition().setDescription(subElement.getTextContent());
                continue;
            }
            target = this.parseBeanReference(element, subElement, parserContext, builder);
        }
        if (target instanceof RuntimeBeanReference) {
            builder.addPropertyValue(TARGET_BEAN_NAME_PROP, (Object)target.getBeanName());
        } else {
            builder.addPropertyValue(TARGET_PROP, target);
        }
        builder.addPropertyValue(LISTENERS_PROP, (Object)listeners);
    }

    protected void applyDefaults(ParserContext parserContext, OsgiDefaultsDefinition defaults, BeanDefinitionBuilder builder) {
        if (parserContext.isDefaultLazyInit()) {
            builder.setLazyInit(true);
        }
    }

    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        ParserUtils.parseCustomAttributes(element, builder, callbacks);
    }

    protected OsgiDefaultsDefinition resolveDefaults(Document document, ParserContext parserContext) {
        return new OsgiDefaultsDefinition(document, parserContext);
    }

    protected void postProcessListenerDefinition(BeanDefinition wrapperDef) {
    }

    private boolean parseInterfaces(Element parent, Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String name = element.getLocalName();
        if ("interfaces".equals(name)) {
            if (parent.hasAttribute(INTERFACE)) {
                parserContext.getReaderContext().error("either 'interface' attribute or <intefaces> sub-element has be specified", (Object)parent);
            }
            Set interfaces = this.parsePropertySetElement(parserContext, element, (BeanDefinition)builder.getBeanDefinition());
            builder.addPropertyValue("interfaces", (Object)interfaces);
            return true;
        }
        return false;
    }

    private boolean parseServiceProperties(Element parent, Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String name = element.getLocalName();
        if (PROPS_ID.equals(name)) {
            Object props = null;
            String ref = element.getAttribute(REF).trim();
            boolean hasRef = StringUtils.hasText((String)ref);
            if (DomUtils.getChildElementsByTagName((Element)element, (String)"entry").size() > 0) {
                if (hasRef) {
                    parserContext.getReaderContext().error("Nested service properties definition cannot be used when attribute 'ref' is specified", (Object)element);
                } else {
                    props = this.parsePropertyMapElement(parserContext, element, (BeanDefinition)builder.getRawBeanDefinition());
                }
            }
            if (hasRef) {
                props = new RuntimeBeanReference(ref);
            }
            if (props != null) {
                builder.addPropertyValue(Conventions.attributeNameToPropertyName((String)PROPS_ID), props);
            } else {
                parserContext.getReaderContext().error("Invalid service property declaration", (Object)element);
            }
            return true;
        }
        return false;
    }

    private Object parseBeanReference(Element parent, Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (parent.hasAttribute(REF)) {
            parserContext.getReaderContext().error("nested bean definition/reference cannot be used when attribute 'ref' is specified", (Object)parent);
        }
        return this.parsePropertySubElement(parserContext, element, (BeanDefinition)builder.getBeanDefinition());
    }

    protected BeanDefinition parseListener(ParserContext context, Element element, BeanDefinitionBuilder builder) {
        NodeList nl = element.getChildNodes();
        Object target = null;
        String targetName = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            Element nestedDefinition = (Element)node;
            if (element.hasAttribute(REF)) {
                context.getReaderContext().error("nested bean declaration is not allowed if 'ref' attribute has been specified", (Object)nestedDefinition);
            }
            if (!((target = this.parsePropertySubElement(context, nestedDefinition, (BeanDefinition)builder.getBeanDefinition())) instanceof RuntimeBeanReference)) continue;
            targetName = ((RuntimeBeanReference)target).getBeanName();
        }
        MutablePropertyValues vals = new MutablePropertyValues();
        NamedNodeMap attrs = element.getAttributes();
        for (int x = 0; x < attrs.getLength(); ++x) {
            Attr attribute = (Attr)attrs.item(x);
            String name = attribute.getLocalName();
            if (REF.equals(name)) {
                targetName = attribute.getValue();
                continue;
            }
            vals.addPropertyValue(Conventions.attributeNameToPropertyName((String)name), (Object)attribute.getValue());
        }
        RootBeanDefinition wrapperDef = new RootBeanDefinition(OsgiServiceRegistrationListenerAdapter.class);
        if (targetName != null) {
            vals.addPropertyValue(TARGET_BEAN_NAME_PROP, targetName);
        } else {
            vals.addPropertyValue(TARGET_PROP, target);
        }
        wrapperDef.setPropertyValues(vals);
        return wrapperDef;
    }

    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    protected Object parsePropertySubElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return context.getDelegate().parsePropertySubElement(beanDef, beanDefinition);
    }

    protected Set parsePropertySetElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return context.getDelegate().parseSetElement(beanDef, beanDefinition);
    }

    protected Map parsePropertyMapElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return context.getDelegate().parseMapElement(beanDef, beanDefinition);
    }

    protected void validateServiceReferences(Element element, String serviceId, ParserContext parserContext) {
        String[] names;
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        for (String name : names = registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(name);
            Collection exporters = (Collection)definition.getAttribute("org.eclipse.gemini.blueprint.config.internal.reference.listener.ref.attr");
            if (exporters == null || !exporters.contains(serviceId)) continue;
            parserContext.getReaderContext().error("Service exporter '" + serviceId + "' cannot be used as a reference listener by '" + name + "'", (Object)element);
        }
    }

    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        String id = super.resolveId(element, definition, parserContext);
        this.validateServiceReferences(element, id, parserContext);
        return id;
    }

    class ServiceAttributeCallback
    implements AttributeCallback {
        ServiceAttributeCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder bldr) {
            String name = attribute.getLocalName();
            if (ServiceBeanDefinitionParser.INTERFACE.equals(name)) {
                bldr.addPropertyValue("interfaces", (Object)attribute.getValue());
                return false;
            }
            if (ServiceBeanDefinitionParser.REF.equals(name)) {
                return false;
            }
            if (ServiceBeanDefinitionParser.AUTOEXPORT.equals(name)) {
                String label = attribute.getValue().toUpperCase(Locale.ENGLISH).replace('-', '_');
                bldr.addPropertyValue(ServiceBeanDefinitionParser.AUTOEXPORT_PROP, (Object)Enum.valueOf(DefaultInterfaceDetector.class, label));
                return false;
            }
            if (ServiceBeanDefinitionParser.CONTEXT_CLASSLOADER.equals(name)) {
                String value = attribute.getValue().toUpperCase(Locale.ENGLISH).replace('-', '_');
                bldr.addPropertyValue(ServiceBeanDefinitionParser.CCL_PROP, (Object)value);
                return false;
            }
            return true;
        }
    }
}

