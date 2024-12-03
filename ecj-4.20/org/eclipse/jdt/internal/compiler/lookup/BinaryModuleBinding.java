/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashMap;
import java.util.stream.Stream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class BinaryModuleBinding
extends ModuleBinding {
    private IModule.IPackageExport[] unresolvedExports;
    private IModule.IPackageExport[] unresolvedOpens;
    private char[][] unresolvedUses;
    private IModule.IService[] unresolvedProvides;

    public static ModuleBinding create(IModule module, LookupEnvironment existingEnvironment) {
        if (module.isAutomatic()) {
            return new AutomaticModuleBinding(module, existingEnvironment);
        }
        return new BinaryModuleBinding((IBinaryModule)module, existingEnvironment);
    }

    private BinaryModuleBinding(IBinaryModule module, LookupEnvironment existingEnvironment) {
        super(module.name(), existingEnvironment);
        existingEnvironment.root.knownModules.put(this.moduleName, this);
        this.cachePartsFrom(module);
    }

    void cachePartsFrom(IBinaryModule module) {
        if (module.isOpen()) {
            this.modifiers |= 0x20;
        }
        this.tagBits |= module.getTagBits();
        IModule.IModuleReference[] requiresReferences = module.requires();
        this.requires = new ModuleBinding[requiresReferences.length];
        this.requiresTransitive = new ModuleBinding[requiresReferences.length];
        int count = 0;
        int transitiveCount = 0;
        int i = 0;
        while (i < requiresReferences.length) {
            ModuleBinding requiredModule = this.environment.getModule(requiresReferences[i].name());
            if (requiredModule != null) {
                this.requires[count++] = requiredModule;
                if (requiresReferences[i].isTransitive()) {
                    this.requiresTransitive[transitiveCount++] = requiredModule;
                }
            }
            ++i;
        }
        if (count < this.requires.length) {
            this.requires = new ModuleBinding[count];
            System.arraycopy(this.requires, 0, this.requires, 0, count);
        }
        if (transitiveCount < this.requiresTransitive.length) {
            this.requiresTransitive = new ModuleBinding[transitiveCount];
            System.arraycopy(this.requiresTransitive, 0, this.requiresTransitive, 0, transitiveCount);
        }
        this.unresolvedExports = module.exports();
        this.unresolvedOpens = module.opens();
        this.unresolvedUses = module.uses();
        this.unresolvedProvides = module.provides();
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            this.scanForNullDefaultAnnotation(module);
        }
        if ((this.tagBits & 0x400000000000L) != 0L || this.environment.globalOptions.storeAnnotations) {
            this.setAnnotations(BinaryTypeBinding.createAnnotations(module.getAnnotations(), this.environment, null), true);
        }
    }

    private void scanForNullDefaultAnnotation(IBinaryModule binaryModule) {
        char[][] nonNullByDefaultAnnotationName = this.environment.getNonNullByDefaultAnnotationName();
        if (nonNullByDefaultAnnotationName == null) {
            return;
        }
        IBinaryAnnotation[] annotations = binaryModule.getAnnotations();
        if (annotations != null) {
            int nullness = 0;
            int length = annotations.length;
            int i = 0;
            while (i < length) {
                int typeBit;
                char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L' && (typeBit = this.environment.getNullAnnotationBit(BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName))) == 128) {
                    nullness |= BinaryTypeBinding.getNonNullByDefaultValue(annotations[i], this.environment);
                }
                ++i;
            }
            this.defaultNullness = nullness;
        }
    }

    @Override
    public PlainPackageBinding[] getExports() {
        if (this.exportedPackages == null && this.unresolvedExports != null) {
            this.resolvePackages();
        }
        return super.getExports();
    }

    @Override
    public PlainPackageBinding[] getOpens() {
        if (this.openedPackages == null && this.unresolvedOpens != null) {
            this.resolvePackages();
        }
        return super.getOpens();
    }

    private void resolvePackages() {
        PlainPackageBinding declaredPackage;
        this.exportedPackages = new PlainPackageBinding[this.unresolvedExports.length];
        int count = 0;
        int i = 0;
        while (i < this.unresolvedExports.length) {
            IModule.IPackageExport export = this.unresolvedExports[i];
            declaredPackage = this.getOrCreateDeclaredPackage(CharOperation.splitOn('.', export.name()));
            this.exportedPackages[count++] = declaredPackage;
            declaredPackage.isExported = Boolean.TRUE;
            this.recordExportRestrictions(declaredPackage, export.targets());
            ++i;
        }
        if (count < this.exportedPackages.length) {
            this.exportedPackages = new PlainPackageBinding[count];
            System.arraycopy(this.exportedPackages, 0, this.exportedPackages, 0, count);
        }
        this.openedPackages = new PlainPackageBinding[this.unresolvedOpens.length];
        count = 0;
        i = 0;
        while (i < this.unresolvedOpens.length) {
            IModule.IPackageExport opens = this.unresolvedOpens[i];
            declaredPackage = this.getOrCreateDeclaredPackage(CharOperation.splitOn('.', opens.name()));
            this.openedPackages[count++] = declaredPackage;
            this.recordOpensRestrictions(declaredPackage, opens.targets());
            ++i;
        }
        if (count < this.openedPackages.length) {
            this.openedPackages = new PlainPackageBinding[count];
            System.arraycopy(this.openedPackages, 0, this.openedPackages, 0, count);
        }
    }

    @Override
    PlainPackageBinding getDeclaredPackage(char[] flatName) {
        this.getExports();
        this.completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        return super.getDeclaredPackage(flatName);
    }

    @Override
    public TypeBinding[] getUses() {
        if (this.uses == null) {
            this.uses = new TypeBinding[this.unresolvedUses.length];
            int i = 0;
            while (i < this.unresolvedUses.length) {
                this.uses[i] = this.environment.getType(CharOperation.splitOn('.', this.unresolvedUses[i]), this);
                ++i;
            }
        }
        return super.getUses();
    }

    @Override
    public TypeBinding[] getServices() {
        if (this.services == null) {
            this.resolveServices();
        }
        return super.getServices();
    }

    @Override
    public TypeBinding[] getImplementations(TypeBinding binding) {
        if (this.implementations == null) {
            this.resolveServices();
        }
        return super.getImplementations(binding);
    }

    private void resolveServices() {
        this.services = new TypeBinding[this.unresolvedProvides.length];
        this.implementations = new HashMap();
        int i = 0;
        while (i < this.unresolvedProvides.length) {
            this.services[i] = this.environment.getType(CharOperation.splitOn('.', this.unresolvedProvides[i].name()), this);
            char[][] implNames = this.unresolvedProvides[i].with();
            TypeBinding[] impls = new TypeBinding[implNames.length];
            int j = 0;
            while (j < implNames.length) {
                impls[j] = this.environment.getType(CharOperation.splitOn('.', implNames[j]), this);
                ++j;
            }
            this.implementations.put(this.services[i], impls);
            ++i;
        }
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.retrieveAnnotations(this);
    }

    private static class AutomaticModuleBinding
    extends ModuleBinding {
        boolean autoNameFromManifest;
        boolean hasScannedPackages;

        public AutomaticModuleBinding(IModule module, LookupEnvironment existingEnvironment) {
            super(module.name(), existingEnvironment);
            existingEnvironment.root.knownModules.put(this.moduleName, this);
            this.isAuto = true;
            this.autoNameFromManifest = module.isAutoNameFromManifest();
            this.requires = Binding.NO_MODULES;
            this.requiresTransitive = Binding.NO_MODULES;
            this.exportedPackages = Binding.NO_PLAIN_PACKAGES;
            this.hasScannedPackages = false;
        }

        @Override
        public boolean hasUnstableAutoName() {
            return !this.autoNameFromManifest;
        }

        @Override
        public ModuleBinding[] getRequiresTransitive() {
            if (this.requiresTransitive == NO_MODULES) {
                char[][] autoModules = ((IModuleAwareNameEnvironment)this.environment.nameEnvironment).getAllAutomaticModules();
                this.requiresTransitive = (ModuleBinding[])Stream.of(autoModules).filter(name -> !CharOperation.equals(name, this.moduleName)).map(name -> this.environment.getModule((char[])name)).filter(m -> m != null).toArray(ModuleBinding[]::new);
            }
            return this.requiresTransitive;
        }

        @Override
        PlainPackageBinding getDeclaredPackage(char[] flatName) {
            if (!this.hasScannedPackages) {
                char[][] cArray = ((IModuleAwareNameEnvironment)this.environment.nameEnvironment).listPackages(this.nameForCUCheck());
                int n = cArray.length;
                int n2 = 0;
                while (n2 < n) {
                    char[] packageName = cArray[n2];
                    this.getOrCreateDeclaredPackage(CharOperation.splitOn('.', packageName));
                    ++n2;
                }
                this.hasScannedPackages = true;
            }
            return (PlainPackageBinding)this.declaredPackages.get(flatName);
        }

        @Override
        public char[] nameForLookup() {
            return ANY_NAMED;
        }

        @Override
        public char[] nameForCUCheck() {
            return this.moduleName;
        }
    }
}

