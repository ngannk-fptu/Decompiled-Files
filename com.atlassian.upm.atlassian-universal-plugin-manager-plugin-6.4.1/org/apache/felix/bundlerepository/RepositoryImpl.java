/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.Referral;
import org.apache.felix.bundlerepository.RepositoryAdminImpl;
import org.apache.felix.bundlerepository.ResourceComparator;
import org.apache.felix.bundlerepository.ResourceImpl;
import org.apache.felix.bundlerepository.Util;
import org.apache.felix.bundlerepository.metadataparser.XmlCommonHandler;
import org.apache.felix.bundlerepository.metadataparser.kxmlsax.KXml2SAXParser;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Resource;

public class RepositoryImpl
implements Repository {
    private String m_name = null;
    private long m_lastmodified = 0L;
    private URL m_url = null;
    private final Logger m_logger;
    private Resource[] m_resources = null;
    private Referral[] m_referrals = null;
    private RepositoryAdminImpl m_repoAdmin = null;
    private ResourceComparator m_nameComparator = new ResourceComparator();
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$RepositoryImpl;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$ResourceImpl;
    static /* synthetic */ Class class$org$osgi$service$obr$Repository;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$Referral;
    static /* synthetic */ Class class$org$osgi$service$obr$Resource;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$CategoryImpl;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$RequirementImpl;
    static /* synthetic */ Class class$org$osgi$service$obr$Requirement;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$CapabilityImpl;
    static /* synthetic */ Class class$org$osgi$service$obr$Capability;
    static /* synthetic */ Class class$org$apache$felix$bundlerepository$PropertyImpl;
    static /* synthetic */ Class class$java$lang$String;

    public RepositoryImpl(RepositoryAdminImpl repoAdmin, URL url, Logger logger) throws Exception {
        this(repoAdmin, url, Integer.MAX_VALUE, logger);
    }

    public RepositoryImpl(RepositoryAdminImpl repoAdmin, URL url, final int hopCount, Logger logger) throws Exception {
        this.m_repoAdmin = repoAdmin;
        this.m_url = url;
        this.m_logger = logger;
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws Exception {
                    RepositoryImpl.this.parseRepositoryFile(hopCount);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (Exception)ex.getCause();
        }
    }

    public URL getURL() {
        return this.m_url;
    }

    protected void setURL(URL url) {
        this.m_url = url;
    }

    public Resource[] getResources() {
        return this.m_resources;
    }

    public void addResource(Resource resource) {
        ((ResourceImpl)resource).setRepository(this);
        if (this.m_resources == null) {
            this.m_resources = new Resource[]{resource};
        } else {
            Resource[] newResources = new Resource[this.m_resources.length + 1];
            System.arraycopy(this.m_resources, 0, newResources, 0, this.m_resources.length);
            newResources[this.m_resources.length] = resource;
            this.m_resources = newResources;
        }
        Arrays.sort(this.m_resources, this.m_nameComparator);
    }

    public Referral[] getReferrals() {
        return this.m_referrals;
    }

    public void addReferral(Referral referral) throws Exception {
        if (this.m_referrals == null) {
            this.m_referrals = new Referral[]{referral};
        } else {
            Referral[] newResources = new Referral[this.m_referrals.length + 1];
            System.arraycopy(this.m_referrals, 0, newResources, 0, this.m_referrals.length);
            newResources[this.m_referrals.length] = referral;
            this.m_referrals = newResources;
        }
    }

    public String getName() {
        return this.m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public long getLastModified() {
        return this.m_lastmodified;
    }

    public void setLastmodified(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss.SSS");
        try {
            this.m_lastmodified = format.parse(s).getTime();
        }
        catch (ParseException parseException) {
            // empty catch block
        }
    }

    protected Object put(Object key, Object value) {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseRepositoryFile(int hopCount) throws Exception {
        block17: {
            InputStream is = null;
            BufferedReader br = null;
            try {
                URLConnection conn = this.m_url.openConnection();
                String auth = System.getProperty("http.proxyAuth");
                if (auth != null && auth.length() > 0 && ("http".equals(this.m_url.getProtocol()) || "https".equals(this.m_url.getProtocol()))) {
                    String base64 = Util.base64Encode(auth);
                    conn.setRequestProperty("Proxy-Authorization", "Basic " + base64);
                }
                if (this.m_url.getPath().endsWith(".zip")) {
                    ZipInputStream zin = new ZipInputStream(conn.getInputStream());
                    ZipEntry entry = zin.getNextEntry();
                    while (entry != null) {
                        if (entry.getName().equals("repository.xml")) {
                            is = zin;
                            break;
                        }
                        entry = zin.getNextEntry();
                    }
                } else {
                    is = conn.getInputStream();
                }
                if (is != null) {
                    XmlCommonHandler handler = new XmlCommonHandler(this.m_logger);
                    Object factory = new Object(){

                        public RepositoryImpl newInstance() {
                            return RepositoryImpl.this;
                        }
                    };
                    Method repoSetter = (class$org$apache$felix$bundlerepository$RepositoryImpl == null ? (class$org$apache$felix$bundlerepository$RepositoryImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.RepositoryImpl")) : class$org$apache$felix$bundlerepository$RepositoryImpl).getDeclaredMethod("put", class$java$lang$Object == null ? (class$java$lang$Object = RepositoryImpl.class$("java.lang.Object")) : class$java$lang$Object, class$java$lang$Object == null ? (class$java$lang$Object = RepositoryImpl.class$("java.lang.Object")) : class$java$lang$Object);
                    Method resSetter = (class$org$apache$felix$bundlerepository$ResourceImpl == null ? (class$org$apache$felix$bundlerepository$ResourceImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.ResourceImpl")) : class$org$apache$felix$bundlerepository$ResourceImpl).getDeclaredMethod("put", class$java$lang$Object == null ? (class$java$lang$Object = RepositoryImpl.class$("java.lang.Object")) : class$java$lang$Object, class$java$lang$Object == null ? (class$java$lang$Object = RepositoryImpl.class$("java.lang.Object")) : class$java$lang$Object);
                    handler.addType("repository", factory, class$org$osgi$service$obr$Repository == null ? (class$org$osgi$service$obr$Repository = RepositoryImpl.class$("org.osgi.service.obr.Repository")) : class$org$osgi$service$obr$Repository, repoSetter);
                    handler.addType("referral", class$org$apache$felix$bundlerepository$Referral == null ? (class$org$apache$felix$bundlerepository$Referral = RepositoryImpl.class$("org.apache.felix.bundlerepository.Referral")) : class$org$apache$felix$bundlerepository$Referral, null, null);
                    handler.addType("resource", class$org$apache$felix$bundlerepository$ResourceImpl == null ? (class$org$apache$felix$bundlerepository$ResourceImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.ResourceImpl")) : class$org$apache$felix$bundlerepository$ResourceImpl, class$org$osgi$service$obr$Resource == null ? (class$org$osgi$service$obr$Resource = RepositoryImpl.class$("org.osgi.service.obr.Resource")) : class$org$osgi$service$obr$Resource, resSetter);
                    handler.addType("category", class$org$apache$felix$bundlerepository$CategoryImpl == null ? (class$org$apache$felix$bundlerepository$CategoryImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.CategoryImpl")) : class$org$apache$felix$bundlerepository$CategoryImpl, null, null);
                    handler.addType("require", class$org$apache$felix$bundlerepository$RequirementImpl == null ? (class$org$apache$felix$bundlerepository$RequirementImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.RequirementImpl")) : class$org$apache$felix$bundlerepository$RequirementImpl, class$org$osgi$service$obr$Requirement == null ? (class$org$osgi$service$obr$Requirement = RepositoryImpl.class$("org.osgi.service.obr.Requirement")) : class$org$osgi$service$obr$Requirement, null);
                    handler.addType("capability", class$org$apache$felix$bundlerepository$CapabilityImpl == null ? (class$org$apache$felix$bundlerepository$CapabilityImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.CapabilityImpl")) : class$org$apache$felix$bundlerepository$CapabilityImpl, class$org$osgi$service$obr$Capability == null ? (class$org$osgi$service$obr$Capability = RepositoryImpl.class$("org.osgi.service.obr.Capability")) : class$org$osgi$service$obr$Capability, null);
                    handler.addType("p", class$org$apache$felix$bundlerepository$PropertyImpl == null ? (class$org$apache$felix$bundlerepository$PropertyImpl = RepositoryImpl.class$("org.apache.felix.bundlerepository.PropertyImpl")) : class$org$apache$felix$bundlerepository$PropertyImpl, null, null);
                    handler.setDefaultType(class$java$lang$String == null ? (class$java$lang$String = RepositoryImpl.class$("java.lang.String")) : class$java$lang$String, null, null);
                    br = new BufferedReader(new InputStreamReader(is));
                    KXml2SAXParser parser = new KXml2SAXParser(br);
                    parser.parseXML(handler);
                    if (--hopCount > 0 && this.m_referrals != null) {
                        for (int i = 0; i < this.m_referrals.length; ++i) {
                            Referral referral = this.m_referrals[i];
                            URL url = new URL(this.getURL(), referral.getUrl());
                            hopCount = referral.getDepth() > hopCount ? hopCount : referral.getDepth();
                            this.m_repoAdmin.addRepository(url, hopCount);
                        }
                    }
                    break block17;
                }
                throw new Exception("Unable to get input stream for repository.");
            }
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (IOException ex) {}
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

