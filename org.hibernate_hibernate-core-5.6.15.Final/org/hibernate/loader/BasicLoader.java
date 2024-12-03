/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import java.util.ArrayList;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.CollectionAliases;
import org.hibernate.loader.DefaultEntityAliases;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.GeneratedCollectionAliases;
import org.hibernate.loader.Loader;
import org.hibernate.loader.MultipleBagFetchException;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.type.BagType;

public abstract class BasicLoader
extends Loader {
    protected static final String[] NO_SUFFIX = new String[]{""};
    private EntityAliases[] descriptors;
    private CollectionAliases[] collectionDescriptors;

    public BasicLoader(SessionFactoryImplementor factory) {
        super(factory);
    }

    @Override
    protected final EntityAliases[] getEntityAliases() {
        return this.descriptors;
    }

    @Override
    protected final CollectionAliases[] getCollectionAliases() {
        return this.collectionDescriptors;
    }

    protected abstract String[] getSuffixes();

    protected abstract String[] getCollectionSuffixes();

    @Override
    protected void postInstantiate() {
        Loadable[] persisters = this.getEntityPersisters();
        String[] suffixes = this.getSuffixes();
        this.descriptors = new EntityAliases[persisters.length];
        for (int i = 0; i < this.descriptors.length; ++i) {
            this.descriptors[i] = new DefaultEntityAliases(persisters[i], suffixes[i]);
        }
        CollectionPersister[] collectionPersisters = this.getCollectionPersisters();
        ArrayList<String> bagRoles = null;
        if (collectionPersisters != null) {
            String[] collectionSuffixes = this.getCollectionSuffixes();
            this.collectionDescriptors = new CollectionAliases[collectionPersisters.length];
            for (int i = 0; i < collectionPersisters.length; ++i) {
                if (this.isBag(collectionPersisters[i])) {
                    if (bagRoles == null) {
                        bagRoles = new ArrayList<String>();
                    }
                    bagRoles.add(collectionPersisters[i].getRole());
                }
                this.collectionDescriptors[i] = new GeneratedCollectionAliases(collectionPersisters[i], collectionSuffixes[i]);
            }
        } else {
            this.collectionDescriptors = null;
        }
        if (bagRoles != null && bagRoles.size() > 1) {
            throw new MultipleBagFetchException(bagRoles);
        }
    }

    private boolean isBag(CollectionPersister collectionPersister) {
        return collectionPersister.getCollectionType().getClass().isAssignableFrom(BagType.class);
    }

    public static String[] generateSuffixes(int length) {
        return BasicLoader.generateSuffixes(0, length);
    }

    public static String[] generateSuffixes(int seed, int length) {
        if (length == 0) {
            return NO_SUFFIX;
        }
        String[] suffixes = new String[length];
        for (int i = 0; i < length; ++i) {
            suffixes[i] = AliasConstantsHelper.get(i + seed);
        }
        return suffixes;
    }
}

