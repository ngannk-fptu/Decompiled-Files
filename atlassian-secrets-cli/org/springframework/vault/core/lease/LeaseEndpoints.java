/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.web.client.RestOperations;

public enum LeaseEndpoints {
    Legacy{

        @Override
        public void revoke(Lease lease, RestOperations operations) {
            operations.exchange("sys/revoke", HttpMethod.PUT, LeaseEndpoints.getLeaseRevocationBody(lease), Map.class, lease.getLeaseId());
        }

        @Override
        public Lease renew(Lease lease, RestOperations operations) {
            HttpEntity leaseRenewalEntity = LeaseEndpoints.getLeaseRenewalBody(lease);
            ResponseEntity entity = LeaseEndpoints.put(operations, leaseRenewalEntity, "sys/renew");
            Assert.state(entity.getBody() != null, "Renew response must not be null");
            return LeaseEndpoints.toLease((Map)entity.getBody());
        }
    }
    ,
    SysLeases{

        @Override
        public void revoke(Lease lease, RestOperations operations) {
            Leases.revoke(lease, operations);
        }

        @Override
        public Lease renew(Lease lease, RestOperations operations) {
            return Leases.renew(lease, operations);
        }
    }
    ,
    Leases{

        @Override
        public void revoke(Lease lease, RestOperations operations) {
            operations.exchange("sys/leases/revoke", HttpMethod.PUT, LeaseEndpoints.getLeaseRevocationBody(lease), Map.class, lease.getLeaseId());
        }

        @Override
        public Lease renew(Lease lease, RestOperations operations) {
            HttpEntity leaseRenewalEntity = LeaseEndpoints.getLeaseRenewalBody(lease);
            ResponseEntity entity = LeaseEndpoints.put(operations, leaseRenewalEntity, "sys/leases/renew");
            Assert.state(entity.getBody() != null, "Renew response must not be null");
            return LeaseEndpoints.toLease((Map)entity.getBody());
        }
    }
    ,
    LeasesRevokedByPrefix{

        @Override
        public void revoke(Lease lease, RestOperations operations) {
            String endpoint = "sys/leases/revoke-prefix/" + lease.getLeaseId();
            operations.put(endpoint, null, new Object[0]);
        }

        @Override
        public Lease renew(Lease lease, RestOperations operations) {
            HttpEntity leaseRenewalEntity = LeaseEndpoints.getLeaseRenewalBody(lease);
            ResponseEntity entity = LeaseEndpoints.put(operations, leaseRenewalEntity, "sys/leases/renew");
            Assert.state(entity.getBody() != null, "Renew response must not be null");
            return LeaseEndpoints.toLease((Map)entity.getBody());
        }
    };


    abstract void revoke(Lease var1, RestOperations var2);

    abstract Lease renew(Lease var1, RestOperations var2);

    private static Lease toLease(Map<String, Object> body) {
        String leaseId = (String)body.get("lease_id");
        Number leaseDuration = (Number)body.get("lease_duration");
        boolean renewable = (Boolean)body.get("renewable");
        return Lease.of(leaseId, Duration.ofSeconds(leaseDuration != null ? leaseDuration.longValue() : 0L), renewable);
    }

    private static HttpEntity<Object> getLeaseRenewalBody(Lease lease) {
        HashMap<String, String> leaseRenewalData = new HashMap<String, String>();
        leaseRenewalData.put("lease_id", lease.getLeaseId());
        leaseRenewalData.put("increment", Long.toString(lease.getLeaseDuration().getSeconds()));
        return new HttpEntity<Object>(leaseRenewalData);
    }

    private static HttpEntity<Object> getLeaseRevocationBody(Lease lease) {
        HashMap<String, String> leaseRenewalData = new HashMap<String, String>();
        leaseRenewalData.put("lease_id", lease.getLeaseId());
        return new HttpEntity<Object>(leaseRenewalData);
    }

    private static ResponseEntity<Map<String, Object>> put(RestOperations operations, HttpEntity<Object> entity, String url) {
        return (ResponseEntity)ResponseEntity.class.cast(operations.exchange(url, HttpMethod.PUT, entity, Map.class, new Object[0]));
    }
}

