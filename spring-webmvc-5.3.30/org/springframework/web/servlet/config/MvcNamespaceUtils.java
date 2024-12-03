/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.parsing.ComponentDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.web.cors.CorsConfiguration
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.servlet.theme.FixedThemeResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.util.UrlPathHelper;

public abstract class MvcNamespaceUtils {
    private static final String BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME = BeanNameUrlHandlerMapping.class.getName();
    private static final String SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME = SimpleControllerHandlerAdapter.class.getName();
    private static final String HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME = HttpRequestHandlerAdapter.class.getName();
    private static final String URL_PATH_HELPER_BEAN_NAME = "mvcUrlPathHelper";
    private static final String PATH_MATCHER_BEAN_NAME = "mvcPathMatcher";
    private static final String CORS_CONFIGURATION_BEAN_NAME = "mvcCorsConfigurations";
    private static final String HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME = "mvcHandlerMappingIntrospector";

    public static void registerDefaultComponents(ParserContext context, @Nullable Object source) {
        MvcNamespaceUtils.registerBeanNameUrlHandlerMapping(context, source);
        MvcNamespaceUtils.registerHttpRequestHandlerAdapter(context, source);
        MvcNamespaceUtils.registerSimpleControllerHandlerAdapter(context, source);
        MvcNamespaceUtils.registerHandlerMappingIntrospector(context, source);
        MvcNamespaceUtils.registerLocaleResolver(context, source);
        MvcNamespaceUtils.registerThemeResolver(context, source);
        MvcNamespaceUtils.registerViewNameTranslator(context, source);
        MvcNamespaceUtils.registerFlashMapManager(context, source);
    }

