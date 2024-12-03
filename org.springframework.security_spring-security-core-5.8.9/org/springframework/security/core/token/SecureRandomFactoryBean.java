/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.core.io.Resource
 *  org.springframework.util.Assert
 *  org.springframework.util.FileCopyUtils
 */
package org.springframework.security.core.token;

import java.io.InputStream;
import java.security.SecureRandom;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

public class SecureRandomFactoryBean
implements FactoryBean<SecureRandom> {
    private String algorithm = "SHA1PRNG";
    private Resource seed;

    public SecureRandom getObject() throws Exception {
        SecureRandom random = SecureRandom.getInstance(this.algorithm);
        random.nextBytes(new byte[1]);
        if (this.seed != null) {
            byte[] seedBytes = FileCopyUtils.copyToByteArray((InputStream)this.seed.getInputStream());
            random.setSeed(seedBytes);
        }
        return random;
    }

    public Class<SecureRandom> getObjectType() {
        return SecureRandom.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setAlgorithm(String algorithm) {
        Assert.hasText((String)algorithm, (String)"Algorithm required");
        this.algorithm = algorithm;
    }

    public void setSeed(Resource seed) {
        this.seed = seed;
    }
}

