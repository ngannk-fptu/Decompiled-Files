/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.guardrails.macro;

import com.atlassian.migration.agent.service.guardrails.macro.MacroInformationSupplier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Generated;

public class StaticMacroInformation
implements MacroInformationSupplier {
    @Override
    public Set<String> filterMacrosWithMigrationIssue(Collection<String> macros) {
        return StaticMacroInformation.filterExistingMacros(macros, Macro::isMigrationIssue);
    }

    @Override
    public Set<String> filterDeprecatedMacro(Collection<String> macros) {
        return StaticMacroInformation.filterExistingMacros(macros, Macro::isDeprecated);
    }

    @Override
    public Set<String> filterMacrosNotWorkingOnFabric(Collection<String> macros) {
        return StaticMacroInformation.filterExistingMacros(macros, Macro::isFabricEditorNotSupported);
    }

    private static Set<String> filterExistingMacros(Collection<String> macros, Predicate<Macro> filter) {
        return macros.stream().filter(StaticMacroInformation.filterMacros(filter)::contains).collect(Collectors.toSet());
    }

    private static Set<String> filterMacros(Predicate<Macro> filter) {
        return Arrays.stream(Macro.values()).filter(filter).map(Macro::getName).collect(Collectors.toSet());
    }

    @Generated
    public StaticMacroInformation() {
    }

    private static enum Macro {
        VIEW_FILE("view-file", false, false, true),
        INFO("info", false, false, true),
        RECENTLY_UPDATED("recently-updated", true, false, false),
        PANEL("panel", false, false, true),
        CODE("code", false, true, true),
        EXPAND("expand", false, false, true),
        STATUS("status", false, false, true),
        NOTE("note", false, false, true),
        TIP("tip", false, false, true),
        COLUMN("column", false, false, true),
        SECTION("section", false, false, true),
        SPACE_DETAILS("space-details", false, true, true),
        WARNING("warning", false, false, true),
        ATTACHMENTS("attachments", true, false, false),
        INCLUDE("include", true, false, false),
        EXCERPT("excerpt", true, false, false),
        EXCERPT_INCLUDE("excerpt-include", true, false, false),
        CONTENT_REPORT_TABLE("content-report-table", true, false, false),
        PROFILE_PICTURE("profile-picture", true, false, false),
        PROFILE("profile", true, false, false),
        TASKS_REPORT_MACRO("tasks-report-macro", true, false, false),
        HTML("html", true, true, true),
        NOFORMAT("noformat", false, true, true),
        VIEWXLS("viewxls", true, false, false),
        ROADMAP("roadmap", true, false, false),
        JIRACHART("jirachart", true, false, false),
        JIRAISSUES("jiraissues", true, false, false),
        MULTIMEDIA("multimedia", false, false, true),
        FAVPAGES("favpages", true, true, true),
        GALLERY("gallery", true, false, false),
        VIEWDOC("viewdoc", false, false, true),
        CHEESE("cheese", false, true, true),
        RECENTLY_UPDATED_DASHBOARD("recently-updated-dashboard", true, false, false),
        INCLUDE_MACRO("include", true, false, false),
        CONTRIBUTORS_SUMMARY("contributors-summary", true, true, true),
        CREATE_SPACE_BUTTON("create-space-button", false, false, true),
        GLOBAL_REPORTS("global-reports", true, true, true),
        HTML_INCLUDE("html-include", true, true, true),
        IM("im", true, true, true),
        LOREMIPSUM("loremipsum", false, false, true),
        NAVMAP("navmap", false, false, true),
        NETWORK("network", true, true, false),
        PAGETREE("pagetree", true, false, false),
        RELATED_LABELS("related-labels", true, false, false),
        RECENTLY_USED_LABELS("recently-used-labels", true, true, true),
        RSS("rss", true, true, true),
        SEARCH("search", true, true, true),
        SPACE_ATTACHMENTS("space-attachments", false, true, true),
        CONTENT_BY_USER("content-by-user", true, true, true);

        private final String name;
        private final boolean migrationIssue;
        private final boolean isDeprecated;
        private final boolean fabricEditorNotSupported;

        @Generated
        public String getName() {
            return this.name;
        }

        @Generated
        public boolean isMigrationIssue() {
            return this.migrationIssue;
        }

        @Generated
        public boolean isDeprecated() {
            return this.isDeprecated;
        }

        @Generated
        public boolean isFabricEditorNotSupported() {
            return this.fabricEditorNotSupported;
        }

        @Generated
        private Macro(String name, boolean migrationIssue, boolean isDeprecated, boolean fabricEditorNotSupported) {
            this.name = name;
            this.migrationIssue = migrationIssue;
            this.isDeprecated = isDeprecated;
            this.fabricEditorNotSupported = fabricEditorNotSupported;
        }
    }
}

