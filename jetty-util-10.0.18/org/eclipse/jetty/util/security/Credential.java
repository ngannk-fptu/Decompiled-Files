/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.security;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.security.CredentialProvider;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.security.UnixCrypt;
import org.eclipse.jetty.util.thread.AutoLock;

public abstract class Credential
implements Serializable {
    private static final long serialVersionUID = -7760551052768181572L;
    private static final List<CredentialProvider> CREDENTIAL_PROVIDERS = ServiceLoader.load(CredentialProvider.class).stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());

    public abstract boolean check(Object var1);

    public static Credential getCredential(String credential) {
        if (credential.startsWith("CRYPT:")) {
            return new Crypt(credential);
        }
        if (credential.startsWith("MD5:")) {
            return new MD5(credential);
        }
        for (CredentialProvider cp : CREDENTIAL_PROVIDERS) {
            Credential credentialObj;
            if (!credential.startsWith(cp.getPrefix()) || (credentialObj = cp.getCredential(credential)) == null) continue;
            return credentialObj;
        }
        return new Password(credential);
    }

    protected static boolean stringEquals(String known, String unknown) {
        boolean sameObject;
        boolean bl = sameObject = known == unknown;
        if (sameObject) {
            return true;
        }
        if (known == null || unknown == null) {
            return false;
        }
        boolean result = true;
        int l1 = known.length();
        int l2 = unknown.length();
        for (int i = 0; i < l2; ++i) {
            result &= (l1 == 0 ? unknown.charAt(l2 - i - 1) : known.charAt(i % l1)) == unknown.charAt(i);
        }
        return result && l1 == l2;
    }

    protected static boolean byteEquals(byte[] known, byte[] unknown) {
        if (known == unknown) {
            return true;
        }
        if (known == null || unknown == null) {
            return false;
        }
        boolean result = true;
        int l1 = known.length;
        int l2 = unknown.length;
        for (int i = 0; i < l2; ++i) {
            result &= (l1 == 0 ? unknown[l2 - i - 1] : known[i % l1]) == unknown[i];
        }
        return result && l1 == l2;
    }

    public static class Crypt
    extends Credential {
        private static final long serialVersionUID = -2027792997664744210L;
        private static final String __TYPE = "CRYPT:";
        private final String _cooked;

        Crypt(String cooked) {
            this._cooked = cooked.startsWith(__TYPE) ? cooked.substring(__TYPE.length()) : cooked;
        }

        @Override
        public boolean check(Object credentials) {
            if (credentials instanceof char[]) {
                credentials = new String((char[])credentials);
            }
            return Crypt.stringEquals(this._cooked, UnixCrypt.crypt(credentials.toString(), this._cooked));
        }

        public boolean equals(Object credential) {
            if (!(credential instanceof Crypt)) {
                return false;
            }
            Crypt c = (Crypt)credential;
            return Crypt.stringEquals(this._cooked, c._cooked);
        }

        public static String crypt(String user, String pw) {
            return __TYPE + UnixCrypt.crypt(pw, user);
        }
    }

    public static class MD5
    extends Credential {
        private static final long serialVersionUID = 5533846540822684240L;
        private static final String __TYPE = "MD5:";
        private static final AutoLock __md5Lock = new AutoLock();
        private static MessageDigest __md;
        private final byte[] _digest;

        MD5(String digest) {
            digest = digest.startsWith(__TYPE) ? digest.substring(__TYPE.length()) : digest;
            this._digest = StringUtil.fromHexString(digest);
        }

        public byte[] getDigest() {
            return this._digest;
        }

        @Override
        public boolean check(Object credentials) {
            try {
                if (credentials instanceof char[]) {
                    credentials = new String((char[])credentials);
                }
                if (credentials instanceof Password || credentials instanceof String) {
                    byte[] digest;
                    try (AutoLock l = __md5Lock.lock();){
                        if (__md == null) {
                            __md = MessageDigest.getInstance("MD5");
                        }
                        __md.reset();
                        __md.update(credentials.toString().getBytes(StandardCharsets.ISO_8859_1));
                        digest = __md.digest();
                    }
                    return MD5.byteEquals(this._digest, digest);
                }
                if (credentials instanceof MD5) {
                    return this.equals(credentials);
                }
                if (credentials instanceof Credential) {
                    return ((Credential)credentials).check(this);
                }
                return false;
            }
            catch (Exception e) {
                return false;
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof MD5) {
                return MD5.byteEquals(this._digest, ((MD5)obj)._digest);
            }
            return false;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public static String digest(String password) {
            try {
                AutoLock l = __md5Lock.lock();
                try {
                    if (__md == null) {
                        try {
                            __md = MessageDigest.getInstance("MD5");
                        }
                        catch (Exception e) {
                            System.err.println("Unable to access MD5 message digest");
                            e.printStackTrace();
                            String string = null;
                            if (l == null) return string;
                            l.close();
                            return string;
                        }
                    }
                    __md.reset();
                    __md.update(password.getBytes(StandardCharsets.ISO_8859_1));
                    byte[] digest = __md.digest();
                    return __TYPE + StringUtil.toHexString(digest);
                }
                finally {
                    if (l != null) {
                        try {
                            l.close();
                        }
                        catch (Throwable throwable) {
                            Throwable throwable2;
                            throwable2.addSuppressed(throwable);
                        }
                    }
                }
            }
            catch (Exception e) {
                System.err.println("Message Digest Failure");
                e.printStackTrace();
                return null;
            }
        }
    }
}

