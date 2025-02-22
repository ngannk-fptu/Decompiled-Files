/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.commitments;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Commitment;
import org.bouncycastle.crypto.Committer;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

public class HashCommitter
implements Committer {
    private final Digest digest;
    private final int byteLength;
    private final SecureRandom random;

    public HashCommitter(ExtendedDigest digest, SecureRandom random) {
        this.digest = digest;
        this.byteLength = digest.getByteLength();
        this.random = random;
    }

    @Override
    public Commitment commit(byte[] message) {
        if (message.length > this.byteLength / 2) {
            throw new DataLengthException("Message to be committed to too large for digest.");
        }
        byte[] w = new byte[this.byteLength - message.length];
        this.random.nextBytes(w);
        return new Commitment(w, this.calculateCommitment(w, message));
    }

    @Override
    public boolean isRevealed(Commitment commitment, byte[] message) {
        if (message.length + commitment.getSecret().length != this.byteLength) {
            throw new DataLengthException("Message and witness secret lengths do not match.");
        }
        byte[] calcCommitment = this.calculateCommitment(commitment.getSecret(), message);
        return Arrays.constantTimeAreEqual(commitment.getCommitment(), calcCommitment);
    }

    private byte[] calculateCommitment(byte[] w, byte[] message) {
        byte[] commitment = new byte[this.digest.getDigestSize()];
        this.digest.update(w, 0, w.length);
        this.digest.update(message, 0, message.length);
        this.digest.doFinal(commitment, 0);
        return commitment;
    }
}

