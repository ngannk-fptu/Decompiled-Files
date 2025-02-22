/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBeginDocument;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBeginResource;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentDocumentNeededResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentDocumentSuppliedResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentEndComments;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentEndOfFile;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentHiResBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentIncludeResource;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentLanguageLevel;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPage;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageHiResBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPages;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentTitle;

public final class DSCCommentFactory {
    private static final Map DSC_FACTORIES = new HashMap();

    private DSCCommentFactory() {
    }

    public static DSCComment createDSCCommentFor(String name) {
        Class clazz = (Class)DSC_FACTORIES.get(name);
        if (clazz == null) {
            return null;
        }
        try {
            return (DSCComment)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating instance for '" + name + "': " + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal Access error while instantiating instance for '" + name + "': " + e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        DSC_FACTORIES.put("EndComments", DSCCommentEndComments.class);
        DSC_FACTORIES.put("BeginResource", DSCCommentBeginResource.class);
        DSC_FACTORIES.put("IncludeResource", DSCCommentIncludeResource.class);
        DSC_FACTORIES.put("PageResources", DSCCommentPageResources.class);
        DSC_FACTORIES.put("BeginDocument", DSCCommentBeginDocument.class);
        DSC_FACTORIES.put("Page", DSCCommentPage.class);
        DSC_FACTORIES.put("Pages", DSCCommentPages.class);
        DSC_FACTORIES.put("BoundingBox", DSCCommentBoundingBox.class);
        DSC_FACTORIES.put("HiResBoundingBox", DSCCommentHiResBoundingBox.class);
        DSC_FACTORIES.put("PageBoundingBox", DSCCommentPageBoundingBox.class);
        DSC_FACTORIES.put("PageHiResBoundingBox", DSCCommentPageHiResBoundingBox.class);
        DSC_FACTORIES.put("LanguageLevel", DSCCommentLanguageLevel.class);
        DSC_FACTORIES.put("DocumentNeededResources", DSCCommentDocumentNeededResources.class);
        DSC_FACTORIES.put("DocumentSuppliedResources", DSCCommentDocumentSuppliedResources.class);
        DSC_FACTORIES.put("Title", DSCCommentTitle.class);
        DSC_FACTORIES.put("EOF", DSCCommentEndOfFile.class);
    }
}

