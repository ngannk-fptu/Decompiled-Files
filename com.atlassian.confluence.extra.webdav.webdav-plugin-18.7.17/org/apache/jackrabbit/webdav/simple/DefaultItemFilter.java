/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.webdav.simple.ItemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultItemFilter
implements ItemFilter {
    private static Logger log = LoggerFactory.getLogger(DefaultItemFilter.class);
    private List<String> prefixFilter = new ArrayList<String>();
    private List<String> uriFilter = new ArrayList<String>();
    private List<String> nodetypeFilter = new ArrayList<String>();

    @Override
    public void setFilteredURIs(String[] uris) {
        if (uris != null) {
            for (String uri : uris) {
                this.uriFilter.add(uri);
            }
        }
    }

    @Override
    public void setFilteredPrefixes(String[] prefixes) {
        if (prefixes != null) {
            for (String prefix : prefixes) {
                this.prefixFilter.add(prefix);
            }
        }
    }

    @Override
    public void setFilteredNodetypes(String[] nodetypeNames) {
        if (nodetypeNames != null) {
            for (String nodetypeName : nodetypeNames) {
                this.nodetypeFilter.add(nodetypeName);
            }
        }
    }

    @Override
    public boolean isFilteredItem(Item item) {
        return this.isFilteredNamespace(item) || this.isFilteredNodeType(item);
    }

    @Override
    public boolean isFilteredItem(String displayName, Session session) {
        return this.isFilteredNamespace(displayName, session);
    }

    private boolean isFilteredNamespace(String name, Session session) {
        if (this.prefixFilter.isEmpty() && this.uriFilter.isEmpty()) {
            return false;
        }
        int pos = name.indexOf(58);
        if (pos < 0) {
            return false;
        }
        try {
            String prefix = name.substring(0, pos);
            String uri = session.getNamespaceURI(prefix);
            return this.prefixFilter.contains(prefix) || this.uriFilter.contains(uri);
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    private boolean isFilteredNamespace(Item item) {
        try {
            return this.isFilteredNamespace(item.getName(), item.getSession());
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    private boolean isFilteredNodeType(Item item) {
        if (this.nodetypeFilter.isEmpty()) {
            return false;
        }
        try {
            String ntName = item.isNode() ? ((Node)item).getDefinition().getDeclaringNodeType().getName() : ((Property)item).getDefinition().getDeclaringNodeType().getName();
            return this.nodetypeFilter.contains(ntName);
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }
}

