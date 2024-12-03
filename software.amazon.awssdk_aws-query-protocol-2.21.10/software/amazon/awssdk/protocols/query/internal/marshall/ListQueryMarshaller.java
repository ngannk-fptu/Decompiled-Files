/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.protocols.query.internal.marshall;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryMarshallerContext;

@SdkInternalApi
public class ListQueryMarshaller
implements QueryMarshaller<List<?>> {
    private final PathResolver pathResolver;

    private ListQueryMarshaller(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public void marshall(QueryMarshallerContext context, String path, List<?> val, SdkField<List<?>> sdkField) {
        if (val.isEmpty() && !(val instanceof SdkAutoConstructList)) {
            context.request().putRawQueryParameter(path, "");
            return;
        }
        for (int i = 0; i < val.size(); ++i) {
            ListTrait listTrait = (ListTrait)sdkField.getTrait(ListTrait.class);
            String listPath = this.pathResolver.resolve(path, i, listTrait);
            QueryMarshaller<Object> marshaller = context.marshallerRegistry().getMarshaller(listTrait.memberFieldInfo().marshallingType(), val);
            marshaller.marshall(context, listPath, val.get(i), (SdkField<Object>)listTrait.memberFieldInfo());
        }
    }

    public static ListQueryMarshaller awsQuery() {
        return new ListQueryMarshaller((path, i, listTrait) -> listTrait.isFlattened() ? String.format("%s.%d", path, i + 1) : String.format("%s.%s.%d", path, listTrait.memberFieldInfo().locationName(), i + 1));
    }

    public static ListQueryMarshaller ec2Query() {
        return new ListQueryMarshaller((path, i, listTrait) -> String.format("%s.%d", path, i + 1));
    }

    @FunctionalInterface
    private static interface PathResolver {
        public String resolve(String var1, int var2, ListTrait var3);
    }
}

