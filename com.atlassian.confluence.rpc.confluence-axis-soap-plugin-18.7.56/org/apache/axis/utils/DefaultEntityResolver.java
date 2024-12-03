/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class DefaultEntityResolver
implements EntityResolver {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$XMLUtils == null ? (class$org$apache$axis$utils$XMLUtils = DefaultEntityResolver.class$("org.apache.axis.utils.XMLUtils")) : class$org$apache$axis$utils$XMLUtils).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$XMLUtils;

    public InputSource resolveEntity(String publicId, String systemId) {
        return XMLUtils.getEmptyInputSource();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

