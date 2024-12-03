/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.MapUtils
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.DefaultPartitionDataProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Outputs;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Parameter;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Partition;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Partitions;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.MapUtils;

@SdkInternalApi
public class PartitionFn
extends SingleArgFn {
    public static final String ID = "aws.partition";
    public static final Identifier NAME = Identifier.of("name");
    public static final Identifier DNS_SUFFIX = Identifier.of("dnsSuffix");
    public static final Identifier DUAL_STACK_DNS_SUFFIX = Identifier.of("dualStackDnsSuffix");
    public static final Identifier SUPPORTS_FIPS = Identifier.of("supportsFIPS");
    public static final Identifier SUPPORTS_DUAL_STACK = Identifier.of("supportsDualStack");
    public static final Identifier INFERRED = Identifier.of("inferred");
    private final LazyValue<PartitionData> partitionData = LazyValue.builder().initializer(this::loadPartitionData).build();
    private final LazyValue<Partition> awsPartition = LazyValue.builder().initializer(this::findAwsPartition).build();

    public PartitionFn(FnNode node) {
        super(node);
    }

    public static PartitionFn ofExprs(Expr expr) {
        return new PartitionFn(FnNode.ofExprs(ID, expr));
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitPartition(this);
    }

    public static PartitionFn fromParam(Parameter param) {
        return PartitionFn.ofExprs(param.expr());
    }

    @Override
    public Value evalArg(Value arg) {
        String regionName = arg.expectString();
        PartitionData data = this.partitionData.value();
        boolean inferred = false;
        Partition matchedPartition = (Partition)data.regionMap.get(regionName);
        if (matchedPartition == null) {
            for (Partition p : data.partitions) {
                Pattern regex = Pattern.compile(p.regionRegex());
                if (!regex.matcher(regionName).matches()) continue;
                matchedPartition = p;
                inferred = true;
                break;
            }
        }
        if (matchedPartition == null) {
            matchedPartition = this.awsPartition.value();
        }
        Outputs matchedOutputs = matchedPartition.outputs();
        return Value.fromRecord(MapUtils.of((Object)NAME, (Object)Value.fromStr(matchedPartition.id()), (Object)DNS_SUFFIX, (Object)Value.fromStr(matchedOutputs.dnsSuffix()), (Object)DUAL_STACK_DNS_SUFFIX, (Object)Value.fromStr(matchedOutputs.dualStackDnsSuffix()), (Object)SUPPORTS_FIPS, (Object)Value.fromBool(matchedOutputs.supportsFips()), (Object)SUPPORTS_DUAL_STACK, (Object)Value.fromBool(matchedOutputs.supportsDualStack()), (Object)INFERRED, (Object)Value.fromBool(inferred)));
    }

    private PartitionData loadPartitionData() {
        DefaultPartitionDataProvider provider = new DefaultPartitionDataProvider();
        Partitions partitions = provider.loadPartitions();
        PartitionData partitionData = new PartitionData();
        partitions.partitions().forEach(part -> {
            partitionData.partitions.add(part);
            part.regions().forEach((name, override) -> partitionData.regionMap.put(name, part));
        });
        return partitionData;
    }

    private Partition findAwsPartition() {
        return this.partitionData.value().partitions.stream().filter(p -> p.id().equalsIgnoreCase("aws")).findFirst().orElse(null);
    }

    private static final class LazyValue<T> {
        private final Supplier<T> initializer;
        private T value;
        private boolean initialized;

        private LazyValue(Builder<T> builder) {
            this.initializer = ((Builder)builder).initializer;
        }

        public T value() {
            if (!this.initialized) {
                this.value = this.initializer.get();
                this.initialized = true;
            }
            return this.value;
        }

        public static <T> Builder<T> builder() {
            return new Builder();
        }

        public static class Builder<T> {
            private Supplier<T> initializer;

            public Builder<T> initializer(Supplier<T> initializer) {
                this.initializer = initializer;
                return this;
            }

            public LazyValue<T> build() {
                return new LazyValue(this);
            }
        }
    }

    private static class PartitionData {
        private final List<Partition> partitions = new ArrayList<Partition>();
        private final Map<String, Partition> regionMap = new HashMap<String, Partition>();

        private PartitionData() {
        }
    }
}

