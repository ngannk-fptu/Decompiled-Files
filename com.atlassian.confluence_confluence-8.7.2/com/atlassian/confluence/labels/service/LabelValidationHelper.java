/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.stream.Collectors;

public class LabelValidationHelper {
    private final ServiceCommandValidator validator;
    private final User user;
    private final LabelPermissionEnforcer labelPermissionEnforcer;
    private final Labelable entity;

    LabelValidationHelper(ServiceCommandValidator validator, User user, LabelPermissionEnforcer labelPermissionEnforcer, Labelable entity) {
        this.validator = validator;
        this.user = user;
        this.labelPermissionEnforcer = labelPermissionEnforcer;
        this.entity = entity;
    }

    @Deprecated
    public void validateLables(Collection<String> labelNames) {
        this.validateLabels(labelNames);
    }

    public void validateLabels(Collection<String> labelNames) {
        int entityLabelsWithoutFavourites;
        Collection parsedLabels = labelNames.stream().map(labelName -> LabelParser.parse(labelName, this.user)).collect(Collectors.toList());
        if (parsedLabels.stream().allMatch(LabelUtil::isFavouriteLabel)) {
            return;
        }
        int countWithoutFavourites = LabelUtil.countParsedLabelNamesWithoutFavourites(parsedLabels);
        if (countWithoutFavourites > 20) {
            this.validator.addValidationError("labels.too.many.entries", countWithoutFavourites, 20);
            return;
        }
        if (this.entity != null && countWithoutFavourites + (entityLabelsWithoutFavourites = LabelUtil.countLabelsWithoutFavourites(this.entity.getLabels())) > 500) {
            this.validator.addValidationError("labels.over.max", countWithoutFavourites + entityLabelsWithoutFavourites, 500);
            return;
        }
        labelNames.forEach(this::validateLabel);
    }

    private boolean validateLabel(String label) {
        ParsedLabelName ref = LabelParser.parse(label, this.user);
        if (ref == null) {
            this.validator.addValidationError("label.contains.invalid.chars", HtmlUtil.htmlEncode(label), LabelParser.getInvalidCharactersAsString());
            return false;
        }
        if (!LabelParser.isValidNameLength(ref)) {
            this.validator.addValidationError("label.name.is.too.long", 255);
            return false;
        }
        if (!LabelParser.isValidPrefixLength(ref)) {
            this.validator.addValidationError("label.prefix.is.too.long", 255);
            return false;
        }
        if (LabelParser.isPersonalLabel(label) && !LabelParser.isLabelOwnedByUser(label, this.user)) {
            this.validator.addValidationError("not.permitted.to.add.labels", HtmlUtil.htmlEncode(label));
            return false;
        }
        if (this.entity != null && !this.labelPermissionEnforcer.userCanEditLabel(ref, this.entity)) {
            this.validator.addValidationError(this.user == null ? "not.permitted.to.add.labels" : "only.personal.labels.permitted", new Object[0]);
            return false;
        }
        return true;
    }
}

