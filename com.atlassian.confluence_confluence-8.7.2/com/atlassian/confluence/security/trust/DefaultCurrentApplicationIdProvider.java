/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.security.trust.CurrentApplicationIdProvider;
import java.math.BigInteger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

public class DefaultCurrentApplicationIdProvider
implements CurrentApplicationIdProvider {
    private ConfluenceSidManager sidManager;

    @Override
    public String getCurrentApplicationId() {
        String sid;
        try {
            sid = this.sidManager.getSid();
        }
        catch (ConfigurationException e) {
            throw new RuntimeException("Unable to retrieve server id", e);
        }
        return DefaultCurrentApplicationIdProvider.getAliasForSid(sid);
    }

    static String getAliasForSid(String sid) {
        byte[] idHash = ArrayUtils.subarray((byte[])DigestUtils.md5((String)sid), (int)0, (int)3);
        return "confluence:" + new BigInteger(1, idHash).intValue();
    }

    public void setSidManager(ConfluenceSidManager sidManager) {
        this.sidManager = sidManager;
    }
}

