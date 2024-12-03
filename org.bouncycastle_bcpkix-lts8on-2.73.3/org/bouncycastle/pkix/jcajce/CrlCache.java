/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.PKIXCRLStore
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Iterable
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.pkix.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import org.bouncycastle.jcajce.PKIXCRLStore;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

class CrlCache {
    private static final int DEFAULT_TIMEOUT = 15000;
    private static Map<URI, WeakReference<PKIXCRLStore>> cache = Collections.synchronizedMap(new WeakHashMap());

    CrlCache() {
    }

    static synchronized PKIXCRLStore getCrl(CertificateFactory certFact, Date validDate, URI distributionPoint) throws IOException, CRLException {
        PKIXCRLStore crlStore = null;
        WeakReference<PKIXCRLStore> markerRef = cache.get(distributionPoint);
        if (markerRef != null) {
            crlStore = (PKIXCRLStore)markerRef.get();
        }
        if (crlStore != null) {
            boolean isExpired = false;
            for (X509CRL crl : crlStore.getMatches(null)) {
                Date nextUpdate = crl.getNextUpdate();
                if (nextUpdate == null || !nextUpdate.before(validDate)) continue;
                isExpired = true;
                break;
            }
            if (!isExpired) {
                return crlStore;
            }
        }
        Collection crls = distributionPoint.getScheme().equals("ldap") ? CrlCache.getCrlsFromLDAP(certFact, distributionPoint) : CrlCache.getCrls(certFact, distributionPoint);
        LocalCRLStore localCRLStore = new LocalCRLStore((Store<CRL>)new CollectionStore(crls));
        cache.put(distributionPoint, new WeakReference(localCRLStore));
        return localCRLStore;
    }

    private static Collection getCrlsFromLDAP(CertificateFactory certFact, URI distributionPoint) throws IOException, CRLException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", distributionPoint.toString());
        byte[] val = null;
        try {
            InitialDirContext ctx = new InitialDirContext(env);
            Attributes avals = ctx.getAttributes("");
            Attribute aval = avals.get("certificateRevocationList;binary");
            val = (byte[])aval.get();
        }
        catch (NamingException e) {
            throw new CRLException("issue connecting to: " + distributionPoint.toString(), e);
        }
        if (val == null || val.length == 0) {
            throw new CRLException("no CRL returned from: " + distributionPoint);
        }
        return certFact.generateCRLs(new ByteArrayInputStream(val));
    }

    private static Collection getCrls(CertificateFactory certFact, URI distributionPoint) throws IOException, CRLException {
        HttpURLConnection crlCon = (HttpURLConnection)distributionPoint.toURL().openConnection();
        crlCon.setConnectTimeout(15000);
        crlCon.setReadTimeout(15000);
        InputStream crlIn = crlCon.getInputStream();
        Collection<? extends CRL> crls = certFact.generateCRLs(crlIn);
        crlIn.close();
        return crls;
    }

    private static class LocalCRLStore<T extends CRL>
    implements PKIXCRLStore,
    Iterable<CRL> {
        private Collection<CRL> _local;

        public LocalCRLStore(Store<CRL> collection) {
            this._local = new ArrayList<CRL>(collection.getMatches(null));
        }

        public Collection getMatches(Selector selector) {
            if (selector == null) {
                return new ArrayList<CRL>(this._local);
            }
            ArrayList<CRL> col = new ArrayList<CRL>();
            for (CRL obj : this._local) {
                if (!selector.match((Object)obj)) continue;
                col.add(obj);
            }
            return col;
        }

        public Iterator<CRL> iterator() {
            return this.getMatches(null).iterator();
        }
    }
}

