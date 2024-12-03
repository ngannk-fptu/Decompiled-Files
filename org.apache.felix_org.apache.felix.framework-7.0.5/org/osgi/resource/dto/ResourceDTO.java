/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.resource.dto;

import java.util.List;
import org.osgi.dto.DTO;
import org.osgi.resource.dto.CapabilityDTO;
import org.osgi.resource.dto.RequirementDTO;

public class ResourceDTO
extends DTO {
    public int id;
    public List<CapabilityDTO> capabilities;
    public List<RequirementDTO> requirements;
}

