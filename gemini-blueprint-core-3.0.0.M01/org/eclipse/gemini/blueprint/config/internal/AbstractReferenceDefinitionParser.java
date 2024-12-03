/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.core.Conventions
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.config.internal;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.eclipse.gemini.blueprint.config.internal.OsgiDefaultsDefinition;
import org.eclipse.gemini.blueprint.config.internal.adapter.OsgiServiceLifecycleListenerAdapter;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.eclipse.gemini.blueprint.config.internal.util.ReferenceParsingUtil;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.util.BeanReferenceFactoryBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractReferenceDefinitionParser
extends AbstractBeanDefinitionParser {
    private static final String LISTENERS_PROP = "listeners";
    private static final String AVAILABILITY_PROP = "availability";
    private static final String SERVICE_BEAN_NAME_PROP = "serviceBeanName";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String CCL_PROP = "importContextClassLoader";
    private static final String TARGET_BEAN_NAME_PROP = "targetBeanName";
    private static final String TARGET_PROP = "target";
    private static final String LISTENER = "listener";
    private static final String REFERENCE_LISTENER = "reference-listener";
    private static final String REF = "ref";
    private static final String INTERFACE = "interface";
    private static final String INTERFACES = "interfaces";
    private static final String AVAILABILITY = "availability";
    private static final String CARDINALITY = "cardinality";
    private static final String SERVICE_BEAN_NAME = "bean-name";
    private static final String CONTEXT_CLASSLOADER = "context-class-loader";
    public static final String GENERATED_REF = "org.eclipse.gemini.blueprint.config.reference.generated";
    public static final String PROMOTED_REF = "org.eclipse.gemini.blueprint.config.reference.promoted";

    protected OsgiDefaultsDefinition resolveDefaults(Document document, ParserContext parserContext) {
        return new OsgiDefaultsDefinition(document, parserContext);
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        Class beanClass = this.getBeanClass(element);
        Assert.notNull((Object)beanClass);
        if (beanClass != null) {
            builder.getRawBeanDefinition().setBeanClass(beanClass);
        }
        builder.setRole(2);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource((Object)element));
        OsgiDefaultsDefinition defaults = this.resolveDefaults(element.getOwnerDocument(), parserContext);
        this.applyDefaults(parserContext, defaults, builder);
        this.doParse(element, parserContext, builder);
        AbstractBeanDefinition def = builder.getBeanDefinition();
        if (parserContext.isNested()) {
            String value = element.getAttribute("id");
            value = StringUtils.hasText((String)value) ? value + "#" : "";
            String generatedName = this.generateBeanName(value, (BeanDefinition)def, parserContext);
            def.setLazyInit(true);
            def.setAutowireCandidate(false);
            def.setAttribute(PROMOTED_REF, (Object)Boolean.TRUE);
            BeanDefinitionHolder holder = new BeanDefinitionHolder((BeanDefinition)def, generatedName);
            BeanDefinitionReaderUtils.registerBeanDefinition((BeanDefinitionHolder)holder, (BeanDefinitionRegistry)parserContext.getRegistry());
            return this.createBeanReferenceDefinition(generatedName, (BeanDefinition)def);
        }
        return def;
    }

    protected void applyDefaults(ParserContext parserContext, OsgiDefaultsDefinition defaults, BeanDefinitionBuilder builder) {
        if (parserContext.isNested()) {
            builder.setScope(parserContext.getContainingBeanDefinition().getScope());
        }
        if (parserContext.isDefaultLazyInit()) {
            builder.setLazyInit(true);
        }
    }

    private AbstractBeanDefinition createBeanReferenceDefinition(String beanName, BeanDefinition actualDef) {
        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(BeanReferenceFactoryBean.class);
        def.setAttribute(GENERATED_REF, (Object)Boolean.TRUE);
        def.setOriginatingBeanDefinition(actualDef);
        def.setDependsOn(new String[]{beanName});
        def.setSynthetic(true);
        MutablePropertyValues mpv = new MutablePropertyValues();
        mpv.addPropertyValue(TARGET_BEAN_NAME_PROP, (Object)beanName);
        def.setPropertyValues(mpv);
        return def;
    }

    protected void doParse(Element element, ParserContext context, BeanDefinitionBuilder builder) {
        ReferenceParsingUtil.checkAvailabilityAndCardinalityDuplication(element, "availability", CARDINALITY, context);
        OsgiDefaultsDefinition defaults = this.resolveDefaults(element.getOwnerDocument(), context);
        ReferenceAttributesCallback callback = new ReferenceAttributesCallback();
        this.parseAttributes(element, builder, new AttributeCallback[]{callback}, defaults);
        if (!this.isCardinalitySpecified(builder)) {
            this.applyDefaultCardinality(builder, defaults);
        }
        this.parseNestedElements(element, context, builder);
        this.handleNestedDefinition(element, context, builder);
    }

    private boolean isCardinalitySpecified(BeanDefinitionBuilder builder) {
        return builder.getBeanDefinition().getPropertyValues().getPropertyValue("availability") != null;
    }

    protected void handleNestedDefinition(Element element, ParserContext context, BeanDefinitionBuilder builder) {
    }

    protected void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks, OsgiDefaultsDefinition defaults) {
        ParserUtils.parseCustomAttributes(element, builder, callbacks);
    }

    protected abstract Class getBeanClass(Element var1);

    protected void applyDefaultCardinality(BeanDefinitionBuilder builder, OsgiDefaultsDefinition defaults) {
        builder.addPropertyValue("availability", (Object)defaults.getAvailability());
    }

    protected void parseNestedElements(Element element, ParserContext context, BeanDefinitionBuilder builder) {
        this.parseInterfaces(element, context, builder);
        this.parseListeners(element, this.getListenerElementName(), context, builder);
        this.parseListeners(element, LISTENER, context, builder);
    }

    protected String getListenerElementName() {
        return REFERENCE_LISTENER;
    }

    protected void parseInterfaces(Element parent, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Element element = DomUtils.getChildElementByTagName((Element)parent, (String)"interfaces");
        if (element != null) {
            if (parent.hasAttribute(INTERFACE)) {
                parserContext.getReaderContext().error("either 'interface' attribute or <intefaces> sub-element has be specified", (Object)parent);
            }
            Set interfaces = this.parsePropertySetElement(parserContext, element, (BeanDefinition)builder.getBeanDefinition());
            builder.addPropertyValue("interfaces", (Object)interfaces);
        }
    }

    protected void parseListeners(Element element, String subElementName, ParserContext context, BeanDefinitionBuilder builder) {
        List listeners = DomUtils.getChildElementsByTagName((Element)element, (String)subElementName);
        ManagedList listenersRef = new ManagedList();
        for (Element listnr : listeners) {
            Object target = null;
            String targetName = null;
            NodeList nl = listnr.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                if (!(node instanceof Element)) continue;
                Element beanDef = (Element)node;
                if (listnr.hasAttribute(REF)) {
                    context.getReaderContext().error("nested bean declaration is not allowed if 'ref' attribute has been specified", (Object)beanDef);
                }
                if (!((target = this.parsePropertySubElement(context, beanDef, (BeanDefinition)builder.getBeanDefinition())) instanceof RuntimeBeanReference)) continue;
                targetName = ((RuntimeBeanReference)target).getBeanName();
            }
            MutablePropertyValues vals = new MutablePropertyValues();
            NamedNodeMap attrs = listnr.getAttributes();
            for (int x = 0; x < attrs.getLength(); ++x) {
                Attr attribute = (Attr)attrs.item(x);
                String name = attribute.getLocalName();
                if (REF.equals(name)) {
                    targetName = attribute.getValue();
                    continue;
                }
                vals.addPropertyValue(Conventions.attributeNameToPropertyName((String)name), (Object)attribute.getValue());
            }
            RootBeanDefinition wrapperDef = new RootBeanDefinition(OsgiServiceLifecycleListenerAdapter.class);
            if (targetName != null) {
                AbstractBeanDefinition bd;
                LinkedHashSet<String> str;
                BeanDefinition beanDefinition;
                BeanDefinitionRegistry registry = context.getRegistry();
                if (registry.containsBeanDefinition(targetName) && (beanDefinition = registry.getBeanDefinition(targetName)).getBeanClassName().equals(OsgiServiceFactoryBean.class.getName())) {
                    context.getReaderContext().error("service exporter '" + targetName + "' cannot be used as a reference listener", (Object)element);
                }
                if ((str = (LinkedHashSet<String>)(bd = builder.getBeanDefinition()).getAttribute("org.eclipse.gemini.blueprint.config.internal.reference.listener.ref.attr")) == null) {
                    str = new LinkedHashSet<String>(2);
                    bd.setAttribute("org.eclipse.gemini.blueprint.config.internal.reference.listener.ref.attr", str);
                }
                str.add(targetName);
                vals.addPropertyValue(TARGET_BEAN_NAME_PROP, (Object)targetName);
            } else {
                vals.addPropertyValue(TARGET_PROP, target);
            }
            wrapperDef.setPropertyValues(vals);
            wrapperDef.setRole(2);
            this.postProcessListenerDefinition((BeanDefinition)wrapperDef);
            listenersRef.add((Object)wrapperDef);
        }
        PropertyValue previousListener = builder.getRawBeanDefinition().getPropertyValues().getPropertyValue(LISTENERS_PROP);
        if (previousListener != null) {
            ManagedList ml = (ManagedList)previousListener.getValue();
            listenersRef.addAll(0, (Collection)ml);
        }
        builder.addPropertyValue(LISTENERS_PROP, (Object)listenersRef);
    }

    protected void postProcessListenerDefinition(BeanDefinition wrapperDef) {
    }

    protected Object parsePropertySubElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return context.getDelegate().parsePropertySubElement(beanDef, beanDefinition);
    }

    protected Set parsePropertySetElement(ParserContext context, Element beanDef, BeanDefinition beanDefinition) {
        return context.getDelegate().parseSetElement(beanDef, beanDefinition);
    }

    protected String generateBeanName(String prefix, BeanDefinition def, ParserContext parserContext) {
        String name;
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String generated = name = prefix + BeanDefinitionReaderUtils.generateBeanName((BeanDefinition)def, (BeanDefinitionRegistry)registry);
        int counter = 0;
        while (registry.containsBeanDefinition(generated)) {
            generated = name + "#" + counter;
            if (parserContext.isNested()) {
                generated = generated.concat("#generated");
            }
            ++counter;
        }
        return generated;
    }

    class ReferenceAttributesCallback
    implements AttributeCallback {
        ReferenceAttributesCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
            String name = attribute.getLocalName();
            String value = attribute.getValue().trim();
            if (AbstractReferenceDefinitionParser.CARDINALITY.equals(name)) {
                builder.addPropertyValue("availability", (Object)ReferenceParsingUtil.determineAvailabilityFromCardinality(value));
                return false;
            }
            if ("availability".equals(name)) {
                Availability avail = ReferenceParsingUtil.determineAvailability(value);
                builder.addPropertyValue("availability", (Object)avail);
                return false;
            }
            if (AbstractReferenceDefinitionParser.SERVICE_BEAN_NAME.equals(name)) {
                builder.addPropertyValue(AbstractReferenceDefinitionParser.SERVICE_BEAN_NAME_PROP, (Object)value);
                return false;
            }
            if (AbstractReferenceDefinitionParser.INTERFACE.equals(name)) {
                builder.addPropertyValue("interfaces", (Object)value);
                return false;
            }
            if (AbstractReferenceDefinitionParser.CONTEXT_CLASSLOADER.equals(name)) {
                String val = value.toUpperCase(Locale.ENGLISH).replace('-', '_');
                builder.addPropertyValue(AbstractReferenceDefinitionParser.CCL_PROP, (Object)ImportContextClassLoaderEnum.valueOf(val));
                return false;
            }
            return true;
        }
    }
}

