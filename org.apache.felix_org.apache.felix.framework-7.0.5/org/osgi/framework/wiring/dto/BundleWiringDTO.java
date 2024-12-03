/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.wiring.dto;

import java.util.Set;
import org.osgi.dto.DTO;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.resource.dto.WiringDTO;

public class BundleWiringDTO
extends DTO {
    public long bundle;
    public int root;
    public Set<NodeDTO> nodes;
    public Set<BundleRevisionDTO> resources;

    public static class NodeDTO
    extends WiringDTO {
        public boolean inUse;
        public boolean current;
    }
}

