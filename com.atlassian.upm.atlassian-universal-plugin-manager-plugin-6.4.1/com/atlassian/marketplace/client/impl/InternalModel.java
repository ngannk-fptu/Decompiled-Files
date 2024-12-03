/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.impl.EntityValidator;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import com.atlassian.marketplace.client.model.Application;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.ErrorDetail;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.Product;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Option;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class InternalModel {
    public static Addons addons(Links links, ImmutableList<AddonSummary> items, int count) {
        return InternalModel.makeCollectionRep(Addons.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static Addons addons(Links links, List<AddonSummary> items, int count) {
        return InternalModel.makeCollectionRep(Addons.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static AddonCategories addonCategories(Links links, ImmutableList<AddonCategorySummary> items) {
        return InternalModel.makeCollectionRep(AddonCategories.class, links, items, (Option<Integer>)Option.none(Integer.class));
    }

    public static AddonCategories addonCategories(Links links, List<AddonCategorySummary> items) {
        return InternalModel.makeCollectionRep(AddonCategories.class, links, items, (Option<Integer>)Option.none(Integer.class));
    }

    public static AddonReferences addonReferences(Links links, ImmutableList<AddonReference> items, int count) {
        return InternalModel.makeCollectionRep(AddonReferences.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static AddonReferences addonReferences(Links links, List<AddonReference> items, int count) {
        return InternalModel.makeCollectionRep(AddonReferences.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static AddonVersions addonVersions(Links links, ImmutableList<AddonVersionSummary> items, int count) {
        return InternalModel.makeCollectionRep(AddonVersions.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static AddonVersions addonVersions(Links links, List<AddonVersionSummary> items, int count) {
        return InternalModel.makeCollectionRep(AddonVersions.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static Applications applications(Links links, ImmutableList<Application> items) {
        return InternalModel.makeCollectionRep(Applications.class, links, items, (Option<Integer>)Option.none(Integer.class));
    }

    public static Applications applications(Links links, List<Application> items) {
        return InternalModel.makeCollectionRep(Applications.class, links, items, (Option<Integer>)Option.none(Integer.class));
    }

    public static ApplicationVersions applicationVersions(Links links, ImmutableList<ApplicationVersion> items, int count) {
        return InternalModel.makeCollectionRep(ApplicationVersions.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static ApplicationVersions applicationVersions(Links links, List<ApplicationVersion> items, int count) {
        return InternalModel.makeCollectionRep(ApplicationVersions.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static ErrorDetails errorDetails(Iterable<ErrorDetail> errorDetails) {
        return new ErrorDetails(errorDetails);
    }

    public static MinimalLinks minimalLinks(Links links) {
        return new MinimalLinks(links);
    }

    public static Products products(Links links, ImmutableList<Product> items, int count) {
        return InternalModel.makeCollectionRep(Products.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static Products products(Links links, List<Product> items, int count) {
        return InternalModel.makeCollectionRep(Products.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static Vendors vendors(Links links, ImmutableList<VendorSummary> items, int count) {
        return InternalModel.makeCollectionRep(Vendors.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    public static Vendors vendors(Links links, List<VendorSummary> items, int count) {
        return InternalModel.makeCollectionRep(Vendors.class, links, items, (Option<Integer>)Option.some((Object)count));
    }

    private static <A, B> A makeCollectionRep(Class<A> repClass, Links links, ImmutableList<B> items, Option<Integer> count) {
        Map<String, Field> fields = EntityValidator.getClassFields(repClass);
        try {
            A instance = repClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            fields.get("_links").set(instance, links);
            Iterator iterator = count.iterator();
            while (iterator.hasNext()) {
                int c = (Integer)iterator.next();
                if (fields.get("count") == null) continue;
                fields.get("count").set(instance, c);
            }
            Field f = fields.get("_embedded");
            Class<?> ec = f.getType();
            Object e = ec.getConstructor(new Class[0]).newInstance(new Object[0]);
            Field eif = ec.getDeclaredFields()[0];
            eif.setAccessible(true);
            eif.set(e, items);
            f.set(instance, e);
            return instance;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <A, B> A makeCollectionRep(Class<A> repClass, Links links, List<B> items, Option<Integer> count) {
        return InternalModel.makeCollectionRep(repClass, links, ImmutableList.copyOf(items), count);
    }

    public static class Vendors
    extends EntityCollection<VendorSummary> {
        private Embedded _embedded;

        @Override
        public Iterable<VendorSummary> getItems() {
            return this._embedded.vendors;
        }

        public static class Embedded {
            private ImmutableList<VendorSummary> vendors;
        }
    }

    public static class Products
    extends EntityCollection<Product> {
        private Embedded _embedded;

        @Override
        public Iterable<Product> getItems() {
            return this._embedded.products;
        }

        public static class Embedded {
            private ImmutableList<Product> products;
        }
    }

    public static class MinimalLinks {
        private Links _links;

        public MinimalLinks(Links _links) {
            this._links = (Links)Preconditions.checkNotNull((Object)_links);
        }

        public Links getLinks() {
            return this._links;
        }
    }

    public static class LicenseTypes {
        private Embedded _embedded;

        public Iterable<LicenseType> getItems() {
            return this._embedded.types;
        }

        public static class Embedded {
            public ImmutableList<LicenseType> types;
        }
    }

    public static class ErrorDetails {
        ImmutableList<ErrorDetail> errors;

        ErrorDetails(Iterable<ErrorDetail> errorDetails) {
            this.errors = ImmutableList.copyOf(errorDetails);
        }
    }

    public static class Applications {
        public Links _links;
        public Embedded _embedded;

        public static class Embedded {
            public ImmutableList<Application> applications;
        }
    }

    public static class ApplicationVersions
    extends EntityCollection<ApplicationVersion> {
        private Embedded _embedded;

        @Override
        public Iterable<ApplicationVersion> getItems() {
            return this._embedded.versions;
        }

        public static class Embedded {
            private Collection<ApplicationVersion> versions;
        }
    }

    public static class Addons
    extends EntityCollection<AddonSummary> {
        private Embedded _embedded;

        @Override
        public Iterable<AddonSummary> getItems() {
            return this._embedded.addons;
        }

        public static class Embedded {
            private ImmutableList<AddonSummary> addons;
        }
    }

    public static class AddonVersions
    extends EntityCollection<AddonVersionSummary> {
        private Embedded _embedded;

        @Override
        public Iterable<AddonVersionSummary> getItems() {
            return this._embedded.versions;
        }

        public static class Embedded {
            private ImmutableList<AddonVersionSummary> versions;
        }
    }

    public static class AddonReferences
    extends EntityCollection<AddonReference> {
        private Embedded _embedded;

        @Override
        public Iterable<AddonReference> getItems() {
            return this._embedded.addons;
        }

        public static class Embedded {
            private ImmutableList<AddonReference> addons;
        }
    }

    public static class AddonCategories {
        public Links _links;
        public Embedded _embedded;

        public static class Embedded {
            public ImmutableList<AddonCategorySummary> categories;
        }
    }

    public static abstract class EntityCollection<T>
    implements Entity {
        private Links _links;
        private Integer count;
        @RequiredLink(rel="self")
        private URI selfUri;

        protected EntityCollection() {
        }

        protected EntityCollection(Links links, int count) {
            this._links = links;
            this.count = count;
        }

        @Override
        public Links getLinks() {
            return this._links;
        }

        @Override
        public URI getSelfUri() {
            return this.selfUri;
        }

        public int getCount() {
            return this.count;
        }

        public abstract Iterable<T> getItems();
    }
}

