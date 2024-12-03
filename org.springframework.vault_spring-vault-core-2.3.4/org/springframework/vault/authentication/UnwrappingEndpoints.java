/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpMethod
 */
package org.springframework.vault.authentication;

import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;

public enum UnwrappingEndpoints {
    Cubbyhole{

        @Override
        String getPath() {
            return "cubbyhole/response";
        }

        @Override
        VaultResponse unwrap(VaultResponse vaultResponse) {
            return VaultResponses.unwrap((String)((Map)vaultResponse.getRequiredData()).get("response"), VaultResponse.class);
        }

        @Override
        HttpMethod getUnwrapRequestMethod() {
            return HttpMethod.GET;
        }
    }
    ,
    SysWrapping{

        @Override
        String getPath() {
            return "sys/wrapping/unwrap";
        }

        @Override
        VaultResponse unwrap(VaultResponse vaultResponse) {
            return vaultResponse;
        }

        @Override
        HttpMethod getUnwrapRequestMethod() {
            return HttpMethod.POST;
        }
    };


    abstract String getPath();

    abstract VaultResponse unwrap(VaultResponse var1);

    abstract HttpMethod getUnwrapRequestMethod();
}

