/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 */
package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public abstract class XercesBridge {
    private static final XercesBridge instance = XercesBridge.makeInstance();

    public static XercesBridge getInstance() {
        return instance;
    }

    private static XercesBridge makeInstance() {
        String[] classNames = new String[]{"org.cyberneko.html.xercesbridge.XercesBridge_2_3", "org.cyberneko.html.xercesbridge.XercesBridge_2_2", "org.cyberneko.html.xercesbridge.XercesBridge_2_1", "org.cyberneko.html.xercesbridge.XercesBridge_2_0"};
        for (int i = 0; i != classNames.length; ++i) {
            String className = classNames[i];
            XercesBridge bridge = XercesBridge.newInstanceOrNull(className);
            if (bridge == null) continue;
            return bridge;
        }
        throw new IllegalStateException("Failed to create XercesBridge instance");
    }

    private static XercesBridge newInstanceOrNull(String className) {
        try {
            return (XercesBridge)Class.forName(className).newInstance();
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (SecurityException securityException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (IllegalArgumentException illegalArgumentException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InstantiationException instantiationException) {
            // empty catch block
        }
        return null;
    }

    public void NamespaceContext_declarePrefix(NamespaceContext namespaceContext, String ns, String avalue) {
    }

    public abstract String getVersion();

    public abstract void XMLDocumentHandler_startDocument(XMLDocumentHandler var1, XMLLocator var2, String var3, NamespaceContext var4, Augmentations var5);

    public void XMLDocumentHandler_startPrefixMapping(XMLDocumentHandler documentHandler, String prefix, String uri, Augmentations augs) {
    }

    public void XMLDocumentHandler_endPrefixMapping(XMLDocumentHandler documentHandler, String prefix, Augmentations augs) {
    }

    public void XMLDocumentFilter_setDocumentSource(XMLDocumentFilter filter, XMLDocumentSource lastSource) {
    }
}

