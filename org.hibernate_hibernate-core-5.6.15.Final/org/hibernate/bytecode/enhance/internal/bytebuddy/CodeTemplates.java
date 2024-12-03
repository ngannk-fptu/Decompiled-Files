/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$ArgumentHandler
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OffsetMapping
 *  net.bytebuddy.asm.Advice$OffsetMapping$Sort
 *  net.bytebuddy.asm.Advice$OffsetMapping$Target
 *  net.bytebuddy.asm.Advice$OffsetMapping$Target$AbstractReadOnlyAdapter
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 *  net.bytebuddy.description.field.FieldDescription
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.description.method.MethodDescription$Latent
 *  net.bytebuddy.description.method.MethodDescription$Token
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.implementation.bytecode.StackManipulation
 *  net.bytebuddy.implementation.bytecode.StackManipulation$Compound
 *  net.bytebuddy.implementation.bytecode.assign.Assigner
 *  net.bytebuddy.implementation.bytecode.member.MethodInvocation
 *  net.bytebuddy.implementation.bytecode.member.MethodVariableAccess
 */
package org.hibernate.bytecode.enhance.internal.bytebuddy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import org.hibernate.Hibernate;
import org.hibernate.bytecode.enhance.internal.tracker.CompositeOwnerTracker;
import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
import org.hibernate.bytecode.enhance.internal.tracker.NoopCollectionTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SimpleCollectionTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SimpleFieldTracker;
import org.hibernate.bytecode.enhance.spi.CollectionTracker;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.engine.spi.CompositeTracker;
import org.hibernate.engine.spi.ExtendedSelfDirtinessTracker;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

class CodeTemplates {
    CodeTemplates() {
    }

