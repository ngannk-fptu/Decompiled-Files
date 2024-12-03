/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.RedirectViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewControllerRegistry {
    @Nullable
    private ApplicationContext applicationContext;
    private final List<ViewControllerRegistration> registrations = new ArrayList<ViewControllerRegistration>(4);
    private final List<RedirectViewControllerRegistration> redirectRegistrations = new ArrayList<RedirectViewControllerRegistration>(10);
    private int order = 1;

    public ViewControllerRegistry(@Nullable ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ViewControllerRegistration addViewController(String urlPathOrPattern) {
        ViewControllerRegistration registration = new ViewControllerRegistration(urlPathOrPattern);
        registration.setApplicationContext(this.applicationContext);
        this.registrations.add(registration);
        return registration;
    }

    public RedirectViewControllerRegistration addRedirectViewController(String urlPath, String redirectUrl) {
        RedirectViewControllerRegistration registration = new RedirectViewControllerRegistration(urlPath, redirectUrl);
        registration.setApplicationContext(this.applicationContext);
        this.redirectRegistrations.add(registration);
        return registration;
    }

    public void addStatusController(String urlPath, HttpStatus statusCode) {
        ViewControllerRegistration registration = new ViewControllerRegistration(urlPath);
        registration.setApplicationContext(this.applicationContext);
        registration.setStatusCode(statusCode);
        registration.getViewController().setStatusOnly(true);
        this.registrations.add(registration);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Nullable
    protected SimpleUrlHandlerMapping buildHandlerMapping() {
        if (this.registrations.isEmpty() && this.redirectRegistrations.isEmpty()) {
            return null;
        }
        LinkedHashMap<String, ParameterizableViewController> urlMap = new LinkedHashMap<String, ParameterizableViewController>();
        for (ViewControllerRegistration viewControllerRegistration : this.registrations) {
            urlMap.put(viewControllerRegistration.getUrlPath(), viewControllerRegistration.getViewController());
        }
        for (RedirectViewControllerRegistration redirectViewControllerRegistration : this.redirectRegistrations) {
            urlMap.put(redirectViewControllerRegistration.getUrlPath(), redirectViewControllerRegistration.getViewController());
        }
        return new SimpleUrlHandlerMapping(urlMap, this.order);
    }
}

