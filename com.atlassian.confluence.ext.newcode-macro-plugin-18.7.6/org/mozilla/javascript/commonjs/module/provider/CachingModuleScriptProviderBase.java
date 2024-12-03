/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.commonjs.module.provider;

import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

public abstract class CachingModuleScriptProviderBase
implements ModuleScriptProvider,
Serializable {
    private static final long serialVersionUID = -1L;
    private static final int loadConcurrencyLevel;
    private static final int loadLockShift;
    private static final int loadLockMask;
    private static final int loadLockCount;
    private final Object[] loadLocks = new Object[loadLockCount];
    private final ModuleSourceProvider moduleSourceProvider;

    protected CachingModuleScriptProviderBase(ModuleSourceProvider moduleSourceProvider) {
        for (int i = 0; i < this.loadLocks.length; ++i) {
            this.loadLocks[i] = new Object();
        }
        this.moduleSourceProvider = moduleSourceProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public ModuleScript getModuleScript(Context cx, String moduleId, URI moduleUri, URI baseUri, Scriptable paths) throws Exception {
        ModuleScript moduleScript;
        Throwable throwable;
        Reader reader;
        block19: {
            ModuleSource moduleSource;
            block17: {
                ModuleScript moduleScript2;
                block18: {
                    CachedModuleScript cachedModule1 = this.getLoadedModule(moduleId);
                    Object validator1 = CachingModuleScriptProviderBase.getValidator(cachedModule1);
                    ModuleSource moduleSource2 = moduleSource = moduleUri == null ? this.moduleSourceProvider.loadSource(moduleId, paths, validator1) : this.moduleSourceProvider.loadSource(moduleUri, baseUri, validator1);
                    if (moduleSource == ModuleSourceProvider.NOT_MODIFIED) {
                        return cachedModule1.getModule();
                    }
                    if (moduleSource == null) {
                        return null;
                    }
                    reader = moduleSource.getReader();
                    throwable = null;
                    int idHash = moduleId.hashCode();
                    Object object = this.loadLocks[idHash >>> loadLockShift & loadLockMask];
                    // MONITORENTER : object
                    CachedModuleScript cachedModule2 = this.getLoadedModule(moduleId);
                    if (cachedModule2 == null || CachingModuleScriptProviderBase.equal(validator1, CachingModuleScriptProviderBase.getValidator(cachedModule2))) break block17;
                    moduleScript2 = cachedModule2.getModule();
                    // MONITOREXIT : object
                    if (reader == null) return moduleScript2;
                    if (throwable == null) break block18;
                    try {
                        reader.close();
                        return moduleScript2;
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                        return moduleScript2;
                    }
                }
                reader.close();
                return moduleScript2;
            }
            URI sourceUri = moduleSource.getUri();
            ModuleScript moduleScript3 = new ModuleScript(cx.compileReader(reader, sourceUri.toString(), 1, moduleSource.getSecurityDomain()), sourceUri, moduleSource.getBase());
            this.putLoadedModule(moduleId, moduleScript3, moduleSource.getValidator());
            moduleScript = moduleScript3;
            // MONITOREXIT : object
            if (reader == null) return moduleScript;
            if (throwable == null) break block19;
            {
                catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
            try {
                reader.close();
                return moduleScript;
            }
            catch (Throwable throwable4) {
                throwable.addSuppressed(throwable4);
                return moduleScript;
            }
        }
        reader.close();
        return moduleScript;
        catch (Throwable throwable5) {
            if (reader == null) throw throwable5;
            if (throwable == null) {
                reader.close();
                throw throwable5;
            }
            try {
                reader.close();
                throw throwable5;
            }
            catch (Throwable throwable6) {
                throwable.addSuppressed(throwable6);
                throw throwable5;
            }
        }
    }

    protected abstract void putLoadedModule(String var1, ModuleScript var2, Object var3);

    protected abstract CachedModuleScript getLoadedModule(String var1);

    private static Object getValidator(CachedModuleScript cachedModule) {
        return cachedModule == null ? null : cachedModule.getValidator();
    }

    private static boolean equal(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    protected static int getConcurrencyLevel() {
        return loadLockCount;
    }

    static {
        int ssize;
        loadConcurrencyLevel = Runtime.getRuntime().availableProcessors() * 8;
        int sshift = 0;
        for (ssize = 1; ssize < loadConcurrencyLevel; ssize <<= 1) {
            ++sshift;
        }
        loadLockShift = 32 - sshift;
        loadLockMask = ssize - 1;
        loadLockCount = ssize;
    }

    public static class CachedModuleScript {
        private final ModuleScript moduleScript;
        private final Object validator;

        public CachedModuleScript(ModuleScript moduleScript, Object validator) {
            this.moduleScript = moduleScript;
            this.validator = validator;
        }

        ModuleScript getModule() {
            return this.moduleScript;
        }

        Object getValidator() {
            return this.validator;
        }
    }
}

