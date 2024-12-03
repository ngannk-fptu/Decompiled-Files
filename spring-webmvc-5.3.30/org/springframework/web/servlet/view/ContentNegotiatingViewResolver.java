/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.core.Ordered
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.http.MediaType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.HttpMediaTypeNotAcceptableException
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.accept.ContentNegotiationManagerFactoryBean
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.RequestAttributes
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 *  org.springframework.web.context.request.ServletWebRequest
 *  org.springframework.web.context.support.WebApplicationObjectSupport
 */
package org.springframework.web.servlet.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class ContentNegotiatingViewResolver
extends WebApplicationObjectSupport
implements ViewResolver,
Ordered,
InitializingBean {
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    private final ContentNegotiationManagerFactoryBean cnmFactoryBean = new ContentNegotiationManagerFactoryBean();
    private boolean useNotAcceptableStatusCode = false;
    @Nullable
    private List<View> defaultViews;
    @Nullable
    private List<ViewResolver> viewResolvers;
    private int order = Integer.MIN_VALUE;
    private static final View NOT_ACCEPTABLE_VIEW = new View(){

        @Override
        @Nullable
        public String getContentType() {
            return null;
        }

        @Override
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            response.setStatus(406);
        }
    };

    public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable
    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setUseNotAcceptableStatusCode(boolean useNotAcceptableStatusCode) {
        this.useNotAcceptableStatusCode = useNotAcceptableStatusCode;
    }

    public boolean isUseNotAcceptableStatusCode() {
        return this.useNotAcceptableStatusCode;
    }

    public void setDefaultViews(List<View> defaultViews) {
        this.defaultViews = defaultViews;
    }

    public List<View> getDefaultViews() {
        return this.defaultViews != null ? Collections.unmodifiableList(this.defaultViews) : Collections.emptyList();
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    public List<ViewResolver> getViewResolvers() {
        return this.viewResolvers != null ? Collections.unmodifiableList(this.viewResolvers) : Collections.emptyList();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    protected void initServletContext(ServletContext servletContext) {
        Collection matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)this.obtainApplicationContext(), ViewResolver.class).values();
        if (this.viewResolvers == null) {
            this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.size());
            for (ViewResolver viewResolver : matchingBeans) {
                if (this == viewResolver) continue;
                this.viewResolvers.add(viewResolver);
            }
        } else {
            for (int i2 = 0; i2 < this.viewResolvers.size(); ++i2) {
                ViewResolver vr = this.viewResolvers.get(i2);
                if (matchingBeans.contains(vr)) continue;
                String name = vr.getClass().getName() + i2;
                this.obtainApplicationContext().getAutowireCapableBeanFactory().initializeBean((Object)vr, name);
            }
        }
        AnnotationAwareOrderComparator.sort(this.viewResolvers);
        this.cnmFactoryBean.setServletContext(servletContext);
    }

    public void afterPropertiesSet() {
        if (this.contentNegotiationManager == null) {
            this.contentNegotiationManager = this.cnmFactoryBean.build();
        }
        if (this.viewResolvers == null || this.viewResolvers.isEmpty()) {
            this.logger.warn((Object)"No ViewResolvers configured");
        }
    }

    @Override
    @Nullable
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        String mediaTypeInfo;
        List<View> candidateViews;
        View bestView;
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        Assert.state((boolean)(attrs instanceof ServletRequestAttributes), (String)"No current ServletRequestAttributes");
        List<MediaType> requestedMediaTypes = this.getMediaTypes(((ServletRequestAttributes)attrs).getRequest());
        if (requestedMediaTypes != null && (bestView = this.getBestView(candidateViews = this.getCandidateViews(viewName, locale, requestedMediaTypes), requestedMediaTypes, attrs)) != null) {
            return bestView;
        }
        String string = mediaTypeInfo = this.logger.isDebugEnabled() && requestedMediaTypes != null ? " given " + requestedMediaTypes.toString() : "";
        if (this.useNotAcceptableStatusCode) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using 406 NOT_ACCEPTABLE" + mediaTypeInfo));
            }
            return NOT_ACCEPTABLE_VIEW;
        }
        this.logger.debug((Object)("View remains unresolved" + mediaTypeInfo));
        return null;
    }

    @Nullable
    protected List<MediaType> getMediaTypes(HttpServletRequest request) {
        Assert.state((this.contentNegotiationManager != null ? 1 : 0) != 0, (String)"No ContentNegotiationManager set");
        try {
            ServletWebRequest webRequest = new ServletWebRequest(request);
            List acceptableMediaTypes = this.contentNegotiationManager.resolveMediaTypes((NativeWebRequest)webRequest);
            List<MediaType> producibleMediaTypes = this.getProducibleMediaTypes(request);
            LinkedHashSet<MediaType> compatibleMediaTypes = new LinkedHashSet<MediaType>();
            for (MediaType acceptable : acceptableMediaTypes) {
                for (MediaType producible : producibleMediaTypes) {
                    if (!acceptable.isCompatibleWith(producible)) continue;
                    compatibleMediaTypes.add(this.getMostSpecificMediaType(acceptable, producible));
                }
            }
            ArrayList<MediaType> selectedMediaTypes = new ArrayList<MediaType>(compatibleMediaTypes);
            MediaType.sortBySpecificityAndQuality(selectedMediaTypes);
            return selectedMediaTypes;
        }
        catch (HttpMediaTypeNotAcceptableException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)ex.getMessage());
            }
            return null;
        }
    }

    private List<MediaType> getProducibleMediaTypes(HttpServletRequest request) {
        Set mediaTypes = (Set)request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty((Collection)mediaTypes)) {
            return new ArrayList<MediaType>(mediaTypes);
        }
        return Collections.singletonList(MediaType.ALL);
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        return MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceType = produceType.copyQualityValue(acceptType)) < 0 ? acceptType : produceType;
    }

    private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes) throws Exception {
        ArrayList<View> candidateViews = new ArrayList<View>();
        if (this.viewResolvers != null) {
            Assert.state((this.contentNegotiationManager != null ? 1 : 0) != 0, (String)"No ContentNegotiationManager set");
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    candidateViews.add(view);
                }
                for (MediaType requestedMediaType : requestedMediaTypes) {
                    List extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
                    for (String extension : extensions) {
                        String viewNameWithExtension = viewName + '.' + extension;
                        view = viewResolver.resolveViewName(viewNameWithExtension, locale);
                        if (view == null) continue;
                        candidateViews.add(view);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(this.defaultViews)) {
            candidateViews.addAll(this.defaultViews);
        }
        return candidateViews;
    }

    @Nullable
    private View getBestView(List<View> candidateViews, List<MediaType> requestedMediaTypes, RequestAttributes attrs) {
        for (View candidateView : candidateViews) {
            SmartView smartView;
            if (!(candidateView instanceof SmartView) || !(smartView = (SmartView)candidateView).isRedirectView()) continue;
            return candidateView;
        }
        for (MediaType mediaType : requestedMediaTypes) {
            for (View candidateView : candidateViews) {
                MediaType candidateContentType;
                if (!StringUtils.hasText((String)candidateView.getContentType()) || !mediaType.isCompatibleWith(candidateContentType = MediaType.parseMediaType((String)candidateView.getContentType()))) continue;
                mediaType = mediaType.removeQualityValue();
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Selected '" + mediaType + "' given " + requestedMediaTypes));
                }
                attrs.setAttribute(View.SELECTED_CONTENT_TYPE, (Object)mediaType, 0);
                return candidateView;
            }
        }
        return null;
    }
}

