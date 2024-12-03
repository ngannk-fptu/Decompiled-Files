/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.dto;

import java.util.List;
import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

public class FrameworkDTO
extends DTO {
    public List<BundleDTO> bundles;
    public Map<String, Object> properties;
    public List<ServiceReferenceDTO> services;
}

