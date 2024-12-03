/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.build.spi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.plan.build.spi.QuerySpaceTreePrinter;
import org.hibernate.loader.plan.build.spi.ReturnGraphTreePrinter;
import org.hibernate.loader.plan.build.spi.TreePrinterHelper;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.Return;
import org.jboss.logging.Logger;

public class LoadPlanTreePrinter {
    private static final Logger log = CoreLogging.logger(LoadPlanTreePrinter.class);
    public static final LoadPlanTreePrinter INSTANCE = new LoadPlanTreePrinter();

    private LoadPlanTreePrinter() {
    }

    public void logTree(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext) {
        if (!log.isDebugEnabled()) {
            return;
        }
        log.debug((Object)this.toString(loadPlan, aliasResolutionContext));
    }

    private String toString(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        PrintWriter printWriter = new PrintWriter(printStream);
        this.logTree(loadPlan, aliasResolutionContext, printWriter);
        printWriter.flush();
        printStream.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    private void logTree(LoadPlan loadPlan, AliasResolutionContext aliasResolutionContext, PrintWriter printWriter) {
        printWriter.println("LoadPlan(" + this.extractDetails(loadPlan) + ")");
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(1) + "Returns");
        for (Return return_ : loadPlan.getReturns()) {
            ReturnGraphTreePrinter.INSTANCE.write(return_, 2, printWriter);
            printWriter.flush();
        }
        QuerySpaceTreePrinter.INSTANCE.write(loadPlan.getQuerySpaces(), 1, aliasResolutionContext, printWriter);
        printWriter.flush();
    }

    private String extractDetails(LoadPlan loadPlan) {
        switch (loadPlan.getDisposition()) {
            case MIXED: {
                return "mixed";
            }
            case ENTITY_LOADER: {
                return "entity=" + ((EntityReturn)loadPlan.getReturns().get(0)).getEntityPersister().getEntityName();
            }
            case COLLECTION_INITIALIZER: {
                return "collection=" + ((CollectionReturn)loadPlan.getReturns().get(0)).getCollectionPersister().getRole();
            }
        }
        return "???";
    }
}

