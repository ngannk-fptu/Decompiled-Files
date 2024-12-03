/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.MapUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.FnVisitor;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Identifier;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SingleArgFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Value;
import software.amazon.awssdk.utils.MapUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public class ParseUrl
extends SingleArgFn {
    public static final String ID = "parseURL";
    public static final Identifier SCHEME = Identifier.of("scheme");
    public static final Identifier AUTHORITY = Identifier.of("authority");
    public static final Identifier PATH = Identifier.of("path");
    public static final Identifier NORMALIZED_PATH = Identifier.of("normalizedPath");
    public static final Identifier IS_IP = Identifier.of("isIp");

    public ParseUrl(FnNode fnNode) {
        super(fnNode);
    }

    public static ParseUrl ofExprs(Expr expr) {
        return new ParseUrl(FnNode.ofExprs(ID, expr));
    }

    @Override
    public <T> T acceptFnVisitor(FnVisitor<T> visitor) {
        return visitor.visitParseUrl(this);
    }

    @Override
    protected Value evalArg(Value arg) {
        String url = arg.expectString();
        try {
            String normalizedPath;
            String[] dottedParts;
            URL parsed = new URL(url);
            String path = parsed.getPath();
            if (parsed.getQuery() != null) {
                return Value.none();
            }
            boolean isIpAddr = false;
            String host = parsed.getHost();
            if (host.startsWith("[") && host.endsWith("]")) {
                isIpAddr = true;
            }
            if ((dottedParts = host.split("\\.")).length == 4 && Arrays.stream(dottedParts).allMatch(part -> {
                try {
                    int value = Integer.parseInt(part);
                    return value >= 0 && value <= 255;
                }
                catch (NumberFormatException ex) {
                    return false;
                }
            })) {
                isIpAddr = true;
            }
            if (StringUtils.isBlank((CharSequence)path)) {
                normalizedPath = "/";
            } else {
                StringBuilder builder = new StringBuilder();
                if (!path.startsWith("/")) {
                    builder.append("/");
                }
                builder.append(path);
                if (!path.endsWith("/")) {
                    builder.append("/");
                }
                normalizedPath = builder.toString();
            }
            return Value.fromRecord(MapUtils.of((Object)SCHEME, (Object)Value.fromStr(parsed.getProtocol()), (Object)AUTHORITY, (Object)Value.fromStr(parsed.getAuthority()), (Object)PATH, (Object)Value.fromStr(path), (Object)NORMALIZED_PATH, (Object)Value.fromStr(normalizedPath), (Object)IS_IP, (Object)Value.fromBool(isIpAddr)));
        }
        catch (MalformedURLException e) {
            return Value.none();
        }
    }
}

