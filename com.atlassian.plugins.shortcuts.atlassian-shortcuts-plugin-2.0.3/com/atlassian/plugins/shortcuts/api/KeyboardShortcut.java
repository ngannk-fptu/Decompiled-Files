/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.plugins.shortcuts.api;

import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutOperation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class KeyboardShortcut
implements Comparable<KeyboardShortcut> {
    private final Set<List<String>> shortcuts;
    private final String context;
    private final KeyboardShortcutOperation operation;
    private final String descriptionI18nKey;
    private final String defaultDescription;
    private final int order;
    private final boolean hidden;

    public KeyboardShortcut(String context, KeyboardShortcutOperation operation, int order, Set<List<String>> shortcuts, String descriptionI18nKey, String defaultDescription, boolean hidden) {
        this.hidden = hidden;
        this.context = (String)Assertions.notNull((String)"context", (Object)context);
        this.shortcuts = new LinkedHashSet<List<String>>(shortcuts);
        this.operation = (KeyboardShortcutOperation)Assertions.notNull((String)"operation", (Object)operation);
        this.order = order;
        this.descriptionI18nKey = descriptionI18nKey;
        this.defaultDescription = defaultDescription;
    }

    public static Builder builder(KeyboardShortcut copyFrom) {
        return new Builder(copyFrom);
    }

    public String getContext() {
        return this.context;
    }

    public Set<List<String>> getShortcuts() {
        return this.shortcuts;
    }

    public int getOrder() {
        return this.order;
    }

    public KeyboardShortcutOperation getOperation() {
        return this.operation;
    }

    public String getParameter() {
        return this.operation.getParam();
    }

    public String getDescriptionI18nKey() {
        return this.descriptionI18nKey;
    }

    public String getDefaultDescription() {
        return this.defaultDescription;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public int compareTo(KeyboardShortcut shortcut) {
        int order2 = shortcut.getOrder();
        if (this.order == order2) {
            return 0;
        }
        if (this.order < order2) {
            return -1;
        }
        return 1;
    }

    public String toString() {
        return "KeyboardShortcut{context=" + this.context + ", shortcuts=" + this.shortcuts + ", operation=" + this.operation + ", parameter='" + this.operation.getParam() + '\'' + ", descriptionI18nKey='" + this.descriptionI18nKey + '\'' + ", description='" + this.defaultDescription + '\'' + ", order=" + this.order + ", hidden=" + this.hidden + '}';
    }

    public boolean equals(Object other) {
        if (other instanceof KeyboardShortcut) {
            KeyboardShortcut otherShortcut = (KeyboardShortcut)other;
            return this.operation.equals(otherShortcut.getOperation()) && this.shortcuts.equals(otherShortcut.getShortcuts()) && this.context.equals(otherShortcut.getContext());
        }
        return false;
    }

    public int hashCode() {
        return 41 * (31 + this.operation.hashCode()) + 29 * this.shortcuts.hashCode() + this.context.hashCode();
    }

    private Set<List<String>> copyShortcuts() {
        LinkedHashSet<List<String>> result = new LinkedHashSet<List<String>>();
        for (List<String> shortcut : this.shortcuts) {
            result.add(new ArrayList<String>(shortcut));
        }
        return result;
    }

    public static class Builder {
        private Set<List<String>> shortcuts;
        private String context;
        private KeyboardShortcutOperation operation;
        private String descriptionI18nKey;
        private String defaultDescription;
        private int order;
        private boolean hidden;

        public Builder(KeyboardShortcut copyFrom) {
            this.context = copyFrom.getContext();
            this.operation = new KeyboardShortcutOperation(copyFrom.getOperation().getType().name(), copyFrom.getOperation().getParam());
            this.order = copyFrom.getOrder();
            this.shortcuts = copyFrom.copyShortcuts();
            this.descriptionI18nKey = copyFrom.getDescriptionI18nKey();
            this.defaultDescription = copyFrom.getDefaultDescription();
            this.hidden = copyFrom.isHidden();
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public void setDefaultDescription(String defaultDescription) {
            this.defaultDescription = defaultDescription;
        }

        public void setDescriptionI18nKey(String descriptionI18nKey) {
            this.descriptionI18nKey = descriptionI18nKey;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public void setShortcuts(Set<List<String>> shortcuts) {
            this.shortcuts = shortcuts;
        }

        public void setOperationType(String operationType) {
            this.operation = new KeyboardShortcutOperation(operationType, this.operation.getParam());
        }

        public void setOperationParam(String param) {
            this.operation = new KeyboardShortcutOperation(this.operation.getType().name(), param);
        }

        public KeyboardShortcut build() {
            return new KeyboardShortcut(this.context, this.operation, this.order, this.shortcuts, this.descriptionI18nKey, this.defaultDescription, this.hidden);
        }
    }
}

