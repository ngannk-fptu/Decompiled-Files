/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CustomisationFileBundle
extends AbstractSupportZipBundle {
    private static final Logger LOG = LoggerFactory.getLogger(CustomisationFileBundle.class);
    private static final String LAYOUTS = "layouts";
    private final SupportApplicationInfo info;

    public CustomisationFileBundle(BundleManifest bundle, String title, String description, SupportApplicationInfo info, I18nResolver i18nResolver) {
        super(i18nResolver, bundle, title, description);
        this.info = info;
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        ArrayList<SupportZipBundle.Artifact> customisedFiles = new ArrayList<SupportZipBundle.Artifact>();
        File supportDir = new File(this.info.getApplicationHome(), "logs/support");
        if (supportDir.exists() || supportDir.mkdirs()) {
            customisedFiles.addAll(this.getCustomLayouts(supportDir));
            customisedFiles.addAll(this.getCustomHtml(supportDir));
            customisedFiles.addAll(this.getCustomStylesheet(supportDir));
        } else {
            LOG.error("Couldn't create export directory {}", (Object)supportDir.getAbsolutePath());
        }
        return customisedFiles;
    }

    protected abstract TreeMap<String, String> getCustomDecorators();

    protected abstract HashMap<String, String> getCustomHtml();

    protected abstract HashMap<String, String> getCustomStylesheet();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<SupportZipBundle.Artifact> getCustomLayouts(File directory) {
        TreeMap<String, String> customDecorators = this.getCustomDecorators();
        ArrayList<SupportZipBundle.Artifact> tmpCustomLayouts = new ArrayList<SupportZipBundle.Artifact>();
        File customisedLayoutsFile = new File(directory, "customLayouts.txt");
        try (FileWriter layoutsWriter = new FileWriter(customisedLayoutsFile);){
            try {
                StringBuilder customisationsString = new StringBuilder();
                if (customDecorators.isEmpty()) {
                    customisationsString.append("No custom layouts");
                }
                for (String file : customDecorators.keySet()) {
                    customisationsString.append(file + "\n");
                    try {
                        File vmdFile = new File(directory, file);
                        if (vmdFile.getParentFile().exists() || vmdFile.getParentFile().mkdirs()) {
                            try (FileWriter vmdWriter = new FileWriter(vmdFile);){
                                vmdWriter.write(customDecorators.get(file));
                            }
                            catch (IOException e) {
                                LOG.error("Failed to write vmd to {}.", (Object)vmdFile.getPath(), (Object)e);
                            }
                            tmpCustomLayouts.add(new FileSupportZipArtifact(vmdFile, LAYOUTS));
                            continue;
                        }
                        LOG.error("Couldn't create vmd directory {}", (Object)vmdFile.getParentFile().getAbsolutePath());
                    }
                    catch (Exception e) {
                        LOG.error("Can't generate vmd file.", (Throwable)e);
                    }
                }
                layoutsWriter.write(customisationsString.toString());
            }
            catch (IOException e) {
                LOG.error("Failed to write customisations to {}.", (Object)customisedLayoutsFile.getPath(), (Object)e);
            }
            finally {
                layoutsWriter.flush();
            }
            tmpCustomLayouts.add(new FileSupportZipArtifact(customisedLayoutsFile, LAYOUTS));
        }
        catch (Exception e) {
            LOG.error("Can't generate customised layouts file.", (Throwable)e);
        }
        return tmpCustomLayouts;
    }

    private List<SupportZipBundle.Artifact> getCustomHtml(File directory) {
        HashMap<String, String> customHtml = this.getCustomHtml();
        ArrayList<SupportZipBundle.Artifact> tmpCustomHtml = new ArrayList<SupportZipBundle.Artifact>();
        File customisedHtmlFile = new File(directory, "customHtml.txt");
        try (FileWriter htmlWriter = new FileWriter(customisedHtmlFile);){
            try {
                StringBuilder htmlString = new StringBuilder();
                if (customHtml.isEmpty()) {
                    htmlString.append("No custom HTML");
                }
                for (String htmlObj : customHtml.keySet()) {
                    htmlString.append(htmlObj).append("\n").append(customHtml.get(htmlObj)).append("\n\n");
                }
                htmlWriter.write(htmlString.toString());
            }
            catch (IOException e) {
                LOG.error("Failed to write customisations to {}.", (Object)customisedHtmlFile.getPath(), (Object)e);
            }
            tmpCustomHtml.add(new FileSupportZipArtifact(customisedHtmlFile));
        }
        catch (Exception e) {
            LOG.error("Can't generate custom html file.", (Throwable)e);
        }
        return tmpCustomHtml;
    }

    private List<SupportZipBundle.Artifact> getCustomStylesheet(File directory) {
        HashMap<String, String> customStylesheet = this.getCustomStylesheet();
        ArrayList<SupportZipBundle.Artifact> tmpCustomStylesheet = new ArrayList<SupportZipBundle.Artifact>();
        File customStylesheetFile = new File(directory, "customStylesheet.txt");
        try (FileWriter stylesheetWriter = new FileWriter(customStylesheetFile);){
            try {
                StringBuilder stylesheetString = new StringBuilder();
                if (customStylesheet.isEmpty()) {
                    stylesheetString.append("No custom stylesheet");
                }
                for (String stylesheetObj : customStylesheet.keySet()) {
                    stylesheetString.append(stylesheetObj).append("\n").append(customStylesheet.get(stylesheetObj)).append("\n\n");
                }
                stylesheetWriter.write(stylesheetString.toString());
            }
            catch (IOException e) {
                LOG.error("Failed to write customisations to {}.", (Object)customStylesheetFile.getPath(), (Object)e);
            }
            tmpCustomStylesheet.add(new FileSupportZipArtifact(customStylesheetFile));
        }
        catch (Exception e) {
            LOG.error("Can't generate custom stylesheet file.", (Throwable)e);
        }
        return tmpCustomStylesheet;
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }
}

