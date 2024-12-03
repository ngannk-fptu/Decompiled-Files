/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.resource.dto;

import java.util.List;
import org.osgi.dto.DTO;
import org.osgi.resource.dto.CapabilityRefDTO;
import org.osgi.resource.dto.RequirementRefDTO;
import org.osgi.resource.dto.WireDTO;

public class WiringDTO
extends DTO {
    public int id;
    public List<CapabilityRefDTO> capabilities;
    public List<RequirementRefDTO> requirements;
    public List<WireDTO> providedWires;
    public List<WireDTO> requiredWires;
    public int resource;
}

