/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
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
import org.springframework.beans.factory.parsing.QualifierEntry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.MethodOverrides;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeanDefinitionParserDelegate {
    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
    public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";
    public static final String TRUE_VALUE = "true";
    public static final String FALSE_VALUE = "false";
    public static final String DEFAULT_VALUE = "default";
    public static final String DESCRIPTION_ELEMENT = "description";
    public static final String AUTOWIRE_NO_VALUE = "no";
    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
    public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String BEAN_ELEMENT = "bean";
    public static final String META_ELEMENT = "meta";
    public static final String ID_ATTRIBUTE = "id";
    public static final String PARENT_ATTRIBUTE = "parent";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String ABSTRACT_ATTRIBUTE = "abstract";
    public static final String SCOPE_ATTRIBUTE = "scope";
    private static final String SINGLETON_ATTRIBUTE = "singleton";
    public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
    public static final String AUTOWIRE_ATTRIBUTE = "autowire";
    public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";
    public static final String PRIMARY_ATTRIBUTE = "primary";
    public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
    public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
    public static final String INDEX_ATTRIBUTE = "index";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String VALUE_TYPE_ATTRIBUTE = "value-type";
    public static final String KEY_TYPE_ATTRIBUTE = "key-type";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";
    public static final String REPLACED_METHOD_ELEMENT = "replaced-method";
    public static final String REPLACER_ATTRIBUTE = "replacer";
    public static final String ARG_TYPE_ELEMENT = "arg-type";
    public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";
    public static final String REF_ELEMENT = "ref";
    public static final String IDREF_ELEMENT = "idref";
    public static final String BEAN_REF_ATTRIBUTE = "bean";
    public static final String PARENT_REF_ATTRIBUTE = "parent";
    public static final String VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";
    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String SET_ELEMENT = "set";
    public static final String MAP_ELEMENT = "map";
    public static final String ENTRY_ELEMENT = "entry";
    public static final String KEY_ELEMENT = "key";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String KEY_REF_ATTRIBUTE = "key-ref";
    public static final String VALUE_REF_ATTRIBUTE = "value-ref";
    public static final String PROPS_ELEMENT = "props";
    public static final String PROP_ELEMENT = "prop";
    public static final String MERGE_ATTRIBUTE = "merge";
    public static final String QUALIFIER_ELEMENT = "qualifier";
    public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";
    public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";
    public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";
    public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";
    public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";
    public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";
    public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final XmlReaderContext readerContext;
    private final DocumentDefaultsDefinition defaults = new DocumentDefaultsDefinition();
    private final ParseState parseState = new ParseState();
    private final Set<String> usedNames = new HashSet<String>();

    public BeanDefinitionParserDelegate(XmlReaderContext readerContext) {
        Assert.notNull((Object)readerContext, "XmlReaderContext must not be null");
        this.readerContext = readerContext;
    }

    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }

    @Nullable
    protected Object extractSource(Element ele) {
        return this.readerContext.extractSource(ele);
    }

    protected void error(String message, Node source) {
        this.readerContext.error(message, (Object)source, this.parseState.snapshot());
    }

    protected void error(String message, Element source) {
        this.readerContext.error(message, (Object)source, this.parseState.snapshot());
    }

    protected void error(String message, Element source, Throwable cause) {
        this.readerContext.error(message, source, this.parseState.snapshot(), cause);
    }

    public void initDefaults(Element root) {
        this.initDefaults(root, null);
    }

    public void initDefaults(Element root, @Nullable BeanDefinitionParserDelegate parent) {
        this.populateDefaults(this.defaults, parent != null ? parent.defaults : null, root);
        this.readerContext.fireDefaultsRegistered(this.defaults);
    }

    protected void populateDefaults(DocumentDefaultsDefinition defaults, @Nullable DocumentDefaultsDefinition parentDefaults, Element root) {
        String lazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
        if (this.isDefaultValue(lazyInit)) {
            lazyInit = parentDefaults != null ? parentDefaults.getLazyInit() : FALSE_VALUE;
        }
        defaults.setLazyInit(lazyInit);
        String merge = root.getAttribute(DEFAULT_MERGE_ATTRIBUTE);
        if (this.isDefaultValue(merge)) {
            merge = parentDefaults != null ? parentDefaults.getMerge() : FALSE_VALUE;
        }
        defaults.setMerge(merge);
        String autowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
        if (this.isDefaultValue(autowire)) {
            autowire = parentDefaults != null ? parentDefaults.getAutowire() : AUTOWIRE_NO_VALUE;
        }
        defaults.setAutowire(autowire);
        if (root.hasAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE)) {
            defaults.setAutowireCandidates(root.getAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE));
        } else if (parentDefaults != null) {
            defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
        }
        if (root.hasAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE)) {
            defaults.setInitMethod(root.getAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE));
        } else if (parentDefaults != null) {
            defaults.setInitMethod(parentDefaults.getInitMethod());
        }
        if (root.hasAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE)) {
            defaults.setDestroyMethod(root.getAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE));
        } else if (parentDefaults != null) {
            defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
        }
        defaults.setSource(this.readerContext.extractSource(root));
    }

    public DocumentDefaultsDefinition getDefaults() {
        return this.defaults;
    }

    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        BeanDefinitionDefaults bdd = new BeanDefinitionDefaults();
        bdd.setLazyInit(TRUE_VALUE.equalsIgnoreCase(this.defaults.getLazyInit()));
        bdd.setAutowireMode(this.getAutowireMode(DEFAULT_VALUE));
        bdd.setInitMethodName(this.defaults.getInitMethod());
        bdd.setDestroyMethodName(this.defaults.getDestroyMethod());
        return bdd;
    }

    @Nullable
    public String[] getAutowireCandidatePatterns() {
        String candidatePattern = this.defaults.getAutowireCandidates();
        return candidatePattern != null ? StringUtils.commaDelimitedListToStringArray(candidatePattern) : null;
    }

    @Nullable
    public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
        return this.parseBeanDefinitionElement(ele, null);
    }

    @Nullable
    public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
        AbstractBeanDefinition beanDefinition;
        String beanName;
        String id = ele.getAttribute(ID_ATTRIBUTE);
        String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
        ArrayList<String> aliases = new ArrayList<String>();
        if (StringUtils.hasLength(nameAttr)) {
            String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
            aliases.addAll(Arrays.asList(nameArr));
        }
        if (!StringUtils.hasText(beanName = id) && !aliases.isEmpty()) {
            beanName = (String)aliases.remove(0);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
            }
        }
        if (containingBean == null) {
            this.checkNameUniqueness(beanName, aliases, ele);
        }
        if ((beanDefinition = this.parseBeanDefinitionElement(ele, beanName, containingBean)) != null) {
            if (!StringUtils.hasText(beanName)) {
                try {
                    if (containingBean != null) {
                        beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, this.readerContext.getRegistry(), true);
                    } else {
                        beanName = this.readerContext.generateBeanName(beanDefinition);
                        String beanClassName = beanDefinition.getBeanClassName();
                        if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() && !this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
                            aliases.add(beanClassName);
                        }
                    }
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Neither XML 'id' nor 'name' specified - using generated bean name [" + beanName + "]");
                    }
                }
                catch (Exception ex) {
                    this.error(ex.getMessage(), ele);
                    return null;
                }
            }
            String[] aliasesArray = StringUtils.toStringArray(aliases);
            return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
        }
        return null;
    }

    protected void checkNameUniqueness(String beanName, List<String> aliases, Element beanElement) {
        String foundName = null;
        if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
            foundName = beanName;
        }
        if (foundName == null) {
            foundName = CollectionUtils.findFirstMatch(this.usedNames, aliases);
        }
        if (foundName != null) {
            this.error("Bean name '" + foundName + "' is already used in this <beans> element", beanElement);
        }
        this.usedNames.add(beanName);
        this.usedNames.addAll(aliases);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public AbstractBeanDefinition parseBeanDefinitionElement(Element ele, String beanName, @Nullable BeanDefinition containingBean) {
        this.parseState.push(new BeanEntry(beanName));
        String className = null;
        if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
            className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
        }
        String parent = null;
        if (ele.hasAttribute("parent")) {
            parent = ele.getAttribute("parent");
        }
        try {
            AbstractBeanDefinition bd = this.createBeanDefinition(className, parent);
            this.parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
            bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
            this.parseMetaElements(ele, bd);
            this.parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
            this.parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
            this.parseConstructorArgElements(ele, bd);
            this.parsePropertyElements(ele, bd);
            this.parseQualifierElements(ele, bd);
            bd.setResource(this.readerContext.getResource());
            bd.setSource(this.extractSource(ele));
            AbstractBeanDefinition abstractBeanDefinition = bd;
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

    public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName, @Nullable BeanDefinition containingBean, AbstractBeanDefinition bd) {
        String autowireCandidate;
        String lazyInit;
        if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
            this.error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
        } else if (ele.hasAttribute(SCOPE_ATTRIBUTE)) {
            bd.setScope(ele.getAttribute(SCOPE_ATTRIBUTE));
        } else if (containingBean != null) {
            bd.setScope(containingBean.getScope());
        }
        if (ele.hasAttribute(ABSTRACT_ATTRIBUTE)) {
            bd.setAbstract(TRUE_VALUE.equals(ele.getAttribute(ABSTRACT_ATTRIBUTE)));
        }
        if (this.isDefaultValue(lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE))) {
            lazyInit = this.defaults.getLazyInit();
        }
        bd.setLazyInit(TRUE_VALUE.equals(lazyInit));
        String autowire = ele.getAttribute(AUTOWIRE_ATTRIBUTE);
        bd.setAutowireMode(this.getAutowireMode(autowire));
        if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
            String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
            bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
        }
        if (this.isDefaultValue(autowireCandidate = ele.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE))) {
            String candidatePattern = this.defaults.getAutowireCandidates();
            if (candidatePattern != null) {
                String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
                bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
            }
        } else {
            bd.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
        }
        if (ele.hasAttribute(PRIMARY_ATTRIBUTE)) {
            bd.setPrimary(TRUE_VALUE.equals(ele.getAttribute(PRIMARY_ATTRIBUTE)));
        }
        if (ele.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
            String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
            bd.setInitMethodName(initMethodName);
        } else if (this.defaults.getInitMethod() != null) {
            bd.setInitMethodName(this.defaults.getInitMethod());
            bd.setEnforceInitMethod(false);
        }
        if (ele.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
            String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
            bd.setDestroyMethodName(destroyMethodName);
        } else if (this.defaults.getDestroyMethod() != null) {
            bd.setDestroyMethodName(this.defaults.getDestroyMethod());
            bd.setEnforceDestroyMethod(false);
        }
        if (ele.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
            bd.setFactoryMethodName(ele.getAttribute(FACTORY_METHOD_ATTRIBUTE));
        }
        if (ele.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
            bd.setFactoryBeanName(ele.getAttribute(FACTORY_BEAN_ATTRIBUTE));
        }
        return bd;
    }

    protected AbstractBeanDefinition createBeanDefinition(@Nullable String className, @Nullable String parentName) throws ClassNotFoundException {
        return BeanDefinitionReaderUtils.createBeanDefinition(parentName, className, this.readerContext.getBeanClassLoader());
    }

    public void parseMetaElements(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, META_ELEMENT)) continue;
            Element metaElement = (Element)node;
            String key = metaElement.getAttribute("key");
            String value = metaElement.getAttribute("value");
            BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
            attribute.setSource(this.extractSource(metaElement));
            attributeAccessor.addMetadataAttribute(attribute);
        }
    }

    public int getAutowireMode(String attrValue) {
        String attr = attrValue;
        if (this.isDefaultValue(attr)) {
            attr = this.defaults.getAutowire();
        }
        int autowire = 0;
        if (AUTOWIRE_BY_NAME_VALUE.equals(attr)) {
            autowire = 1;
        } else if (AUTOWIRE_BY_TYPE_VALUE.equals(attr)) {
            autowire = 2;
        } else if (AUTOWIRE_CONSTRUCTOR_VALUE.equals(attr)) {
            autowire = 3;
        } else if (AUTOWIRE_AUTODETECT_VALUE.equals(attr)) {
            autowire = 4;
        }
        return autowire;
    }

    public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, CONSTRUCTOR_ARG_ELEMENT)) continue;
            this.parseConstructorArgElement((Element)node, bd);
        }
    }

    public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, PROPERTY_ELEMENT)) continue;
            this.parsePropertyElement((Element)node, bd);
        }
    }

    public void parseQualifierElements(Element beanEle, AbstractBeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, QUALIFIER_ELEMENT)) continue;
            this.parseQualifierElement((Element)node, bd);
        }
    }

    public void parseLookupOverrideSubElements(Element beanEle, MethodOverrides overrides) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, LOOKUP_METHOD_ELEMENT)) continue;
            Element ele = (Element)node;
            String methodName = ele.getAttribute(NAME_ATTRIBUTE);
            String beanRef = ele.getAttribute("bean");
            LookupOverride override = new LookupOverride(methodName, beanRef);
            override.setSource(this.extractSource(ele));
            overrides.addOverride(override);
        }
    }

    public void parseReplacedMethodSubElements(Element beanEle, MethodOverrides overrides) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, REPLACED_METHOD_ELEMENT)) continue;
            Element replacedMethodEle = (Element)node;
            String name = replacedMethodEle.getAttribute(NAME_ATTRIBUTE);
            String callback = replacedMethodEle.getAttribute(REPLACER_ATTRIBUTE);
            ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);
            List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, ARG_TYPE_ELEMENT);
            for (Element argTypeEle : argTypeEles) {
                String match = argTypeEle.getAttribute(ARG_TYPE_MATCH_ATTRIBUTE);
                match = StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle);
                if (!StringUtils.hasText(match)) continue;
                replaceOverride.addTypeIdentifier(match);
            }
            replaceOverride.setSource(this.extractSource(replacedMethodEle));
            overrides.addOverride(replaceOverride);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parseConstructorArgElement(Element ele, BeanDefinition bd) {
        block17: {
            String indexAttr = ele.getAttribute(INDEX_ATTRIBUTE);
            String typeAttr = ele.getAttribute(TYPE_ATTRIBUTE);
            String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
            if (StringUtils.hasLength(indexAttr)) {
                try {
                    int index = Integer.parseInt(indexAttr);
                    if (index < 0) {
                        this.error("'index' cannot be lower than 0", ele);
                        break block17;
                    }
                    try {
                        this.parseState.push(new ConstructorArgumentEntry(index));
                        Object value = this.parsePropertyValue(ele, bd, null);
                        ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                        if (StringUtils.hasLength(typeAttr)) {
                            valueHolder.setType(typeAttr);
                        }
                        if (StringUtils.hasLength(nameAttr)) {
                            valueHolder.setName(nameAttr);
                        }
                        valueHolder.setSource(this.extractSource(ele));
                        if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
                            this.error("Ambiguous constructor-arg entries for index " + index, ele);
                        } else {
                            bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
                        }
                    }
                    finally {
                        this.parseState.pop();
                    }
                }
                catch (NumberFormatException ex) {
                    this.error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
                }
            } else {
                try {
                    this.parseState.push(new ConstructorArgumentEntry());
                    Object value = this.parsePropertyValue(ele, bd, null);
                    ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                    if (StringUtils.hasLength(typeAttr)) {
                        valueHolder.setType(typeAttr);
                    }
                    if (StringUtils.hasLength(nameAttr)) {
                        valueHolder.setName(nameAttr);
                    }
                    valueHolder.setSource(this.extractSource(ele));
                    bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
                }
                finally {
                    this.parseState.pop();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parsePropertyElement(Element ele, BeanDefinition bd) {
        String propertyName = ele.getAttribute(NAME_ATTRIBUTE);
        if (!StringUtils.hasLength(propertyName)) {
            this.error("Tag 'property' must have a 'name' attribute", ele);
            return;
        }
        this.parseState.push(new PropertyEntry(propertyName));
        try {
            if (bd.getPropertyValues().contains(propertyName)) {
                this.error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
                return;
            }
            Object val = this.parsePropertyValue(ele, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);
            this.parseMetaElements(ele, pv);
            pv.setSource(this.extractSource(ele));
            bd.getPropertyValues().addPropertyValue(pv);
        }
        finally {
            this.parseState.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void parseQualifierElement(Element ele, AbstractBeanDefinition bd) {
        String typeName = ele.getAttribute(TYPE_ATTRIBUTE);
        if (!StringUtils.hasLength(typeName)) {
            this.error("Tag 'qualifier' must have a 'type' attribute", ele);
            return;
        }
        this.parseState.push(new QualifierEntry(typeName));
        try {
            AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(typeName);
            qualifier.setSource(this.extractSource(ele));
            String value = ele.getAttribute("value");
            if (StringUtils.hasLength(value)) {
                qualifier.setAttribute("value", value);
            }
            NodeList nl = ele.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                if (!this.isCandidateElement(node) || !this.nodeNameEquals(node, QUALIFIER_ATTRIBUTE_ELEMENT)) continue;
                Element attributeEle = (Element)node;
                String attributeName = attributeEle.getAttribute("key");
                String attributeValue = attributeEle.getAttribute("value");
                if (StringUtils.hasLength(attributeName) && StringUtils.hasLength(attributeValue)) {
                    BeanMetadataAttribute attribute = new BeanMetadataAttribute(attributeName, attributeValue);
                    attribute.setSource(this.extractSource(attributeEle));
                    qualifier.addMetadataAttribute(attribute);
                    continue;
                }
                this.error("Qualifier 'attribute' tag must have a 'name' and 'value'", attributeEle);
                return;
            }
            bd.addQualifier(qualifier);
        }
        finally {
            this.parseState.pop();
        }
    }

    @Nullable
    public Object parsePropertyValue(Element ele, BeanDefinition bd, @Nullable String propertyName) {
        String elementName = propertyName != null ? "<property> element for property '" + propertyName + "'" : "<constructor-arg> element";
        NodeList nl = ele.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element) || this.nodeNameEquals(node, DESCRIPTION_ELEMENT) || this.nodeNameEquals(node, META_ELEMENT)) continue;
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
            if (!StringUtils.hasText(refName)) {
                this.error(elementName + " contains empty 'ref' attribute", ele);
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            ref.setSource(this.extractSource(ele));
            return ref;
        }
        if (hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute("value"));
            valueHolder.setSource(this.extractSource(ele));
            return valueHolder;
        }
        if (subElement != null) {
            return this.parsePropertySubElement(subElement, bd);
        }
        this.error(elementName + " must specify a ref or value", ele);
        return null;
    }

    @Nullable
    public Object parsePropertySubElement(Element ele, @Nullable BeanDefinition bd) {
        return this.parsePropertySubElement(ele, bd, null);
    }

    @Nullable
    public Object parsePropertySubElement(Element ele, @Nullable BeanDefinition bd, @Nullable String defaultValueType) {
        if (!this.isDefaultNamespace(ele)) {
            return this.parseNestedCustomElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, "bean")) {
            BeanDefinitionHolder nestedBd = this.parseBeanDefinitionElement(ele, bd);
            if (nestedBd != null) {
                nestedBd = this.decorateBeanDefinitionIfRequired(ele, nestedBd, bd);
            }
            return nestedBd;
        }
        if (this.nodeNameEquals(ele, "ref")) {
            String refName = ele.getAttribute("bean");
            boolean toParent = false;
            if (!StringUtils.hasLength(refName)) {
                refName = ele.getAttribute("parent");
                toParent = true;
                if (!StringUtils.hasLength(refName)) {
                    this.error("'bean' or 'parent' is required for <ref> element", ele);
                    return null;
                }
            }
            if (!StringUtils.hasText(refName)) {
                this.error("<ref> element contains empty target attribute", ele);
                return null;
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
            ref.setSource(this.extractSource(ele));
            return ref;
        }
        if (this.nodeNameEquals(ele, IDREF_ELEMENT)) {
            return this.parseIdRefElement(ele);
        }
        if (this.nodeNameEquals(ele, "value")) {
            return this.parseValueElement(ele, defaultValueType);
        }
        if (this.nodeNameEquals(ele, NULL_ELEMENT)) {
            TypedStringValue nullHolder = new TypedStringValue(null);
            nullHolder.setSource(this.extractSource(ele));
            return nullHolder;
        }
        if (this.nodeNameEquals(ele, ARRAY_ELEMENT)) {
            return this.parseArrayElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, LIST_ELEMENT)) {
            return this.parseListElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, SET_ELEMENT)) {
            return this.parseSetElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, MAP_ELEMENT)) {
            return this.parseMapElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, PROPS_ELEMENT)) {
            return this.parsePropsElement(ele);
        }
        this.error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
        return null;
    }

    @Nullable
    public Object parseIdRefElement(Element ele) {
        String refName = ele.getAttribute("bean");
        if (!StringUtils.hasLength(refName)) {
            this.error("'bean' is required for <idref> element", ele);
            return null;
        }
        if (!StringUtils.hasText(refName)) {
            this.error("<idref> element contains empty target attribute", ele);
            return null;
        }
        RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
        ref.setSource(this.extractSource(ele));
        return ref;
    }

    public Object parseValueElement(Element ele, @Nullable String defaultTypeName) {
        String value = DomUtils.getTextValue(ele);
        String specifiedTypeName = ele.getAttribute(TYPE_ATTRIBUTE);
        String typeName = specifiedTypeName;
        if (!StringUtils.hasText(typeName)) {
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

    protected TypedStringValue buildTypedStringValue(String value, @Nullable String targetTypeName) throws ClassNotFoundException {
        TypedStringValue typedValue;
        ClassLoader classLoader = this.readerContext.getBeanClassLoader();
        if (!StringUtils.hasText(targetTypeName)) {
            typedValue = new TypedStringValue(value);
        } else if (classLoader != null) {
            Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
            typedValue = new TypedStringValue(value, targetType);
        } else {
            typedValue = new TypedStringValue(value, targetTypeName);
        }
        return typedValue;
    }

    public Object parseArrayElement(Element arrayEle, @Nullable BeanDefinition bd) {
        String elementType = arrayEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
        NodeList nl = arrayEle.getChildNodes();
        ManagedArray target = new ManagedArray(elementType, nl.getLength());
        target.setSource(this.extractSource(arrayEle));
        target.setElementTypeName(elementType);
        target.setMergeEnabled(this.parseMergeAttribute(arrayEle));
        this.parseCollectionElements(nl, target, bd, elementType);
        return target;
    }

    public List<Object> parseListElement(Element collectionEle, @Nullable BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
        NodeList nl = collectionEle.getChildNodes();
        ManagedList<Object> target = new ManagedList<Object>(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }

    public Set<Object> parseSetElement(Element collectionEle, @Nullable BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
        NodeList nl = collectionEle.getChildNodes();
        ManagedSet<Object> target = new ManagedSet<Object>(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }

    protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target, @Nullable BeanDefinition bd, String defaultElementType) {
        for (int i = 0; i < elementNodes.getLength(); ++i) {
            Node node = elementNodes.item(i);
            if (!(node instanceof Element) || this.nodeNameEquals(node, DESCRIPTION_ELEMENT)) continue;
            target.add(this.parsePropertySubElement((Element)node, bd, defaultElementType));
        }
    }

    public Map<Object, Object> parseMapElement(Element mapEle, @Nullable BeanDefinition bd) {
        String defaultKeyType = mapEle.getAttribute(KEY_TYPE_ATTRIBUTE);
        String defaultValueType = mapEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
        List<Element> entryEles = DomUtils.getChildElementsByTagName(mapEle, ENTRY_ELEMENT);
        ManagedMap<Object, Object> map = new ManagedMap<Object, Object>(entryEles.size());
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
                if (this.nodeNameEquals(candidateEle, "key")) {
                    if (keyEle != null) {
                        this.error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
                        continue;
                    }
                    keyEle = candidateEle;
                    continue;
                }
                if (this.nodeNameEquals(candidateEle, DESCRIPTION_ELEMENT)) continue;
                if (valueEle != null) {
                    this.error("<entry> element must not contain more than one value sub-element", entryEle);
                    continue;
                }
                valueEle = candidateEle;
            }
            Object key = null;
            boolean hasKeyAttribute = entryEle.hasAttribute("key");
            boolean hasKeyRefAttribute = entryEle.hasAttribute(KEY_REF_ATTRIBUTE);
            if (hasKeyAttribute && hasKeyRefAttribute || (hasKeyAttribute || hasKeyRefAttribute) && keyEle != null) {
                this.error("<entry> element is only allowed to contain either a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
            }
            if (hasKeyAttribute) {
                key = this.buildTypedStringValueForMap(entryEle.getAttribute("key"), defaultKeyType, entryEle);
            } else if (hasKeyRefAttribute) {
                String refName = entryEle.getAttribute(KEY_REF_ATTRIBUTE);
                if (!StringUtils.hasText(refName)) {
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
            boolean hasValueRefAttribute = entryEle.hasAttribute(VALUE_REF_ATTRIBUTE);
            boolean hasValueTypeAttribute = entryEle.hasAttribute(VALUE_TYPE_ATTRIBUTE);
            if (hasValueAttribute && hasValueRefAttribute || (hasValueAttribute || hasValueRefAttribute) && valueEle != null) {
                this.error("<entry> element is only allowed to contain either 'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);
            }
            if (hasValueTypeAttribute && hasValueRefAttribute || hasValueTypeAttribute && !hasValueAttribute || hasValueTypeAttribute && valueEle != null) {
                this.error("<entry> element is only allowed to contain a 'value-type' attribute when it has a 'value' attribute", entryEle);
            }
            if (hasValueAttribute) {
                String valueType = entryEle.getAttribute(VALUE_TYPE_ATTRIBUTE);
                if (!StringUtils.hasText(valueType)) {
                    valueType = defaultValueType;
                }
                value = this.buildTypedStringValueForMap(entryEle.getAttribute("value"), valueType, entryEle);
            } else if (hasValueRefAttribute) {
                String refName = entryEle.getAttribute(VALUE_REF_ATTRIBUTE);
                if (!StringUtils.hasText(refName)) {
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

    protected final Object buildTypedStringValueForMap(String value, String defaultTypeName, Element entryEle) {
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

    @Nullable
    protected Object parseKeyElement(Element keyEle, @Nullable BeanDefinition bd, String defaultKeyTypeName) {
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
        if (subElement == null) {
            return null;
        }
        return this.parsePropertySubElement(subElement, bd, defaultKeyTypeName);
    }

    public Properties parsePropsElement(Element propsEle) {
        ManagedProperties props = new ManagedProperties();
        props.setSource(this.extractSource(propsEle));
        props.setMergeEnabled(this.parseMergeAttribute(propsEle));
        List<Element> propEles = DomUtils.getChildElementsByTagName(propsEle, PROP_ELEMENT);
        for (Element propEle : propEles) {
            String key = propEle.getAttribute("key");
            String value = DomUtils.getTextValue(propEle).trim();
            TypedStringValue keyHolder = new TypedStringValue(key);
            keyHolder.setSource(this.extractSource(propEle));
            TypedStringValue valueHolder = new TypedStringValue(value);
            valueHolder.setSource(this.extractSource(propEle));
            props.put(keyHolder, valueHolder);
        }
        return props;
    }

    public boolean parseMergeAttribute(Element collectionElement) {
        String value = collectionElement.getAttribute(MERGE_ATTRIBUTE);
        if (this.isDefaultValue(value)) {
            value = this.defaults.getMerge();
        }
        return TRUE_VALUE.equals(value);
    }

    @Nullable
    public BeanDefinition parseCustomElement(Element ele) {
        return this.parseCustomElement(ele, null);
    }

    @Nullable
    public BeanDefinition parseCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
        String namespaceUri = this.getNamespaceURI(ele);
        if (namespaceUri == null) {
            return null;
        }
        NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
        if (handler == null) {
            this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
            return null;
        }
        return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
    }

    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder originalDef) {
        return this.decorateBeanDefinitionIfRequired(ele, originalDef, null);
    }

    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder originalDef, @Nullable BeanDefinition containingBd) {
        BeanDefinitionHolder finalDefinition = originalDef;
        NamedNodeMap attributes = ele.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node node = attributes.item(i);
            finalDefinition = this.decorateIfRequired(node, finalDefinition, containingBd);
        }
        NodeList children = ele.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 1) continue;
            finalDefinition = this.decorateIfRequired(node, finalDefinition, containingBd);
        }
        return finalDefinition;
    }

    public BeanDefinitionHolder decorateIfRequired(Node node, BeanDefinitionHolder originalDef, @Nullable BeanDefinition containingBd) {
        String namespaceUri = this.getNamespaceURI(node);
        if (namespaceUri != null && !this.isDefaultNamespace(namespaceUri)) {
            NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
            if (handler != null) {
                BeanDefinitionHolder decorated = handler.decorate(node, originalDef, new ParserContext(this.readerContext, this, containingBd));
                if (decorated != null) {
                    return decorated;
                }
            } else if (namespaceUri.startsWith("http://www.springframework.org/schema/")) {
                this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
            }
        }
        return originalDef;
    }

    @Nullable
    private BeanDefinitionHolder parseNestedCustomElement(Element ele, @Nullable BeanDefinition containingBd) {
        BeanDefinition innerDefinition = this.parseCustomElement(ele, containingBd);
        if (innerDefinition == null) {
            this.error("Incorrect usage of element '" + ele.getNodeName() + "' in a nested manner. This tag cannot be used nested inside <property>.", ele);
            return null;
        }
        String id = ele.getNodeName() + "#" + ObjectUtils.getIdentityHexString(innerDefinition);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Using generated bean name [" + id + "] for nested custom element '" + ele.getNodeName() + "'");
        }
        return new BeanDefinitionHolder(innerDefinition, id);
    }

    @Nullable
    public String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }

    public String getLocalName(Node node) {
        return node.getLocalName();
    }

    public boolean nodeNameEquals(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(this.getLocalName(node));
    }

    public boolean isDefaultNamespace(@Nullable String namespaceUri) {
        return !StringUtils.hasLength(namespaceUri) || BEANS_NAMESPACE_URI.equals(namespaceUri);
    }

    public boolean isDefaultNamespace(Node node) {
        return this.isDefaultNamespace(this.getNamespaceURI(node));
    }

    private boolean isDefaultValue(String value) {
        return !StringUtils.hasLength(value) || DEFAULT_VALUE.equals(value);
    }

    private boolean isCandidateElement(Node node) {
        return node instanceof Element && (this.isDefaultNamespace(node) || !this.isDefaultNamespace(node.getParentNode()));
    }
}

