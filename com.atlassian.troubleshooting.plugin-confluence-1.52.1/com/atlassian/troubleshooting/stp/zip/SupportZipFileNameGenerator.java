/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.api.ClusterNode;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportZipFileNameGenerator {
    private static final String NOT_ALLOWED_NODEID_CHARS_REGEX = "[^a-zA-Z0-9_-]+";
    private final SupportApplicationInfo applicationInfo;
    private final ClusterService clusterService;

    @Autowired
    public SupportZipFileNameGenerator(SupportApplicationInfo applicationInfo, ClusterService clusterService) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.clusterService = Objects.requireNonNull(clusterService);
    }

    public File generate(File supportDir) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String baseFilename = String.format("%s%s_support_%s", this.applicationInfo.getApplicationName(), this.nodePart(), format.format(new Date()));
        String filename = baseFilename + ".zip";
        int counter = 0;
        File zipFile = new File(supportDir, filename);
        while (zipFile.exists()) {
            zipFile = new File(supportDir, baseFilename + "-" + counter++ + ".zip");
        }
        return zipFile;
    }

    private String nodePart() {
        return this.clusterService.getCurrentNode().map(n -> this.nodeName((ClusterNode)n) + "_" + this.sanitize(n.getId()) + this.nodeAddress((ClusterNode)n)).orElse("");
    }

    private String nodeName(ClusterNode node) {
        return node.getName().map(s -> "_" + this.sanitize((String)s)).orElse("");
    }

    private String nodeAddress(ClusterNode node) {
        return node.getInetAddress().map(s -> "_" + this.sanitize((String)s)).orElse("");
    }

    private String sanitize(String s) {
        return s.replaceAll(NOT_ALLOWED_NODEID_CHARS_REGEX, "-");
    }
}