    public static RuntimeBeanReference registerUrlPathHelper(@Nullable RuntimeBeanReference urlPathHelperRef, ParserContext context, @Nullable Object source) {
        if (urlPathHelperRef != null) {
            if (context.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME)) {
                context.getRegistry().removeAlias(URL_PATH_HELPER_BEAN_NAME);
            }
            context.getRegistry().registerAlias(urlPathHelperRef.getBeanName(), URL_PATH_HELPER_BEAN_NAME);
        } else if (!context.getRegistry().isAlias(URL_PATH_HELPER_BEAN_NAME) && !context.getRegistry().containsBeanDefinition(URL_PATH_HELPER_BEAN_NAME)) {
            RootBeanDefinition urlPathHelperDef = new RootBeanDefinition(UrlPathHelper.class);
            urlPathHelperDef.setSource(source);
            urlPathHelperDef.setRole(2);
            context.getRegistry().registerBeanDefinition(URL_PATH_HELPER_BEAN_NAME, (BeanDefinition)urlPathHelperDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)urlPathHelperDef, URL_PATH_HELPER_BEAN_NAME));
        }
        return new RuntimeBeanReference(URL_PATH_HELPER_BEAN_NAME);
    }

    public static RuntimeBeanReference registerPathMatcher(@Nullable RuntimeBeanReference pathMatcherRef, ParserContext context, @Nullable Object source) {
        if (pathMatcherRef != null) {
            if (context.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME)) {
                context.getRegistry().removeAlias(PATH_MATCHER_BEAN_NAME);
            }
            context.getRegistry().registerAlias(pathMatcherRef.getBeanName(), PATH_MATCHER_BEAN_NAME);
        } else if (!context.getRegistry().isAlias(PATH_MATCHER_BEAN_NAME) && !context.getRegistry().containsBeanDefinition(PATH_MATCHER_BEAN_NAME)) {
            RootBeanDefinition pathMatcherDef = new RootBeanDefinition(AntPathMatcher.class);
            pathMatcherDef.setSource(source);
            pathMatcherDef.setRole(2);
            context.getRegistry().registerBeanDefinition(PATH_MATCHER_BEAN_NAME, (BeanDefinition)pathMatcherDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)pathMatcherDef, PATH_MATCHER_BEAN_NAME));
        }
        return new RuntimeBeanReference(PATH_MATCHER_BEAN_NAME);
    }

    private static void registerBeanNameUrlHandlerMapping(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME)) {
            RootBeanDefinition mappingDef = new RootBeanDefinition(BeanNameUrlHandlerMapping.class);
            mappingDef.setSource(source);
            mappingDef.setRole(2);
            mappingDef.getPropertyValues().add("order", (Object)2);
            RuntimeBeanReference corsRef = MvcNamespaceUtils.registerCorsConfigurations(null, context, source);
            mappingDef.getPropertyValues().add("corsConfigurations", (Object)corsRef);
            context.getRegistry().registerBeanDefinition(BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME, (BeanDefinition)mappingDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)mappingDef, BEAN_NAME_URL_HANDLER_MAPPING_BEAN_NAME));
        }
    }

    private static void registerHttpRequestHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition adapterDef = new RootBeanDefinition(HttpRequestHandlerAdapter.class);
            adapterDef.setSource(source);
            adapterDef.setRole(2);
            context.getRegistry().registerBeanDefinition(HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME, (BeanDefinition)adapterDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)adapterDef, HTTP_REQUEST_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    private static void registerSimpleControllerHandlerAdapter(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition(SimpleControllerHandlerAdapter.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition(SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME, (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, SIMPLE_CONTROLLER_HANDLER_ADAPTER_BEAN_NAME));
        }
    }

    public static RuntimeBeanReference registerCorsConfigurations(@Nullable Map<String, CorsConfiguration> corsConfigurations, ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(CORS_CONFIGURATION_BEAN_NAME)) {
            RootBeanDefinition corsDef = new RootBeanDefinition(LinkedHashMap.class);
            corsDef.setSource(source);
            corsDef.setRole(2);
            if (corsConfigurations != null) {
                corsDef.getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
            }
            context.getReaderContext().getRegistry().registerBeanDefinition(CORS_CONFIGURATION_BEAN_NAME, (BeanDefinition)corsDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)corsDef, CORS_CONFIGURATION_BEAN_NAME));
        } else if (corsConfigurations != null) {
            BeanDefinition corsDef = context.getRegistry().getBeanDefinition(CORS_CONFIGURATION_BEAN_NAME);
            corsDef.getConstructorArgumentValues().addIndexedArgumentValue(0, corsConfigurations);
        }
        return new RuntimeBeanReference(CORS_CONFIGURATION_BEAN_NAME);
    }

    private static void registerHandlerMappingIntrospector(ParserContext context, @Nullable Object source) {
        if (!context.getRegistry().containsBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME)) {
            RootBeanDefinition beanDef = new RootBeanDefinition(HandlerMappingIntrospector.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            beanDef.setLazyInit(true);
            context.getRegistry().registerBeanDefinition(HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME, (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, HANDLER_MAPPING_INTROSPECTOR_BEAN_NAME));
        }
    }

    private static void registerLocaleResolver(ParserContext context, @Nullable Object source) {
        if (!MvcNamespaceUtils.containsBeanInHierarchy(context, "localeResolver")) {
            RootBeanDefinition beanDef = new RootBeanDefinition(AcceptHeaderLocaleResolver.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition("localeResolver", (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, "localeResolver"));
        }
    }

    private static void registerThemeResolver(ParserContext context, @Nullable Object source) {
        if (!MvcNamespaceUtils.containsBeanInHierarchy(context, "themeResolver")) {
            RootBeanDefinition beanDef = new RootBeanDefinition(FixedThemeResolver.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition("themeResolver", (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, "themeResolver"));
        }
    }

    private static void registerViewNameTranslator(ParserContext context, @Nullable Object source) {
        if (!MvcNamespaceUtils.containsBeanInHierarchy(context, "viewNameTranslator")) {
            RootBeanDefinition beanDef = new RootBeanDefinition(DefaultRequestToViewNameTranslator.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition("viewNameTranslator", (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, "viewNameTranslator"));
        }
    }

    private static void registerFlashMapManager(ParserContext context, @Nullable Object source) {
        if (!MvcNamespaceUtils.containsBeanInHierarchy(context, "flashMapManager")) {
            RootBeanDefinition beanDef = new RootBeanDefinition(SessionFlashMapManager.class);
            beanDef.setSource(source);
            beanDef.setRole(2);
            context.getRegistry().registerBeanDefinition("flashMapManager", (BeanDefinition)beanDef);
            context.registerComponent((ComponentDefinition)new BeanComponentDefinition((BeanDefinition)beanDef, "flashMapManager"));
        }
    }

    @Nullable
    public static Object getContentNegotiationManager(ParserContext context) {
        String name = AnnotationDrivenBeanDefinitionParser.HANDLER_MAPPING_BEAN_NAME;
        if (context.getRegistry().containsBeanDefinition(name)) {
            BeanDefinition handlerMappingBeanDef = context.getRegistry().getBeanDefinition(name);
            return handlerMappingBeanDef.getPropertyValues().get("contentNegotiationManager");
        }
        name = "mvcContentNegotiationManager";
        if (context.getRegistry().containsBeanDefinition(name)) {
            return new RuntimeBeanReference(name);
        }
        return null;
    }

    private static boolean containsBeanInHierarchy(ParserContext context, String beanName) {
        BeanDefinitionRegistry registry = context.getRegistry();
        return registry instanceof BeanFactory ? ((BeanFactory)registry).containsBean(beanName) : registry.containsBeanDefinition(beanName);
    }
}

