/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge
 *  org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.mozilla;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;

public class SignedPublicKeyAndChallenge
implements Encodable {
    protected final org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge spkacSeq;

    public SignedPublicKeyAndChallenge(byte[] bytes) {
        this.spkacSeq = org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge.getInstance((Object)bytes);
    }

    protected SignedPublicKeyAndChallenge(org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge struct) {
        this.spkacSeq = struct;
    }

    public org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge toASN1Structure() {
        return this.spkacSeq;
    }

    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge();
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws OperatorCreationException, IOException {
        ContentVerifier verifier = verifierProvider.get(this.spkacSeq.getSignatureAlgorithm());
        OutputStream sOut = verifier.getOutputStream();
        this.spkacSeq.getPublicKeyAndChallenge().encodeTo(sOut, "DER");
        sOut.close();
        return verifier.verify(this.spkacSeq.getSignature().getOctets());
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
    }

    public String getChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge().getChallengeIA5().getString();
    }

    public byte[] getEncoded() throws IOException {
        return this.toASN1Structure().getEncoded();
    }
}

