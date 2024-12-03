/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.impl.search.v2.UserLookupHelper;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.LabelPermissionSupport;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractSearchResult
implements SearchResult {
    private static final String CREATOR_FULLNAME_FIELD = "creator-full-name";
    private Set<String> personalLabels;
    private Map<String, String> extraFields;
    private final Function<String, ConfluenceUser> userLookup;
    private final Supplier<ConfluenceUser> creatorUser = Suppliers.memoize(() -> this.getUserResult(SearchFieldNames.CREATOR));
    private final Supplier<ConfluenceUser> lastModifierUser = Suppliers.memoize(() -> this.getUserResult(SearchFieldNames.LAST_MODIFIER));
    static final Map<String, String> INDEX_FIELD_MAPPINGS = ImmutableMap.builder().put((Object)"attachmentDownloadPath", (Object)SearchFieldNames.ATTACHMENT_DOWNLOAD_PATH).put((Object)"attachmentTypeDescription", (Object)SearchFieldNames.ATTACHMENT_NICE_TYPE).put((Object)"attachmentReadableFileSize", (Object)SearchFieldNames.ATTACHMENT_NICE_FILE_SIZE).put((Object)"attachmentMimeType", (Object)SearchFieldNames.ATTACHMENT_MIME_TYPE).put((Object)"containingContentDisplayTitle", (Object)SearchFieldNames.ATTACHMENT_OWNER_REAL_TITLE).put((Object)"containingPageDisplayTitle", (Object)SearchFieldNames.PAGE_DISPLAY_TITLE).put((Object)"containingContentUrlPath", (Object)SearchFieldNames.ATTACHMENT_OWNER_URL_PATH).put((Object)"containingPageUrlPath", (Object)SearchFieldNames.PAGE_URL_PATH).put((Object)"containingContentId", (Object)SearchFieldNames.ATTACHMENT_OWNER_ID).put((Object)"username", (Object)SearchFieldNames.PERSONAL_INFORMATION_USERNAME).put((Object)"fullName", (Object)SearchFieldNames.PERSONAL_INFORMATION_FULL_NAME).put((Object)"email", (Object)SearchFieldNames.PERSONAL_INFORMATION_EMAIL).put((Object)SearchFieldNames.CONTENT_VERSION, (Object)SearchFieldNames.CONTENT_VERSION).put((Object)SearchFieldNames.LATEST_VERSION_ID, (Object)SearchFieldNames.LATEST_VERSION_ID).put((Object)"creatorFullName", (Object)"creator-full-name").put((Object)"contentPluginKey", (Object)SearchFieldNames.CONTENT_PLUGIN_KEY).build();

    protected AbstractSearchResult() {
        this((Function<String, ConfluenceUser>)((Object)UserLookupHelper.INSTANCE));
    }

    public AbstractSearchResult(Function<String, ConfluenceUser> userLookup) {
        this.userLookup = userLookup;
    }

    @Override
    public Set<String> getLabels(User user) {
        HashSet<String> result = new HashSet<String>();
        String labelText = this.getStringResult(SearchFieldNames.LABEL_TEXT);
        StringTokenizer tokenizer = new StringTokenizer(labelText, " ");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        if (user != null) {
            List<Label> labels = new ArrayList(this.getPersonalLabels().size());
            for (String personalLabel : this.getPersonalLabels()) {
                ParsedLabelName parsedLabel = LabelParser.parse(personalLabel);
                labels.add(parsedLabel.toLabel());
            }
            labels = LabelPermissionSupport.filterVisibleLabels(labels, user, true);
            for (Label filteredLabel : labels) {
                result.add(filteredLabel.toString());
            }
        }
        return result;
    }

    @Override
    public Set<String> getPersonalLabels() {
        if (this.personalLabels == null) {
            this.personalLabels = this.getFieldValues(SearchFieldNames.LABEL).stream().filter(text -> text != null && text.startsWith("~")).collect(Collectors.toSet());
        }
        return this.personalLabels;
    }

    @Override
    public String getContent() {
        return this.getStringResult(SearchFieldNames.CONTENT_STORED);
    }

    protected String getSanitisedContent() {
        String title = this.getDisplayTitle();
        return Optional.ofNullable(this.getContent()).map(String::trim).map(content -> content.startsWith(title) ? content.substring(title.length()) : content).map(String::trim).orElse("");
    }

    protected String getResultExcerpt(int maxLength) {
        String sanitised = this.getSanitisedContent();
        return sanitised.substring(0, Math.min(maxLength, sanitised.length()));
    }

    @Override
    public String getResultExcerpt() {
        return this.getResultExcerpt(320);
    }

    @Override
    public String getType() {
        return this.getStringResult(SearchFieldNames.TYPE);
    }

    @Override
    public String getStatus() {
        return this.getStringResult(SearchFieldNames.CONTENT_STATUS);
    }

    @Override
    public boolean isHomePage() {
        return Boolean.valueOf(this.getStringResult(SearchFieldNames.HOME_PAGE));
    }

    @Override
    public Date getCreationDate() {
        return this.getDateResult(SearchFieldNames.CREATION_DATE);
    }

    @Override
    @Deprecated
    public String getCreator() {
        ConfluenceUser creator = this.getCreatorUser();
        return creator != null ? creator.getName() : null;
    }

    @Override
    public ConfluenceUser getCreatorUser() {
        return (ConfluenceUser)this.creatorUser.get();
    }

    @Override
    public String getOwnerType() {
        String className = this.getStringResult(SearchFieldNames.ATTACHMENT_OWNER_CONTENT_TYPE);
        return className == null ? null : ContentTypeEnum.getByClassName(className).getRepresentation();
    }

    @Override
    public String getOwnerTitle() {
        return this.getStringResult(this.getStringResult(SearchFieldNames.ATTACHMENT_OWNER_CONTENT_TYPE));
    }

    @Override
    public Integer getContentVersion() {
        return this.getIntegerResult(SearchFieldNames.CONTENT_VERSION);
    }

    @Override
    public Date getLastModificationDate() {
        return this.getDateResult(SearchFieldNames.LAST_MODIFICATION_DATE);
    }

    @Override
    @Deprecated
    public String getLastModifier() {
        ConfluenceUser lastModifier = this.getLastModifierUser();
        return lastModifier != null ? lastModifier.getName() : null;
    }

    @Override
    public ConfluenceUser getLastModifierUser() {
        return (ConfluenceUser)this.lastModifierUser.get();
    }

    @Override
    public String getDisplayTitle() {
        return this.getStringResult(SearchFieldMappings.TITLE.getName());
    }

    @Override
    public String getUrlPath() {
        return this.getStringResult(SearchFieldNames.URL_PATH);
    }

    @Override
    public String getLastUpdateDescription() {
        return this.getStringResult(SearchFieldNames.LAST_UPDATE_DESCRIPTION);
    }

    @Override
    public String getSpaceName() {
        return this.getStringResult(SearchFieldNames.SPACE_NAME);
    }

    @Override
    public String getSpaceKey() {
        return this.getStringResult(SearchFieldNames.SPACE_KEY);
    }

    @Override
    public boolean hasLabels() {
        return this.getField(SearchFieldNames.LABEL_TEXT) != null && this.getField(SearchFieldNames.LABEL) != null;
    }

    @Override
    public Map<String, String> getExtraFields() {
        if (this.extraFields == null) {
            this.extraFields = new HashMap<String, String>(INDEX_FIELD_MAPPINGS.size());
            INDEX_FIELD_MAPPINGS.forEach((key, fieldName) -> {
                String fieldValue = this.getField((String)fieldName);
                if (fieldValue != null) {
                    this.extraFields.put((String)key, fieldValue);
                }
            });
        }
        return this.extraFields;
    }

    @Override
    public Handle getHandle() {
        try {
            return new HibernateHandle(this.getStringResult(SearchFieldNames.HANDLE));
        }
        catch (ParseException p) {
            throw new IllegalStateException("Unable to create hibernate handle from search index handle: " + this.getStringResult(SearchFieldNames.HANDLE));
        }
    }

    @Override
    public abstract Set<String> getFieldNames();

    @Override
    public String getField(String fieldName) {
        return this.getFieldValue(fieldName);
    }

    public abstract String getFieldValue(String var1);

    @Override
    public abstract Set<String> getFieldValues(String var1);

    public String toString() {
        return new ToStringBuilder((Object)this).append((Object)this.getSpaceName()).append((Object)this.getDisplayTitle()).append((Object)this.getType()).append((Object)this.getLastModifierUser()).append((Object)this.getLastModificationDate()).append(this.getExtraFields()).toString();
    }

    protected String getStringResult(String ... fieldNames) {
        for (String fieldName : fieldNames) {
            String fieldValue = this.getField(fieldName);
            if (fieldValue == null) continue;
            return fieldValue;
        }
        return null;
    }

    protected ConfluenceUser getUserResult(String fieldName) {
        return this.userLookup.apply(this.getStringResult(fieldName));
    }

    protected Date getDateResult(String key) {
        String value = this.getStringResult(key);
        if (value == null) {
            return null;
        }
        return LuceneUtils.stringToDate(value);
    }

    protected Integer getIntegerResult(String fieldName) {
        String value = this.getStringResult(fieldName);
        return value == null ? null : Integer.valueOf(value);
    }

    protected static abstract class AlternateFieldNames {
        private static final BiMap<String, String> ALTERNATE_NAMES = ImmutableBiMap.of((Object)SearchFieldNames.CONTENT, (Object)SearchFieldNames.CONTENT_STORED, (Object)SearchFieldNames.TITLE, (Object)SearchFieldMappings.DISPLAY_TITLE.getName());

        protected AlternateFieldNames() {
        }

        protected abstract boolean fieldExists(String var1);

        private Stream<String> getAlternateNames(String fieldName) {
            return Stream.of((String)ALTERNATE_NAMES.get((Object)fieldName), (String)ALTERNATE_NAMES.inverse().get((Object)fieldName)).filter(Objects::nonNull);
        }

        public String resolve(String fieldName) {
            return this.getAlternateNames(fieldName).filter(this::fieldExists).findFirst().orElse(fieldName);
        }

        public Set<String> expand(Collection<String> fields) {
            HashSet<String> result = new HashSet<String>(fields);
            fields.stream().flatMap(this::getAlternateNames).forEach(result::add);
            return result;
        }
    }
}

