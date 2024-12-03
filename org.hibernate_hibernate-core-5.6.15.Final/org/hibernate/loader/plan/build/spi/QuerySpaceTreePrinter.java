/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.build.spi.TreePrinterHelper;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionQuerySpace;
import org.hibernate.loader.plan.spi.CompositeQuerySpace;
import org.hibernate.loader.plan.spi.EntityQuerySpace;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.QuerySpaces;

public class QuerySpaceTreePrinter {
    public static final QuerySpaceTreePrinter INSTANCE = new QuerySpaceTreePrinter();
    private static final int detailDepthOffset = 1;

    private QuerySpaceTreePrinter() {
    }

    public String asString(QuerySpaces spaces, AliasResolutionContext aliasResolutionContext) {
        return this.asString(spaces, 0, aliasResolutionContext);
    }

    public String asString(QuerySpaces spaces, int depth, AliasResolutionContext aliasResolutionContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        this.write(spaces, depth, aliasResolutionContext, printStream);
        printStream.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    public void write(QuerySpaces spaces, int depth, AliasResolutionContext aliasResolutionContext, PrintStream printStream) {
        this.write(spaces, depth, aliasResolutionContext, new PrintWriter(printStream));
    }

    public void write(QuerySpaces spaces, int depth, AliasResolutionContext aliasResolutionContext, PrintWriter printWriter) {
        if (spaces == null) {
            printWriter.println("QuerySpaces is null!");
            return;
        }
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + "QuerySpaces");
        for (QuerySpace querySpace : spaces.getRootQuerySpaces()) {
            this.writeQuerySpace(querySpace, depth + 1, aliasResolutionContext, printWriter);
        }
        printWriter.flush();
    }

    private void writeQuerySpace(QuerySpace querySpace, int depth, AliasResolutionContext aliasResolutionContext, PrintWriter printWriter) {
        this.generateDetailLines(querySpace, depth, aliasResolutionContext, printWriter);
        this.writeJoins(querySpace.getJoins(), depth + 1, aliasResolutionContext, printWriter);
    }

    private void generateDetailLines(QuerySpace querySpace, int depth, AliasResolutionContext aliasResolutionContext, PrintWriter printWriter) {
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + this.extractDetails(querySpace));
        if (aliasResolutionContext == null) {
            return;
        }
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "SQL table alias mapping - " + aliasResolutionContext.resolveSqlTableAliasFromQuerySpaceUid(querySpace.getUid()));
        EntityReferenceAliases entityAliases = aliasResolutionContext.resolveEntityReferenceAliases(querySpace.getUid());
        CollectionReferenceAliases collectionReferenceAliases = aliasResolutionContext.resolveCollectionReferenceAliases(querySpace.getUid());
        if (entityAliases != null) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "alias suffix - " + entityAliases.getColumnAliases().getSuffix());
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "suffixed key columns - {" + String.join((CharSequence)", ", entityAliases.getColumnAliases().getSuffixedKeyAliases()) + "}");
        }
        if (collectionReferenceAliases != null) {
            EntityAliases elementAliases;
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "alias suffix - " + collectionReferenceAliases.getCollectionColumnAliases().getSuffix());
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "suffixed key columns - {" + String.join((CharSequence)", ", collectionReferenceAliases.getCollectionColumnAliases().getSuffixedKeyAliases()) + "}");
            EntityAliases entityAliases2 = elementAliases = collectionReferenceAliases.getEntityElementAliases() == null ? null : collectionReferenceAliases.getEntityElementAliases().getColumnAliases();
            if (elementAliases != null) {
                printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + "entity-element alias suffix - " + elementAliases.getSuffix());
                printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 1) + elementAliases.getSuffix() + "entity-element suffixed key columns - " + String.join((CharSequence)", ", elementAliases.getSuffixedKeyAliases()));
            }
        }
    }

    private void writeJoins(Iterable<Join> joins, int depth, AliasResolutionContext aliasResolutionContext, PrintWriter printWriter) {
        for (Join join : joins) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + this.extractDetails(join));
            this.writeQuerySpace(join.getRightHandSide(), depth + 1, aliasResolutionContext, printWriter);
        }
    }

    public String extractDetails(QuerySpace space) {
        if (EntityQuerySpace.class.isInstance(space)) {
            EntityQuerySpace entityQuerySpace = (EntityQuerySpace)space;
            return String.format("%s(uid=%s, entity=%s)", entityQuerySpace.getClass().getSimpleName(), entityQuerySpace.getUid(), entityQuerySpace.getEntityPersister().getEntityName());
        }
        if (CompositeQuerySpace.class.isInstance(space)) {
            CompositeQuerySpace compositeQuerySpace = (CompositeQuerySpace)space;
            return String.format("%s(uid=%s)", compositeQuerySpace.getClass().getSimpleName(), compositeQuerySpace.getUid());
        }
        if (CollectionQuerySpace.class.isInstance(space)) {
            CollectionQuerySpace collectionQuerySpace = (CollectionQuerySpace)space;
            return String.format("%s(uid=%s, collection=%s)", collectionQuerySpace.getClass().getSimpleName(), collectionQuerySpace.getUid(), collectionQuerySpace.getCollectionPersister().getRole());
        }
        return space.toString();
    }

    private String extractDetails(Join join) {
        return String.format("JOIN (%s) : %s -> %s", this.determineJoinType(join), join.getLeftHandSide().getUid(), join.getRightHandSide().getUid());
    }

    private String determineJoinType(Join join) {
        if (JoinDefinedByMetadata.class.isInstance(join)) {
            return "JoinDefinedByMetadata(" + ((JoinDefinedByMetadata)join).getJoinedPropertyName() + ")";
        }
        return join.getClass().getSimpleName();
    }
}

