/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.zdu.rest;

import com.atlassian.zdu.rest.ZduResource;
import com.atlassian.zdu.rest.dto.Cluster;
import com.atlassian.zdu.rest.dto.Link;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.google.common.collect.Lists;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

public class LinkBuilder {
    private LinkBuilder() {
    }

    public static List<Link> forCluster(Cluster cluster) {
        ArrayList links = Lists.newArrayList((Object[])new Link[]{new Link("self", UriBuilder.fromResource(ZduResource.class).build(new Object[0]))});
        if (cluster.getState().canStart()) {
            links.add(new Link("start", LinkBuilder.resourceMethod(ZduResource.class, "startUpgrade", new Object[0])));
        }
        if (cluster.getState().canCancel()) {
            links.add(new Link("cancel", LinkBuilder.resourceMethod(ZduResource.class, "cancelUpgrade", new Object[0])));
        }
        if (cluster.getState().canFinalize()) {
            links.add(new Link("approve", LinkBuilder.resourceMethod(ZduResource.class, "approveUpgrade", new Object[0])));
        }
        if (cluster.getState().canRetry()) {
            links.add(new Link("retryUpgrade", LinkBuilder.resourceMethod(ZduResource.class, "retryFinalization", new Object[0])));
        }
        return links;
    }

    public static List<Link> forClusterNode(NodeInfoDTO node) {
        return Collections.singletonList(new Link("self", LinkBuilder.resourceMethod(ZduResource.class, "getNodeById", node.getId())));
    }

    private static URI resourceMethod(Class<?> resource, String method, Object ... params) {
        return UriBuilder.fromResource(resource).path(resource, method).build(params);
    }
}

