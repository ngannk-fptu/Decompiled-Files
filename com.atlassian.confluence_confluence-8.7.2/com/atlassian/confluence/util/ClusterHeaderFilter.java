/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.CharMatcher
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.CharMatcher;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterHeaderFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(ClusterHeaderFilter.class);
    private final Supplier<ClusterManager> clusterManagerRef = new LazyComponentReference("clusterManager");
    private final AtomicReference<String> lastNodeNameThatWeWarnedAbout = new AtomicReference();

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Maybe<ClusterNodeInformation> nodeRef = this.getClusterNode();
        if (nodeRef.isDefined()) {
            ClusterNodeInformation clusterNode = (ClusterNodeInformation)nodeRef.get();
            response.setHeader("X-Confluence-Cluster-Node", clusterNode.getAnonymizedNodeIdentifier());
            Optional<String> nodeNameRef = clusterNode.humanReadableNodeName();
            if (nodeNameRef.isPresent()) {
                String nodeName = nodeNameRef.get();
                if (CharMatcher.ascii().matchesAllOf((CharSequence)nodeName)) {
                    response.setHeader("X-Confluence-Cluster-Node-Name", nodeName);
                } else if (this.shouldWarnAboutNonAsciiName(nodeName)) {
                    log.warn("Cannot set non-ASCII cluster node name [{}] as HTTP response header", (Object)nodeName);
                }
            }
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private boolean shouldWarnAboutNonAsciiName(String nodeName) {
        return !nodeName.equals(this.lastNodeNameThatWeWarnedAbout.getAndSet(nodeName));
    }

    private Maybe<ClusterNodeInformation> getClusterNode() {
        if (!ContainerManager.isContainerSetup()) {
            return Option.none();
        }
        return Option.option((Object)((ClusterManager)this.clusterManagerRef.get()).getThisNodeInformation());
    }
}

