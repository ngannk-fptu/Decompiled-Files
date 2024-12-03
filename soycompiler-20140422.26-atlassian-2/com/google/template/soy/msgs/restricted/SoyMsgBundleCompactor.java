/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.CompactInterner;
import com.google.template.soy.msgs.restricted.MsgPartUtils;
import com.google.template.soy.msgs.restricted.RenderOnlySoyMsgBundleImpl;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralCaseSpec;
import com.google.template.soy.msgs.restricted.SoyMsgPluralPart;
import com.google.template.soy.msgs.restricted.SoyMsgSelectPart;
import java.util.List;

public final class SoyMsgBundleCompactor {
    private static final SoyMsgPluralCaseSpec DEFAULT_PLURAL_CASE_SPEC = new SoyMsgPluralCaseSpec("other");
    private static final String DEFAULT_SELECT_CASE_SPEC = null;
    private CompactInterner interner = new CompactInterner();

    public SoyMsgBundle compact(SoyMsgBundle input) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SoyMsg msg : input) {
            ImmutableList<SoyMsgPart> parts = this.compactParts(msg.getParts());
            builder.add((Object)new SoyMsg(msg.getId(), msg.getLocaleString(), MsgPartUtils.hasPlrselPart(parts), (List<SoyMsgPart>)parts));
        }
        return new RenderOnlySoyMsgBundleImpl(input.getLocaleString(), (Iterable<SoyMsg>)builder.build());
    }

    private ImmutableList<SoyMsgPart> compactParts(ImmutableList<SoyMsgPart> parts) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (SoyMsgPart part : parts) {
            builder.add((Object)this.compactPart(part));
        }
        return builder.build();
    }

    private SoyMsgPart compactPart(SoyMsgPart part) {
        if (part instanceof SoyMsgPluralPart) {
            part = this.compactPlural((SoyMsgPluralPart)part);
        } else if (part instanceof SoyMsgSelectPart) {
            part = this.compactSelect((SoyMsgSelectPart)part);
        }
        return this.intern(part);
    }

    private SoyMsgPart compactSelect(SoyMsgSelectPart select) {
        return new SoyMsgSelectPart(this.intern(select.getSelectVarName()), this.compactCases(select.getCases(), DEFAULT_SELECT_CASE_SPEC));
    }

    private SoyMsgPart compactPlural(SoyMsgPluralPart plural) {
        return new SoyMsgPluralPart(this.intern(plural.getPluralVarName()), plural.getOffset(), this.compactCases(plural.getCases(), DEFAULT_PLURAL_CASE_SPEC));
    }

    private <T> ImmutableList<Pair<T, ImmutableList<SoyMsgPart>>> compactCases(ImmutableList<Pair<T, ImmutableList<SoyMsgPart>>> cases, T defaultCaseSpec) {
        ImmutableList defaultValue = null;
        for (Pair caseAndValue : cases) {
            if (!Objects.equal(caseAndValue.first, defaultCaseSpec)) continue;
            defaultValue = (ImmutableList)caseAndValue.second;
            break;
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Pair caseAndValue : cases) {
            if (defaultValue != null && !Objects.equal(caseAndValue.first, defaultCaseSpec) && defaultValue.equals(caseAndValue.second)) continue;
            builder.add(Pair.of(caseAndValue.first != null ? (Object)this.intern(caseAndValue.first) : null, this.compactParts((ImmutableList<SoyMsgPart>)((ImmutableList)caseAndValue.second))));
        }
        return builder.build();
    }

    private <T> T intern(T input) {
        return this.interner.intern(input);
    }
}

