/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.resource.dto;

import java.util.Map;
import org.osgi.dto.DTO;

public class RequirementDTO
extends DTO {
    public int id;
    public String namespace;
    public Map<String, String> directives;
    public Map<String, Object> attributes;
    public int resource;
}

