/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ArrayELResolver
 *  javax.el.BeanELResolver
 *  javax.el.CompositeELResolver
 *  javax.el.ELResolver
 *  javax.el.ListELResolver
 *  javax.el.MapELResolver
 *  javax.el.ResourceBundleELResolver
 *  javax.servlet.ServletContext
 *  javax.servlet.jsp.JspFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.tiles.TilesContainer
 *  org.apache.tiles.TilesException
 *  org.apache.tiles.definition.DefinitionsFactory
 *  org.apache.tiles.definition.DefinitionsReader
 *  org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO
 *  org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO
 *  org.apache.tiles.definition.digester.DigesterDefinitionsReader
 *  org.apache.tiles.el.ELAttributeEvaluator
 *  org.apache.tiles.el.ScopeELResolver
 *  org.apache.tiles.el.TilesContextBeanELResolver
 *  org.apache.tiles.el.TilesContextELResolver
 *  org.apache.tiles.evaluator.AttributeEvaluator
 *  org.apache.tiles.evaluator.AttributeEvaluatorFactory
 *  org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory
 *  org.apache.tiles.evaluator.impl.DirectAttributeEvaluator
 *  org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory
 *  org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer
 *  org.apache.tiles.factory.AbstractTilesContainerFactory
 *  org.apache.tiles.factory.BasicTilesContainerFactory
 *  org.apache.tiles.impl.mgmt.CachingTilesContainer
 *  org.apache.tiles.locale.LocaleResolver
 *  org.apache.tiles.preparer.factory.PreparerFactory
 *  org.apache.tiles.request.ApplicationContext
 *  org.apache.tiles.request.ApplicationContextAware
 *  org.apache.tiles.request.ApplicationResource
 *  org.apache.tiles.startup.DefaultTilesInitializer
 *  org.apache.tiles.startup.TilesInitializer
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.PropertyAccessorFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.servlet.view.tiles3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.ScopeELResolver;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationContextAware;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.startup.DefaultTilesInitializer;
import org.apache.tiles.startup.TilesInitializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.tiles3.SpringLocaleResolver;
import org.springframework.web.servlet.view.tiles3.SpringWildcardServletTilesApplicationContext;

