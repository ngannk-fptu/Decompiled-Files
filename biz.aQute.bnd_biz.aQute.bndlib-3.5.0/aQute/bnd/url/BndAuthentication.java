/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.url;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.url.DefaultURLConnectionHandler;
import aQute.lib.base64.Base64;
import aQute.lib.hex.Hex;
import aQute.lib.settings.Settings;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BndPlugin(name="url.bnd.authentication", parameters=Config.class)
public class BndAuthentication
extends DefaultURLConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(BndAuthentication.class);
    private static final String MACHINE = "machine";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PUBLIC_KEY = "publicKey";
    private static final String EMAIL = "email";
    private static SimpleDateFormat httpFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    private static final String X_A_QUTE_AUTHORIZATION = "X-aQute-Authorization";
    private String identity;
    private String email;
    private String machine;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handle(URLConnection connection) throws Exception {
        if (!(connection instanceof HttpURLConnection) || !this.matches(connection)) {
            return;
        }
        if (!(connection instanceof HttpsURLConnection)) {
            logger.debug("bnd authentication should only be used with https: {}", (Object)connection.getURL());
        }
        this.init();
        StringBuilder sb = new StringBuilder(this.identity);
        String dateHeader = connection.getRequestProperty("Date");
        if (dateHeader == null) {
            SimpleDateFormat simpleDateFormat = httpFormat;
            synchronized (simpleDateFormat) {
                dateHeader = httpFormat.format(new Date());
            }
            connection.setRequestProperty("Date", dateHeader);
        }
        Signature hmac = Signature.getInstance("SHA1withRSA");
        hmac.initSign(this.privateKey);
        hmac.update(dateHeader.getBytes());
        sb.append(Base64.encodeBase64(hmac.sign()));
        connection.setRequestProperty(X_A_QUTE_AUTHORIZATION, sb.toString());
    }

    private synchronized void init() throws UnknownHostException {
        if (this.identity != null) {
            return;
        }
        this.machine = InetAddress.getLocalHost().getHostName();
        StringBuilder sb = new StringBuilder();
        sb.append(this.email).append("!");
        if (this.machine != null) {
            sb.append(this.machine);
        }
        sb.append("!").append(Base64.encodeBase64(this.publicKey.getEncoded())).append(":");
        this.identity = sb.toString();
    }

    @Override
    public void setProperties(Map<String, String> map) throws Exception {
        super.setProperties(map);
        String email = map.get(EMAIL);
        if (email == null) {
            Settings settings = this.registry.getPlugin(Settings.class);
            email = settings.getEmail();
            if (email == null) {
                this.error("The bnd authentication URL connection handler has no email set as property, nor have the bnd settings been set", new Object[0]);
                return;
            }
            this.credentials(email, settings.getPublicKey(), settings.getPrivateKey());
        } else {
            String pub = map.get(PUBLIC_KEY);
            String prv = map.get(PRIVATE_KEY);
            if (pub == null || !Hex.isHex(pub)) {
                this.error("The bnd authentication URL public key for email %s is not a hex string %s", email, pub);
                return;
            }
            if (prv == null || !Hex.isHex(prv)) {
                this.error("The bnd authentication URL private key for email %s is not a hex string", email);
                return;
            }
            this.credentials(email, Hex.toByteArray(pub), Hex.toByteArray(prv));
        }
        this.machine = map.get(MACHINE);
    }

    private void credentials(String email, byte[] publicKey, byte[] privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.email = email;
        if (publicKey != null && privateKey != null) {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
        }
    }

    static {
        httpFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    static interface Config
    extends DefaultURLConnectionHandler.Config {
        public String machine();

        public byte[] privateKey();

        public byte[] publicKey();

        public String email();
    }
}

