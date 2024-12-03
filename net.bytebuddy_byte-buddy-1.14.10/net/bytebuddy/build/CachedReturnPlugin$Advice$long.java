/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.CachedReturnPlugin;

@SuppressFBWarnings(value={"NM_CLASS_NAMING_CONVENTION"}, justification="Name is chosen to optimize for simple lookup")
class CachedReturnPlugin$Advice$long {
    private CachedReturnPlugin$Advice$long() {
        throw new UnsupportedOperationException("This class is merely an advice template and should not be instantiated");
    }

    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    protected static long enter(@CachedReturnPlugin.CacheField long cached) {
        return cached;
    }

    @Advice.OnMethodExit
    @SuppressFBWarnings(value={"UC_USELESS_VOID_METHOD", "DLS_DEAD_LOCAL_STORE"}, justification="Advice method serves as a template")
    protected static void exit(@Advice.Return(readOnly=false) long returned, @CachedReturnPlugin.CacheField long cached) {
        if (returned == 0L) {
            returned = cached;
        } else {
            cached = returned;
        }
    }
}

