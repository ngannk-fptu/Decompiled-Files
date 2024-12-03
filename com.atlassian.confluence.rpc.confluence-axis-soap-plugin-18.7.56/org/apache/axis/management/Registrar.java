/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.management;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;

public class Registrar {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$management$Registrar == null ? (class$org$apache$axis$management$Registrar = Registrar.class$("org.apache.axis.management.Registrar")) : class$org$apache$axis$management$Registrar).getName());
    private static ModelerBinding modelerBinding = null;
    static /* synthetic */ Class class$org$apache$axis$management$Registrar;
    static /* synthetic */ Class class$org$apache$axis$management$Registrar$ModelerBinding;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$lang$String;

    public static boolean register(Object objectToRegister, String name, String context) {
        if (Registrar.isBound()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Registering " + objectToRegister + " as " + name));
            }
            return modelerBinding.register(objectToRegister, name, context);
        }
        return false;
    }

    public static boolean isBound() {
        Registrar.createModelerBinding();
        return modelerBinding.canBind();
    }

    private static void createModelerBinding() {
        if (modelerBinding == null) {
            modelerBinding = new ModelerBinding();
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class ModelerBinding {
        protected static Log log = LogFactory.getLog((class$org$apache$axis$management$Registrar$ModelerBinding == null ? (class$org$apache$axis$management$Registrar$ModelerBinding = Registrar.class$("org.apache.axis.management.Registrar$ModelerBinding")) : class$org$apache$axis$management$Registrar$ModelerBinding).getName());
        Object registry;
        Method registerComponent;

        public ModelerBinding() {
            this.bindToModeler();
        }

        public boolean canBind() {
            return this.registry != null;
        }

        public boolean register(Object objectToRegister, String name, String context) {
            if (this.registry != null) {
                Object[] args = new Object[]{objectToRegister, name, context};
                try {
                    this.registerComponent.invoke(this.registry, args);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Registered " + name + " in " + context));
                    }
                }
                catch (IllegalAccessException e) {
                    log.error((Object)e);
                    return false;
                }
                catch (IllegalArgumentException e) {
                    log.error((Object)e);
                    return false;
                }
                catch (InvocationTargetException e) {
                    log.error((Object)e);
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean bindToModeler() {
            Class<?> clazz;
            Exception ex = null;
            try {
                clazz = Class.forName("org.apache.commons.modeler.Registry");
            }
            catch (ClassNotFoundException e) {
                this.registry = null;
                return false;
            }
            try {
                Class[] getRegistryArgs = new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = Registrar.class$("java.lang.Object")) : class$java$lang$Object, class$java$lang$Object == null ? (class$java$lang$Object = Registrar.class$("java.lang.Object")) : class$java$lang$Object};
                Method getRegistry = clazz.getMethod("getRegistry", getRegistryArgs);
                Object[] getRegistryOptions = new Object[]{null, null};
                this.registry = getRegistry.invoke(null, getRegistryOptions);
                Class[] registerArgs = new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = Registrar.class$("java.lang.Object")) : class$java$lang$Object, class$java$lang$String == null ? (class$java$lang$String = Registrar.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = Registrar.class$("java.lang.String")) : class$java$lang$String};
                this.registerComponent = clazz.getMethod("registerComponent", registerArgs);
            }
            catch (IllegalAccessException e) {
                ex = e;
            }
            catch (IllegalArgumentException e) {
                ex = e;
            }
            catch (InvocationTargetException e) {
                ex = e;
            }
            catch (NoSuchMethodException e) {
                ex = e;
            }
            if (ex != null) {
                log.warn((Object)Messages.getMessage("Registrar.cantregister"), (Throwable)ex);
                this.registry = null;
                return false;
            }
            return true;
        }
    }
}

