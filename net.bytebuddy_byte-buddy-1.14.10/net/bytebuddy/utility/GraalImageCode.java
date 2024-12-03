/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GraalImageCode
extends Enum<GraalImageCode> {
    public static final /* enum */ GraalImageCode AGENT;
    public static final /* enum */ GraalImageCode BUILD;
    public static final /* enum */ GraalImageCode RUNTIME;
    public static final /* enum */ GraalImageCode UNKNOWN;
    public static final /* enum */ GraalImageCode NONE;
    @MaybeNull
    private static GraalImageCode current;
    private final boolean defined;
    private final boolean nativeImageExecution;
    private static final /* synthetic */ GraalImageCode[] $VALUES;
    private static final boolean ACCESS_CONTROLLER;

    public static GraalImageCode[] values() {
        return (GraalImageCode[])$VALUES.clone();
    }

    public static GraalImageCode valueOf(String name) {
        return Enum.valueOf(GraalImageCode.class, name);
    }

    @SuppressFBWarnings(value={"LI_LAZY_INIT_STATIC", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="This behaviour is intended to avoid early binding in native images.")
    public static GraalImageCode getCurrent() {
        GraalImageCode current = GraalImageCode.current;
        if (current == null) {
            String vendor;
            String value = GraalImageCode.doPrivileged(new GetSystemPropertyAction("org.graalvm.nativeimage.imagecode"));
            current = value == null ? ((vendor = GraalImageCode.doPrivileged(new GetSystemPropertyAction("java.vm.vendor"))) != null && vendor.toLowerCase(Locale.US).contains("graalvm") ? GraalImageCode.doPrivileged(ImageCodeContextAction.INSTANCE) : NONE) : (value.equalsIgnoreCase("agent") ? AGENT : (value.equalsIgnoreCase("runtime") ? RUNTIME : (value.equalsIgnoreCase("buildtime") ? BUILD : UNKNOWN)));
            GraalImageCode.current = current;
        }
        return current;
    }

    public <T> T[] sorted(T[] value, Comparator<? super T> comparator) {
        if (this.defined) {
            Arrays.sort(value, comparator);
        }
        return value;
    }

    @MaybeNull
    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    private GraalImageCode(boolean defined, boolean nativeImageExecution) {
        this.defined = defined;
        this.nativeImageExecution = nativeImageExecution;
    }

    public boolean isDefined() {
        return this.defined;
    }

    public boolean isNativeImageExecution() {
        return this.nativeImageExecution;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        try {
            Class.forName("java.security.AccessController", false, null);
            ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            ACCESS_CONTROLLER = false;
        }
        catch (SecurityException securityException) {
            ACCESS_CONTROLLER = true;
        }
        AGENT = new GraalImageCode(true, false);
        BUILD = new GraalImageCode(true, false);
        RUNTIME = new GraalImageCode(true, true);
        UNKNOWN = new GraalImageCode(false, false);
        NONE = new GraalImageCode(false, false);
        $VALUES = new GraalImageCode[]{AGENT, BUILD, RUNTIME, UNKNOWN, NONE};
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum ImageCodeContextAction implements PrivilegedAction<GraalImageCode>
    {
        INSTANCE;


        @Override
        public GraalImageCode run() {
            try {
                Method method = Class.forName("java.lang.management.ManagementFactory").getMethod("getRuntimeMXBean", new Class[0]);
                List arguments = (List)method.getReturnType().getMethod("getInputArguments", new Class[0]).invoke(method.invoke(null, new Object[0]), new Object[0]);
                for (String argument : arguments) {
                    if (!argument.startsWith("-agentlib:native-image-agent")) continue;
                    return AGENT;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return NONE;
        }
    }
}

