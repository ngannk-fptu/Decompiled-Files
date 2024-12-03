/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.ajp.AjpAprProtocol
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package com.atlassian.secrets.tomcat.protocol;

import com.atlassian.secrets.tomcat.protocol.ProductTomcatProtocolWithPasswordEncryption;
import com.atlassian.secrets.tomcat.utils.DecryptionUtils;
import com.atlassian.secrets.tomcat.utils.PasswordDataBean;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.coyote.ajp.AjpAprProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class AjpAprProtocolWithPasswordEncryption
extends AjpAprProtocol
implements ProductTomcatProtocolWithPasswordEncryption {
    private static final Log log = LogFactory.getLog(AjpAprProtocolWithPasswordEncryption.class);
    private Optional<String> productEncryptionKey = Optional.empty();
    private Set<PasswordDataBean> passwordsToSet = new HashSet<PasswordDataBean>();

    public void init() throws Exception {
        DecryptionUtils.initPasswords(this.passwordsToSet, this.productEncryptionKey);
        super.init();
    }

    public void setSecret(String secret) {
        this.passwordsToSet.add(new PasswordDataBean(secret, x$0 -> super.setSecret(x$0), "secret"));
    }

    @Override
    public void setProductEncryptionKey(String productEncryptionKey) {
        this.productEncryptionKey = Optional.of(productEncryptionKey);
    }
}

