/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.Vote
 *  com.atlassian.gadgets.directory.Directory
 *  com.atlassian.gadgets.opensocial.spi.GadgetSpecUrlRenderPermission
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.Vote;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.opensocial.spi.GadgetSpecUrlRenderPermission;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={GadgetSpecUrlRenderPermission.class})
public class GadgetSpecUrlRenderPermissionImpl
implements GadgetSpecUrlRenderPermission {
    private final Directory directory;

    @Autowired
    public GadgetSpecUrlRenderPermissionImpl(Directory directory) {
        this.directory = directory;
    }

    public Vote voteOn(String gadgetSpecUri) {
        if (Uri.isValid((String)gadgetSpecUri) && this.directory.contains(URI.create(gadgetSpecUri))) {
            return Vote.ALLOW;
        }
        return Vote.PASS;
    }
}

