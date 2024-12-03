/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.MemorySensitiveClassPathRepository;

public class SyntheticRepository
extends MemorySensitiveClassPathRepository {
    private static final Map<ClassPath, SyntheticRepository> MAP = new HashMap<ClassPath, SyntheticRepository>();

    public static SyntheticRepository getInstance() {
        return SyntheticRepository.getInstance(ClassPath.SYSTEM_CLASS_PATH);
    }

    public static SyntheticRepository getInstance(ClassPath classPath) {
        return MAP.computeIfAbsent(classPath, SyntheticRepository::new);
    }

    private SyntheticRepository(ClassPath path) {
        super(path);
    }
}

