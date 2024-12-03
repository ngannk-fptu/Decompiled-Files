/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DefaultBeanDefinitionDocumentReader
implements BeanDefinitionDocumentReader {
    public static final String BEAN_ELEMENT = "bean";
    public static final String NESTED_BEANS_ELEMENT = "beans";
    public static final String ALIAS_ELEMENT = "alias";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String ALIAS_ATTRIBUTE = "alias";
    public static final String IMPORT_ELEMENT = "import";
    public static final String RESOURCE_ATTRIBUTE = "resource";
    public static final String PROFILE_ATTRIBUTE = "profile";
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private XmlReaderContext readerContext;
    @Nullable
    private BeanDefinitionParserDelegate delegate;

    @Override
    public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
        this.readerContext = readerContext;
        this.doRegisterBeanDefinitions(doc.getDocumentElement());
    }

    protected final XmlReaderContext getReaderContext() {
        Assert.state(this.readerContext != null, "No XmlReaderContext available");
        return this.readerContext;
    }

    @Nullable
    protected Object extractSource(Element ele) {
        return this.getReaderContext().extractSource(ele);
    }

    protected void doRegisterBeanDefinitions(Element root) {
        String profileSpec;
        BeanDefinitionParserDelegate parent = this.delegate;
        this.delegate = this.createDelegate(this.getReaderContext(), root, parent);
        if (this.delegate.isDefaultNamespace(root) && StringUtils.hasText(profileSpec = root.getAttribute(PROFILE_ATTRIBUTE))) {
            String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, ",; ");
            if (!this.getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Skipped XML bean definition file due to specified profiles [" + profileSpec + "] not matching: " + this.getReaderContext().getResource()));
                }
                return;
            }
        }
        this.preProcessXml(root);
        this.parseBeanDefinitions(root, this.delegate);
        this.postProcessXml(root);
        this.delegate = parent;
    }

    protected BeanDefinitionParserDelegate createDelegate(XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }

    protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
        if (delegate.isDefaultNamespace(root)) {
            NodeList nl = root.getChildNodes();
            for (int i2 = 0; i2 < nl.getLength(); ++i2) {
                Node node = nl.item(i2);
                if (!(node instanceof Element)) continue;
                Element ele = (Element)node;
                if (delegate.isDefaultNamespace(ele)) {
                    this.parseDefaultElement(ele, delegate);
                    continue;
                }
                delegate.parseCustomElement(ele);
            }
        } else {
            delegate.parseCustomElement(root);
        }
    }

    private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
        if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
            this.importBeanDefinitionResource(ele);
        } else if (delegate.nodeNameEquals(ele, "alias")) {
            this.processAliasRegistration(ele);
        } else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
            this.processBeanDefinition(ele, delegate);
        } else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
            this.doRegisterBeanDefinitions(ele);
        }
    }

    protected void importBeanDefinitionResource(Element ele) {
        String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
        if (!StringUtils.hasText(location)) {
            this.getReaderContext().error("Resource location must not be empty", ele);
            return;
        }
        location = this.getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);
        LinkedHashSet<Resource> actualResources = new LinkedHashSet<Resource>(4);
        boolean absoluteLocation = false;
        try {
            absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        if (absoluteLocation) {
            try {
                int importCount = this.getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Imported " + importCount + " bean definitions from URL location [" + location + "]"));
                }
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to import bean definitions from URL location [" + location + "]", (Object)ele, ex);
            }
        } else {
            try {
                int importCount;
                Resource relativeResource = this.getReaderContext().getResource().createRelative(location);
                if (relativeResource.exists()) {
                    importCount = this.getReaderContext().getReader().loadBeanDefinitions(relativeResource);
                    actualResources.add(relativeResource);
                } else {
                    String baseLocation = this.getReaderContext().getResource().getURL().toString();
                    importCount = this.getReaderContext().getReader().loadBeanDefinitions(StringUtils.applyRelativePath(baseLocation, location), actualResources);
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Imported " + importCount + " bean definitions from relative location [" + location + "]"));
                }
            }
            catch (IOException ex) {
                this.getReaderContext().error("Failed to resolve current resource location", (Object)ele, ex);
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]", (Object)ele, ex);
            }
        }
        Resource[] actResArray = actualResources.toArray(new Resource[0]);
        this.getReaderContext().fireImportProcessed(location, actResArray, this.extractSource(ele));
    }

    protected void processAliasRegistration(Element ele) {
        String name = ele.getAttribute(NAME_ATTRIBUTE);
        String alias = ele.getAttribute("alias");
        boolean valid = true;
        if (!StringUtils.hasText(name)) {
            this.getReaderContext().error("Name must not be empty", ele);
            valid = false;
        }
        if (!StringUtils.hasText(alias)) {
            this.getReaderContext().error("Alias must not be empty", ele);
            valid = false;
        }
        if (valid) {
            try {
                this.getReaderContext().getRegistry().registerAlias(name, alias);
            }
            catch (Exception ex) {
                this.getReaderContext().error("Failed to register alias '" + alias + "' for bean with name '" + name + "'", (Object)ele, ex);
            }
            this.getReaderContext().fireAliasRegistered(name, alias, this.extractSource(ele));
        }
    }

    protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
        BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
        if (bdHolder != null) {
            bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
            try {
                BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, this.getReaderContext().getRegistry());
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to register bean definition with name '" + bdHolder.getBeanName() + "'", (Object)ele, ex);
            }
            this.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
        }
    }

    protected void preProcessXml(Element root) {
    }

    protected void postProcessXml(Element root) {
    }
}