    static class GetterMapping
    implements Advice.OffsetMapping {
        private final FieldDescription persistentField;

        GetterMapping(FieldDescription persistentField) {
            this.persistentField = persistentField;
        }

        public Advice.OffsetMapping.Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Advice.ArgumentHandler argumentHandler, Advice.OffsetMapping.Sort sort) {
            MethodDescription.Token signature = new MethodDescription.Token("$$_hibernate_read_" + this.persistentField.getName(), 1, this.persistentField.getType());
            MethodDescription.Latent method = new MethodDescription.Latent(instrumentedType.getSuperClass().asErasure(), signature);
            return new Advice.OffsetMapping.Target.AbstractReadOnlyAdapter((MethodDescription)method){
                final /* synthetic */ MethodDescription val$method;
                {
                    this.val$method = methodDescription;
                }

                public StackManipulation resolveRead() {
                    return new StackManipulation.Compound(new StackManipulation[]{MethodVariableAccess.loadThis(), MethodInvocation.invoke((MethodDescription)this.val$method).special(this.val$method.getDeclaringType().asErasure())});
                }
            };
        }
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    static @interface MappedBy {
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    static @interface FieldValue {
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    static @interface FieldName {
    }

    static class ManyToManyHandler {
        ManyToManyHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@Advice.This Object self, @FieldValue Collection<?> field, @Advice.Argument(value=0) Collection<?> argument, @MappedBy String mappedBy) {
            if (field != null && Hibernate.isPropertyInitialized(field, mappedBy)) {
                Object[] array;
                for (Object array1 : array = field.toArray()) {
                    if (argument != null && argument.contains(array1)) continue;
                    ManyToManyHandler.getter(array1).remove(self);
                }
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This Object self, @Advice.Argument(value=0) Collection<?> argument, @MappedBy String mappedBy) {
            if (argument != null && Hibernate.isPropertyInitialized(argument, mappedBy)) {
                Object[] array;
                for (Object array1 : array = argument.toArray()) {
                    Collection<Object> c;
                    if (!Hibernate.isPropertyInitialized(array1, mappedBy) || (c = ManyToManyHandler.getter(array1)) == self || c == null) continue;
                    c.add(self);
                }
            }
        }

        static Collection<Object> getter(Object self) {
            throw new AssertionError();
        }
    }

    static class ManyToOneHandler {
        ManyToOneHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@Advice.This Object self, @FieldValue Object field, @MappedBy String mappedBy) {
            Collection<Object> c;
            if (field != null && Hibernate.isPropertyInitialized(field, mappedBy) && (c = ManyToOneHandler.getter(field)) != null) {
                c.remove(self);
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This Object self, @Advice.Argument(value=0) Object argument, @MappedBy String mappedBy) {
            Collection<Object> c;
            if (argument != null && Hibernate.isPropertyInitialized(argument, mappedBy) && (c = ManyToOneHandler.getter(argument)) != null && !c.contains(self)) {
                c.add(self);
            }
        }

        static Collection<Object> getter(Object target) {
            throw new AssertionError();
        }
    }

    static class OneToManyOnMapHandler {
        OneToManyOnMapHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@FieldValue Map<?, ?> field, @Advice.Argument(value=0) Map<?, ?> argument, @MappedBy String mappedBy) {
            if (field != null && Hibernate.isPropertyInitialized(field, mappedBy)) {
                Object[] array;
                for (Object array1 : array = field.values().toArray()) {
                    if (argument != null && argument.values().contains(array1)) continue;
                    OneToManyOnMapHandler.setterNull(array1, null);
                }
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This Object self, @Advice.Argument(value=0) Map<?, ?> argument, @MappedBy String mappedBy) {
            if (argument != null && Hibernate.isPropertyInitialized(argument, mappedBy)) {
                Object[] array;
                for (Object array1 : array = argument.values().toArray()) {
                    if (!Hibernate.isPropertyInitialized(array1, mappedBy) || OneToManyOnMapHandler.getter(array1) == self) continue;
                    OneToManyOnMapHandler.setterSelf(array1, self);
                }
            }
        }

        static Object getter(Object target) {
            throw new AssertionError();
        }

        static void setterNull(Object target, Object argument) {
            throw new AssertionError();
        }

        static void setterSelf(Object target, Object argument) {
            throw new AssertionError();
        }
    }

    static class OneToManyOnCollectionHandler {
        OneToManyOnCollectionHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@FieldValue Collection<?> field, @Advice.Argument(value=0) Collection<?> argument, @MappedBy String mappedBy) {
            if (field != null && Hibernate.isPropertyInitialized(field, mappedBy)) {
                Object[] array;
                for (Object array1 : array = field.toArray()) {
                    if (argument != null && argument.contains(array1)) continue;
                    OneToManyOnCollectionHandler.setterNull(array1, null);
                }
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This Object self, @Advice.Argument(value=0) Collection<?> argument, @MappedBy String mappedBy) {
            if (argument != null && Hibernate.isPropertyInitialized(argument, mappedBy)) {
                Object[] array;
                for (Object array1 : array = argument.toArray()) {
                    if (!Hibernate.isPropertyInitialized(array1, mappedBy) || OneToManyOnCollectionHandler.getter(array1) == self) continue;
                    OneToManyOnCollectionHandler.setterSelf(array1, self);
                }
            }
        }

        static Object getter(Object target) {
            throw new AssertionError();
        }

        static void setterNull(Object target, Object argument) {
            throw new AssertionError();
        }

        static void setterSelf(Object target, Object argument) {
            throw new AssertionError();
        }
    }

    static class OneToOneHandler {
        OneToOneHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@FieldValue Object field, @Advice.Argument(value=0) Object argument, @MappedBy String mappedBy) {
            if (field != null && Hibernate.isPropertyInitialized(field, mappedBy) && argument != null) {
                OneToOneHandler.setterNull(field, null);
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This Object self, @Advice.Argument(value=0) Object argument, @MappedBy String mappedBy) {
            if (argument != null && Hibernate.isPropertyInitialized(argument, mappedBy) && OneToOneHandler.getter(argument) != self) {
                OneToOneHandler.setterSelf(argument, self);
            }
        }

        static Object getter(Object target) {
            throw new AssertionError();
        }

        static void setterNull(Object target, Object argument) {
            throw new AssertionError();
        }

        static void setterSelf(Object target, Object argument) {
            throw new AssertionError();
        }
    }

    static class CompositeOwnerDirtyCheckingHandler {
        CompositeOwnerDirtyCheckingHandler() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_trackChange(@Advice.Argument(value=0) String name, @Advice.FieldValue(value="$$_hibernate_compositeOwners") CompositeOwnerTracker $$_hibernate_compositeOwners) {
            if ($$_hibernate_compositeOwners != null) {
                $$_hibernate_compositeOwners.callOwner("." + name);
            }
        }
    }

    static class CompositeDirtyCheckingHandler {
        CompositeDirtyCheckingHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@Advice.FieldValue(value="$$_hibernate_compositeOwners") CompositeOwnerTracker $$_hibernate_compositeOwners) {
            if ($$_hibernate_compositeOwners != null) {
                $$_hibernate_compositeOwners.callOwner("");
            }
        }
    }

    static class CompositeFieldDirtyCheckingHandler {
        CompositeFieldDirtyCheckingHandler() {
        }

        @Advice.OnMethodEnter
        static void enter(@FieldName String fieldName, @FieldValue Object field) {
            if (field != null) {
                ((CompositeTracker)field).$$_hibernate_clearOwner(fieldName);
            }
        }

        @Advice.OnMethodExit
        static void exit(@Advice.This CompositeOwner self, @FieldName String fieldName, @FieldValue Object field) {
            if (field != null) {
                ((CompositeTracker)field).$$_hibernate_setOwner(fieldName, self);
            }
            self.$$_hibernate_trackChange(fieldName);
        }
    }

    static class InitializeLazyAttributeLoadingInterceptor {
        InitializeLazyAttributeLoadingInterceptor() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_removeDirtyFields(@Advice.Argument(value=0, readOnly=false) LazyAttributeLoadingInterceptor lazyInterceptor, @Advice.FieldValue(value="$$_hibernate_attributeInterceptor") PersistentAttributeInterceptor $$_hibernate_attributeInterceptor) {
            if ($$_hibernate_attributeInterceptor instanceof LazyAttributeLoadingInterceptor) {
                lazyInterceptor = (LazyAttributeLoadingInterceptor)$$_hibernate_attributeInterceptor;
            }
        }
    }

    static class ClearDirtyCollectionNames {
        ClearDirtyCollectionNames() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_clearDirtyCollectionNames(@Advice.This ExtendedSelfDirtinessTracker self, @Advice.FieldValue(value="$$_hibernate_collectionTracker", readOnly=false) CollectionTracker $$_hibernate_collectionTracker) {
            if ($$_hibernate_collectionTracker == null) {
                $$_hibernate_collectionTracker = new SimpleCollectionTracker();
            }
            self.$$_hibernate_removeDirtyFields(null);
        }
    }

    static class MapGetCollectionClearDirtyNames {
        MapGetCollectionClearDirtyNames() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_clearDirtyCollectionNames(@FieldName String fieldName, @FieldValue Map<?, ?> map, @Advice.Argument(value=0, readOnly=false) LazyAttributeLoadingInterceptor lazyInterceptor, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if (lazyInterceptor == null || lazyInterceptor.isAttributeLoaded(fieldName)) {
                if (map == null || map instanceof PersistentCollection && !((PersistentCollection)((Object)map)).wasInitialized()) {
                    $$_hibernate_collectionTracker.add(fieldName, -1);
                } else {
                    $$_hibernate_collectionTracker.add(fieldName, map.size());
                }
            }
        }
    }

    static class CollectionGetCollectionClearDirtyNames {
        CollectionGetCollectionClearDirtyNames() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_clearDirtyCollectionNames(@FieldName String fieldName, @FieldValue Collection<?> collection, @Advice.Argument(value=0, readOnly=false) LazyAttributeLoadingInterceptor lazyInterceptor, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if (lazyInterceptor == null || lazyInterceptor.isAttributeLoaded(fieldName)) {
                if (collection == null || collection instanceof PersistentCollection && !((PersistentCollection)((Object)collection)).wasInitialized()) {
                    $$_hibernate_collectionTracker.add(fieldName, -1);
                } else {
                    $$_hibernate_collectionTracker.add(fieldName, collection.size());
                }
            }
        }
    }

    static class MapGetCollectionFieldDirtyNames {
        MapGetCollectionFieldDirtyNames() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_areCollectionFieldsDirty(@FieldName String fieldName, @FieldValue Map<?, ?> map, @Advice.Argument(value=0) DirtyTracker tracker, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if ($$_hibernate_collectionTracker != null) {
                int size = $$_hibernate_collectionTracker.getSize(fieldName);
                if (map == null && size != -1) {
                    tracker.add(fieldName);
                } else if (map != null && (!(map instanceof PersistentCollection) || ((PersistentCollection)((Object)map)).wasInitialized()) && size != map.size()) {
                    tracker.add(fieldName);
                }
            }
        }
    }

    static class CollectionGetCollectionFieldDirtyNames {
        CollectionGetCollectionFieldDirtyNames() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_areCollectionFieldsDirty(@FieldName String fieldName, @FieldValue Collection<?> collection, @Advice.Argument(value=0) DirtyTracker tracker, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if ($$_hibernate_collectionTracker != null) {
                int size = $$_hibernate_collectionTracker.getSize(fieldName);
                if (collection == null && size != -1) {
                    tracker.add(fieldName);
                } else if (collection != null && (!(collection instanceof PersistentCollection) || ((PersistentCollection)((Object)collection)).wasInitialized()) && size != collection.size()) {
                    tracker.add(fieldName);
                }
            }
        }
    }

    static class MapAreCollectionFieldsDirty {
        MapAreCollectionFieldsDirty() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_areCollectionFieldsDirty(@Advice.Return(readOnly=false) boolean returned, @FieldName String fieldName, @FieldValue Map<?, ?> map, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if (!returned && $$_hibernate_collectionTracker != null) {
                int size = $$_hibernate_collectionTracker.getSize(fieldName);
                if (map == null && size != -1) {
                    returned = true;
                } else if (map != null && (!(map instanceof PersistentCollection) || ((PersistentCollection)((Object)map)).wasInitialized()) && size != map.size()) {
                    returned = true;
                }
            }
        }
    }

    static class CollectionAreCollectionFieldsDirty {
        CollectionAreCollectionFieldsDirty() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_areCollectionFieldsDirty(@Advice.Return(readOnly=false) boolean returned, @FieldName String fieldName, @FieldValue Collection<?> collection, @Advice.FieldValue(value="$$_hibernate_collectionTracker") CollectionTracker $$_hibernate_collectionTracker) {
            if (!returned && $$_hibernate_collectionTracker != null) {
                int size = $$_hibernate_collectionTracker.getSize(fieldName);
                if (collection == null && size != -1) {
                    returned = true;
                } else if (collection != null && (!(collection instanceof PersistentCollection) || ((PersistentCollection)((Object)collection)).wasInitialized()) && size != collection.size()) {
                    returned = true;
                }
            }
        }
    }

    static class SuspendDirtyTracking {
        SuspendDirtyTracking() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_suspendDirtyTracking(@Advice.Argument(value=0) boolean suspend, @Advice.FieldValue(value="$$_hibernate_tracker", readOnly=false) DirtyTracker $$_hibernate_tracker) {
            if ($$_hibernate_tracker == null) {
                $$_hibernate_tracker = new SimpleFieldTracker();
            }
            $$_hibernate_tracker.suspend(suspend);
        }
    }

    static class ClearDirtyAttributesWithoutCollections {
        ClearDirtyAttributesWithoutCollections() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_clearDirtyAttributes(@Advice.FieldValue(value="$$_hibernate_tracker") DirtyTracker $$_hibernate_tracker) {
            if ($$_hibernate_tracker != null) {
                $$_hibernate_tracker.clear();
            }
        }
    }

    static class ClearDirtyAttributes {
        ClearDirtyAttributes() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_clearDirtyAttributes(@Advice.This ExtendedSelfDirtinessTracker self, @Advice.FieldValue(value="$$_hibernate_tracker", readOnly=false) DirtyTracker $$_hibernate_tracker) {
            if ($$_hibernate_tracker != null) {
                $$_hibernate_tracker.clear();
            }
            self.$$_hibernate_clearDirtyCollectionNames();
        }
    }

    static class AreFieldsDirtyWithoutCollections {
        AreFieldsDirtyWithoutCollections() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_hasDirtyAttributes(@Advice.Return(readOnly=false) boolean returned, @Advice.FieldValue(value="$$_hibernate_tracker") DirtyTracker $$_hibernate_tracker) {
            returned = $$_hibernate_tracker != null && !$$_hibernate_tracker.isEmpty();
        }
    }

    static class AreFieldsDirty {
        AreFieldsDirty() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_hasDirtyAttributes(@Advice.This ExtendedSelfDirtinessTracker self, @Advice.Return(readOnly=false) boolean returned, @Advice.FieldValue(value="$$_hibernate_tracker", readOnly=false) DirtyTracker $$_hibernate_tracker) {
            returned = $$_hibernate_tracker != null && !$$_hibernate_tracker.isEmpty() || self.$$_hibernate_areCollectionFieldsDirty();
        }
    }

    static class GetCollectionTrackerWithoutCollections {
        GetCollectionTrackerWithoutCollections() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_getCollectionTracker(@Advice.Return(readOnly=false) CollectionTracker returned) {
            returned = NoopCollectionTracker.INSTANCE;
        }
    }

    static class GetDirtyAttributesWithoutCollections {
        GetDirtyAttributesWithoutCollections() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_getDirtyAttributes(@Advice.Return(readOnly=false) String[] returned, @Advice.FieldValue(value="$$_hibernate_tracker") DirtyTracker $$_hibernate_tracker) {
            returned = $$_hibernate_tracker == null ? new String[]{} : $$_hibernate_tracker.get();
        }
    }

    static class GetDirtyAttributes {
        GetDirtyAttributes() {
        }

        @Advice.OnMethodExit
        static void $$_hibernate_getDirtyAttributes(@Advice.This ExtendedSelfDirtinessTracker self, @Advice.Return(readOnly=false) String[] returned, @Advice.FieldValue(value="$$_hibernate_tracker", readOnly=false) DirtyTracker $$_hibernate_tracker, @Advice.FieldValue(value="$$_hibernate_collectionTracker", readOnly=false) CollectionTracker $$_hibernate_collectionTracker) {
            if ($$_hibernate_collectionTracker == null) {
                returned = $$_hibernate_tracker == null ? new String[]{} : $$_hibernate_tracker.get();
            } else {
                if ($$_hibernate_tracker == null) {
                    $$_hibernate_tracker = new SimpleFieldTracker();
                }
                self.$$_hibernate_getCollectionFieldDirtyNames($$_hibernate_tracker);
                returned = $$_hibernate_tracker.get();
            }
        }
    }

    static class TrackChange {
        TrackChange() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_trackChange(@Advice.Argument(value=0) String name, @Advice.FieldValue(value="$$_hibernate_tracker", readOnly=false) DirtyTracker $$_hibernate_tracker) {
            if ($$_hibernate_tracker == null) {
                $$_hibernate_tracker = new SimpleFieldTracker();
            }
            $$_hibernate_tracker.add(name);
        }
    }

    static class ClearOwner {
        ClearOwner() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_setOwner(@Advice.Argument(value=0) String name, @Advice.FieldValue(value="$$_hibernate_compositeOwners", readOnly=false) CompositeOwnerTracker $$_hibernate_compositeOwners) {
            if ($$_hibernate_compositeOwners != null) {
                $$_hibernate_compositeOwners.removeOwner(name);
            }
        }
    }

    static class SetOwner {
        SetOwner() {
        }

        @Advice.OnMethodEnter
        static void $$_hibernate_setOwner(@Advice.Argument(value=0) String name, @Advice.Argument(value=1) CompositeOwner tracker, @Advice.FieldValue(value="$$_hibernate_compositeOwners", readOnly=false) CompositeOwnerTracker $$_hibernate_compositeOwners) {
            if ($$_hibernate_compositeOwners == null) {
                $$_hibernate_compositeOwners = new CompositeOwnerTracker();
            }
            $$_hibernate_compositeOwners.add(name, tracker);
        }
    }
}

