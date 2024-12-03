/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.service.repository;

import aQute.bnd.util.dto.DTO;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ResolverInfo {
    public ResolveStatus getResolveStatus(byte[] var1) throws Exception;

    public ResolveStatus getResolveStatus(String var1) throws Exception;

    public static class ResolveStatus
    extends DTO {
        public State state;
        public String message;
    }

    public static enum State {
        Pending,
        Missing,
        Unresolveable,
        Resolveable;

    }
}

