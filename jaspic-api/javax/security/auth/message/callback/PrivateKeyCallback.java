/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.callback;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import javax.security.auth.callback.Callback;
import javax.security.auth.x500.X500Principal;

public class PrivateKeyCallback
implements Callback {
    private final Request request;
    private Certificate[] chain;
    private PrivateKey key;

    public PrivateKeyCallback(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setKey(PrivateKey key, Certificate[] chain) {
        this.key = key;
        this.chain = chain;
    }

    public PrivateKey getKey() {
        return this.key;
    }

    public Certificate[] getChain() {
        return this.chain;
    }

    public static interface Request {
    }

    public static class IssuerSerialNumRequest
    implements Request {
        private final X500Principal issuer;
        private final BigInteger serialNum;

        public IssuerSerialNumRequest(X500Principal issuer, BigInteger serialNum) {
            this.issuer = issuer;
            this.serialNum = serialNum;
        }

        public X500Principal getIssuer() {
            return this.issuer;
        }

        public BigInteger getSerialNum() {
            return this.serialNum;
        }
    }

    public static class SubjectKeyIDRequest
    implements Request {
        private final byte[] subjectKeyID;

        public SubjectKeyIDRequest(byte[] subjectKeyID) {
            this.subjectKeyID = subjectKeyID;
        }

        public byte[] getSubjectKeyID() {
            return this.subjectKeyID;
        }
    }

    public static class DigestRequest
    implements Request {
        private final byte[] digest;
        private final String algorithm;

        public DigestRequest(byte[] digest, String algorithm) {
            this.digest = digest;
            this.algorithm = algorithm;
        }

        public byte[] getDigest() {
            return this.digest;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }
    }

    public static class AliasRequest
    implements Request {
        private final String alias;

        public AliasRequest(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return this.alias;
        }
    }
}

