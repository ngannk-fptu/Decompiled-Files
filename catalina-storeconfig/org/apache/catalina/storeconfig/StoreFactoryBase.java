/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.storeconfig;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.catalina.storeconfig.IStoreFactory;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class StoreFactoryBase
implements IStoreFactory {
    private static Log log = LogFactory.getLog(StoreFactoryBase.class);
    private StoreRegistry registry;
    private StoreAppender storeAppender = new StoreAppender();
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.storeconfig");
    private static final String info = "org.apache.catalina.config.StoreFactoryBase/1.0";

    public String getInfo() {
        return info;
    }

    @Override
    public StoreAppender getStoreAppender() {
        return this.storeAppender;
    }

    @Override
    public void setStoreAppender(StoreAppender storeAppender) {
        this.storeAppender = storeAppender;
    }

    @Override
    public void setRegistry(StoreRegistry aRegistry) {
        this.registry = aRegistry;
    }

    @Override
    public StoreRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public void storeXMLHead(PrintWriter aWriter) {
        aWriter.print("<?xml version=\"1.0\" encoding=\"");
        aWriter.print(this.getRegistry().getEncoding());
        aWriter.println("\"?>");
    }

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass());
        if (elementDesc != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("factory.storeTag", new Object[]{elementDesc.getTag(), aElement}));
            }
            this.getStoreAppender().printIndent(aWriter, indent + 2);
            if (!elementDesc.isChildren()) {
                this.getStoreAppender().printTag(aWriter, indent, aElement, elementDesc);
            } else {
                this.getStoreAppender().printOpenTag(aWriter, indent + 2, aElement, elementDesc);
                this.storeChildren(aWriter, indent + 2, aElement, elementDesc);
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printCloseTag(aWriter, elementDesc);
            }
        } else {
            log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aElement.getClass()}));
        }
    }

    public void storeChildren(PrintWriter aWriter, int indent, Object aElement, StoreDescription elementDesc) throws Exception {
    }

    protected void storeElement(PrintWriter aWriter, int indent, Object aTagElement) throws Exception {
        if (aTagElement != null) {
            IStoreFactory elementFactory = this.getRegistry().findStoreFactory(aTagElement.getClass());
            if (elementFactory != null) {
                StoreDescription desc = this.getRegistry().findDescription(aTagElement.getClass());
                if (desc != null) {
                    if (!desc.isTransientChild(aTagElement.getClass().getName())) {
                        elementFactory.store(aWriter, indent, aTagElement);
                    }
                } else {
                    log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aTagElement.getClass()}));
                }
            } else {
                log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aTagElement.getClass()}));
            }
        }
    }

    protected void storeElementArray(PrintWriter aWriter, int indent, Object[] elements) throws Exception {
        if (elements != null) {
            for (Object element : elements) {
                try {
                    this.storeElement(aWriter, indent, element);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }
}

