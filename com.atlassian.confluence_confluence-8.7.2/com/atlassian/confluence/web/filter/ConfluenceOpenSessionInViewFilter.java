/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.orm.hibernate5.support.OpenSessionInViewFilter
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;

public class ConfluenceOpenSessionInViewFilter
extends OpenSessionInViewFilter {
    public ConfluenceOpenSessionInViewFilter() {
        this.setSessionFactoryBeanName("sessionFactory");
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!ContainerManager.isContainerSetup() || !this.isDatabaseSetUp()) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        super.doFilterInternal(request, response, filterChain);
    }

    private boolean isDatabaseSetUp() {
        HibernateConfig hibernateConfig = (HibernateConfig)ContainerManager.getComponent((String)"hibernateConfig");
        return hibernateConfig.isHibernateSetup();
    }
}

