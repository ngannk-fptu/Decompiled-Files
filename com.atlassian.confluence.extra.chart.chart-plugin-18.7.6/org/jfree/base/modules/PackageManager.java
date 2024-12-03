/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.jfree.base.AbstractBoot;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.base.config.PropertyFileConfiguration;
import org.jfree.base.log.PadMessage;
import org.jfree.base.modules.DefaultModuleInfo;
import org.jfree.base.modules.Module;
import org.jfree.base.modules.ModuleInfo;
import org.jfree.base.modules.PackageSorter;
import org.jfree.base.modules.PackageState;
import org.jfree.util.Configuration;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;

public final class PackageManager {
    private static final int RETURN_MODULE_LOADED = 0;
    private static final int RETURN_MODULE_UNKNOWN = 1;
    private static final int RETURN_MODULE_ERROR = 2;
    private final PackageConfiguration packageConfiguration;
    private final ArrayList modules;
    private final ArrayList initSections;
    private AbstractBoot booter;
    private static HashMap instances;

    public static PackageManager createInstance(AbstractBoot booter) {
        if (instances == null) {
            instances = new HashMap();
            PackageManager manager = new PackageManager(booter);
            instances.put(booter, manager);
            return manager;
        }
        PackageManager manager = (PackageManager)instances.get(booter);
        if (manager == null) {
            manager = new PackageManager(booter);
            instances.put(booter, manager);
        }
        return manager;
    }

    private PackageManager(AbstractBoot booter) {
        if (booter == null) {
            throw new NullPointerException();
        }
        this.booter = booter;
        this.packageConfiguration = new PackageConfiguration();
        this.modules = new ArrayList();
        this.initSections = new ArrayList();
    }

    public boolean isModuleAvailable(ModuleInfo moduleDescription) {
        PackageState[] packageStates = this.modules.toArray(new PackageState[this.modules.size()]);
        for (int i = 0; i < packageStates.length; ++i) {
            PackageState state = packageStates[i];
            if (!state.getModule().getModuleClass().equals(moduleDescription.getModuleClass())) continue;
            return state.getState() == 2;
        }
        return false;
    }

    public void load(String modulePrefix) {
        if (this.initSections.contains(modulePrefix)) {
            return;
        }
        this.initSections.add(modulePrefix);
        Configuration config = this.booter.getGlobalConfig();
        Iterator it = config.findPropertyKeys(modulePrefix);
        int count = 0;
        while (it.hasNext()) {
            String moduleClass;
            String key = (String)it.next();
            if (!key.endsWith(".Module") || (moduleClass = config.getConfigProperty(key)) == null || moduleClass.length() <= 0) continue;
            this.addModule(moduleClass);
            ++count;
        }
        Log.debug("Loaded a total of " + count + " modules under prefix: " + modulePrefix);
    }

    public synchronized void initializeModules() {
        PackageState mod;
        int i;
        PackageSorter.sort(this.modules);
        for (i = 0; i < this.modules.size(); ++i) {
            mod = (PackageState)this.modules.get(i);
            if (!mod.configure(this.booter)) continue;
            Log.debug(new Log.SimpleMessage("Conf: ", new PadMessage(mod.getModule().getModuleClass(), 70), " [", mod.getModule().getSubSystem(), "]"));
        }
        for (i = 0; i < this.modules.size(); ++i) {
            mod = (PackageState)this.modules.get(i);
            if (!mod.initialize(this.booter)) continue;
            Log.debug(new Log.SimpleMessage("Init: ", new PadMessage(mod.getModule().getModuleClass(), 70), " [", mod.getModule().getSubSystem(), "]"));
        }
    }

    public synchronized void addModule(String modClass) {
        DefaultModuleInfo modInfo = new DefaultModuleInfo(modClass, null, null, null);
        ArrayList loadModules = new ArrayList();
        if (this.loadModule(modInfo, new ArrayList(), loadModules, false)) {
            for (int i = 0; i < loadModules.size(); ++i) {
                Module mod = (Module)loadModules.get(i);
                this.modules.add(new PackageState(mod));
            }
        }
    }

    private int containsModule(ArrayList tempModules, ModuleInfo module) {
        int i;
        if (tempModules != null) {
            ModuleInfo[] mods = tempModules.toArray(new ModuleInfo[tempModules.size()]);
            for (i = 0; i < mods.length; ++i) {
                if (!mods[i].getModuleClass().equals(module.getModuleClass())) continue;
                return 0;
            }
        }
        PackageState[] packageStates = this.modules.toArray(new PackageState[this.modules.size()]);
        for (i = 0; i < packageStates.length; ++i) {
            if (!packageStates[i].getModule().getModuleClass().equals(module.getModuleClass())) continue;
            if (packageStates[i].getState() == -2) {
                return 2;
            }
            return 0;
        }
        return 1;
    }

    private void dropFailedModule(PackageState state) {
        if (!this.modules.contains(state)) {
            this.modules.add(state);
        }
    }