public class TilesConfigurer
implements ServletContextAware,
InitializingBean,
DisposableBean {
    private static final boolean tilesElPresent = ClassUtils.isPresent((String)"org.apache.tiles.el.ELAttributeEvaluator", (ClassLoader)TilesConfigurer.class.getClassLoader());
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private TilesInitializer tilesInitializer;
    @Nullable
    private String[] definitions;
    private boolean checkRefresh = false;
    private boolean validateDefinitions = true;
    @Nullable
    private Class<? extends DefinitionsFactory> definitionsFactoryClass;
    @Nullable
    private Class<? extends PreparerFactory> preparerFactoryClass;
    private boolean useMutableTilesContainer = false;
    @Nullable
    private ServletContext servletContext;

    public void setTilesInitializer(TilesInitializer tilesInitializer) {
        this.tilesInitializer = tilesInitializer;
    }

    public void setCompleteAutoload(boolean completeAutoload) {
        if (completeAutoload) {
            try {
                this.tilesInitializer = new SpringCompleteAutoloadTilesInitializer();
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Tiles-Extras 3.0 not available", ex);
            }
        } else {
            this.tilesInitializer = null;
        }
    }

    public void setDefinitions(String ... definitions) {
        this.definitions = definitions;
    }

    public void setCheckRefresh(boolean checkRefresh) {
        this.checkRefresh = checkRefresh;
    }

    public void setValidateDefinitions(boolean validateDefinitions) {
        this.validateDefinitions = validateDefinitions;
    }

    public void setDefinitionsFactoryClass(Class<? extends DefinitionsFactory> definitionsFactoryClass) {
        this.definitionsFactoryClass = definitionsFactoryClass;
    }

    public void setPreparerFactoryClass(Class<? extends PreparerFactory> preparerFactoryClass) {
        this.preparerFactoryClass = preparerFactoryClass;
    }

    public void setUseMutableTilesContainer(boolean useMutableTilesContainer) {
        this.useMutableTilesContainer = useMutableTilesContainer;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void afterPropertiesSet() throws TilesException {
        Assert.state((this.servletContext != null ? 1 : 0) != 0, (String)"No ServletContext available");
        SpringWildcardServletTilesApplicationContext preliminaryContext = new SpringWildcardServletTilesApplicationContext(this.servletContext);
        if (this.tilesInitializer == null) {
            this.tilesInitializer = new SpringTilesInitializer();
        }
        this.tilesInitializer.initialize((ApplicationContext)preliminaryContext);
    }

    public void destroy() throws TilesException {
        if (this.tilesInitializer != null) {
            this.tilesInitializer.destroy();
        }
    }

    private static class CompositeELResolverImpl
    extends CompositeELResolver {
        public CompositeELResolverImpl() {
            this.add((ELResolver)new ScopeELResolver());
            this.add((ELResolver)new TilesContextELResolver((ELResolver)new TilesContextBeanELResolver()));
            this.add((ELResolver)new TilesContextBeanELResolver());
            this.add((ELResolver)new ArrayELResolver(false));
            this.add((ELResolver)new ListELResolver(false));
            this.add((ELResolver)new MapELResolver(false));
            this.add((ELResolver)new ResourceBundleELResolver());
            this.add((ELResolver)new BeanELResolver(false));
        }
    }

    private class TilesElActivator {
        private TilesElActivator() {
        }

        public AttributeEvaluator createEvaluator() {
            ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
            evaluator.setExpressionFactory(JspFactory.getDefaultFactory().getJspApplicationContext(TilesConfigurer.this.servletContext).getExpressionFactory());
            evaluator.setResolver((ELResolver)new CompositeELResolverImpl());
            return evaluator;
        }
    }

    private static class SpringCompleteAutoloadTilesContainerFactory
    extends CompleteAutoloadTilesContainerFactory {
        private SpringCompleteAutoloadTilesContainerFactory() {
        }

        protected LocaleResolver createLocaleResolver(ApplicationContext applicationContext) {
            return new SpringLocaleResolver();
        }
    }

    private static class SpringCompleteAutoloadTilesInitializer
    extends CompleteAutoloadTilesInitializer {
        private SpringCompleteAutoloadTilesInitializer() {
        }

        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new SpringCompleteAutoloadTilesContainerFactory();
        }
    }

    private class SpringTilesContainerFactory
    extends BasicTilesContainerFactory {
        private SpringTilesContainerFactory() {
        }

        protected TilesContainer createDecoratedContainer(TilesContainer originalContainer, ApplicationContext context) {
            return TilesConfigurer.this.useMutableTilesContainer ? new CachingTilesContainer(originalContainer) : originalContainer;
        }

        protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
            if (TilesConfigurer.this.definitions != null) {
                ArrayList<ApplicationResource> result = new ArrayList<ApplicationResource>();
                for (String definition : TilesConfigurer.this.definitions) {
                    Collection resources2 = applicationContext.getResources(definition);
                    if (resources2 == null) continue;
                    result.addAll(resources2);
                }
                return result;
            }
            return super.getSources(applicationContext);
        }

        protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(ApplicationContext applicationContext, LocaleResolver resolver) {
            BaseLocaleUrlDefinitionDAO dao = super.instantiateLocaleDefinitionDao(applicationContext, resolver);
            if (TilesConfigurer.this.checkRefresh && dao instanceof CachingLocaleUrlDefinitionDAO) {
                ((CachingLocaleUrlDefinitionDAO)dao).setCheckRefresh(true);
            }
            return dao;
        }

        protected DefinitionsReader createDefinitionsReader(ApplicationContext context) {
            DigesterDefinitionsReader reader = (DigesterDefinitionsReader)super.createDefinitionsReader(context);
            reader.setValidating(TilesConfigurer.this.validateDefinitions);
            return reader;
        }

        protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext, LocaleResolver resolver) {
            if (TilesConfigurer.this.definitionsFactoryClass != null) {
                BeanWrapper bw;
                DefinitionsFactory factory = (DefinitionsFactory)BeanUtils.instantiateClass((Class)TilesConfigurer.this.definitionsFactoryClass);
                if (factory instanceof ApplicationContextAware) {
                    ((ApplicationContextAware)factory).setApplicationContext(applicationContext);
                }
                if ((bw = PropertyAccessorFactory.forBeanPropertyAccess((Object)factory)).isWritableProperty("localeResolver")) {
                    bw.setPropertyValue("localeResolver", (Object)resolver);
                }
                if (bw.isWritableProperty("definitionDAO")) {
                    bw.setPropertyValue("definitionDAO", (Object)this.createLocaleDefinitionDao(applicationContext, resolver));
                }
                return factory;
            }
            return super.createDefinitionsFactory(applicationContext, resolver);
        }

        protected PreparerFactory createPreparerFactory(ApplicationContext context) {
            if (TilesConfigurer.this.preparerFactoryClass != null) {
                return (PreparerFactory)BeanUtils.instantiateClass((Class)TilesConfigurer.this.preparerFactoryClass);
            }
            return super.createPreparerFactory(context);
        }

        protected LocaleResolver createLocaleResolver(ApplicationContext context) {
            return new SpringLocaleResolver();
        }

        protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext context, LocaleResolver resolver) {
            Object evaluator = tilesElPresent && JspFactory.getDefaultFactory() != null ? new TilesElActivator().createEvaluator() : new DirectAttributeEvaluator();
            return new BasicAttributeEvaluatorFactory((AttributeEvaluator)evaluator);
        }
    }

    private class SpringTilesInitializer
    extends DefaultTilesInitializer {
        private SpringTilesInitializer() {
        }

        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new SpringTilesContainerFactory();
        }
    }
}

