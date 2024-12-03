/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.est;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Store;

public class EnrollmentResponse {
    private final Store<X509CertificateHolder> store;
    private final long notBefore;
    private final ESTRequest requestToRetry;
    private final Source source;
    private final PrivateKeyInfo privateKeyInfo;

    public EnrollmentResponse(Store<X509CertificateHolder> store, long notBefore, ESTRequest requestToRetry, Source session) {
        this.store = store;
        this.notBefore = notBefore;
        this.requestToRetry = requestToRetry;
        this.source = session;
        this.privateKeyInfo = null;
    }

    public EnrollmentResponse(Store<X509CertificateHolder> store, long notBefore, ESTRequest requestToRetry, Source session, PrivateKeyInfo privateKeyInfo) {
        this.store = store;
        this.notBefore = notBefore;
        this.requestToRetry = requestToRetry;
        this.source = session;
        this.privateKeyInfo = privateKeyInfo;
    }

    public boolean canRetry() {
        return this.notBefore < System.currentTimeMillis();
    }

    public Store<X509CertificateHolder> getStore() {
        return this.store;
    }

    public long getNotBefore() {
        return this.notBefore;
    }

    public ESTRequest getRequestToRetry() {
        return this.requestToRetry;
    }

    public Object getSession() {
        return this.source.getSession();
    }

    public Source getSource() {
        return this.source;
    }

    public boolean isCompleted() {
        return this.requestToRetry == null;
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }
}

