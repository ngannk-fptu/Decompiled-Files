/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.wiring.dto;

import java.util.Set;
import org.osgi.dto.DTO;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;

public class FrameworkWiringDTO
extends DTO {
    public Set<BundleWiringDTO.NodeDTO> wirings;
    public Set<BundleRevisionDTO> resources;
}

