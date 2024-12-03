/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.http11.Http11Nio2Protocol
 */
package com.atlassian.secrets.tomcat.protocol;

import com.atlassian.secrets.tomcat.protocol.ProductTomcatProtocolWithPasswordEncryption;
import com.atlassian.secrets.tomcat.utils.DecryptionUtils;
import com.atlassian.secrets.tomcat.utils.PasswordDataBean;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.coyote.http11.Http11Nio2Protocol;

public class Http11Nio2ProtocolWithPasswordEncryption
extends Http11Nio2Protocol
implements ProductTomcatProtocolWithPasswordEncryption {
    private Optional<String> productEncryptionKey = Optional.empty();
    private final Set<PasswordDataBean> passwordsToSet = new HashSet<PasswordDataBean>();

    public void init() throws Exception {
        DecryptionUtils.initPasswords(this.passwordsToSet, this.productEncryptionKey);
        super.init();
    }

    public void setKeystorePass(String certificateKeystorePassword) {
        this.passwordsToSet.add(new PasswordDataBean(certificateKeystorePassword, x$0 -> super.setKeystorePass(x$0), "KeystorePass"));
    }

    public void setKeyPass(String certificateKeyPassword) {
        this.passwordsToSet.add(new PasswordDataBean(certificateKeyPassword, x$0 -> super.setKeyPass(x$0), "KeyPass"));
    }

    public void setSSLPassword(String certificateKeyPassword) {
        this.passwordsToSet.add(new PasswordDataBean(certificateKeyPassword, x$0 -> super.setSSLPassword(x$0), "SSLPassword"));
    }

    public void setTruststorePass(String truststorePassword) {
        this.passwordsToSet.add(new PasswordDataBean(truststorePassword, x$0 -> super.setTruststorePass(x$0), "TrustsorePass"));
    }

    @Override
    public void setProductEncryptionKey(String productEncryptionKey) {
        this.productEncryptionKey = Optional.of(productEncryptionKey);
    }
}

