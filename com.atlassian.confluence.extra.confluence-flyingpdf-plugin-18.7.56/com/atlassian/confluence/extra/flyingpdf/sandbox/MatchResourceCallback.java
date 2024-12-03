/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxCallbackContext
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchResourceCallback
implements SandboxCallback<String, Boolean> {
    private static final Logger log = LoggerFactory.getLogger(MatchResourceCallback.class);

    public Boolean apply(SandboxCallbackContext context, String uri) {
        Optional downloadResourceManager = context.get(DownloadResourceManager.class);
        if (!downloadResourceManager.isPresent()) {
            log.error("DownloadResourceManager is not registered in callback context");
            return false;
        }
        return ((DownloadResourceManager)downloadResourceManager.get()).matches(uri);
    }

    public SandboxSerializer<String> inputSerializer() {
        return SandboxSerializers.stringSerializer();
    }

    public SandboxSerializer<Boolean> outputSerializer() {
        return new SandboxSerializer<Boolean>(){

            public byte[] serialize(Boolean status) {
                byte[] byArray;
                if (status.booleanValue()) {
                    byte[] byArray2 = new byte[1];
                    byArray = byArray2;
                    byArray2[0] = 1;
                } else {
                    byte[] byArray3 = new byte[1];
                    byArray = byArray3;
                    byArray3[0] = 0;
                }
                return byArray;
            }

            public Boolean deserialize(byte[] bytes) {
                return bytes[0] == 1 ? Boolean.TRUE : Boolean.FALSE;
            }
        };
    }
}

