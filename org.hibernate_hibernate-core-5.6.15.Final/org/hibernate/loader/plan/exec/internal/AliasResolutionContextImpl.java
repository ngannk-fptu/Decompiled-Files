/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.loader.plan.exec.internal;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.DefaultEntityAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.GeneratedCollectionAliases;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.loader.plan.build.spi.QuerySpaceTreePrinter;
import org.hibernate.loader.plan.build.spi.TreePrinterHelper;
import org.hibernate.loader.plan.exec.internal.CollectionReferenceAliasesImpl;
import org.hibernate.loader.plan.exec.internal.EntityReferenceAliasesImpl;
import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.EntityType;
import org.jboss.logging.Logger;

public class AliasResolutionContextImpl
implements AliasResolutionContext {
    private static final Logger log = CoreLogging.logger(AliasResolutionContextImpl.class);
    private final SessionFactoryImplementor sessionFactory;
    private int currentAliasSuffix;
    private int currentTableAliasSuffix;
    private Map<String, EntityReferenceAliases> entityReferenceAliasesMap;
    private Map<String, CollectionReferenceAliases> collectionReferenceAliasesMap;
    private Map<String, String> querySpaceUidToSqlTableAliasMap;
    private Map<String, String> compositeQuerySpaceUidToSqlTableAliasMap;

    public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory) {
        this(sessionFactory, 0);
    }

    public AliasResolutionContextImpl(SessionFactoryImplementor sessionFactory, int suffixSeed) {
        this.sessionFactory = sessionFactory;
        this.currentAliasSuffix = suffixSeed;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.sessionFactory;
    }

    public EntityReferenceAliases generateEntityReferenceAliases(String uid, EntityPersister entityPersister) {
        return this.generateEntityReferenceAliases(uid, this.createTableAlias(entityPersister), entityPersister);
    }

    private EntityReferenceAliases generateEntityReferenceAliases(String uid, String tableAlias, EntityPersister entityPersister) {
        EntityReferenceAliasesImpl entityReferenceAliases = new EntityReferenceAliasesImpl(tableAlias, this.createEntityAliases(entityPersister));
        this.registerQuerySpaceAliases(uid, entityReferenceAliases);
        return entityReferenceAliases;
    }

    private String createTableAlias(EntityPersister entityPersister) {
        return this.createTableAlias(StringHelper.unqualifyEntityName(entityPersister.getEntityName()));
    }

    private String createTableAlias(String name) {
        return StringHelper.generateAlias(name, this.currentTableAliasSuffix++);
    }

    private EntityAliases createEntityAliases(EntityPersister entityPersister) {
        return new DefaultEntityAliases((Loadable)entityPersister, this.createSuffix());
    }

    private String createSuffix() {
        return AliasConstantsHelper.get(this.currentAliasSuffix++);
    }

    public CollectionReferenceAliases generateCollectionReferenceAliases(String collectionQuerySpaceUid, CollectionPersister persister, String elementQuerySpaceUid) {
        String tableAlias;
        String manyToManyTableAlias;
        if (persister.getElementType().isEntityType() && elementQuerySpaceUid == null) {
            throw new IllegalArgumentException("elementQuerySpaceUid must be non-null for one-to-many or many-to-many associations.");
        }
        if (persister.isManyToMany()) {
            manyToManyTableAlias = this.createTableAlias(persister.getRole());
            tableAlias = this.createTableAlias(persister.getElementDefinition().toEntityDefinition().getEntityPersister());
        } else {
            manyToManyTableAlias = null;
            tableAlias = this.createTableAlias(persister.getRole());
        }
        CollectionReferenceAliasesImpl collectionAliases = new CollectionReferenceAliasesImpl(tableAlias, manyToManyTableAlias, this.createCollectionAliases(persister), this.createCollectionElementAliases(persister, tableAlias, elementQuerySpaceUid));
        this.registerQuerySpaceAliases(collectionQuerySpaceUid, collectionAliases);
        return collectionAliases;
    }

    private CollectionAliases createCollectionAliases(CollectionPersister collectionPersister) {
        return new GeneratedCollectionAliases(collectionPersister, this.createSuffix());
    }

    private EntityReferenceAliases createCollectionElementAliases(CollectionPersister collectionPersister, String tableAlias, String elementQuerySpaceUid) {
        if (!collectionPersister.getElementType().isEntityType()) {
            return null;
        }
        EntityType entityElementType = (EntityType)collectionPersister.getElementType();
        return this.generateEntityReferenceAliases(elementQuerySpaceUid, tableAlias, (EntityPersister)((Object)entityElementType.getAssociatedJoinable(this.sessionFactory())));
    }

    private void registerQuerySpaceAliases(String querySpaceUid, EntityReferenceAliases entityReferenceAliases) {
        if (this.entityReferenceAliasesMap == null) {
            this.entityReferenceAliasesMap = new HashMap<String, EntityReferenceAliases>();
        }
        this.entityReferenceAliasesMap.put(querySpaceUid, entityReferenceAliases);
        this.registerSqlTableAliasMapping(querySpaceUid, entityReferenceAliases.getTableAlias());
    }

    private void registerSqlTableAliasMapping(String querySpaceUid, String sqlTableAlias) {
        String old;
        if (this.querySpaceUidToSqlTableAliasMap == null) {
            this.querySpaceUidToSqlTableAliasMap = new HashMap<String, String>();
        }
        if ((old = this.querySpaceUidToSqlTableAliasMap.put(StringHelper.safeInterning(querySpaceUid), StringHelper.safeInterning(sqlTableAlias))) != null && !old.equals(sqlTableAlias)) {
            throw new IllegalStateException(String.format("Attempt to register multiple SQL table aliases [%s, %s, etc] against query space uid [%s]", old, sqlTableAlias, querySpaceUid));
        }
    }

    @Override
    public String resolveSqlTableAliasFromQuerySpaceUid(String querySpaceUid) {
        String alias = null;
        if (this.querySpaceUidToSqlTableAliasMap != null) {
            alias = this.querySpaceUidToSqlTableAliasMap.get(querySpaceUid);
        }
        if (alias == null && this.compositeQuerySpaceUidToSqlTableAliasMap != null) {
            alias = this.compositeQuerySpaceUidToSqlTableAliasMap.get(querySpaceUid);
        }
        return alias;
    }

    @Override
    public EntityReferenceAliases resolveEntityReferenceAliases(String querySpaceUid) {
        return this.entityReferenceAliasesMap == null ? null : this.entityReferenceAliasesMap.get(querySpaceUid);
    }

    private void registerQuerySpaceAliases(String querySpaceUid, CollectionReferenceAliases collectionReferenceAliases) {
        if (this.collectionReferenceAliasesMap == null) {
            this.collectionReferenceAliasesMap = new HashMap<String, CollectionReferenceAliases>();
        }
        this.collectionReferenceAliasesMap.put(querySpaceUid, collectionReferenceAliases);
        this.registerSqlTableAliasMapping(querySpaceUid, collectionReferenceAliases.getCollectionTableAlias());
    }

    @Override
    public CollectionReferenceAliases resolveCollectionReferenceAliases(String querySpaceUid) {
        return this.collectionReferenceAliasesMap == null ? null : this.collectionReferenceAliasesMap.get(querySpaceUid);
    }

    public void registerCompositeQuerySpaceUidResolution(String rightHandSideUid, String leftHandSideTableAlias) {
        if (this.compositeQuerySpaceUidToSqlTableAliasMap == null) {
            this.compositeQuerySpaceUidToSqlTableAliasMap = new HashMap<String, String>();
        }
        this.compositeQuerySpaceUidToSqlTableAliasMap.put(rightHandSideUid, leftHandSideTableAlias);
    }

    public void dumpResolutions(LoadPlan loadPlan) {
        if (log.isDebugEnabled()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream);
            PrintWriter printWriter = new PrintWriter(printStream);
            printWriter.println("LoadPlan QuerySpace resolutions");
            for (QuerySpace querySpace : loadPlan.getQuerySpaces().getRootQuerySpaces()) {
                this.dumpQuerySpace(querySpace, 1, printWriter);
            }
            printWriter.flush();
            printStream.flush();
            log.debug((Object)new String(byteArrayOutputStream.toByteArray()));
        }
    }

    private void dumpQuerySpace(QuerySpace querySpace, int depth, PrintWriter printWriter) {
        this.generateDetailLines(querySpace, depth, printWriter);
        this.dumpJoins(querySpace.getJoins(), depth + 1, printWriter);
    }

    private void generateDetailLines(QuerySpace querySpace, int depth, PrintWriter printWriter) {
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + querySpace.getUid() + " -> " + this.extractDetails(querySpace));
        printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "SQL table alias mapping - " + this.resolveSqlTableAliasFromQuerySpaceUid(querySpace.getUid()));
        EntityReferenceAliases entityAliases = this.resolveEntityReferenceAliases(querySpace.getUid());
        CollectionReferenceAliases collectionReferenceAliases = this.resolveCollectionReferenceAliases(querySpace.getUid());
        if (entityAliases != null) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "alias suffix - " + entityAliases.getColumnAliases().getSuffix());
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "suffixed key columns - " + String.join((CharSequence)", ", entityAliases.getColumnAliases().getSuffixedKeyAliases()));
        }
        if (collectionReferenceAliases != null) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "alias suffix - " + collectionReferenceAliases.getCollectionColumnAliases().getSuffix());
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "suffixed key columns - " + String.join((CharSequence)", ", collectionReferenceAliases.getCollectionColumnAliases().getSuffixedKeyAliases()));
            EntityReferenceAliases elementAliases = collectionReferenceAliases.getEntityElementAliases();
            if (elementAliases != null) {
                printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + "entity-element alias suffix - " + elementAliases.getColumnAliases().getSuffix());
                printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth + 3) + elementAliases.getColumnAliases().getSuffix() + "entity-element suffixed key columns - " + String.join((CharSequence)", ", elementAliases.getColumnAliases().getSuffixedKeyAliases()));
            }
        }
    }

    private String extractDetails(QuerySpace querySpace) {
        return QuerySpaceTreePrinter.INSTANCE.extractDetails(querySpace);
    }

    private void dumpJoins(Iterable<Join> joins, int depth, PrintWriter printWriter) {
        for (Join join : joins) {
            printWriter.println(TreePrinterHelper.INSTANCE.generateNodePrefix(depth) + "JOIN (" + join.getLeftHandSide().getUid() + " -> " + join.getRightHandSide().getUid() + ")");
            this.dumpQuerySpace(join.getRightHandSide(), depth + 1, printWriter);
        }
    }
}

