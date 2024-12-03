/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.util;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.wsdl.SDDocumentResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MetadataUtil {
    public static Map<String, SDDocument> getMetadataClosure(@NotNull String systemId, @NotNull SDDocumentResolver resolver, boolean onlyTopLevelSchemas) {
        HashMap<String, SDDocument> closureDocs = new HashMap<String, SDDocument>();
        HashSet<String> remaining = new HashSet<String>();
        remaining.add(systemId);
        while (!remaining.isEmpty()) {
            Iterator it = remaining.iterator();
            String current = (String)it.next();
            remaining.remove(current);
            SDDocument currentDoc = resolver.resolve(current);
            SDDocument old = closureDocs.put(currentDoc.getURL().toExternalForm(), currentDoc);
            assert (old == null);
            Set<String> imports = currentDoc.getImports();
            if (currentDoc.isSchema() && onlyTopLevelSchemas) continue;
            for (String importedDoc : imports) {
                if (closureDocs.get(importedDoc) != null) continue;
                remaining.add(importedDoc);
            }
        }
        return closureDocs;
    }
}

