/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 *  org.springframework.beans.factory.config.RuntimeBeanNameReference
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.parsing.BeanEntry
 *  org.springframework.beans.factory.parsing.ConstructorArgumentEntry
 *  org.springframework.beans.factory.parsing.ParseState
 *  org.springframework.beans.factory.parsing.ParseState$Entry
 *  org.springframework.beans.factory.parsing.PropertyEntry
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.ManagedArray
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.ManagedProperties
 *  org.springframework.beans.factory.support.ManagedSet
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.blueprint.config.internal.BlueprintDefaultsDefinition;
import org.eclipse.gemini.blueprint.blueprint.config.internal.ParsingUtils;
import org.eclipse.gemini.blueprint.blueprint.config.internal.support.InstanceEqualityRuntimeBeanReference;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.beans.factory.parsing.ConstructorArgumentEntry;
import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BlueprintParser {
    private static final Log log = LogFactory.getLog(BlueprintParser.class);
    public static final String BEAN = "bean";
    public static final String COMPONENT_ID_ATTR = "component-id";
    public static final String CONSTRUCTOR_ARG = "argument";
    private static final String FACTORY_REF_ATTR = "factory-ref";
    private static final String LAZY_INIT_ATTR = "activation";
    private static final String LAZY_INIT_VALUE = "lazy";
    private static final String EAGER_INIT_VALUE = "eager";
    public static final String NAMESPACE_URI = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    public static final String DECLARED_SCOPE = "org.eclipse.gemini.blueprint.blueprint.xml.bean.declared.scope";
    private final ParseState parseState;
    private final Collection<String> usedNames;
    private ParserContext parserContext;
    private BlueprintDefaultsDefinition defaults;

    public BlueprintParser() {
        this(null, null);
    }

    private BlueprintParser(ParserContext parserContext) {
        this(null, null);
        this.parserContext = parserContext;
    }

    public BlueprintParser(ParseState parseState, Collection<String> usedNames) {
        this.parseState = parseState != null ? parseState : new ParseState();
        this.usedNames = usedNames != null ? usedNames : new LinkedHashSet();
    }

    public BeanDefinitionHolder parseAsHolder(Element componentElement, ParserContext parserContext) {
        this.parserContext = parserContext;
        this.defaults = new BlueprintDefaultsDefinition(componentElement.getOwnerDocument(), parserContext);
        BeanDefinitionHolder bdHolder = this.parseComponentDefinitionElement(componentElement, null);
        BeanDefinition bd = bdHolder.getBeanDefinition();
        if (bd != null) {
            bd.setAttribute("org.eclipse.gemini.blueprint.blueprint.config.internal.marker", (Object)Boolean.TRUE);
        }
        return bdHolder;
    }

    public BeanDefinition parse(Element componentElement, ParserContext parserContext) {
        return this.parseAsHolder(componentElement, parserContext).getBeanDefinition();
    }

    private BeanDefinitionHolder parseComponentDefinitionElement(Element ele, BeanDefinition containingBean) {
        AbstractBeanDefinition beanDefinition;
        String beanName;
        String id = ele.getAttribute("id");
        String nameAttr = ele.getAttribute("name");
        ArrayList<String> aliases = new ArrayList<String>(4);
        if (StringUtils.hasLength((String)nameAttr)) {
            String[] nameArr = StringUtils.tokenizeToStringArray((String)nameAttr, (String)",; ");
            aliases.addAll(Arrays.asList(nameArr));
        }
        if (!StringUtils.hasText((String)(beanName = id)) && !aliases.isEmpty()) {
            beanName = (String)aliases.remove(0);
            if (log.isDebugEnabled()) {
                log.debug((Object)("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases"));
            }
        }
        if (containingBean == null) {
            if (this.checkNameUniqueness(beanName, aliases, this.usedNames)) {
                this.error("Bean name '" + beanName + "' is already used in this file", ele);
            }
            if (ParsingUtils.isReservedName(beanName, ele, this.parserContext)) {
                this.error("Blueprint reserved name '" + beanName + "' cannot be used", ele);
            }
        }
        if ((beanDefinition = this.parseBeanDefinitionElement(ele, beanName, containingBean)) != null) {
            if (!StringUtils.hasText((String)beanName)) {
                try {
                    beanName = containingBean != null ? ParsingUtils.generateBlueprintBeanName((BeanDefinition)beanDefinition, this.parserContext.getRegistry(), true) : ParsingUtils.generateBlueprintBeanName((BeanDefinition)beanDefinition, this.parserContext.getRegistry(), false);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Neither XML 'id' nor 'name' specified - using generated bean name [" + beanName + "]"));
                    }
                }
                catch (Exception ex) {
                    this.error(ex.getMessage(), ele, ex);
                    return null;
                }
            }
            return new BeanDefinitionHolder((BeanDefinition)beanDefinition, beanName);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AbstractBeanDefinition parseBeanDefinitionElement(Element ele, String beanName, BeanDefinition containingBean) {
        this.parseState.push((ParseState.Entry)new BeanEntry(beanName));
        String className = null;
        if (ele.hasAttribute("class")) {
            className = ele.getAttribute("class").trim();
        }
        try {
            AbstractBeanDefinition beanDefinition = BeanDefinitionReaderUtils.createBeanDefinition(null, (String)className, (ClassLoader)this.parserContext.getReaderContext().getBeanClassLoader());
            String activation = ele.getAttribute(LAZY_INIT_ATTR);
            String scope = ele.getAttribute("scope");
            if (EAGER_INIT_VALUE.equals(activation) && "prototype".equals(scope)) {
                this.error("Prototype beans cannot be eagerly activated", ele);
            }
            if (StringUtils.hasText((String)scope)) {
                beanDefinition.setAttribute(DECLARED_SCOPE, (Object)Boolean.TRUE);
            }
            this.parseAttributes(ele, beanName, beanDefinition);
            if (containingBean != null) {
                beanDefinition.setLazyInit(true);
                beanDefinition.setScope("prototype");
            }
            beanDefinition.setDescription(DomUtils.getChildElementValueByTagName((Element)ele, (String)"description"));
            this.parseConstructorArgElements(ele, beanDefinition);
            this.parsePropertyElements(ele, beanDefinition);
            beanDefinition.setResource(this.parserContext.getReaderContext().getResource());
            beanDefinition.setSource(this.extractSource(ele));
            AbstractBeanDefinition abstractBeanDefinition = beanDefinition;
            return abstractBeanDefinition;
        }
        catch (ClassNotFoundException ex) {
            this.error("Bean class [" + className + "] not found", ele, ex);
        }
        catch (NoClassDefFoundError err) {
            this.error("Class that bean class [" + className + "] depends on not found", ele, err);
        }
        catch (Throwable ex) {
            this.error("Unexpected failure during bean definition parsing", ele, ex);
        }
        finally {
            this.parseState.pop();
        }
        return null;
    }

    private AbstractBeanDefinition parseAttributes(Element ele, String beanName, AbstractBeanDefinition beanDefinition) {
        AbstractBeanDefinition bd = this.parserContext.getDelegate().parseBeanDefinitionAttributes(ele, beanName, null, beanDefinition);
        String lazyInit = ele.getAttribute(LAZY_INIT_ATTR);
        if (StringUtils.hasText((String)lazyInit)) {
            if (lazyInit.equalsIgnoreCase(LAZY_INIT_VALUE)) {
                bd.setLazyInit(true);
            } else {
                bd.setLazyInit(false);
            }
        } else {
            bd.setLazyInit(this.getDefaults(ele).getDefaultInitialization());
        }
        String componentFactory = ele.getAttribute(FACTORY_REF_ATTR);
        if (StringUtils.hasText((String)componentFactory)) {
            bd.setFactoryBeanName(componentFactory);
        }
        if (StringUtils.hasText((String)bd.getDestroyMethodName()) && "prototype".equalsIgnoreCase(bd.getScope())) {
            this.error("Blueprint prototype beans cannot define destroy methods", ele);
        }
        return bd;
    }

    private boolean checkNameUniqueness(String beanName, Collection<String> aliases, Collection<String> usedNames) {
        String foundName = null;
        if (StringUtils.hasText((String)beanName) && usedNames.contains(beanName)) {
            foundName = beanName;
        }
        if (foundName == null) {
            foundName = (String)CollectionUtils.findFirstMatch(usedNames, aliases);
        }
        usedNames.add(beanName);
        usedNames.addAll(aliases);
        return foundName != null;
    }

    private void parseConstructorArgElements(Element ele, AbstractBeanDefinition beanDefinition) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element) || !DomUtils.nodeNameEquals((Node)node, (String)CONSTRUCTOR_ARG)) continue;
            this.parseConstructorArgElement((Element)node, beanDefinition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseConstructorArgElement(Element ele, AbstractBeanDefinition beanDefinition) {
        String indexAttr = ele.getAttribute("index");
        String typeAttr = ele.getAttribute("type");
        boolean hasIndex = false;
        int index = -1;
        if (StringUtils.hasLength((String)indexAttr)) {
            hasIndex = true;
            try {
                index = Integer.parseInt(indexAttr);
            }
            catch (NumberFormatException ex) {
                this.error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
            }
            if (index < 0) {
                this.error("'index' cannot be lower than 0", ele);
            }
        }
        try {
            this.parseState.push((ParseState.Entry)(hasIndex ? new ConstructorArgumentEntry(index) : new ConstructorArgumentEntry()));
            ConstructorArgumentValues values = beanDefinition.getConstructorArgumentValues();
            Integer indexInt = index;
            if (values.getIndexedArgumentValues().containsKey(indexInt)) {
                this.error("duplicate 'index' with value=[" + index + "] specified", ele);
            }
            Object value = this.parsePropertyValue(ele, (BeanDefinition)beanDefinition, null);
            ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
            if (StringUtils.hasLength((String)typeAttr)) {
                valueHolder.setType(typeAttr);
            }
            valueHolder.setSource(this.extractSource(ele));
            if (hasIndex) {
                values.addIndexedArgumentValue(index, valueHolder);
            } else {
                values.addGenericArgumentValue(valueHolder);
            }
            if (!values.getGenericArgumentValues().isEmpty() && !values.getIndexedArgumentValues().isEmpty()) {
                this.error("indexed and non-indexed constructor arguments are not supported by Blueprint; consider using the Spring namespace instead", ele);
            }
        }
        finally {
            this.parseState.pop();
        }
    }

    private void parsePropertyElements(Element ele, AbstractBeanDefinition beanDefinition) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element) || !DomUtils.nodeNameEquals((Node)node, (String)"property")) continue;
            this.parsePropertyElement((Element)node, (BeanDefinition)beanDefinition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parsePropertyElement(Element ele, BeanDefinition bd) {
        String propertyName = ele.getAttribute("name");
        if (!StringUtils.hasLength((String)propertyName)) {
            this.error("Tag 'property' must have a 'name' attribute", ele);
            return;
        }
        this.parseState.push((ParseState.Entry)new PropertyEntry(propertyName));
        try {
            if (bd.getPropertyValues().contains(propertyName)) {
                this.error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
                return;
            }
            Object val = this.parsePropertyValue(ele, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);
            pv.setSource(this.parserContext.extractSource((Object)ele));
            bd.getPropertyValues().addPropertyValue(pv);
        }
        finally {
            this.parseState.pop();
        }
    }

    private Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = propertyName != null ? "<property> element for property '" + propertyName + "'" : "<constructor-arg> element";
        NodeList nl = ele.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element) || DomUtils.nodeNameEquals((Node)node, (String)"description")) continue;
            if (subElement != null) {
                this.error(elementName + " must not contain more than one sub-element", ele);
                continue;
            }
            subElement = (Element)node;
        }
        boolean hasRefAttribute = ele.hasAttribute("ref");
        boolean hasValueAttribute = ele.hasAttribute("value");
        if (hasRefAttribute && hasValueAttribute || (hasRefAttribute || hasValueAttribute) && subElement != null) {
            this.error(elementName + " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
        }
        if (hasRefAttribute) {
            String refName = ele.getAttribute("ref");
            if (!StringUtils.hasText((String)refName)) {
                this.error(elementName + " contains empty 'ref' attribute", ele);
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            ref.setSource(this.parserContext.extractSource((Object)ele));
            return ref;
        }
        if (hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute("value"));
            valueHolder.setSource(this.parserContext.extractSource((Object)ele));
            return valueHolder;
        }
        if (subElement != null) {
            return this.parsePropertySubElement(subElement, bd, null);
        }
        this.error(elementName + " must specify a ref or value", ele);
        return null;
    }

    public static Object parsePropertySubElement(ParserContext parserContext, Element ele, BeanDefinition bd) {
        return new BlueprintParser(parserContext).parsePropertySubElement(ele, bd, null);
    }

    public static Map<?, ?> parsePropertyMapElement(ParserContext parserContext, Element ele, BeanDefinition bd) {
        return new BlueprintParser(parserContext).parseMapElement(ele, bd);
    }

    public static Set<?> parsePropertySetElement(ParserContext parserContext, Element ele, BeanDefinition bd) {
        return new BlueprintParser(parserContext).parseSetElement(ele, bd);
    }

    private Object parsePropertySubElement(Element ele, BeanDefinition bd, String defaultValueType) {
        String namespaceUri = ele.getNamespaceURI();
        if (this.parserContext.getDelegate().isDefaultNamespace(namespaceUri)) {
            return this.parserContext.getDelegate().parsePropertySubElement(ele, bd);
        }
        if (!NAMESPACE_URI.equals(namespaceUri)) {
            return this.parserContext.getDelegate().parseCustomElement(ele);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)BEAN)) {
            BeanDefinitionHolder bdHolder = this.parseComponentDefinitionElement(ele, bd);
            if (bdHolder != null) {
                bdHolder = ParsingUtils.decorateBeanDefinitionIfRequired(ele, bdHolder, this.parserContext);
            }
            return bdHolder;
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"ref")) {
            return this.parseRefElement(ele);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"idref")) {
            return this.parseIdRefElement(ele);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"value")) {
            return this.parseValueElement(ele, defaultValueType);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"null")) {
            TypedStringValue nullHolder = new TypedStringValue(null);
            nullHolder.setSource(this.parserContext.extractSource((Object)ele));
            return nullHolder;
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"array")) {
            return this.parseArrayElement(ele, bd);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"list")) {
            return this.parseListElement(ele, bd);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"set")) {
            return this.parseSetElement(ele, bd);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"map")) {
            return this.parseMapElement(ele, bd);
        }
        if (DomUtils.nodeNameEquals((Node)ele, (String)"props")) {
            return this.parsePropsElement(ele);
        }
        return this.parserContext.getDelegate().parseCustomElement(ele, bd);
    }

    private Object parseRefElement(Element ele) {
        String refName = ele.getAttribute(COMPONENT_ID_ATTR);
        if (!StringUtils.hasLength((String)refName)) {
            this.error("'component-id' is required for <ref> element", ele);
            return null;
        }
        if (!StringUtils.hasText((String)refName)) {
            this.error("<ref> element contains empty target attribute", ele);
            return null;
        }
        InstanceEqualityRuntimeBeanReference ref = new InstanceEqualityRuntimeBeanReference(refName);
        ref.setSource(this.parserContext.extractSource((Object)ele));
        return ref;
    }

    private Object parseIdRefElement(Element ele) {
        String refName = ele.getAttribute(COMPONENT_ID_ATTR);
        if (!StringUtils.hasLength((String)refName)) {
            this.error("'component-id' is required for <idref> element", ele);
            return null;
        }
        if (!StringUtils.hasText((String)refName)) {
            this.error("<idref> element contains empty target attribute", ele);
            return null;
        }
        RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
        ref.setSource(this.parserContext.extractSource((Object)ele));
        return ref;
    }

    private Object parseValueElement(Element ele, String defaultTypeName) {
        String value = DomUtils.getTextValue((Element)ele);
        String specifiedTypeName = ele.getAttribute("type");
        String typeName = specifiedTypeName;
        if (!StringUtils.hasText((String)typeName)) {
            typeName = defaultTypeName;
        }
        try {
            TypedStringValue typedValue = this.buildTypedStringValue(value, typeName);
            typedValue.setSource(this.extractSource(ele));
            typedValue.setSpecifiedTypeName(specifiedTypeName);
            return typedValue;
        }
        catch (ClassNotFoundException ex) {
            this.error("Type class [" + typeName + "] not found for <value> element", ele, ex);
            return value;
        }
    }

    private TypedStringValue buildTypedStringValue(String value, String targetTypeName) throws ClassNotFoundException {
        TypedStringValue typedValue;
        ClassLoader classLoader = this.parserContext.getReaderContext().getBeanClassLoader();
        if (!StringUtils.hasText((String)targetTypeName)) {
            typedValue = new TypedStringValue(value);
        } else if (classLoader != null) {
            Class targetType = ClassUtils.forName((String)targetTypeName, (ClassLoader)classLoader);
            typedValue = new TypedStringValue(value, targetType);
        } else {
            typedValue = new TypedStringValue(value, targetTypeName);
        }
        return typedValue;
    }

    public Object parseArrayElement(Element arrayEle, BeanDefinition bd) {
        String elementType = arrayEle.getAttribute("value-type");
        NodeList nl = arrayEle.getChildNodes();
        ManagedArray target = new ManagedArray(elementType, nl.getLength());
        target.setSource(this.extractSource(arrayEle));
        target.setElementTypeName(elementType);
        target.setMergeEnabled(this.parseMergeAttribute(arrayEle));
        this.parseCollectionElements(nl, (Collection<Object>)target, bd, elementType);
        return target;
    }

    public List<?> parseListElement(Element collectionEle, BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute("value-type");
        NodeList nl = collectionEle.getChildNodes();
        ManagedList target = new ManagedList(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, (Collection<Object>)target, bd, defaultElementType);
        return target;
    }

    public Set<?> parseSetElement(Element collectionEle, BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute("value-type");
        NodeList nl = collectionEle.getChildNodes();
        ManagedSet target = new ManagedSet(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, (Collection<Object>)target, bd, defaultElementType);
        return target;
    }

    protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target, BeanDefinition bd, String defaultElementType) {
        for (int i = 0; i < elementNodes.getLength(); ++i) {
            Node node = elementNodes.item(i);
            if (!(node instanceof Element) || DomUtils.nodeNameEquals((Node)node, (String)"description")) continue;
            target.add(this.parsePropertySubElement((Element)node, bd, defaultElementType));
        }
    }

    public Map<?, ?> parseMapElement(Element mapEle, BeanDefinition bd) {
        String defaultKeyType = mapEle.getAttribute("key-type");
        String defaultValueType = mapEle.getAttribute("value-type");
        List entryEles = DomUtils.getChildElementsByTagName((Element)mapEle, (String)"entry");
        ManagedMap map = new ManagedMap(entryEles.size());
        map.setSource(this.extractSource(mapEle));
        map.setKeyTypeName(defaultKeyType);
        map.setValueTypeName(defaultValueType);
        map.setMergeEnabled(this.parseMergeAttribute(mapEle));
        for (Element entryEle : entryEles) {
            NodeList entrySubNodes = entryEle.getChildNodes();
            Element keyEle = null;
            Element valueEle = null;
            for (int j = 0; j < entrySubNodes.getLength(); ++j) {
                Node node = entrySubNodes.item(j);
                if (!(node instanceof Element)) continue;
                Element candidateEle = (Element)node;
                if (DomUtils.nodeNameEquals((Node)candidateEle, (String)"key")) {
                    if (keyEle != null) {
                        this.error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
                        continue;
                    }
                    keyEle = candidateEle;
                    continue;
                }
                if (valueEle != null) {
                    this.error("<entry> element must not contain more than one value sub-element", entryEle);
                    continue;
                }
                valueEle = candidateEle;
            }
            Object key = null;
            boolean hasKeyAttribute = entryEle.hasAttribute("key");
            boolean hasKeyRefAttribute = entryEle.hasAttribute("key-ref");
            if (hasKeyAttribute && hasKeyRefAttribute || (hasKeyAttribute || hasKeyRefAttribute) && keyEle != null) {
                this.error("<entry> element is only allowed to contain either a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
            }
            if (hasKeyAttribute) {
                key = this.buildTypedStringValueForMap(entryEle.getAttribute("key"), defaultKeyType, entryEle);
            } else if (hasKeyRefAttribute) {
                String refName = entryEle.getAttribute("key-ref");
                if (!StringUtils.hasText((String)refName)) {
                    this.error("<entry> element contains empty 'key-ref' attribute", entryEle);
                }
                RuntimeBeanReference ref = new RuntimeBeanReference(refName);
                ref.setSource(this.extractSource(entryEle));
                key = ref;
            } else if (keyEle != null) {
                key = this.parseKeyElement(keyEle, bd, defaultKeyType);
            } else {
                this.error("<entry> element must specify a key", entryEle);
            }
            Object value = null;
            boolean hasValueAttribute = entryEle.hasAttribute("value");
            boolean hasValueRefAttribute = entryEle.hasAttribute("value-ref");
            if (hasValueAttribute && hasValueRefAttribute || (hasValueAttribute || hasValueRefAttribute) && valueEle != null) {
                this.error("<entry> element is only allowed to contain either 'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);
            }
            if (hasValueAttribute) {
                value = this.buildTypedStringValueForMap(entryEle.getAttribute("value"), defaultValueType, entryEle);
            } else if (hasValueRefAttribute) {
                String refName = entryEle.getAttribute("value-ref");
                if (!StringUtils.hasText((String)refName)) {
                    this.error("<entry> element contains empty 'value-ref' attribute", entryEle);
                }
                RuntimeBeanReference ref = new RuntimeBeanReference(refName);
                ref.setSource(this.extractSource(entryEle));
                value = ref;
            } else if (valueEle != null) {
                value = this.parsePropertySubElement(valueEle, bd, defaultValueType);
            } else {
                this.error("<entry> element must specify a value", entryEle);
            }
            map.put(key, value);
        }
        return map;
    }

    public Properties parsePropsElement(Element propsEle) {
        ManagedProperties props = new ManagedProperties();
        props.setSource(this.extractSource(propsEle));
        props.setMergeEnabled(this.parseMergeAttribute(propsEle));
        List propEles = DomUtils.getChildElementsByTagName((Element)propsEle, (String)"prop");
        for (Element propEle : propEles) {
            String key = propEle.getAttribute("key");
            String value = DomUtils.getTextValue((Element)propEle).trim();
            TypedStringValue keyHolder = new TypedStringValue(key);
            keyHolder.setSource(this.extractSource(propEle));
            TypedStringValue valueHolder = new TypedStringValue(value);
            valueHolder.setSource(this.extractSource(propEle));
            props.put((Object)keyHolder, (Object)valueHolder);
        }
        return props;
    }

    private boolean parseMergeAttribute(Element element) {
        return this.parserContext.getDelegate().parseMergeAttribute(element);
    }

    private Object buildTypedStringValueForMap(String value, String defaultTypeName, Element entryEle) {
        try {
            TypedStringValue typedValue = this.buildTypedStringValue(value, defaultTypeName);
            typedValue.setSource(this.extractSource(entryEle));
            return typedValue;
        }
        catch (ClassNotFoundException ex) {
            this.error("Type class [" + defaultTypeName + "] not found for Map key/value type", entryEle, ex);
            return value;
        }
    }

    private Object parseKeyElement(Element keyEle, BeanDefinition bd, String defaultKeyTypeName) {
        NodeList nl = keyEle.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            if (subElement != null) {
                this.error("<key> element must not contain more than one value sub-element", keyEle);
                continue;
            }
            subElement = (Element)node;
        }
        return this.parsePropertySubElement(subElement, bd, defaultKeyTypeName);
    }

    private Object extractSource(Element ele) {
        return this.parserContext.extractSource((Object)ele);
    }

    private void error(String message, Node source) {
        this.parserContext.getReaderContext().error(message, (Object)source, this.parseState.snapshot());
    }

    private void error(String message, Node source, Throwable cause) {
        this.parserContext.getReaderContext().error(message, (Object)source, this.parseState.snapshot(), cause);
    }

    private BlueprintDefaultsDefinition getDefaults(Element ele) {
        if (this.defaults == null) {
            this.defaults = new BlueprintDefaultsDefinition(ele.getOwnerDocument(), this.parserContext);
        }
        return this.defaults;
    }
}

