/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.SchemaOutputResolver
 */
package com.sun.xml.bind.v2.schemagen;

import com.sun.xml.bind.Util;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;

final class FoolProofResolver
extends SchemaOutputResolver {
    private static final Logger logger = Util.getClassLogger();
    private final SchemaOutputResolver resolver;

    public FoolProofResolver(SchemaOutputResolver resolver) {
        assert (resolver != null);
        this.resolver = resolver;
    }

    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        logger.entering(((Object)((Object)this)).getClass().getName(), "createOutput", new Object[]{namespaceUri, suggestedFileName});
        Result r = this.resolver.createOutput(namespaceUri, suggestedFileName);
        if (r != null) {
            String sysId = r.getSystemId();
            logger.finer("system ID = " + sysId);
            if (sysId == null) {
                throw new AssertionError((Object)"system ID cannot be null");
            }
        }
        logger.exiting(((Object)((Object)this)).getClass().getName(), "createOutput", r);
        return r;
    }
}

