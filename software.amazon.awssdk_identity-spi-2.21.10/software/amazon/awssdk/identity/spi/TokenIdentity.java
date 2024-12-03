/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.identity.spi;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@ThreadSafe
public interface TokenIdentity
extends Identity {
    public String token();

    public static TokenIdentity create(final String token) {
        Validate.paramNotNull((Object)token, (String)"token");
        return new TokenIdentity(){

            @Override
            public String token() {
                return token;
            }

            public String toString() {
                return ToString.builder((String)"TokenIdentity").add("token", (Object)token).build();
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                TokenIdentity that = (TokenIdentity)o;
                return Objects.equals(token, that.token());
            }

            public int hashCode() {
                return Objects.hashCode(this.token());
            }
        };
    }
}

