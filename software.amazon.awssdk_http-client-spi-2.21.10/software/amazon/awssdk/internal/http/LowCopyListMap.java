/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.Lazy
 */
package software.amazon.awssdk.internal.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.Lazy;

@SdkInternalApi
public final class LowCopyListMap {
    private LowCopyListMap() {
    }

    public static ForBuilder emptyHeaders() {
        return new ForBuilder(() -> new TreeMap(String.CASE_INSENSITIVE_ORDER));
    }

    public static ForBuilder emptyQueryParameters() {
        return new ForBuilder(LinkedHashMap::new);
    }

    @ThreadSafe
    public static final class ForBuildable {
        private final Supplier<Map<String, List<String>>> mapConstructor;
        private final Lazy<Map<String, List<String>>> deeplyUnmodifiableMap;
        private final Map<String, List<String>> map;

        private ForBuildable(ForBuilder forBuilder) {
            this.mapConstructor = forBuilder.mapConstructor;
            this.map = forBuilder.map;
            this.deeplyUnmodifiableMap = new Lazy(() -> CollectionUtils.deepUnmodifiableMap(this.map, this.mapConstructor));
        }

        public Map<String, List<String>> forExternalRead() {
            return (Map)this.deeplyUnmodifiableMap.getValue();
        }

        public Map<String, List<String>> forInternalRead() {
            return this.map;
        }

        public ForBuilder forBuilder() {
            return new ForBuilder(this);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ForBuildable that = (ForBuildable)o;
            return this.map.equals(that.map);
        }

        public int hashCode() {
            return this.map.hashCode();
        }
    }

    @NotThreadSafe
    public static final class ForBuilder {
        private final Supplier<Map<String, List<String>>> mapConstructor;
        private boolean mapIsShared = false;
        private Map<String, List<String>> map;

        private ForBuilder(Supplier<Map<String, List<String>>> mapConstructor) {
            this.mapConstructor = mapConstructor;
            this.map = mapConstructor.get();
        }

        private ForBuilder(ForBuildable forBuildable) {
            this.mapConstructor = forBuildable.mapConstructor;
            this.map = forBuildable.map;
            this.mapIsShared = true;
        }

        public void clear() {
            this.map = this.mapConstructor.get();
            this.mapIsShared = false;
        }

        public void setFromExternal(Map<String, List<String>> map) {
            this.map = CollectionUtils.deepCopyMap(map, this.mapConstructor);
            this.mapIsShared = false;
        }

        public Map<String, List<String>> forInternalWrite() {
            if (this.mapIsShared) {
                this.map = CollectionUtils.deepCopyMap(this.map, this.mapConstructor);
                this.mapIsShared = false;
            }
            return this.map;
        }

        public Map<String, List<String>> forInternalRead() {
            return this.map;
        }

        public ForBuildable forBuildable() {
            this.mapIsShared = true;
            return new ForBuildable(this);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ForBuilder that = (ForBuilder)o;
            return this.map.equals(that.map);
        }

        public int hashCode() {
            return this.map.hashCode();
        }
    }
}

