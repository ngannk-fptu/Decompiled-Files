/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jfree.base.modules.Module;
import org.jfree.base.modules.ModuleInfo;
import org.jfree.base.modules.PackageState;
import org.jfree.util.Log;

public final class PackageSorter {
    private PackageSorter() {
    }

    public static void sort(List modules) {
        int i;
        HashMap<String, SortModule> moduleMap = new HashMap<String, SortModule>();
        ArrayList<PackageState> errorModules = new ArrayList<PackageState>();
        ArrayList<SortModule> weightModules = new ArrayList<SortModule>();
        for (int i2 = 0; i2 < modules.size(); ++i2) {
            PackageState state = (PackageState)modules.get(i2);
            if (state.getState() == -2) {
                errorModules.add(state);
                continue;
            }
            SortModule mod = new SortModule(state);
            weightModules.add(mod);
            moduleMap.put(state.getModule().getModuleClass(), mod);
        }
        Object[] weigths = weightModules.toArray(new SortModule[weightModules.size()]);
        for (int i3 = 0; i3 < weigths.length; ++i3) {
            SortModule sortMod = weigths[i3];
            sortMod.setDependSubsystems(PackageSorter.collectSubsystemModules(sortMod.getState().getModule(), moduleMap));
        }
        boolean doneWork = true;
        while (doneWork) {
            doneWork = false;
            for (int i4 = 0; i4 < weigths.length; ++i4) {
                Object mod = weigths[i4];
                int position = PackageSorter.searchModulePosition((SortModule)mod, moduleMap);
                if (position == ((SortModule)mod).getPosition()) continue;
                ((SortModule)mod).setPosition(position);
                doneWork = true;
            }
        }
        Arrays.sort(weigths);
        modules.clear();
        for (i = 0; i < weigths.length; ++i) {
            modules.add(((SortModule)weigths[i]).getState());
        }
        for (i = 0; i < errorModules.size(); ++i) {
            modules.add(errorModules.get(i));
        }
    }

    private static int searchModulePosition(SortModule smodule, HashMap moduleMap) {
        SortModule reqMod;
        String moduleName;
        int modPos;
        Module module = smodule.getState().getModule();
        int position = 0;
        ModuleInfo[] modInfo = module.getOptionalModules();
        for (modPos = 0; modPos < modInfo.length; ++modPos) {
            moduleName = modInfo[modPos].getModuleClass();
            reqMod = (SortModule)moduleMap.get(moduleName);
            if (reqMod == null || reqMod.getPosition() < position) continue;
            position = reqMod.getPosition() + 1;
        }
        modInfo = module.getRequiredModules();
        for (modPos = 0; modPos < modInfo.length; ++modPos) {
            moduleName = modInfo[modPos].getModuleClass();
            reqMod = (SortModule)moduleMap.get(moduleName);
            if (reqMod == null) {
                Log.warn("Invalid state: Required dependency of '" + moduleName + "' had an error.");
                continue;
            }
            if (reqMod.getPosition() < position) continue;
            position = reqMod.getPosition() + 1;
        }
        String subSystem = module.getSubSystem();
        Iterator it = moduleMap.values().iterator();
        while (it.hasNext()) {
            Module subSysMod;
            SortModule mod = (SortModule)it.next();
            if (mod.getState().getModule() == module || subSystem.equals((subSysMod = mod.getState().getModule()).getSubSystem()) || !smodule.getDependSubsystems().contains(subSysMod.getSubSystem()) || PackageSorter.isBaseModule(subSysMod, module) || mod.getPosition() < position) continue;
            position = mod.getPosition() + 1;
        }
        return position;
    }

    private static boolean isBaseModule(Module mod, ModuleInfo mi) {
        int i;
        ModuleInfo[] info = mod.getRequiredModules();
        for (i = 0; i < info.length; ++i) {
            if (!info[i].getModuleClass().equals(mi.getModuleClass())) continue;
            return true;
        }
        info = mod.getOptionalModules();
        for (i = 0; i < info.length; ++i) {
            if (!info[i].getModuleClass().equals(mi.getModuleClass())) continue;
            return true;
        }
        return false;
    }

    private static ArrayList collectSubsystemModules(Module childMod, HashMap moduleMap) {
        Object dependentModule;
        int i;
        ArrayList<String> collector = new ArrayList<String>();
        ModuleInfo[] info = childMod.getRequiredModules();
        for (i = 0; i < info.length; ++i) {
            dependentModule = (SortModule)moduleMap.get(info[i].getModuleClass());
            if (dependentModule == null) {
                Log.warn(new Log.SimpleMessage("A dependent module was not found in the list of known modules.", info[i].getModuleClass()));
                continue;
            }
            collector.add(((SortModule)dependentModule).getState().getModule().getSubSystem());
        }
        info = childMod.getOptionalModules();
        for (i = 0; i < info.length; ++i) {
            dependentModule = (Module)moduleMap.get(info[i].getModuleClass());
            if (dependentModule == null) {
                Log.warn("A dependent module was not found in the list of known modules.");
                continue;
            }
            collector.add(dependentModule.getSubSystem());
        }
        return collector;
    }

    private static class SortModule
    implements Comparable {
        private int position = -1;
        private final PackageState state;
        private ArrayList dependSubsystems;

        public SortModule(PackageState state) {
            this.state = state;
        }

        public ArrayList getDependSubsystems() {
            return this.dependSubsystems;
        }

        public void setDependSubsystems(ArrayList dependSubsystems) {
            this.dependSubsystems = dependSubsystems;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public PackageState getState() {
            return this.state;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SortModule: ");
            buffer.append(this.position);
            buffer.append(" ");
            buffer.append(this.state.getModule().getName());
            buffer.append(" ");
            buffer.append(this.state.getModule().getModuleClass());
            return buffer.toString();
        }

        public int compareTo(Object o) {
            SortModule otherModule = (SortModule)o;
            if (this.position > otherModule.position) {
                return 1;
            }
            if (this.position < otherModule.position) {
                return -1;
            }
            return 0;
        }
    }
}

