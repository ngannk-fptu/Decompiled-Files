/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

public class ModelAndViewMethodReturnValueHandler
implements HandlerMethodReturnValueHandler {
    @Nullable
    private String[] redirectPatterns;

    public void setRedirectPatterns(String ... redirectPatterns) {
        this.redirectPatterns = redirectPatterns;
    }

    @Nullable
    public String[] getRedirectPatterns() {
        return this.redirectPatterns;
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return ModelAndView.class.isAssignableFrom(returnType.getParameterType());
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        ModelAndView mav = (ModelAndView)returnValue;
        if (mav.isReference()) {
            String viewName = mav.getViewName();
            mavContainer.setViewName(viewName);
            if (viewName != null && this.isRedirectViewName(viewName)) {
                mavContainer.setRedirectModelScenario(true);
            }
        } else {
            View view = mav.getView();
            mavContainer.setView((Object)view);
            if (view instanceof SmartView && ((SmartView)view).isRedirectView()) {
                mavContainer.setRedirectModelScenario(true);
            }
        }
        mavContainer.setStatus(mav.getStatus());
        mavContainer.addAllAttributes(mav.getModel());
    }

    protected boolean isRedirectViewName(String viewName) {
        return PatternMatchUtils.simpleMatch((String[])this.redirectPatterns, (String)viewName) || viewName.startsWith("redirect:");
    }
}

