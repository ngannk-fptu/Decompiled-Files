/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.pqc.crypto.xmss.KeyedHashFunctions;
import org.bouncycastle.pqc.crypto.xmss.OTSHashAddress;
import org.bouncycastle.pqc.crypto.xmss.WOTSPlus;
import org.bouncycastle.pqc.crypto.xmss.WOTSPlusSignature;
import org.bouncycastle.pqc.crypto.xmss.XMSSNode;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSSignature;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.pqc.crypto.xmss.XMSSVerifierUtil;
import org.bouncycastle.util.Arrays;

public class XMSSSigner
implements StateAwareMessageSigner {
    private XMSSPrivateKeyParameters privateKey;
    private XMSSPublicKeyParameters publicKey;
    private XMSSParameters params;
    private WOTSPlus wotsPlus;
    private KeyedHashFunctions khf;
    private boolean initSign;
    private boolean hasGenerated;

    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            this.initSign = true;
            this.hasGenerated = false;
            this.privateKey = (XMSSPrivateKeyParameters)cipherParameters;
            this.params = this.privateKey.getParameters();
        } else {
            this.initSign = false;
            this.publicKey = (XMSSPublicKeyParameters)cipherParameters;
            this.params = this.publicKey.getParameters();
        }
        this.wotsPlus = this.params.getWOTSPlus();
        this.khf = this.wotsPlus.getKhf();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] generateSignature(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("message == null");
        }
        if (this.initSign) {
            if (this.privateKey == null) {
                throw new IllegalStateException("signing key no longer usable");
            }
        } else {
            throw new IllegalStateException("signer not initialized for signature generation");
        }
        XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.privateKey;
        synchronized (xMSSPrivateKeyParameters) {
            byte[] byArray2;
            if (this.privateKey.getUsagesRemaining() <= 0L) {
                throw new ExhaustedPrivateKeyException("no usages of private key remaining");
            }
            if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty()) {
                throw new IllegalStateException("not initialized");
            }
            try {
                int n = this.privateKey.getIndex();
                this.hasGenerated = true;
                byte[] byArray3 = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(n, 32));
                byte[] byArray4 = Arrays.concatenate(byArray3, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(n, this.params.getTreeDigestSize()));
                byte[] byArray5 = this.khf.HMsg(byArray4, byArray);
                OTSHashAddress oTSHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(n).build();
                WOTSPlusSignature wOTSPlusSignature = this.wotsSign(byArray5, oTSHashAddress);
                byArray2 = new XMSSSignature.Builder(this.params).withIndex(n).withRandom(byArray3).withWOTSPlusSignature(wOTSPlusSignature).withAuthPath(this.privateKey.getBDSState().getAuthenticationPath()).build().toByteArray();
                this.privateKey.getBDSState().markUsed();
                this.privateKey.rollKey();
            }
            catch (Throwable throwable) {
                this.privateKey.getBDSState().markUsed();
                this.privateKey.rollKey();
                throw throwable;
            }
            return byArray2;
        }
    }

    public long getUsagesRemaining() {
        return this.privateKey.getUsagesRemaining();
    }

    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        XMSSSignature xMSSSignature = new XMSSSignature.Builder(this.params).withSignature(byArray2).build();
        int n = xMSSSignature.getIndex();
        this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.publicKey.getPublicSeed());
        byte[] byArray3 = Arrays.concatenate(xMSSSignature.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(n, this.params.getTreeDigestSize()));
        byte[] byArray4 = this.khf.HMsg(byArray3, byArray);
        int n2 = this.params.getHeight();
        int n3 = XMSSUtil.getLeafIndex(n, n2);
        OTSHashAddress oTSHashAddress = (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(n).build();
        XMSSNode xMSSNode = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, n2, byArray4, xMSSSignature, oTSHashAddress, n3);
        return Arrays.constantTimeAreEqual(xMSSNode.getValue(), this.publicKey.getRoot());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.privateKey;
        synchronized (xMSSPrivateKeyParameters) {
            if (this.hasGenerated) {
                XMSSPrivateKeyParameters xMSSPrivateKeyParameters2 = this.privateKey;
                this.privateKey = null;
                return xMSSPrivateKeyParameters2;
            }
            XMSSPrivateKeyParameters xMSSPrivateKeyParameters3 = this.privateKey;
            if (xMSSPrivateKeyParameters3 != null) {
                this.privateKey = this.privateKey.getNextKey();
            }
            return xMSSPrivateKeyParameters3;
        }
    }

    private WOTSPlusSignature wotsSign(byte[] byArray, OTSHashAddress oTSHashAddress) {
        if (byArray.length != this.params.getTreeDigestSize()) {
            throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
        }
        if (oTSHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        }
        this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), oTSHashAddress), this.privateKey.getPublicSeed());
        return this.wotsPlus.sign(byArray, oTSHashAddress);
    }
}

