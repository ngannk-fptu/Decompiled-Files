/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyEditorRegistrar
 *  org.springframework.beans.PropertyEditorRegistry
 *  org.springframework.beans.propertyeditors.CustomCollectionEditor
 *  org.springframework.beans.propertyeditors.CustomMapEditor
 *  org.springframework.beans.propertyeditors.PropertiesEditor
 */
package org.eclipse.gemini.blueprint.blueprint.container.support;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.gemini.blueprint.blueprint.container.support.DateEditor;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;

public class BlueprintEditorRegistrar
implements PropertyEditorRegistrar {
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Date.class, (PropertyEditor)new DateEditor());
        registry.registerCustomEditor(Stack.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(Stack.class)));
        registry.registerCustomEditor(Vector.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(Vector.class)));
        registry.registerCustomEditor(Collection.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(Collection.class)));
        registry.registerCustomEditor(Set.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(Set.class)));
        registry.registerCustomEditor(SortedSet.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(SortedSet.class)));
        registry.registerCustomEditor(List.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(List.class)));
        registry.registerCustomEditor(SortedMap.class, (PropertyEditor)new CustomMapEditor(SortedMap.class));
        registry.registerCustomEditor(HashSet.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(HashSet.class)));
        registry.registerCustomEditor(LinkedHashSet.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(LinkedHashSet.class)));
        registry.registerCustomEditor(TreeSet.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(TreeSet.class)));
        registry.registerCustomEditor(ArrayList.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(ArrayList.class)));
        registry.registerCustomEditor(LinkedList.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(LinkedList.class)));
        registry.registerCustomEditor(HashMap.class, (PropertyEditor)new CustomMapEditor(HashMap.class));
        registry.registerCustomEditor(LinkedHashMap.class, (PropertyEditor)new CustomMapEditor(LinkedHashMap.class));
        registry.registerCustomEditor(Hashtable.class, (PropertyEditor)new CustomMapEditor(Hashtable.class));
        registry.registerCustomEditor(TreeMap.class, (PropertyEditor)new CustomMapEditor(TreeMap.class));
        registry.registerCustomEditor(Properties.class, (PropertyEditor)new PropertiesEditor());
        registry.registerCustomEditor(ConcurrentMap.class, (PropertyEditor)new CustomMapEditor(ConcurrentHashMap.class));
        registry.registerCustomEditor(ConcurrentHashMap.class, (PropertyEditor)new CustomMapEditor(ConcurrentHashMap.class));
        registry.registerCustomEditor(Queue.class, (PropertyEditor)((Object)new BlueprintCustomCollectionEditor(LinkedList.class)));
        registry.registerCustomEditor(Dictionary.class, (PropertyEditor)new CustomMapEditor(Hashtable.class));
    }

    private static class BlueprintCustomCollectionEditor
    extends CustomCollectionEditor {
        public BlueprintCustomCollectionEditor(Class<? extends Collection> collectionType) {
            super(collectionType);
        }

        protected Collection createCollection(Class collectionType, int initialCapacity) {
            if (!collectionType.isInterface()) {
                try {
                    return (Collection)collectionType.newInstance();
                }
                catch (Exception ex) {
                    throw new IllegalArgumentException("Could not instantiate collection class [" + collectionType.getName() + "]: " + ex.getMessage());
                }
            }
            if (List.class.equals((Object)collectionType)) {
                return new ArrayList(initialCapacity);
            }
            if (Set.class.equals((Object)collectionType)) {
                return new LinkedHashSet(initialCapacity);
            }
            if (SortedSet.class.equals((Object)collectionType)) {
                return new TreeSet();
            }
            return new ArrayList(initialCapacity);
        }
    }
}

