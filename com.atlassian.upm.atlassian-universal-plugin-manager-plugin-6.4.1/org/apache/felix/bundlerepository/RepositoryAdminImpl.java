/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.InvalidSyntaxException
 */
package org.apache.felix.bundlerepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.felix.bundlerepository.LocalRepositoryImpl;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.MapToDictionary;
import org.apache.felix.bundlerepository.RepositoryImpl;
import org.apache.felix.bundlerepository.ResolverImpl;
import org.apache.felix.bundlerepository.ResourceComparator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

public class RepositoryAdminImpl
implements RepositoryAdmin {
    static BundleContext m_context = null;
    private final Logger m_logger;
    private final LocalRepositoryImpl m_local;
    private List m_urlList = new ArrayList();
    private Map m_repoMap = new HashMap();
    private boolean m_initialized = false;
    private Comparator m_nameComparator = new ResourceComparator();
    public static final String REPOSITORY_URL_PROP = "obr.repository.url";
    public static final String EXTERN_REPOSITORY_TAG = "extern-repositories";

    public RepositoryAdminImpl(BundleContext context, Logger logger) {
        m_context = context;
        this.m_logger = logger;
        this.m_local = new LocalRepositoryImpl(context, logger);
    }

    LocalRepositoryImpl getLocalRepository() {
        return this.m_local;
    }

    public void dispose() {
        this.m_local.dispose();
    }

    public Repository addRepository(URL url) throws Exception {
        return this.addRepository(url, Integer.MAX_VALUE);
    }

    public synchronized Repository addRepository(URL url, int hopCount) throws Exception {
        if (!this.m_urlList.contains(url)) {
            this.m_urlList.add(url);
        }
        RepositoryImpl repo = new RepositoryImpl(this, url, hopCount, this.m_logger);
        this.m_repoMap.put(url, repo);
        return repo;
    }

    public synchronized boolean removeRepository(URL url) {
        this.m_repoMap.remove(url);
        return this.m_urlList.remove(url);
    }

    public synchronized Repository[] listRepositories() {
        if (!this.m_initialized) {
            this.initialize();
        }
        return this.m_repoMap.values().toArray(new Repository[this.m_repoMap.size()]);
    }

    public synchronized Resource getResource(String respositoryId) {
        return null;
    }

    public synchronized Resolver resolver() {
        if (!this.m_initialized) {
            this.initialize();
        }
        return new ResolverImpl(m_context, this, this.m_logger);
    }

    public synchronized Resource[] discoverResources(String filterExpr) {
        if (!this.m_initialized) {
            this.initialize();
        }
        Filter filter = null;
        try {
            filter = m_context.createFilter(filterExpr);
        }
        catch (InvalidSyntaxException ex) {
            this.m_logger.log(2, "Error while discovering resources for " + filterExpr, ex);
            return new Resource[0];
        }
        Resource[] resources = null;
        MapToDictionary dict = new MapToDictionary(null);
        Repository[] repos = this.listRepositories();
        ArrayList<Resource> matchList = new ArrayList<Resource>();
        for (int repoIdx = 0; repos != null && repoIdx < repos.length; ++repoIdx) {
            resources = repos[repoIdx].getResources();
            for (int resIdx = 0; resources != null && resIdx < resources.length; ++resIdx) {
                dict.setSourceMap(resources[resIdx].getProperties());
                if (!filter.match((Dictionary)dict)) continue;
                matchList.add(resources[resIdx]);
            }
        }
        resources = matchList.toArray(new Resource[matchList.size()]);
        Arrays.sort(resources, this.m_nameComparator);
        return resources;
    }

    private void initialize() {
        StringTokenizer st;
        String urlStr;
        this.m_initialized = true;
        if (this.m_urlList.size() == 0 && (urlStr = m_context.getProperty(REPOSITORY_URL_PROP)) != null && (st = new StringTokenizer(urlStr)).countTokens() > 0) {
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                try {
                    this.m_urlList.add(new URL(token));
                }
                catch (MalformedURLException ex) {
                    this.m_logger.log(2, "Repository url " + token + " cannot be used. Skipped.", ex);
                }
            }
        }
        this.m_repoMap.clear();
        for (int i = 0; i < this.m_urlList.size(); ++i) {
            URL url = (URL)this.m_urlList.get(i);
            try {
                RepositoryImpl repo = new RepositoryImpl(this, url, this.m_logger);
                if (repo == null) continue;
                this.m_repoMap.put(url, repo);
                continue;
            }
            catch (Exception ex) {
                this.m_logger.log(2, "RepositoryAdminImpl: Exception creating repository " + url.toExternalForm() + ". Repository is skipped.", ex);
            }
        }
    }
}

