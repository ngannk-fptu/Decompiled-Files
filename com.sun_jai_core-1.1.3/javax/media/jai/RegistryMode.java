/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.media.jai.registry.CollectionRegistryMode;
import javax.media.jai.registry.RemoteRenderableRegistryMode;
import javax.media.jai.registry.RemoteRenderedRegistryMode;
import javax.media.jai.registry.RenderableCollectionRegistryMode;
import javax.media.jai.registry.RenderableRegistryMode;
import javax.media.jai.registry.RenderedRegistryMode;
import javax.media.jai.registry.TileDecoderRegistryMode;
import javax.media.jai.registry.TileEncoderRegistryMode;
import javax.media.jai.util.CaselessStringKey;

public class RegistryMode {
    private static Hashtable registryModes = new Hashtable(4);
    private static HashSet immutableNames = new HashSet();
    private CaselessStringKey name;
    private Class descriptorClass;
    private Class productClass;
    private Method factoryMethod;
    private boolean arePreferencesSupported;
    private boolean arePropertiesSupported;

    private static boolean addMode(RegistryMode mode, boolean immutable) {
        if (registryModes.containsKey(mode.name)) {
            return false;
        }
        registryModes.put(mode.name, mode);
        if (immutable) {
            immutableNames.add(mode.name);
        }
        return true;
    }

    public static synchronized boolean addMode(RegistryMode mode) {
        return RegistryMode.addMode(mode, false);
    }

    public static synchronized boolean removeMode(String name) {
        CaselessStringKey key = new CaselessStringKey(name);
        if (immutableNames.contains(key)) {
            return false;
        }
        return registryModes.remove(key) != null;
    }

    public static synchronized String[] getModeNames() {
        String[] names = new String[registryModes.size()];
        int i = 0;
        Enumeration e = registryModes.keys();
        while (e.hasMoreElements()) {
            CaselessStringKey key = (CaselessStringKey)e.nextElement();
            names[i++] = key.getName();
        }
        if (i <= 0) {
            return null;
        }
        return names;
    }

    public static synchronized String[] getModeNames(Class descriptorClass) {
        String[] names = new String[registryModes.size()];
        int i = 0;
        Enumeration e = registryModes.elements();
        while (e.hasMoreElements()) {
            RegistryMode mode = (RegistryMode)e.nextElement();
            if (mode.getDescriptorClass() != descriptorClass) continue;
            names[i++] = mode.getName();
        }
        if (i <= 0) {
            return null;
        }
        String[] matchedNames = new String[i];
        for (int j = 0; j < i; ++j) {
            matchedNames[j] = names[j];
        }
        return matchedNames;
    }

    public static RegistryMode getMode(String name) {
        CaselessStringKey key = new CaselessStringKey(name);
        return (RegistryMode)registryModes.get(key);
    }

    public static synchronized Set getDescriptorClasses() {
        HashSet<Class> set = new HashSet<Class>();
        Enumeration e = registryModes.elements();
        while (e.hasMoreElements()) {
            RegistryMode mode = (RegistryMode)e.nextElement();
            set.add(mode.descriptorClass);
        }
        return set;
    }

    protected RegistryMode(String name, Class descriptorClass, Class productClass, Method factoryMethod, boolean arePreferencesSupported, boolean arePropertiesSupported) {
        this.name = new CaselessStringKey(name);
        this.descriptorClass = descriptorClass;
        this.productClass = productClass;
        this.factoryMethod = factoryMethod;
        this.arePreferencesSupported = arePreferencesSupported;
        this.arePropertiesSupported = arePropertiesSupported;
    }

    public final String getName() {
        return this.name.getName();
    }

    public final Method getFactoryMethod() {
        return this.factoryMethod;
    }

    public final boolean arePreferencesSupported() {
        return this.arePreferencesSupported;
    }

    public final boolean arePropertiesSupported() {
        return this.arePropertiesSupported;
    }

    public final Class getDescriptorClass() {
        return this.descriptorClass;
    }

    public final Class getProductClass() {
        return this.productClass;
    }

    public final Class getFactoryClass() {
        return this.factoryMethod.getDeclaringClass();
    }

    static {
        RegistryMode.addMode(new RenderedRegistryMode(), true);
        RegistryMode.addMode(new RenderableRegistryMode(), true);
        RegistryMode.addMode(new CollectionRegistryMode(), true);
        RegistryMode.addMode(new RenderableCollectionRegistryMode(), true);
        RegistryMode.addMode(new RemoteRenderedRegistryMode(), true);
        RegistryMode.addMode(new RemoteRenderableRegistryMode(), true);
        RegistryMode.addMode(new TileEncoderRegistryMode(), true);
        RegistryMode.addMode(new TileDecoderRegistryMode(), true);
    }
}

