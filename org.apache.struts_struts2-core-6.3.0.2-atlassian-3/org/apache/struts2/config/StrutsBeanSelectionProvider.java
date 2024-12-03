/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodAccessor
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.UnknownHandlerManager;
import com.opensymphony.xwork2.conversion.ConversionAnnotationProcessor;
import com.opensymphony.xwork2.conversion.ConversionFileProcessor;
import com.opensymphony.xwork2.conversion.ConversionPropertiesProcessor;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.ognl.BeanInfoCacheFactory;
import com.opensymphony.xwork2.ognl.ExpressionCacheFactory;
import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import ognl.MethodAccessor;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.components.date.DateFormatter;
import org.apache.struts2.config.AbstractBeanSelectionProvider;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.interceptor.exec.ExecutorProvider;
import org.apache.struts2.ognl.OgnlGuard;
import org.apache.struts2.url.QueryStringBuilder;
import org.apache.struts2.url.QueryStringParser;
import org.apache.struts2.url.UrlDecoder;
import org.apache.struts2.url.UrlEncoder;
import org.apache.struts2.util.ContentTypeMatcher;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.util.UrlHelper;

public class StrutsBeanSelectionProvider
extends AbstractBeanSelectionProvider {
    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) {
        this.alias(ObjectFactory.class, "struts.objectFactory", builder, props);
        this.alias(ActionFactory.class, "struts.objectFactory.actionFactory", builder, props);
        this.alias(ResultFactory.class, "struts.objectFactory.resultFactory", builder, props);
        this.alias(ConverterFactory.class, "struts.objectFactory.converterFactory", builder, props);
        this.alias(InterceptorFactory.class, "struts.objectFactory.interceptorFactory", builder, props);
        this.alias(ValidatorFactory.class, "struts.objectFactory.validatorFactory", builder, props);
        this.alias(UnknownHandlerFactory.class, "struts.objectFactory.unknownHandlerFactory", builder, props);
        this.alias(FileManagerFactory.class, "struts.fileManagerFactory", builder, props, Scope.SINGLETON);
        this.alias(RootAccessor.class, "struts.compoundRootAccessor", builder, props);
        this.alias(MethodAccessor.class, "struts.methodAccessor", builder, props);
        this.alias(XWorkConverter.class, "struts.xworkConverter", builder, props);
        this.alias(CollectionConverter.class, "struts.converter.collection", builder, props);
        this.alias(ArrayConverter.class, "struts.converter.array", builder, props);
        this.alias(DateConverter.class, "struts.converter.date", builder, props);
        this.alias(NumberConverter.class, "struts.converter.number", builder, props);
        this.alias(StringConverter.class, "struts.converter.string", builder, props);
        this.alias(ConversionPropertiesProcessor.class, "struts.converter.properties.processor", builder, props);
        this.alias(ConversionFileProcessor.class, "struts.converter.file.processor", builder, props);
        this.alias(ConversionAnnotationProcessor.class, "struts.converter.annotation.processor", builder, props);
        this.alias(TypeConverterCreator.class, "struts.converter.creator", builder, props);
        this.alias(TypeConverterHolder.class, "struts.converter.holder", builder, props);
        this.alias(TextProvider.class, "struts.textProvider", builder, props, Scope.PROTOTYPE);
        this.alias(TextProviderFactory.class, "struts.textProviderFactory", builder, props, Scope.PROTOTYPE);
        this.alias(LocaleProviderFactory.class, "struts.localeProviderFactory", builder, props);
        this.alias(LocalizedTextProvider.class, "struts.localizedTextProvider", builder, props);
        this.alias(ActionProxyFactory.class, "struts.actionProxyFactory", builder, props);
        this.alias(ObjectTypeDeterminer.class, "struts.objectTypeDeterminer", builder, props);
        this.alias(ActionMapper.class, "struts.mapper.class", builder, props);
        this.alias(MultiPartRequest.class, "struts.multipart.parser", builder, props, Scope.PROTOTYPE);
        this.alias(FreemarkerManager.class, "struts.freemarker.manager.classname", builder, props);
        this.alias(UrlRenderer.class, "struts.urlRenderer", builder, props);
        this.alias(ActionValidatorManager.class, "struts.actionValidatorManager", builder, props);
        this.alias(ValueStackFactory.class, "struts.valueStackFactory", builder, props);
        this.alias(ReflectionProvider.class, "struts.reflectionProvider", builder, props);
        this.alias(ReflectionContextFactory.class, "struts.reflectionContextFactory", builder, props);
        this.alias(PatternMatcher.class, "struts.patternMatcher", builder, props);
        this.alias(ContentTypeMatcher.class, "struts.contentTypeMatcher", builder, props);
        this.alias(StaticContentLoader.class, "struts.staticContentLoader", builder, props);
        this.alias(UnknownHandlerManager.class, "struts.unknownHandlerManager", builder, props);
        this.alias(UrlHelper.class, "struts.view.urlHelper", builder, props);
        this.alias(TextParser.class, "struts.expression.parser", builder, props);
        this.alias(DispatcherErrorHandler.class, "struts.dispatcher.errorHandler", builder, props);
        this.alias(ExcludedPatternsChecker.class, "struts.excludedPatterns.checker", builder, props, Scope.PROTOTYPE);
        this.alias(AcceptedPatternsChecker.class, "struts.acceptedPatterns.checker", builder, props, Scope.PROTOTYPE);
        this.alias(NotExcludedAcceptedPatternsChecker.class, "struts.notExcludedAcceptedPatterns.checker", builder, props, Scope.SINGLETON);
        this.alias(DateFormatter.class, "struts.date.formatter", builder, props, Scope.SINGLETON);
        this.alias(ExpressionCacheFactory.class, "struts.ognl.expressionCacheFactory", builder, props, Scope.SINGLETON);
        this.alias(BeanInfoCacheFactory.class, "struts.ognl.beanInfoCacheFactory", builder, props, Scope.SINGLETON);
        this.alias(SecurityMemberAccess.class, "struts.securityMemberAccess", builder, props, Scope.PROTOTYPE);
        this.alias(OgnlGuard.class, "struts.ognlGuard", builder, props, Scope.SINGLETON);
        this.alias(QueryStringBuilder.class, "struts.url.queryStringBuilder", builder, props, Scope.SINGLETON);
        this.alias(QueryStringParser.class, "struts.url.queryStringParser", builder, props, Scope.SINGLETON);
        this.alias(UrlEncoder.class, "struts.url.encoder", builder, props, Scope.SINGLETON);
        this.alias(UrlDecoder.class, "struts.url.decoder", builder, props, Scope.SINGLETON);
        this.alias(ExecutorProvider.class, "struts.executor.provider", builder, props, Scope.SINGLETON);
        this.switchDevMode(props);
    }

    private void switchDevMode(LocatableProperties props) {
        if ("true".equalsIgnoreCase(props.getProperty("struts.devMode"))) {
            if (props.getProperty("struts.i18n.reload") == null) {
                props.setProperty("struts.i18n.reload", "true");
            }
            if (props.getProperty("struts.configuration.xml.reload") == null) {
                props.setProperty("struts.configuration.xml.reload", "true");
            }
            if (props.getProperty("struts.freemarker.templatesCache.updateDelay") == null) {
                props.setProperty("struts.freemarker.templatesCache.updateDelay", "0");
            }
        }
    }
}

