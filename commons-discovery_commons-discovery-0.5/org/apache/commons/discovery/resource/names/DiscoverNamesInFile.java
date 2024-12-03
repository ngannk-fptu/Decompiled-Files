/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DiscoverNamesInFile
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverNamesInFile.class);
    private ResourceDiscover _discoverResources;
    private final String _prefix;
    private final String _suffix;

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public DiscoverNamesInFile() {
        this._discoverResources = new DiscoverResources();
        this._prefix = null;
        this._suffix = null;
    }

    public DiscoverNamesInFile(String prefix, String suffix) {
        this._discoverResources = new DiscoverResources();
        this._prefix = prefix;
        this._suffix = suffix;
    }

    public DiscoverNamesInFile(ClassLoaders loaders) {
        this._discoverResources = new DiscoverResources(loaders);
        this._prefix = null;
        this._suffix = null;
    }

    public DiscoverNamesInFile(ClassLoaders loaders, String prefix, String suffix) {
        this._discoverResources = new DiscoverResources(loaders);
        this._prefix = prefix;
        this._suffix = suffix;
    }

    public DiscoverNamesInFile(ResourceDiscover discoverer) {
        this._discoverResources = discoverer;
        this._prefix = null;
        this._suffix = null;
    }

    public DiscoverNamesInFile(ResourceDiscover discoverer, String prefix, String suffix) {
        this._discoverResources = discoverer;
        this._prefix = prefix;
        this._suffix = suffix;
    }

    public void setDiscoverer(ResourceDiscover discover) {
        this._discoverResources = discover;
    }

    public ResourceDiscover getDiscover() {
        return this._discoverResources;
    }

    @Override
    public ResourceNameIterator findResourceNames(String serviceName) {
        String fileName = this._prefix != null && this._prefix.length() > 0 ? this._prefix + serviceName : serviceName;
        if (this._suffix != null && this._suffix.length() > 0) {
            fileName = fileName + this._suffix;
        }
        if (log.isDebugEnabled()) {
            if (this._prefix != null && this._suffix != null) {
                log.debug((Object)("find: serviceName='" + serviceName + "' as '" + fileName + "'"));
            } else {
                log.debug((Object)("find: serviceName = '" + fileName + "'"));
            }
        }
        final ResourceIterator files = this.getDiscover().findResources(fileName);
        return new ResourceNameIterator(){
            private int idx = 0;
            private List<String> classNames = null;
            private String resource = null;

            @Override
            public boolean hasNext() {
                if (this.resource == null) {
                    this.resource = this.getNextClassName();
                }
                return this.resource != null;
            }

            @Override
            public String nextResourceName() {
                String element = this.resource;
                this.resource = null;
                return element;
            }

            private String getNextClassName() {
                if (this.classNames == null || this.idx >= this.classNames.size()) {
                    this.classNames = this.getNextClassNames();
                    this.idx = 0;
                    if (this.classNames == null) {
                        return null;
                    }
                }
                String className = this.classNames.get(this.idx++);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("getNextClassResource: next class='" + className + "'"));
                }
                return className;
            }

            private List<String> getNextClassNames() {
                while (files.hasNext()) {
                    List results = DiscoverNamesInFile.this.readServices(files.nextResource());
                    if (results == null || results.size() <= 0) continue;
                    return results;
                }
                return null;
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<String> readServices(Resource info) {
        ArrayList<String> results = new ArrayList<String>();
        InputStream is = info.getResourceAsStream();
        if (is != null) {
            try {
                try {
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    try {
                        String serviceImplName;
                        while ((serviceImplName = rd.readLine()) != null) {
                            int idx = serviceImplName.indexOf(35);
                            if (idx >= 0) {
                                serviceImplName = serviceImplName.substring(0, idx);
                            }
                            if ((serviceImplName = serviceImplName.trim()).length() == 0) continue;
                            results.add(serviceImplName);
                        }
                    }
                    finally {
                        rd.close();
                    }
                }
                finally {
                    is.close();
                }
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        return results;
    }
}

