/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.http.MediaType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.support.ContextExposingHttpServletRequest
 *  org.springframework.web.context.support.WebApplicationObjectSupport
 */
package org.springframework.web.servlet.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;

public abstract class AbstractView
extends WebApplicationObjectSupport
implements View,
BeanNameAware {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;
    @Nullable
    private String contentType = "text/html;charset=ISO-8859-1";
    @Nullable
    private String requestContextAttribute;
    private final Map<String, Object> staticAttributes = new LinkedHashMap<String, Object>();
    private boolean exposePathVariables = true;
    private boolean exposeContextBeansAsAttributes = false;
    @Nullable
    private Set<String> exposedContextBeanNames;
    @Nullable
    private String beanName;

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    @Nullable
    public String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setAttributesCSV(@Nullable String propString) throws IllegalArgumentException {
        if (propString != null) {
            StringTokenizer st = new StringTokenizer(propString, ",");
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                int eqIdx = tok.indexOf(61);
                if (eqIdx == -1) {
                    throw new IllegalArgumentException("Expected '=' in attributes CSV string '" + propString + "'");
                }
                if (eqIdx >= tok.length() - 2) {
                    throw new IllegalArgumentException("At least 2 characters ([]) required in attributes CSV string '" + propString + "'");
                }
                String name = tok.substring(0, eqIdx);
                int beginIndex = eqIdx + 2;
                int endIndex = tok.length() - 1;
                String value = tok.substring(beginIndex, endIndex);
                this.addStaticAttribute(name, value);
            }
        }
    }

    public void setAttributes(Properties attributes) {
        CollectionUtils.mergePropertiesIntoMap((Properties)attributes, this.staticAttributes);
    }

    public void setAttributesMap(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach(this::addStaticAttribute);
        }
    }

    public Map<String, Object> getAttributesMap() {
        return this.staticAttributes;
    }

    public void addStaticAttribute(String name, Object value) {
        this.staticAttributes.put(name, value);
    }

    public Map<String, Object> getStaticAttributes() {
        return Collections.unmodifiableMap(this.staticAttributes);
    }

    public void setExposePathVariables(boolean exposePathVariables) {
        this.exposePathVariables = exposePathVariables;
    }

    public boolean isExposePathVariables() {
        return this.exposePathVariables;
    }

    public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
        this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
    }

    public void setExposedContextBeanNames(String ... exposedContextBeanNames) {
        this.exposedContextBeanNames = new HashSet<String>(Arrays.asList(exposedContextBeanNames));
    }

    public void setBeanName(@Nullable String beanName) {
        this.beanName = beanName;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("View " + this.formatViewName() + ", model " + (model != null ? model : Collections.emptyMap()) + (this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes)));
        }
        Map<String, Object> mergedModel = this.createMergedOutputModel(model, request, response);
        this.prepareResponse(request, response);
        this.renderMergedOutputModel(mergedModel, this.getRequestToExpose(request), response);
    }

    protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        Map pathVars = this.exposePathVariables ? (Map)request.getAttribute(View.PATH_VARIABLES) : null;
        int size = this.staticAttributes.size();
        size += model != null ? model.size() : 0;
        LinkedHashMap mergedModel = CollectionUtils.newLinkedHashMap((int)(size += pathVars != null ? pathVars.size() : 0));
        mergedModel.putAll(this.staticAttributes);
        if (pathVars != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }
        if (this.requestContextAttribute != null) {
            mergedModel.put(this.requestContextAttribute, this.createRequestContext(request, response, mergedModel));
        }
        return mergedModel;
    }

    protected RequestContext createRequestContext(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
        return new RequestContext(request, response, this.getServletContext(), model);
    }

    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        if (this.generatesDownloadContent()) {
            response.setHeader("Pragma", "private");
            response.setHeader("Cache-Control", "private, must-revalidate");
        }
    }

    protected boolean generatesDownloadContent() {
        return false;
    }

    protected HttpServletRequest getRequestToExpose(HttpServletRequest originalRequest) {
        if (this.exposeContextBeansAsAttributes || this.exposedContextBeanNames != null) {
            WebApplicationContext wac = this.getWebApplicationContext();
            Assert.state((wac != null ? 1 : 0) != 0, (String)"No WebApplicationContext");
            return new ContextExposingHttpServletRequest(originalRequest, wac, this.exposedContextBeanNames);
        }
        return originalRequest;
    }

    protected abstract void renderMergedOutputModel(Map<String, Object> var1, HttpServletRequest var2, HttpServletResponse var3) throws Exception;

    protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.forEach((name, value) -> {
            if (value != null) {
                request.setAttribute(name, value);
            } else {
                request.removeAttribute(name);
            }
        });
    }

    protected ByteArrayOutputStream createTemporaryOutputStream() {
        return new ByteArrayOutputStream(4096);
    }

    protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
        response.setContentType(this.getContentType());
        response.setContentLength(baos.size());
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo((OutputStream)out);
        out.flush();
    }

    protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
        MediaType mediaType = (MediaType)request.getAttribute(View.SELECTED_CONTENT_TYPE);
        if (mediaType != null && mediaType.isConcrete()) {
            response.setContentType(mediaType.toString());
        } else {
            response.setContentType(this.getContentType());
        }
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.formatViewName();
    }

    protected String formatViewName() {
        return this.getBeanName() != null ? "name '" + this.getBeanName() + "'" : "[" + this.getClass().getSimpleName() + "]";
    }
}