    private boolean loadModule(ModuleInfo moduleInfo, ArrayList incompleteModules, ArrayList modules, boolean fatal) {
        try {
            Class<?> c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(moduleInfo.getModuleClass());
            Module module = (Module)c.newInstance();
            if (!this.acceptVersion(moduleInfo, module)) {
                Log.warn("Module " + module.getName() + ": required version: " + moduleInfo + ", but found Version: \n" + module);
                PackageState state = new PackageState(module, -2);
                this.dropFailedModule(state);
                return false;
            }
            int moduleContained = this.containsModule(modules, module);
            if (moduleContained == 2) {
                Log.debug("Indicated failure for module: " + module.getModuleClass());
                PackageState state = new PackageState(module, -2);
                this.dropFailedModule(state);
                return false;
            }
            if (moduleContained == 1) {
                if (incompleteModules.contains(module)) {
                    Log.error(new Log.SimpleMessage("Circular module reference: This module definition is invalid: ", module.getClass()));
                    PackageState state = new PackageState(module, -2);
                    this.dropFailedModule(state);
                    return false;
                }
                incompleteModules.add(module);
                ModuleInfo[] required = module.getRequiredModules();
                for (int i = 0; i < required.length; ++i) {
                    if (this.loadModule(required[i], incompleteModules, modules, true)) continue;
                    Log.debug("Indicated failure for module: " + module.getModuleClass());
                    PackageState state = new PackageState(module, -2);
                    this.dropFailedModule(state);
                    return false;
                }
                ModuleInfo[] optional = module.getOptionalModules();
                for (int i = 0; i < optional.length; ++i) {
                    if (this.loadModule(optional[i], incompleteModules, modules, true)) continue;
                    Log.debug(new Log.SimpleMessage("Optional module: ", optional[i].getModuleClass(), " was not loaded."));
                }
                if (this.containsModule(modules, module) == 1) {
                    modules.add(module);
                }
                incompleteModules.remove(module);
            }
            return true;
        }
        catch (ClassNotFoundException cnfe) {
            if (fatal) {
                Log.warn(new Log.SimpleMessage("Unresolved dependency for package: ", moduleInfo.getModuleClass()));
            }
            Log.debug(new Log.SimpleMessage("ClassNotFound: ", cnfe.getMessage()));
            return false;
        }
        catch (Exception e) {
            Log.warn(new Log.SimpleMessage("Exception while loading module: ", moduleInfo), e);
            return false;
        }
    }

    private boolean acceptVersion(ModuleInfo moduleRequirement, Module module) {
        int compare;
        if (moduleRequirement.getMajorVersion() == null) {
            return true;
        }
        if (module.getMajorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a major version.");
        } else {
            compare = this.acceptVersion(moduleRequirement.getMajorVersion(), module.getMajorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getMinorVersion() == null) {
            return true;
        }
        if (module.getMinorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a minor version.");
        } else {
            compare = this.acceptVersion(moduleRequirement.getMinorVersion(), module.getMinorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getPatchLevel() == null) {
            return true;
        }
        if (module.getPatchLevel() == null) {
            Log.debug("Module " + module.getName() + " does not define a patch level.");
        } else if (this.acceptVersion(moduleRequirement.getPatchLevel(), module.getPatchLevel()) > 0) {
            Log.debug("Did not accept patchlevel: " + moduleRequirement.getPatchLevel() + " - " + module.getPatchLevel());
            return false;
        }
        return true;
    }

    private int acceptVersion(String modVer, String depModVer) {
        char[] depVerArray;
        char[] modVerArray;
        int mLength = Math.max(modVer.length(), depModVer.length());
        if (modVer.length() > depModVer.length()) {
            modVerArray = modVer.toCharArray();
            depVerArray = new char[mLength];
            int delta = modVer.length() - depModVer.length();
            Arrays.fill(depVerArray, 0, delta, ' ');
            System.arraycopy(depVerArray, delta, depModVer.toCharArray(), 0, depModVer.length());
        } else if (modVer.length() < depModVer.length()) {
            depVerArray = depModVer.toCharArray();
            modVerArray = new char[mLength];
            char[] b1 = new char[mLength];
            int delta = depModVer.length() - modVer.length();
            Arrays.fill(b1, 0, delta, ' ');
            System.arraycopy(b1, delta, modVer.toCharArray(), 0, modVer.length());
        } else {
            depVerArray = depModVer.toCharArray();
            modVerArray = modVer.toCharArray();
        }
        return new String(modVerArray).compareTo(new String(depVerArray));
    }

    public PackageConfiguration getPackageConfiguration() {
        return this.packageConfiguration;
    }

    public Module[] getAllModules() {
        Module[] mods = new Module[this.modules.size()];
        for (int i = 0; i < this.modules.size(); ++i) {
            PackageState state = (PackageState)this.modules.get(i);
            mods[i] = state.getModule();
        }
        return mods;
    }

    public Module[] getActiveModules() {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (int i = 0; i < this.modules.size(); ++i) {
            PackageState state = (PackageState)this.modules.get(i);
            if (state.getState() != 2) continue;
            mods.add(state.getModule());
        }
        return mods.toArray(new Module[mods.size()]);
    }

    public void printUsedModules(PrintStream p) {
        int i;
        Module[] allMods = this.getAllModules();
        ArrayList<Module> activeModules = new ArrayList<Module>();
        ArrayList<Module> failedModules = new ArrayList<Module>();
        for (i = 0; i < allMods.length; ++i) {
            if (this.isModuleAvailable(allMods[i])) {
                activeModules.add(allMods[i]);
                continue;
            }
            failedModules.add(allMods[i]);
        }
        p.print("Active modules: ");
        p.println(activeModules.size());
        p.println("----------------------------------------------------------");
        for (i = 0; i < activeModules.size(); ++i) {
            Module mod = (Module)activeModules.get(i);
            p.print(new PadMessage(mod.getModuleClass(), 70));
            p.print(" [");
            p.print(mod.getSubSystem());
            p.println("]");
            p.print("  Version: ");
            p.print(mod.getMajorVersion());
            p.print("-");
            p.print(mod.getMinorVersion());
            p.print("-");
            p.print(mod.getPatchLevel());
            p.print(" Producer: ");
            p.println(mod.getProducer());
            p.print("  Description: ");
            p.println(mod.getDescription());
        }
    }

    public static class PackageConfiguration
    extends PropertyFileConfiguration {
        public void insertConfiguration(HierarchicalConfiguration config) {
            super.insertConfiguration(config);
        }
    }
}

