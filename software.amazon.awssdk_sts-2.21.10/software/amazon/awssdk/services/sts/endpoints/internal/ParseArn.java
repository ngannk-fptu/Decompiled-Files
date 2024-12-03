/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.MapUtils
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Optional;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Arn;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.FnNode;
import software.amazon.awssdk.services.sts.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;
import software.amazon.awssdk.services.sts.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;
import software.amazon.awssdk.utils.MapUtils;

@SdkInternalApi
public class ParseArn
extends SingleArgFn {
    public static final String ID = "aws.parseArn";
    public static final Identifier PARTITION = Identifier.of("partition");
    public static final Identifier SERVICE = Identifier.of("service");
    public static final Identifier REGION = Identifier.of("region");
    public static final Identifier ACCOUNT_ID = Identifier.of("accountId");
    private static final Identifier RESOURCE_ID = Identifier.of("resourceId");

    public ParseArn(FnNode fnNode) {
        super(fnNode);
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitParseArn(this);
    }

    public static ParseArn ofExprs(Expr expr) {
        return new ParseArn(FnNode.ofExprs(ID, expr));
    }

    @Override
    protected Value evalArg(Value arg) {
        String value = arg.expectString();
        Optional<Arn> arnOpt = Arn.parse(value);
        return arnOpt.map(arn -> Value.fromRecord(MapUtils.of((Object)PARTITION, (Object)Value.fromStr(arn.partition()), (Object)SERVICE, (Object)Value.fromStr(arn.service()), (Object)REGION, (Object)Value.fromStr(arn.region()), (Object)ACCOUNT_ID, (Object)Value.fromStr(arn.accountId()), (Object)RESOURCE_ID, (Object)Value.fromArray(arn.resource().stream().map(v -> Value.fromStr(v)).collect(Collectors.toList()))))).orElse(new Value.None());
    }
}

