/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.vdurmont.semver4j.Semver
 *  com.vdurmont.semver4j.Semver$SemverType
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AbstractCloudMigrationRegistrar;
import com.atlassian.migration.app.MigratabliltyInfoKt;
import com.vdurmont.semver4j.Semver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 \n2\u00020\u0001:\u0003\n\u000b\fB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\u00072\b\u0010\t\u001a\u0004\u0018\u00010\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"Lcom/atlassian/migration/app/MigratabliltyInfo;", "", "registrar", "Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar;", "(Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar;)V", "getCloudAppKeys", "", "", "serverAppKey", "cloudAppKeyFromMaa", "Companion", "MigrabilityVersionException", "VersionRange", "app-migration-assistant"})
public final class MigratabliltyInfo {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final AbstractCloudMigrationRegistrar registrar;

    public MigratabliltyInfo(@NotNull AbstractCloudMigrationRegistrar registrar) {
        Intrinsics.checkNotNullParameter((Object)registrar, (String)"registrar");
        this.registrar = registrar;
    }

    @NotNull
    public final Set<String> getCloudAppKeys(@NotNull String serverAppKey, @Nullable String cloudAppKeyFromMaa) {
        Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
        MigratabliltyInfoKt.access$getLog$p().debug("Getting cloud app keys for serverAppKey={}, cloudAppKeyFromMaa={}", (Object)serverAppKey, (Object)cloudAppKeyFromMaa);
        Set cloudAppKeys = new HashSet();
        if (StringUtils.isNotBlank((CharSequence)cloudAppKeyFromMaa)) {
            String string = cloudAppKeyFromMaa;
            Intrinsics.checkNotNull((Object)string);
            cloudAppKeys.add(string);
        } else {
            cloudAppKeys.addAll((Collection)this.registrar.getRegisteredCloudKeys(serverAppKey));
        }
        if (cloudAppKeys.isEmpty()) {
            cloudAppKeys.add(serverAppKey);
        }
        MigratabliltyInfoKt.access$getLog$p().debug("Got cloud app keys as cloudAppKeys={}", (Object)cloudAppKeys);
        return cloudAppKeys;
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u000e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bJ\u001c\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00042\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\u001a\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u00042\b\u0010\u000e\u001a\u0004\u0018\u00010\u0004\u00a8\u0006\u000f"}, d2={"Lcom/atlassian/migration/app/MigratabliltyInfo$Companion;", "", "()V", "calculateNextMigratableVersion", "", "pluginVersion", "cloudMigrationAssistantCompatibility", "cloudMigrationAssistantCompatibilityRangeList", "", "Lcom/atlassian/migration/app/MigratabliltyInfo$VersionRange;", "needsUpgrade", "", "installedVersion", "cloudCompatibleRangeVersion", "cloudCompatibleVersion", "app-migration-assistant"})
    public static final class Companion {
        private Companion() {
        }

        public final boolean needsUpgrade(@Nullable String installedVersion, @Nullable String cloudCompatibleVersion) throws MigrabilityVersionException {
            MigratabliltyInfoKt.access$getLog$p().debug("Needs upgrade check for installedVersion={}, cloudCompatibleVersion={}", (Object)installedVersion, (Object)cloudCompatibleVersion);
            try {
                Semver cloudCompatibleSemver = new Semver(cloudCompatibleVersion, Semver.SemverType.LOOSE);
                Semver installedSemver = new Semver(installedVersion, Semver.SemverType.LOOSE);
                if (installedSemver.isLowerThan(cloudCompatibleSemver)) {
                    MigratabliltyInfoKt.access$getLog$p().debug("Needed upgrade is true");
                    return true;
                }
            }
            catch (Exception e) {
                String string = "Could not read versions for installed version: %s, cloud compatible version: %s";
                Object[] objectArray = new Object[]{installedVersion, cloudCompatibleVersion};
                String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(format, *args)");
                throw new MigrabilityVersionException(string2);
            }
            MigratabliltyInfoKt.access$getLog$p().debug("Needed upgrade is false");
            return false;
        }

        public final boolean needsUpgrade(@NotNull String installedVersion, @NotNull List<VersionRange> cloudCompatibleRangeVersion) throws MigrabilityVersionException {
            Intrinsics.checkNotNullParameter((Object)installedVersion, (String)"installedVersion");
            Intrinsics.checkNotNullParameter(cloudCompatibleRangeVersion, (String)"cloudCompatibleRangeVersion");
            if (cloudCompatibleRangeVersion.isEmpty()) {
                throw new MigrabilityVersionException("cloudCompatibleRangeVersion is empty");
            }
            try {
                Semver installedSemVer = new Semver(installedVersion, Semver.SemverType.LOOSE);
                Iterable $this$forEach$iv = cloudCompatibleRangeVersion;
                boolean $i$f$forEach = false;
                for (Object element$iv : $this$forEach$iv) {
                    VersionRange range = (VersionRange)element$iv;
                    boolean bl = false;
                    if (range.getEnd() == null && installedSemVer.isGreaterThanOrEqualTo(new Semver(range.getStart(), Semver.SemverType.LOOSE))) {
                        return false;
                    }
                    if (range.getStart() == null && installedSemVer.isLowerThanOrEqualTo(new Semver(range.getEnd(), Semver.SemverType.LOOSE))) {
                        return false;
                    }
                    if (range.getStart() == null || range.getEnd() == null || !installedSemVer.isLowerThanOrEqualTo(range.getEnd()) || !installedSemVer.isGreaterThanOrEqualTo(range.getStart())) continue;
                    return false;
                }
                return true;
            }
            catch (Exception e) {
                String string = "Could not read versions for installed version: %s and cloud compatible version ranges";
                Object[] objectArray = new Object[]{installedVersion};
                String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(format, *args)");
                throw new MigrabilityVersionException(string2);
            }
        }

        /*
         * WARNING - void declaration
         */
        @NotNull
        public final String calculateNextMigratableVersion(@NotNull String pluginVersion, @NotNull String cloudMigrationAssistantCompatibility, @Nullable List<VersionRange> cloudMigrationAssistantCompatibilityRangeList) {
            void $this$forEach$iv;
            Iterable $this$sortedBy$iv;
            void $this$filterTo$iv$iv;
            Intrinsics.checkNotNullParameter((Object)pluginVersion, (String)"pluginVersion");
            Intrinsics.checkNotNullParameter((Object)cloudMigrationAssistantCompatibility, (String)"cloudMigrationAssistantCompatibility");
            List<VersionRange> ranges = cloudMigrationAssistantCompatibilityRangeList;
            Collection collection = ranges;
            if (collection == null || collection.isEmpty()) {
                return cloudMigrationAssistantCompatibility;
            }
            Semver currentVersion = new Semver(pluginVersion, Semver.SemverType.LOOSE);
            Iterable $this$filter$iv = ranges;
            boolean $i$f$filter = false;
            Iterable iterable = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                VersionRange it = (VersionRange)element$iv$iv;
                boolean bl = false;
                CharSequence charSequence = it.getStart();
                boolean bl2 = !(charSequence == null || StringsKt.isBlank((CharSequence)charSequence));
                if (!bl2) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filter$iv = (List)destination$iv$iv;
            boolean $i$f$sortedBy = false;
            $this$sortedBy$iv = CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(){

                public final int compare(T a, T b) {
                    VersionRange it = (VersionRange)a;
                    boolean bl = false;
                    Comparable comparable = (Comparable)((Object)it.getStart());
                    it = (VersionRange)b;
                    Comparable comparable2 = comparable;
                    bl = false;
                    return ComparisonsKt.compareValues((Comparable)comparable2, (Comparable)((Comparable)((Object)it.getStart())));
                }
            });
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                VersionRange it = (VersionRange)element$iv;
                boolean bl = false;
                if (!new Semver(it.getStart(), Semver.SemverType.LOOSE).isGreaterThan(currentVersion)) continue;
                String string = it.getStart();
                if (string == null) {
                    string = "";
                }
                return string;
            }
            return "";
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00060\u0001j\u0002`\u0002B\u000f\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\u0006"}, d2={"Lcom/atlassian/migration/app/MigratabliltyInfo$MigrabilityVersionException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "message", "", "(Ljava/lang/String;)V", "app-migration-assistant"})
    public static final class MigrabilityVersionException
    extends Exception {
        public MigrabilityVersionException(@Nullable String message) {
            super(message);
        }
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\n\b\u0001\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0001\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0005R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\t"}, d2={"Lcom/atlassian/migration/app/MigratabliltyInfo$VersionRange;", "", "start", "", "end", "(Ljava/lang/String;Ljava/lang/String;)V", "getEnd", "()Ljava/lang/String;", "getStart", "app-migration-assistant"})
    public static final class VersionRange {
        @Nullable
        private final String start;
        @Nullable
        private final String end;

        @JsonCreator
        public VersionRange(@JsonProperty(value="start") @Nullable String start, @JsonProperty(value="end") @Nullable String end) {
            this.start = start;
            this.end = end;
        }

        @Nullable
        public final String getStart() {
            return this.start;
        }

        @Nullable
        public final String getEnd() {
            return this.end;
        }
    }
}

