/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.resource.dto;

import org.osgi.dto.DTO;
import org.osgi.resource.dto.CapabilityRefDTO;
import org.osgi.resource.dto.RequirementRefDTO;

public class WireDTO
extends DTO {
    public CapabilityRefDTO capability;
    public RequirementRefDTO requirement;
    public int provider;
    public int requirer;
}

