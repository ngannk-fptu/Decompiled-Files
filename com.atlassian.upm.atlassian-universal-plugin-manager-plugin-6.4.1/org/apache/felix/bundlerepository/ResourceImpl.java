/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.felix.bundlerepository.CategoryImpl;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resource;

public class ResourceImpl
implements Resource {
    private final String URI = "uri";
    private Repository m_repo = null;
    private Map m_map = null;
    private List m_catList = new ArrayList();
    private List m_capList = new ArrayList();
    private List m_reqList = new ArrayList();
    private String m_resourceURI = null;
    private String m_docURI = null;
    private String m_licenseURI = null;
    private String m_sourceURI = null;
    private String m_javadocURI = null;
    private boolean m_converted = false;

    public ResourceImpl() {
        this(null);
    }

    public ResourceImpl(ResourceImpl resource) {
        this.m_map = new TreeMap(new Comparator(){

            public int compare(Object o1, Object o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        if (resource != null) {
            this.m_map.putAll(resource.getProperties());
            this.m_catList.addAll(resource.m_catList);
            this.m_capList.addAll(resource.m_capList);
            this.m_reqList.addAll(resource.m_reqList);
        }
    }

    public boolean equals(Object o) {
        if (this.getSymbolicName() == null || this.getVersion() == null) {
            return this == o;
        }
        if (o instanceof Resource) {
            return this.getSymbolicName().equals(((Resource)o).getSymbolicName()) && this.getVersion().equals((Object)((Resource)o).getVersion());
        }
        return false;
    }

    public int hashCode() {
        if (this.getSymbolicName() == null || this.getVersion() == null) {
            return super.hashCode();
        }
        return this.getSymbolicName().hashCode() ^ this.getVersion().hashCode();
    }

    public Map getProperties() {
        if (!this.m_converted) {
            this.convertURItoURL();
        }
        return this.m_map;
    }

    public String getPresentationName() {
        return (String)this.m_map.get("presentationname");
    }

    public String getSymbolicName() {
        return (String)this.m_map.get("symbolicname");
    }

    public String getId() {
        return (String)this.m_map.get("id");
    }

    public Version getVersion() {
        Version v = (Version)this.m_map.get("version");
        v = v == null ? Version.emptyVersion : v;
        return v;
    }

    public URL getURL() {
        if (!this.m_converted) {
            this.convertURItoURL();
        }
        return (URL)this.m_map.get("url");
    }

    public Requirement[] getRequirements() {
        return this.m_reqList.toArray(new Requirement[this.m_reqList.size()]);
    }

    protected void addRequire(Requirement req) {
        this.m_reqList.add(req);
    }

    public Capability[] getCapabilities() {
        return this.m_capList.toArray(new Capability[this.m_capList.size()]);
    }

    protected void addCapability(Capability cap) {
        this.m_capList.add(cap);
    }

    public String[] getCategories() {
        return this.m_catList.toArray(new String[this.m_catList.size()]);
    }

    protected void addCategory(CategoryImpl cat) {
        this.m_catList.add(cat.getId());
    }

    public Repository getRepository() {
        return this.m_repo;
    }

    protected void setRepository(Repository repo) {
        this.m_repo = repo;
    }

    protected Object put(Object key, Object value) {
        if (key.equals("license")) {
            this.m_licenseURI = (String)value;
        } else if (key.equals("documentation")) {
            this.m_docURI = (String)value;
        } else if (key.equals("source")) {
            this.m_sourceURI = (String)value;
        } else if (key.equals("javadoc")) {
            this.m_javadocURI = (String)value;
        } else if (key.equals("uri")) {
            this.m_resourceURI = (String)value;
        } else {
            if (key.equals("version")) {
                value = new Version(value.toString());
            } else if (key.equals("size")) {
                value = Long.valueOf(value.toString());
            }
            return this.m_map.put(key, value);
        }
        return null;
    }

    private void convertURItoURL() {
        if (this.m_repo != null) {
            try {
                URL base = this.m_repo.getURL();
                if (this.m_resourceURI != null) {
                    this.m_map.put("url", new URL(base, this.m_resourceURI));
                }
                if (this.m_docURI != null) {
                    this.m_map.put("documentation", new URL(base, this.m_docURI));
                }
                if (this.m_licenseURI != null) {
                    this.m_map.put("license", new URL(base, this.m_licenseURI));
                }
                if (this.m_sourceURI != null) {
                    this.m_map.put("source", new URL(base, this.m_sourceURI));
                }
                if (this.m_javadocURI != null) {
                    this.m_map.put("javadoc", new URL(base, this.m_javadocURI));
                }
                this.m_converted = true;
            }
            catch (MalformedURLException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}

