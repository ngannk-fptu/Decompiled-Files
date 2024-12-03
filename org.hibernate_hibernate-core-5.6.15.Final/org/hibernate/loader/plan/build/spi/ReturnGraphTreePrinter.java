/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.hibernate.loader.plan.build.spi.TreePrinterHelper;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.CompositeFetch;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.loader.plan.spi.ScalarReturn;

public class ReturnGraphTreePrinter {
    public static final ReturnGraphTreePrinter INSTANCE = new ReturnGraphTreePrinter();

    private ReturnGraphTreePrinter() {
    }

    public String asString(Return rootReturn) {
        return this.asString(rootReturn, 0);
    }

    public String asString(Return rootReturn, int depth) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        this.write(rootReturn, depth, ps);
        ps.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    public void write(Return rootReturn, PrintStream printStream) {
        this.write(rootReturn, new PrintWriter(printStream));
    }

    public void write(Return rootReturn, int depth, PrintStream printStream) {
        this.write(rootReturn, depth, new PrintWriter(printStream));
    }

    public void write(Return rootReturn, PrintWriter printWriter) {
        this.write(rootReturn, 0, printWriter);
    }

    public void write(Return rootReturn, int depth, PrintWriter printWriter) {
        if (rootReturn == null) {
            printWriter.println("Return is null!");
            return;
        }
        printWriter.write(TreePrinterHelper.INSTANCE.generateNodePrefix(depth));
        if (ScalarReturn.class.isInstance(rootReturn)) {
            printWriter.println(this.extractDetails((ScalarReturn)rootReturn));
        } else if (EntityReturn.class.isInstance(rootReturn)) {
            EntityReturn entityReturn = (EntityReturn)rootReturn;
            printWriter.println(this.extractDetails(entityReturn));
            this.writeEntityReferenceFetches(entityReturn, depth + 1, printWriter);
        } else if (CollectionReference.class.isInstance(rootReturn)) {
            CollectionReference collectionReference = (CollectionReference)((Object)rootReturn);
            printWriter.println(this.extractDetails(collectionReference));
            this.writeCollectionReferenceFetches(collectionReference, depth + 1, printWriter);
        }
        printWriter.flush();
    }

    private String extractDetails(ScalarReturn rootReturn) {
        return String.format("%s(name=%s, type=%s)", rootReturn.getClass().getSimpleName(), rootReturn.getName(), rootReturn.getType().getName());
    }

    private String extractDetails(EntityReference entityReference) {
        return String.format("%s(entity=%s, querySpaceUid=%s, path=%s)", entityReference.getClass().getSimpleName(), entityReference.getEntityPersister().getEntityName(), entityReference.getQuerySpaceUid(), entityReference.getPropertyPath().getFullPath());
    }

    private String extractDetails(CollectionReference collectionReference) {
        return String.format("%s(collection=%s, querySpaceUid=%s, path=%s)", collectionReference.getClass().getSimpleName(), collectionReference.getCollectionPersister().getRole(), collectionReference.getQuerySpaceUid(), collectionReference.getPropertyPath().getFullPath());
    }

    private String extractDetails(CompositeFetch compositeFetch) {
        return String.format("%s(composite=%s, querySpaceUid=%s, path=%s)", compositeFetch.getClass().getSimpleName(), compositeFetch.getFetchedType().getReturnedClass().getName(), compositeFetch.getQuerySpaceUid(), compositeFetch.getPropertyPath().getFullPath());
    }

    private void writeEntityReferenceFetches(EntityReference entityReference, int depth, PrintWriter printWriter) {
        if (BidirectionalEntityReference.class.isInstance(entityReference)) {
            return;
        }
        if (entityReference.getIdentifierDescription().hasFetches()) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + "(entity id) ");
            this.writeFetches(((FetchSource)((Object)entityReference.getIdentifierDescription())).getFetches(), depth + 1, printWriter);
        }
        this.writeFetches(entityReference.getFetches(), depth, printWriter);
    }

    private void writeFetches(Fetch[] fetches, int depth, PrintWriter printWriter) {
        for (Fetch fetch : fetches) {
            this.writeFetch(fetch, depth, printWriter);
        }
    }

    private void writeFetch(Fetch fetch, int depth, PrintWriter printWriter) {
        printWriter.print(TreePrinterHelper.INSTANCE.generateNodePrefix(depth));
        if (EntityFetch.class.isInstance(fetch)) {
            EntityFetch entityFetch = (EntityFetch)fetch;
            printWriter.println(this.extractDetails(entityFetch));
            this.writeEntityReferenceFetches(entityFetch, depth + 1, printWriter);
        } else if (CompositeFetch.class.isInstance(fetch)) {
            CompositeFetch compositeFetch = (CompositeFetch)fetch;
            printWriter.println(this.extractDetails(compositeFetch));
            this.writeCompositeFetchFetches(compositeFetch, depth + 1, printWriter);
        } else if (CollectionAttributeFetch.class.isInstance(fetch)) {
            CollectionAttributeFetch collectionFetch = (CollectionAttributeFetch)fetch;
            printWriter.println(this.extractDetails(collectionFetch));
            this.writeCollectionReferenceFetches(collectionFetch, depth + 1, printWriter);
        }
    }

    private void writeCompositeFetchFetches(CompositeFetch compositeFetch, int depth, PrintWriter printWriter) {
        this.writeFetches(compositeFetch.getFetches(), depth, printWriter);
    }

    private void writeCollectionReferenceFetches(CollectionReference collectionReference, int depth, PrintWriter printWriter) {
        CollectionFetchableElement elementGraph;
        CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
        if (indexGraph != null) {
            printWriter.print(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + "(collection index) ");
            if (EntityReference.class.isInstance(indexGraph)) {
                EntityReference indexGraphAsEntityReference = (EntityReference)((Object)indexGraph);
                printWriter.println(this.extractDetails(indexGraphAsEntityReference));
                this.writeEntityReferenceFetches(indexGraphAsEntityReference, depth + 1, printWriter);
            } else if (CompositeFetch.class.isInstance(indexGraph)) {
                CompositeFetch indexGraphAsCompositeFetch = (CompositeFetch)((Object)indexGraph);
                printWriter.println(this.extractDetails(indexGraphAsCompositeFetch));
                this.writeCompositeFetchFetches(indexGraphAsCompositeFetch, depth + 1, printWriter);
            }
        }
        if ((elementGraph = collectionReference.getElementGraph()) != null) {
            printWriter.print(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + "(collection element) ");
            if (EntityReference.class.isInstance(elementGraph)) {
                EntityReference elementGraphAsEntityReference = (EntityReference)((Object)elementGraph);
                printWriter.println(this.extractDetails(elementGraphAsEntityReference));
                this.writeEntityReferenceFetches(elementGraphAsEntityReference, depth + 1, printWriter);
            } else if (CompositeFetch.class.isInstance(elementGraph)) {
                CompositeFetch elementGraphAsCompositeFetch = (CompositeFetch)((Object)elementGraph);
                printWriter.println(this.extractDetails(elementGraphAsCompositeFetch));
                this.writeCompositeFetchFetches(elementGraphAsCompositeFetch, depth + 1, printWriter);
            }
        }
    }
}

