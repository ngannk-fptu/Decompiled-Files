/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.web.multipart.support;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;

public abstract class StandardServletPartUtils {
    public static MultiValueMap<String, Part> getParts(HttpServletRequest request) throws MultipartException {
        try {
            LinkedMultiValueMap parts = new LinkedMultiValueMap();
            for (Part part : request.getParts()) {
                parts.add((Object)part.getName(), (Object)part);
            }
            return parts;
        }
        catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public static List<Part> getParts(HttpServletRequest request, String name) throws MultipartException {
        try {
            ArrayList<Part> parts = new ArrayList<Part>(1);
            for (Part part : request.getParts()) {
                if (!part.getName().equals(name)) continue;
                parts.add(part);
            }
            return parts;
        }
        catch (Exception ex) {
            throw new MultipartException("Failed to get request parts", ex);
        }
    }

    public static void bindParts(HttpServletRequest request, MutablePropertyValues mpvs, boolean bindEmpty) throws MultipartException {
        StandardServletPartUtils.getParts(request).forEach((key, values) -> {
            if (values.size() == 1) {
                Part part = (Part)values.get(0);
                if (bindEmpty || part.getSize() > 0L) {
                    mpvs.add(key, (Object)part);
                }
            } else {
                mpvs.add(key, values);
            }
        });
    }
}

