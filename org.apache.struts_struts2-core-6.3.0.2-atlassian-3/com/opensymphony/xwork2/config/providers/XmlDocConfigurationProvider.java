/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.BeanSelectionProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.ConfigurationUtil;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.config.providers.CycleDetector;
import com.opensymphony.xwork2.config.providers.DirectedGraph;
import com.opensymphony.xwork2.config.providers.InterceptorBuilder;
import com.opensymphony.xwork2.config.providers.ValueSubstitutor;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlHelper;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XmlDocConfigurationProvider
implements ConfigurationProvider {
    private static final Logger LOG = LogManager.getLogger(XmlConfigurationProvider.class);
    protected final Map<String, Element> declaredPackages = new HashMap<String, Element>();
    protected List<Document> documents;
    protected ObjectFactory objectFactory;
    protected Map<String, String> dtdMappings = new HashMap<String, String>();
    protected Configuration configuration;
    protected ProviderAllowlist providerAllowlist;
    protected boolean throwExceptionOnDuplicateBeans = true;
    protected ValueSubstitutor valueSubstitutor;
    protected Set<Class<?>> allowlistClasses = new HashSet();

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject(required=false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    public XmlDocConfigurationProvider(Document ... documents) {
        this.documents = Arrays.asList(documents);
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    public void setDtdMappings(Map<String, String> mappings) {
        this.dtdMappings = Collections.unmodifiableMap(mappings);
    }

    public Map<String, String> getDtdMappings() {
        return this.dtdMappings;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = configuration;
    }

    private void registerAllowlist() {
        this.providerAllowlist = this.configuration.getContainer().getInstance(ProviderAllowlist.class);
        this.providerAllowlist.registerAllowlist(this, this.allowlistClasses);
    }

    @Override
    public void destroy() {
        this.providerAllowlist.clearAllowlist(this);
    }

    protected Class<?> allowAndLoadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = this.loadClass(className);
        this.allowlistClasses.add(clazz);
        this.allowlistClasses.addAll(ClassUtils.getAllSuperclasses(clazz));
        this.allowlistClasses.addAll(ClassUtils.getAllInterfaces(clazz));
        return clazz;
    }

    protected Class<?> loadClass(String className) throws ClassNotFoundException {
        return this.objectFactory.getClassInstance(className);
    }

    public static void iterateElementChildren(Document doc, Consumer<Element> function) {
        XmlDocConfigurationProvider.iterateElementChildren(doc.getDocumentElement(), function);
    }

    public static void iterateElementChildren(Node node, Consumer<Element> function) {
        XmlDocConfigurationProvider.iterateChildren(node, childNode -> {
            if (!(childNode instanceof Element)) {
                return;
            }
            function.accept((Element)childNode);
        });
    }

    public static void iterateChildren(Node node, Consumer<Node> function) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            function.accept(children.item(i));
        }
    }

    public static void iterateChildrenByTagName(Element el, String tagName, Consumer<Element> function) {
        NodeList childrenByTag = el.getElementsByTagName(tagName);
        for (int i = 0; i < childrenByTag.getLength(); ++i) {
            Element childEl = (Element)childrenByTag.item(i);
            function.accept(childEl);
        }
    }

    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        this.allowlistClasses.clear();
        HashMap loadedBeans = new HashMap();
        for (Document doc : this.documents) {
            XmlDocConfigurationProvider.iterateElementChildren(doc, (Element child) -> {
                switch (child.getNodeName()) {
                    case "bean-selection": {
                        this.registerBeanSelection((Element)child, containerBuilder, props);
                        break;
                    }
                    case "bean": {
                        this.registerBean((Element)child, loadedBeans, containerBuilder);
                        break;
                    }
                    case "constant": {
                        this.registerConstant((Element)child, props);
                        break;
                    }
                    case "unknown-handler-stack": {
                        this.registerUnknownHandlerStack((Element)child);
                    }
                }
            });
        }
    }

    protected void registerBeanSelection(Element child, ContainerBuilder containerBuilder, LocatableProperties props) {
        String name = child.getAttribute("name");
        String impl = child.getAttribute("class");
        try {
            Class classImpl = ClassLoaderUtil.loadClass(impl, this.getClass());
            if (!BeanSelectionProvider.class.isAssignableFrom(classImpl)) {
                throw new ConfigurationException(String.format("The bean-provider: name:%s class:%s does not implement %s", name, impl, BeanSelectionProvider.class.getName()), (Object)child);
            }
            BeanSelectionProvider provider = (BeanSelectionProvider)classImpl.newInstance();
            provider.register(containerBuilder, props);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ConfigurationException(String.format("Unable to load bean-provider: name:%s class:%s", name, impl), e, child);
        }
    }

    protected void registerBean(Element child, Map<String, Node> loadedBeans, ContainerBuilder containerBuilder) {
        String type = child.getAttribute("type");
        String name = child.getAttribute("name");
        String impl = child.getAttribute("class");
        String onlyStatic = child.getAttribute("static");
        String scopeStr = child.getAttribute("scope");
        boolean optional = "true".equals(child.getAttribute("optional"));
        Scope scope = Scope.fromString(scopeStr);
        if (name.isEmpty()) {
            name = "default";
        }
        try {
            Class classImpl;
            Class classType = classImpl = ClassLoaderUtil.loadClass(impl, this.getClass());
            if (!type.isEmpty()) {
                classType = ClassLoaderUtil.loadClass(type, this.getClass());
            }
            if ("true".equals(onlyStatic)) {
                classImpl.getDeclaredClasses();
                containerBuilder.injectStatics(classImpl);
            } else {
                if (containerBuilder.contains(classType, name)) {
                    Location loc = LocationUtils.getLocation(loadedBeans.get(classType.getName() + name));
                    if (this.throwExceptionOnDuplicateBeans) {
                        throw new ConfigurationException(String.format("Bean type %s with the name %s has already been loaded by %s", classType, name, loc), (Object)child);
                    }
                }
                classImpl.getDeclaredConstructors();
                LOG.debug("Loaded type: {} name: {} impl: {}", (Object)type, (Object)name, (Object)impl);
                containerBuilder.factory(classType, name, new LocatableFactory(name, classType, classImpl, scope, child), scope);
            }
            loadedBeans.put(classType.getName() + name, child);
        }
        catch (Throwable ex) {
            if (!optional) {
                throw new ConfigurationException("Unable to load bean: type:" + type + " class:" + impl, ex, child);
            }
            LOG.debug("Unable to load optional class: {}", (Object)impl);
        }
    }

    protected void registerConstant(Element child, LocatableProperties props) {
        String name = child.getAttribute("name");
        String value = child.getAttribute("value");
        if (this.valueSubstitutor != null) {
            LOG.debug("Substituting value [{}] using [{}]", (Object)value, (Object)this.valueSubstitutor.getClass().getName());
            value = this.valueSubstitutor.substitute(value);
        }
        props.setProperty(name, value, child);
    }

    protected void registerUnknownHandlerStack(Element child) {
        ArrayList<UnknownHandlerConfig> unknownHandlerStack = new ArrayList<UnknownHandlerConfig>();
        XmlDocConfigurationProvider.iterateChildrenByTagName(child, "unknown-handler-ref", unknownHandler -> {
            Location location = LocationUtils.getLocation(unknownHandler);
            unknownHandlerStack.add(new UnknownHandlerConfig(unknownHandler.getAttribute("name"), location));
        });
        if (!unknownHandlerStack.isEmpty()) {
            this.configuration.setUnknownHandlerStack(unknownHandlerStack);
        }
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void loadPackages() throws ConfigurationException {
        ArrayList<Element> reloads = new ArrayList<Element>();
        this.verifyPackageStructure();
        for (Document doc : this.documents) {
            XmlDocConfigurationProvider.iterateElementChildren(doc, (Element child) -> {
                PackageConfig cfg;
                if ("package".equals(child.getNodeName()) && (cfg = this.addPackage((Element)child)).isNeedsRefresh()) {
                    reloads.add((Element)child);
                }
            });
            this.loadExtraConfiguration(doc);
        }
        if (!reloads.isEmpty()) {
            this.reloadRequiredPackages(reloads);
        }
        for (Document doc : this.documents) {
            this.loadExtraConfiguration(doc);
        }
        this.declaredPackages.clear();
        this.registerAllowlist();
        this.configuration = null;
    }

    private void verifyPackageStructure() {
        DirectedGraph graph = new DirectedGraph();
        for (Document doc : this.documents) {
            XmlDocConfigurationProvider.iterateElementChildren(doc, (Element child) -> {
                if (!"package".equals(child.getNodeName())) {
                    return;
                }
                String packageName = child.getAttribute("name");
                this.declaredPackages.put(packageName, (Element)child);
                graph.addNode(packageName);
                String extendsAttribute = child.getAttribute("extends");
                for (String parent : ConfigurationUtil.buildParentListFromString(extendsAttribute)) {
                    graph.addNode(parent);
                    graph.addEdge(packageName, parent);
                }
            });
        }
        CycleDetector detector = new CycleDetector(graph);
        if (detector.containsCycle()) {
            StringBuilder builder = new StringBuilder("The following packages participate in cycles:");
            for (String packageName : detector.getVerticesInCycles()) {
                builder.append(" ");
                builder.append(packageName);
            }
            throw new ConfigurationException(builder.toString());
        }
    }

    protected void loadExtraConfiguration(Document doc) {
    }

    private void reloadRequiredPackages(List<Element> reloads) {
        if (reloads.isEmpty()) {
            return;
        }
        ArrayList<Element> result = new ArrayList<Element>();
        for (Element pkg : reloads) {
            PackageConfig cfg = this.addPackage(pkg);
            if (!cfg.isNeedsRefresh()) continue;
            result.add(pkg);
        }
        if (!result.isEmpty() && result.size() != reloads.size()) {
            this.reloadRequiredPackages(result);
            return;
        }
        for (Element rp : result) {
            String parent = rp.getAttribute("extends");
            if (parent.isEmpty() || !ConfigurationUtil.buildParentsFromString(this.configuration, parent).isEmpty()) continue;
            LOG.error("Unable to find parent packages {}", (Object)parent);
        }
    }

    protected PackageConfig addPackage(Element packageElement) throws ConfigurationException {
        String packageName = packageElement.getAttribute("name");
        PackageConfig packageConfig = this.configuration.getPackageConfig(packageName);
        if (packageConfig != null) {
            LOG.debug("Package [{}] already loaded, skipping re-loading it and using existing PackageConfig [{}]", (Object)packageName, (Object)packageConfig);
            return packageConfig;
        }
        PackageConfig.Builder newPackage = this.buildPackageContext(packageElement);
        if (newPackage.isNeedsRefresh()) {
            return newPackage.build();
        }
        LOG.debug("Loaded {}", (Object)newPackage);
        this.addResultTypes(newPackage, packageElement);
        this.loadInterceptors(newPackage, packageElement);
        this.loadDefaultInterceptorRef(newPackage, packageElement);
        this.loadDefaultClassRef(newPackage, packageElement);
        this.loadGlobalResults(newPackage, packageElement);
        this.loadGlobalAllowedMethods(newPackage, packageElement);
        this.loadGlobalExceptionMappings(newPackage, packageElement);
        XmlDocConfigurationProvider.iterateChildrenByTagName(packageElement, "action", actionElement -> this.addAction((Element)actionElement, newPackage));
        this.loadDefaultActionRef(newPackage, packageElement);
        PackageConfig cfg = newPackage.build();
        this.configuration.addPackageConfig(cfg.getName(), cfg);
        return cfg;
    }

    protected void addAction(Element actionElement, PackageConfig.Builder packageContext) throws ConfigurationException {
        Map<String, ResultConfig> results;
        String name = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");
        Location location = DomHelper.getLocationObject(actionElement);
        if (!className.isEmpty()) {
            this.verifyAction(className, name, location);
        }
        try {
            results = this.buildResults(actionElement, packageContext);
        }
        catch (ConfigurationException e) {
            throw new ConfigurationException(String.format("Error building results for action %s in namespace %s", name, packageContext.getNamespace()), e, actionElement);
        }
        ActionConfig actionConfig = this.buildActionConfig(actionElement, location, packageContext, results);
        packageContext.addActionConfig(actionConfig.getName(), actionConfig);
        LOG.debug("Loaded {}{} in '{}' package: {}", (Object)(StringUtils.isNotEmpty((CharSequence)packageContext.getNamespace()) ? packageContext.getNamespace() + "/" : ""), (Object)name, (Object)packageContext.getName(), (Object)actionConfig);
    }

    protected ActionConfig buildActionConfig(Element actionElement, Location location, PackageConfig.Builder packageContext, Map<String, ResultConfig> results) {
        String actionName = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");
        String methodName = StringUtils.trimToNull((String)actionElement.getAttribute("method"));
        List<InterceptorMapping> interceptorList = this.buildInterceptorList(actionElement, packageContext);
        List<ExceptionMappingConfig> exceptionMappings = this.buildExceptionMappings(actionElement, packageContext);
        Set<String> allowedMethods = this.buildAllowedMethods(actionElement, packageContext);
        return ((ActionConfig.Builder)new ActionConfig.Builder(packageContext.getName(), actionName, className).methodName(methodName).addResultConfigs(results).addInterceptors((List)interceptorList)).addExceptionMappings(exceptionMappings).addParams(XmlHelper.getParams(actionElement)).setStrictMethodInvocation(packageContext.isStrictMethodInvocation()).addAllowedMethod(allowedMethods).location(location).build();
    }

    @Deprecated
    protected boolean verifyAction(String className, String name, Location loc) {
        this.verifyAction(className, loc);
        return true;
    }

    protected void verifyAction(String className, Location loc) {
        if (className.contains("{")) {
            LOG.debug("Action class [{}] contains a wildcard replacement value, so it can't be verified", (Object)className);
            return;
        }
        try {
            Class<?> clazz = this.allowAndLoadClass(className);
            if (this.objectFactory.isNoArgConstructorRequired()) {
                if (!Modifier.isPublic(clazz.getModifiers())) {
                    throw new ConfigurationException("Action class [" + className + "] is not public", (Object)loc);
                }
                clazz.getConstructor(new Class[0]);
            }
        }
        catch (ClassNotFoundException e) {
            if (this.objectFactory.isNoArgConstructorRequired()) {
                throw new ConfigurationException("Action class [" + className + "] not found", e, loc);
            }
            LOG.warn("Action class [" + className + "] not found");
            LOG.debug("Action class [" + className + "] not found", (Throwable)e);
        }
        catch (NoSuchMethodException e) {
            throw new ConfigurationException("Action class [" + className + "] does not have a public no-arg constructor", e, loc);
        }
        catch (RuntimeException ex) {
            LOG.info("Unable to verify action class [{}] exists at initialization", (Object)className);
            LOG.debug("Action verification cause", (Throwable)ex);
        }
        catch (Exception ex) {
            throw new ConfigurationException("Unable to verify action class [" + className + "]", ex, loc);
        }
    }

    protected void addResultTypes(PackageConfig.Builder packageContext, Element element) {
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "result-type", resultTypeElement -> {
            String name = resultTypeElement.getAttribute("name");
            String className = resultTypeElement.getAttribute("class");
            String def = resultTypeElement.getAttribute("default");
            Location loc = DomHelper.getLocationObject(resultTypeElement);
            Class<?> clazz = this.verifyResultType(className, loc);
            String paramName = null;
            try {
                paramName = (String)clazz.getField("DEFAULT_PARAM").get(null);
            }
            catch (Throwable t) {
                LOG.debug("The result type [{}] doesn't have a default param [DEFAULT_PARAM] defined!", (Object)className, (Object)t);
            }
            packageContext.addResultTypeConfig(this.buildResultTypeConfig((Element)resultTypeElement, loc, paramName));
            if (BooleanUtils.toBoolean((String)def)) {
                packageContext.defaultResultType(name);
            }
        });
    }

    protected ResultTypeConfig buildResultTypeConfig(Element resultTypeElement, Location location, String paramName) {
        String name = resultTypeElement.getAttribute("name");
        String className = resultTypeElement.getAttribute("class");
        ResultTypeConfig.Builder resultType = new ResultTypeConfig.Builder(name, className).defaultResultParam(paramName).location(location);
        Map<String, String> params = XmlHelper.getParams(resultTypeElement);
        if (!params.isEmpty()) {
            resultType.addParams(params);
        }
        return resultType.build();
    }

    protected Class<?> verifyResultType(String className, Location loc) {
        try {
            return this.allowAndLoadClass(className);
        }
        catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new ConfigurationException("Result class [" + className + "] not found", e, loc);
        }
    }

    protected PackageConfig.Builder buildPackageContext(Element packageElement) {
        String parent = packageElement.getAttribute("extends");
        String abstractVal = packageElement.getAttribute("abstract");
        boolean isAbstract = Boolean.parseBoolean(abstractVal);
        String name = StringUtils.defaultString((String)packageElement.getAttribute("name"));
        String namespace = StringUtils.defaultString((String)packageElement.getAttribute("namespace"));
        boolean strictDMI = true;
        if (packageElement.hasAttribute("strict-method-invocation")) {
            strictDMI = Boolean.parseBoolean(packageElement.getAttribute("strict-method-invocation"));
        }
        PackageConfig.Builder cfg = new PackageConfig.Builder(name).namespace(namespace).isAbstract(isAbstract).strictMethodInvocation(strictDMI).location(DomHelper.getLocationObject(packageElement));
        if (parent.isEmpty()) {
            return cfg;
        }
        ArrayList<PackageConfig> parents = new ArrayList<PackageConfig>();
        for (String parentPackageName : ConfigurationUtil.buildParentListFromString(parent)) {
            if (this.configuration.getPackageConfigNames().contains(parentPackageName)) {
                parents.add(this.configuration.getPackageConfig(parentPackageName));
                continue;
            }
            if (this.declaredPackages.containsKey(parentPackageName)) {
                if (this.configuration.getPackageConfig(parentPackageName) == null) {
                    this.addPackage(this.declaredPackages.get(parentPackageName));
                }
                parents.add(this.configuration.getPackageConfig(parentPackageName));
                continue;
            }
            throw new ConfigurationException("Parent package is not defined: " + parentPackageName);
        }
        if (parents.isEmpty()) {
            cfg.needsRefresh(true);
        } else {
            cfg.addParents(parents);
        }
        return cfg;
    }

    protected Map<String, ResultConfig> buildResults(Element element, PackageConfig.Builder packageContext) {
        LinkedHashMap<String, ResultConfig> results = new LinkedHashMap<String, ResultConfig>();
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "result", resultElement -> {
            Node parNode = resultElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }
            String resultName = resultElement.getAttribute("name");
            String resultType = resultElement.getAttribute("type");
            if (StringUtils.isEmpty((CharSequence)resultName)) {
                resultName = "success";
            }
            if (resultType.isEmpty() && (resultType = packageContext.getFullDefaultResultType()).isEmpty()) {
                throw new ConfigurationException("No result type specified for result named '" + resultName + "', perhaps the parent package does not specify the result type?", resultElement);
            }
            ResultTypeConfig config = packageContext.getResultType(resultType);
            if (config == null) {
                throw new ConfigurationException(String.format("There is no result type defined for type '%s' mapped with name '%s'. Did you mean '%s'?", resultType, resultName, XmlDocConfigurationProvider.guessResultType(resultType)), resultElement);
            }
            String resultClass = config.getClassName();
            if (resultClass == null) {
                throw new ConfigurationException("Result type '" + resultType + "' is invalid");
            }
            Set<String> resultNamesSet = TextParseUtil.commaDelimitedStringToSet(resultName);
            if (resultNamesSet.isEmpty()) {
                resultNamesSet.add(resultName);
            }
            Map<String, String> params = this.buildResultParams((Element)resultElement, config);
            Location location = DomHelper.getLocationObject(element);
            for (String name : resultNamesSet) {
                ResultConfig resultConfig = this.buildResultConfig(name, config, location, params);
                results.put(resultConfig.getName(), resultConfig);
            }
        });
        return results;
    }

    protected ResultConfig buildResultConfig(String name, ResultTypeConfig config, Location location, Map<String, String> params) {
        return new ResultConfig.Builder(name, config.getClassName()).location(location).addParams(params).build();
    }

    protected Map<String, String> buildResultParams(Element resultElement, ResultTypeConfig config) {
        Map<String, String> resultParams = XmlHelper.getParams(resultElement);
        if (resultParams.isEmpty() && resultElement.getChildNodes().getLength() > 0) {
            resultParams = new LinkedHashMap<String, String>();
            String paramName = config.getDefaultResultParam();
            if (paramName != null) {
                StringBuilder paramValue = new StringBuilder();
                XmlDocConfigurationProvider.iterateChildren(resultElement, child -> {
                    String val;
                    if (child.getNodeType() == 3 && (val = child.getNodeValue()) != null) {
                        paramValue.append(val);
                    }
                });
                String val = paramValue.toString().trim();
                if (val.length() > 0) {
                    resultParams.put(paramName, val);
                }
            } else {
                LOG.debug("No default parameter defined for result [{}] of type [{}] ", (Object)config.getName(), (Object)config.getClassName());
            }
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        Map<String, String> configParams = config.getParams();
        if (configParams != null) {
            params.putAll(configParams);
        }
        params.putAll(resultParams);
        return params;
    }

    protected static String guessResultType(String type) {
        if (type == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean capNext = false;
        for (int x = 0; x < type.length(); ++x) {
            char c = type.charAt(x);
            if (c == '-') {
                capNext = true;
                continue;
            }
            if (Character.isLowerCase(c) && capNext) {
                c = Character.toUpperCase(c);
                capNext = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Deprecated
    protected List<ExceptionMappingConfig> buildExceptionMappings(Element element, PackageConfig.Builder packageContext) {
        return this.buildExceptionMappings(element);
    }

    protected List<ExceptionMappingConfig> buildExceptionMappings(Element element) {
        ArrayList<ExceptionMappingConfig> exceptionMappings = new ArrayList<ExceptionMappingConfig>();
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "exception-mapping", ehElement -> {
            Node parNode = ehElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }
            String emName = ehElement.getAttribute("name");
            String exceptionClassName = ehElement.getAttribute("exception");
            String exceptionResult = ehElement.getAttribute("result");
            Map<String, String> params = XmlHelper.getParams(ehElement);
            if (emName.isEmpty()) {
                emName = exceptionResult;
            }
            ExceptionMappingConfig ehConfig = new ExceptionMappingConfig.Builder(emName, exceptionClassName, exceptionResult).addParams(params).location(DomHelper.getLocationObject(ehElement)).build();
            exceptionMappings.add(ehConfig);
        });
        return exceptionMappings;
    }

    protected Set<String> buildAllowedMethods(Element element, PackageConfig.Builder packageContext) {
        HashSet<String> allowedMethods;
        NodeList allowedMethodsEls = element.getElementsByTagName("allowed-methods");
        if (allowedMethodsEls.getLength() > 0) {
            allowedMethods = new HashSet<String>(packageContext.getGlobalAllowedMethods());
            Node allowedMethodsNode = allowedMethodsEls.item(0);
            XmlDocConfigurationProvider.addAllowedMethodsToSet(allowedMethodsNode, allowedMethods);
        } else if (packageContext.isStrictMethodInvocation()) {
            allowedMethods = new HashSet<String>(packageContext.getGlobalAllowedMethods());
        } else {
            allowedMethods = new HashSet();
            allowedMethods.add("*");
        }
        LOG.debug("Collected allowed methods: {}", allowedMethods);
        return Collections.unmodifiableSet(allowedMethods);
    }

    protected void loadDefaultActionRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-action-ref");
        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element)resultTypeList.item(0);
            packageContext.defaultActionRef(defaultRefElement.getAttribute("name"));
        }
    }

    protected void loadGlobalResults(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalResultList = packageElement.getElementsByTagName("global-results");
        if (globalResultList.getLength() > 0) {
            Element globalResultElement = (Element)globalResultList.item(0);
            Map<String, ResultConfig> results = this.buildResults(globalResultElement, packageContext);
            packageContext.addGlobalResultConfigs(results);
        }
    }

    protected void loadGlobalAllowedMethods(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalAllowedMethodsElms = packageElement.getElementsByTagName("global-allowed-methods");
        if (globalAllowedMethodsElms.getLength() > 0) {
            HashSet<String> globalAllowedMethods = new HashSet<String>();
            Node globalAllowedMethodsNode = globalAllowedMethodsElms.item(0);
            XmlDocConfigurationProvider.addAllowedMethodsToSet(globalAllowedMethodsNode, globalAllowedMethods);
            packageContext.addGlobalAllowedMethods(globalAllowedMethods);
        }
    }

    protected static void addAllowedMethodsToSet(Node allowedMethodsNode, Set<String> allowedMethodsSet) {
        if (allowedMethodsNode == null) {
            return;
        }
        StringBuilder allowedMethodsSB = new StringBuilder();
        XmlDocConfigurationProvider.iterateChildren(allowedMethodsNode, allowedMethodsChildNode -> {
            if (allowedMethodsChildNode != null && allowedMethodsChildNode.getNodeType() == 3) {
                String childNodeValue = allowedMethodsChildNode.getNodeValue();
                String string = childNodeValue = childNodeValue != null ? childNodeValue.trim() : "";
                if (childNodeValue.length() > 0) {
                    allowedMethodsSB.append(childNodeValue);
                }
            }
        });
        if (allowedMethodsSB.length() > 0) {
            allowedMethodsSet.addAll(TextParseUtil.commaDelimitedStringToSet(allowedMethodsSB.toString()));
        }
    }

    protected void loadDefaultClassRef(PackageConfig.Builder packageContext, Element element) {
        NodeList defaultClassRefList = element.getElementsByTagName("default-class-ref");
        if (defaultClassRefList.getLength() > 0) {
            Element defaultClassRefElement = (Element)defaultClassRefList.item(0);
            String className = defaultClassRefElement.getAttribute("class");
            Location location = DomHelper.getLocationObject(defaultClassRefElement);
            this.verifyAction(className, location);
            packageContext.defaultClassRef(className);
        }
    }

    protected void loadGlobalExceptionMappings(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalExceptionMappingList = packageElement.getElementsByTagName("global-exception-mappings");
        if (globalExceptionMappingList.getLength() > 0) {
            Element globalExceptionMappingElement = (Element)globalExceptionMappingList.item(0);
            List<ExceptionMappingConfig> exceptionMappings = this.buildExceptionMappings(globalExceptionMappingElement, packageContext);
            packageContext.addGlobalExceptionMappingConfigs(exceptionMappings);
        }
    }

    protected List<InterceptorMapping> buildInterceptorList(Element element, PackageConfig.Builder context) throws ConfigurationException {
        ArrayList<InterceptorMapping> interceptorList = new ArrayList<InterceptorMapping>();
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "interceptor-ref", interceptorRefElement -> {
            Node parNode = interceptorRefElement.getParentNode();
            if (!parNode.equals(element) && !parNode.getNodeName().equals(element.getNodeName())) {
                return;
            }
            List<InterceptorMapping> interceptors = this.lookupInterceptorReference(context, (Element)interceptorRefElement);
            interceptorList.addAll(interceptors);
        });
        return interceptorList;
    }

    protected void loadInterceptors(PackageConfig.Builder context, Element element) throws ConfigurationException {
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "interceptor", interceptorElement -> {
            String className = interceptorElement.getAttribute("class");
            Location location = DomHelper.getLocationObject(interceptorElement);
            this.verifyInterceptor(className, location);
            context.addInterceptorConfig(this.buildInterceptorConfig((Element)interceptorElement));
        });
        this.loadInterceptorStacks(element, context);
    }

    protected void verifyInterceptor(String className, Location loc) {
        try {
            this.allowAndLoadClass(className);
        }
        catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOG.warn("Interceptor class [" + className + "] at location " + loc + " not found");
            LOG.debug("Interceptor class [" + className + "] not found", e);
        }
    }

    protected InterceptorConfig buildInterceptorConfig(Element interceptorElement) {
        String interceptorName = interceptorElement.getAttribute("name");
        String className = interceptorElement.getAttribute("class");
        Map<String, String> params = XmlHelper.getParams(interceptorElement);
        return new InterceptorConfig.Builder(interceptorName, className).addParams(params).location(DomHelper.getLocationObject(interceptorElement)).build();
    }

    protected void loadInterceptorStacks(Element element, PackageConfig.Builder context) throws ConfigurationException {
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "interceptor-stack", interceptorStackElement -> {
            InterceptorStackConfig config = this.loadInterceptorStack((Element)interceptorStackElement, context);
            context.addInterceptorStackConfig(config);
        });
    }

    protected InterceptorStackConfig loadInterceptorStack(Element element, PackageConfig.Builder context) throws ConfigurationException {
        String name = element.getAttribute("name");
        InterceptorStackConfig.Builder config = new InterceptorStackConfig.Builder(name).location(DomHelper.getLocationObject(element));
        XmlDocConfigurationProvider.iterateChildrenByTagName(element, "interceptor-ref", interceptorRefElement -> {
            List<InterceptorMapping> interceptors = this.lookupInterceptorReference(context, (Element)interceptorRefElement);
            config.addInterceptors((List)interceptors);
        });
        return config.build();
    }

    protected List<InterceptorMapping> lookupInterceptorReference(PackageConfig.Builder context, Element interceptorRefElement) throws ConfigurationException {
        String refName = interceptorRefElement.getAttribute("name");
        Map<String, String> refParams = XmlHelper.getParams(interceptorRefElement);
        Location loc = LocationUtils.getLocation(interceptorRefElement);
        return InterceptorBuilder.constructInterceptorReference(context, refName, refParams, loc, this.objectFactory);
    }

    protected void loadDefaultInterceptorRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-interceptor-ref");
        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element)resultTypeList.item(0);
            packageContext.defaultInterceptorRef(defaultRefElement.getAttribute("name"));
        }
    }
}

