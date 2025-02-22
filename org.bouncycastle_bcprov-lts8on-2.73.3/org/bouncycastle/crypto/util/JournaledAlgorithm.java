/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.util.JournalingSecureRandom;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

public class JournaledAlgorithm
implements Encodable,
Serializable {
    private transient JournalingSecureRandom journaling;
    private transient AlgorithmIdentifier algID;

    public JournaledAlgorithm(AlgorithmIdentifier aid, JournalingSecureRandom journaling) {
        if (aid == null) {
            throw new NullPointerException("AlgorithmIdentifier passed to JournaledAlgorithm is null");
        }
        if (journaling == null) {
            throw new NullPointerException("JournalingSecureRandom passed to JournaledAlgorithm is null");
        }
        this.journaling = journaling;
        this.algID = aid;
    }

    public JournaledAlgorithm(byte[] encoding) {
        this(encoding, CryptoServicesRegistrar.getSecureRandom());
    }

    public JournaledAlgorithm(byte[] encoding, SecureRandom random) {
        if (encoding == null) {
            throw new NullPointerException("encoding passed to JournaledAlgorithm is null");
        }
        if (random == null) {
            throw new NullPointerException("random passed to JournaledAlgorithm is null");
        }
        this.initFromEncoding(encoding, random);
    }

    private void initFromEncoding(byte[] encoding, SecureRandom random) {
        ASN1Sequence seq = ASN1Sequence.getInstance(encoding);
        this.algID = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.journaling = new JournalingSecureRandom(ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets(), random);
    }

    public JournalingSecureRandom getJournalingSecureRandom() {
        return this.journaling;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algID;
    }

    public void storeState(File tempfile) throws IOException {
        if (tempfile == null) {
            throw new NullPointerException("file for storage is null in JournaledAlgorithm");
        }
        try (FileOutputStream fOut = new FileOutputStream(tempfile);){
            this.storeState(fOut);
        }
    }

    public void storeState(OutputStream out) throws IOException {
        if (out == null) {
            throw new NullPointerException("output stream for storage is null in JournaledAlgorithm");
        }
        out.write(this.getEncoded());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JournaledAlgorithm getState(InputStream stateIn, SecureRandom random) throws IOException, ClassNotFoundException {
        if (stateIn == null) {
            throw new NullPointerException("stream for loading is null in JournaledAlgorithm");
        }
        try (BufferedInputStream fIn = new BufferedInputStream(stateIn);){
            JournaledAlgorithm journaledAlgorithm = new JournaledAlgorithm(Streams.readAll(fIn), random);
            return journaledAlgorithm;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static JournaledAlgorithm getState(File tempfile, SecureRandom random) throws IOException, ClassNotFoundException {
        if (tempfile == null) {
            throw new NullPointerException("File for loading is null in JournaledAlgorithm");
        }
        try (BufferedInputStream fIn = new BufferedInputStream(new FileInputStream(tempfile));){
            JournaledAlgorithm journaledAlgorithm = new JournaledAlgorithm(Streams.readAll(fIn), random);
            return journaledAlgorithm;
        }
    }

    @Override
    public byte[] getEncoded() throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.algID);
        v.add(new DEROctetString(this.journaling.getFullTranscript()));
        return new DERSequence(v).getEncoded();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.initFromEncoding((byte[])in.readObject(), CryptoServicesRegistrar.getSecureRandom());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getEncoded());
    }
}

