/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension$Parameter
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.util.List;
import javax.websocket.Extension;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.PerMessageDeflate;
import org.apache.tomcat.websocket.Transformation;

public class TransformationFactory {
    private static final StringManager sm = StringManager.getManager(TransformationFactory.class);
    private static final TransformationFactory factory = new TransformationFactory();

    private TransformationFactory() {
    }

    public static TransformationFactory getInstance() {
        return factory;
    }

    public Transformation create(String name, List<List<Extension.Parameter>> preferences, boolean isServer) {
        if ("permessage-deflate".equals(name)) {
            return PerMessageDeflate.negotiate(preferences, isServer);
        }
        if (Constants.ALLOW_UNSUPPORTED_EXTENSIONS) {
            return null;
        }
        throw new IllegalArgumentException(sm.getString("transformerFactory.unsupportedExtension", new Object[]{name}));
    }
}

