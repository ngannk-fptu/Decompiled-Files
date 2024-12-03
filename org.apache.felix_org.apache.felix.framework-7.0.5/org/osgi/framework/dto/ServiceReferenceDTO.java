/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework.dto;

import java.util.Map;
import org.osgi.dto.DTO;

public class ServiceReferenceDTO
extends DTO {
    public long id;
    public long bundle;
    public Map<String, Object> properties;
    public long[] usingBundles;
}

