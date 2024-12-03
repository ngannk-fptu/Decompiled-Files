/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import org.jfree.base.modules.Module;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.Log;

public class PackageState {
    public static final int STATE_NEW = 0;
    public static final int STATE_CONFIGURED = 1;
    public static final int STATE_INITIALIZED = 2;
    public static final int STATE_ERROR = -2;
    private final Module module;
    private int state;

    public PackageState(Module module) {
        this(module, 0);
    }

    public PackageState(Module module, int state) {
        if (module == null) {
            throw new NullPointerException("Module must not be null.");
        }
        if (state != 1 && state != -2 && state != 2 && state != 0) {
            throw new IllegalArgumentException("State is not valid");
        }
        this.module = module;
        this.state = state;
    }

    public boolean configure(SubSystem subSystem) {
        if (this.state == 0) {
            try {
                this.module.configure(subSystem);
                this.state = 1;
                return true;
            }
            catch (NoClassDefFoundError noClassDef) {
                Log.warn(new Log.SimpleMessage("Unable to load module classes for ", this.module.getName(), ":", noClassDef.getMessage()));
                this.state = -2;
            }
            catch (Exception e) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to configure the module " + this.module.getName(), e);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to configure the module " + this.module.getName());
                }
                this.state = -2;
            }
        }
        return false;
    }

    public Module getModule() {
        return this.module;
    }

    public int getState() {
        return this.state;
    }

    public boolean initialize(SubSystem subSystem) {
        if (this.state == 1) {
            try {
                this.module.initialize(subSystem);
                this.state = 2;
                return true;
            }
            catch (NoClassDefFoundError noClassDef) {
                Log.warn(new Log.SimpleMessage("Unable to load module classes for ", this.module.getName(), ":", noClassDef.getMessage()));
                this.state = -2;
            }
            catch (ModuleInitializeException me) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName(), me);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName());
                }
                this.state = -2;
            }
            catch (Exception e) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName(), e);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName());
                }
                this.state = -2;
            }
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageState)) {
            return false;
        }
        PackageState packageState = (PackageState)o;
        return this.module.getModuleClass().equals(packageState.module.getModuleClass());
    }

    public int hashCode() {
        return this.module.hashCode();
    }
}

