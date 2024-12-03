/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.ao.dao.InlineTaskDao;
import com.atlassian.confluence.plugins.tasklist.ao.dao.TaskOrderClause;
import com.atlassian.confluence.plugins.tasklist.search.SearchTaskParameters;
import com.atlassian.confluence.plugins.tasklist.search.SortColumn;
import com.atlassian.confluence.plugins.tasklist.service.TaskPaginationService;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AOInlineTaskDao
implements InlineTaskDao {
    private static final Logger log = LoggerFactory.getLogger(AOInlineTaskDao.class);
    private static final String INLINE_TASK_TABLE_NAME = "AO_BAF3AA_AOINLINE_TASK";
    private static final Class<AOInlineTask> AO_TASK_TYPE = AOInlineTask.class;
    private static DatabaseProvider DATABASE_PROVIDER;
    private final ActiveObjects ao;
    private final UserAccessor userAccessor;
    private String[] needed_quote_names = new String[]{"t.GLOBAL_ID", "t.CONTENT_ID", "t.TASK_STATUS", "t.DUE_DATE", "t.COMPLETE_DATE", "t.CONTENT_ID", "t.ASSIGNEE_USER_KEY", "t.CREATOR_USER_KEY", "t.CREATE_DATE", "t.UPDATE_DATE", "t.*", "AO_BAF3AA_AOINLINE_TASK"};
    private Map<String, String> tableFieldName;
    private static final Joiner JOINER_SPACE;
    private static final Function<Long, String> SELECT_ID_GENERIC;
    private static final Function<Long, String> SELECT_ID_HSQLDB;
    private static final Function<Long, String> SELECT_ID_ORACLE;
    private final LazyReference<Function<Long, String>> SELECT_ID_FUNCTION = new LazyReference<Function<Long, String>>(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Function<Long, String> create() throws Exception {
            AOInlineTaskDao.this.ao.moduleMetaData().awaitInitialization();
            AOInlineTask[] aoInlineTask = (AOInlineTask[])AOInlineTaskDao.this.ao.find(AO_TASK_TYPE, Query.select().limit(1));
            if (aoInlineTask.length == 0) {
                throw new RuntimeException("'AO_BAF3AA_AOINLINE_TASK' table doesn't exist!");
            }
            DatabaseProvider provider = aoInlineTask[0].getEntityManager().getProvider();
            try (Connection connection = null;){
                connection = provider.getConnection();
                String dbName = connection.getMetaData().getDatabaseProductName();
                boolean isOracle = StringUtils.startsWith((CharSequence)dbName, (CharSequence)"Oracle");
                boolean isHSQLDB = StringUtils.startsWith((CharSequence)dbName, (CharSequence)"HSQL");
                if (isOracle) {
                    Function<Long, String> function = SELECT_ID_ORACLE;
                    return function;
                }
                if (isHSQLDB) {
                    Function<Long, String> function = SELECT_ID_HSQLDB;
                    return function;
                }
                Function<Long, String> function = SELECT_ID_GENERIC;
                return function;
            }
        }
    };

    @Autowired
    public AOInlineTaskDao(ActiveObjects ao, UserAccessor userAccessor) {
        this.ao = ao;
        this.userAccessor = userAccessor;
    }

    @Override
    public Task create(Task task) {
        AOInlineTask aoInlineTask = (AOInlineTask)this.ao.create(AOInlineTask.class, new DBParam[0]);
        this.prepareAOInlineTask(aoInlineTask, task);
        aoInlineTask.save();
        return this.asTask(aoInlineTask, task.getTitle());
    }

    @Override
    public Task update(Task task) {
        AOInlineTask aoInlineTask = this.findAOTask(task.getContentId(), task.getId());
        this.prepareAOInlineTask(aoInlineTask, task);
        aoInlineTask.save();
        return this.asTask(aoInlineTask);
    }

    private void prepareAOInlineTask(AOInlineTask aoInlineTask, Task task) {
        aoInlineTask.setId(task.getId());
        aoInlineTask.setContentId(task.getContentId());
        aoInlineTask.setTaskStatus(task.getStatus());
        aoInlineTask.setBody(task.getBody());
        aoInlineTask.setAssigneeUserKey(this.getUserKey(task.getAssignee()));
        aoInlineTask.setDueDate(task.getDueDate());
        aoInlineTask.setUpdateDate(task.getUpdateDate());
        aoInlineTask.setCompleteDate(task.getCompleteDate());
        aoInlineTask.setCompleteUserKey(this.getUserKey(task.getCompleteUser()));
        aoInlineTask.setCreatorUserKey(this.getUserKey(task.getCreator()));
        aoInlineTask.setCreateDate(task.getCreateDate());
    }

    @Override
    public Task find(long globalId) {
        return this.asTask(this.findAOTask(globalId));
    }

    @Override
    public Task find(long contentId, long id) {
        AOInlineTask aoInlineTask = this.findAOTask(contentId, id);
        return this.asTask(aoInlineTask);
    }

    @Override
    public long countAll() {
        return this.ao.count(AO_TASK_TYPE);
    }

    @Override
    public List<Task> findAll() {
        AOInlineTask[] aoInlineTasks = (AOInlineTask[])this.ao.find(AO_TASK_TYPE);
        return this.asListTasks(Arrays.asList(aoInlineTasks));
    }

    private List<AOInlineTask> findWithSQL(String sql, Object ... params) {
        log.debug("Executing AO SQL [{}] with params {}", (Object)sql, Arrays.asList(params));
        Object[] aoInlineTasks = (AOInlineTask[])this.ao.findWithSQL(AOInlineTask.class, "GLOBAL_ID", sql, params);
        return Lists.newArrayList((Object[])aoInlineTasks);
    }

    private AOInlineTask findAOTask(long globalId) {
        return (AOInlineTask)this.ao.get(AO_TASK_TYPE, (Object)globalId);
    }

    private AOInlineTask findAOTask(long contentId, long id) {
        AOInlineTask[] aoInlineTasks = (AOInlineTask[])this.ao.find(AO_TASK_TYPE, Query.select().where("CONTENT_ID = ? AND ID = ?", new Object[]{contentId, id}));
        return aoInlineTasks.length > 0 ? aoInlineTasks[0] : null;
    }

    @Override
    public List<Task> findByContentId(long contentId) {
        return this.asListTasks(this.getByContentId(contentId));
    }

    @Override
    public Collection<AOInlineTask> getByContentId(long contentId) {
        return Arrays.asList(this.findBy("CONTENT_ID", contentId));
    }

    @Override
    public Set<Long> findTaskIdsByContentId(long contentId) {
        AOInlineTask[] aoInlineTasks = (AOInlineTask[])this.ao.find(AO_TASK_TYPE, Query.select().where("CONTENT_ID = ?", new Object[]{contentId}));
        ImmutableSet.Builder setBuilder = ImmutableSet.builder();
        for (AOInlineTask task : aoInlineTasks) {
            setBuilder.add((Object)task.getId());
        }
        return setBuilder.build();
    }

    @Override
    public List<Task> findByCreator(UserKey userKey) {
        return this.asListTasks(Arrays.asList(this.findBy("CREATOR_USER_KEY", userKey.getStringValue())));
    }

    @Override
    public List<Task> findByAssignee(UserKey userKey) {
        return this.asListTasks(Arrays.asList(this.findBy("ASSIGNEE_USER_KEY", userKey.getStringValue())));
    }

    private AOInlineTask[] findBy(String criteria, Object value) {
        return (AOInlineTask[])this.ao.find(AO_TASK_TYPE, Query.select().where(criteria + " = ?", new Object[]{value}));
    }

    @Override
    public void delete(long globalId) {
        AOInlineTask aoInlineTask = this.findAOTask(globalId);
        if (aoInlineTask != null) {
            this.ao.delete(new RawEntity[]{aoInlineTask});
        }
    }

    @Override
    public void delete(long contentId, long id) {
        AOInlineTask aoInlineTask = this.findAOTask(contentId, id);
        if (aoInlineTask != null) {
            this.ao.delete(new RawEntity[]{aoInlineTask});
        }
    }

    @Override
    public void deleteAll() {
        this.ao.deleteWithSQL(AOInlineTask.class, null, new Object[0]);
    }

    @Override
    public void deleteByContentId(long contentId) {
        DatabaseProvider provider = this.getDBProvider();
        if (provider != null) {
            this.ao.deleteWithSQL(AOInlineTask.class, "CONTENT_ID = ?", new Object[]{contentId});
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteBySpaceId(long spaceId) {
        DatabaseProvider provider = this.getDBProvider();
        if (provider == null) {
            return;
        }
        Connection connection = null;
        java.sql.Statement stmt = null;
        try {
            try {
                connection = provider.getConnection();
                stmt = provider.preparedStatement(connection, (CharSequence)("DELETE FROM " + provider.quote(INLINE_TASK_TABLE_NAME) + " WHERE " + provider.quote("CONTENT_ID") + " IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = ?)"));
                stmt.setLong(1, spaceId);
                stmt.executeUpdate();
            }
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    finally {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildQuestionMarkString(int length) {
        return "(" + Joiner.on((String)",").join(Collections.nCopies(length, "?")) + ")";
    }

    @Override
    public AOInlineTask[] getFirstTasksOrderedById(int limit) {
        return (AOInlineTask[])this.ao.find(AOInlineTask.class, Query.select().order("GLOBAL_ID").limit(limit));
    }

    @Override
    public AOInlineTask[] getTasksWithIdGreaterThan(long fromId, int limit) {
        return (AOInlineTask[])this.ao.find(AOInlineTask.class, Query.select().where("\"GLOBAL_ID\" > ?", new Object[]{fromId}).order("GLOBAL_ID").limit(limit));
    }

    @Override
    public AOInlineTask get(long globalId) {
        return this.findAOTask(globalId);
    }

    @Override
    public PageResponse<Task> searchTask(@Nonnull SearchTaskParameters params, TaskPaginationService taskPaginationService, PageRequest pageRequest) {
        Statement statement = new Statement();
        List<Long> spaceIds = params.getSpaceIds();
        List<Long> pageIds = params.getPageIds();
        List<Long> labelIds = params.getLabelIds();
        List<String> assigneeUserKeys = params.getAssigneeUserKeys();
        List<String> creatorUserKeys = params.getCreatorUserKeys();
        String status = params.getStatus() == null ? "" : params.getStatus().name();
        Date startDueDate = params.getStartDueDate();
        Date endDueDate = params.getEndDueDate();
        Date startCreateDate = params.getStartCreatedDate();
        Date endCreateDate = params.getEndCreatedDate();
        boolean usePage = CollectionUtils.isNotEmpty(pageIds);
        boolean useLabel = CollectionUtils.isNotEmpty(labelIds);
        boolean useSpace = CollectionUtils.isNotEmpty(spaceIds);
        boolean useAssignee = CollectionUtils.isNotEmpty(assigneeUserKeys);
        boolean useCreator = CollectionUtils.isNotEmpty(creatorUserKeys);
        boolean useStatus = StringUtils.isNotBlank((CharSequence)status);
        String spaceStatement = "( s.SPACEID in " + this.buildQuestionMarkString(spaceIds.size()) + " )";
        if (usePage) {
            String qStr = this.buildQuestionMarkString(pageIds.size());
            String pageIdStatements = JOINER_SPACE.join(Iterables.transform(pageIds, (Function)((Function)this.SELECT_ID_FUNCTION.get())));
            String pageStatement = "( t.CONTENT_ID in ( select a.DESCENDENTID from CONTENT c join CONFANCESTORS a on c.CONTENTID = a.DESCENDENTID where a.ANCESTORID in " + qStr + " " + pageIdStatements + " ) )";
            if (useSpace) {
                pageStatement = "( " + pageStatement + " OR " + spaceStatement + " )";
            }
            statement.addAllCondition(pageStatement, pageIds, pageIds, spaceIds);
        } else if (useSpace) {
            statement.addAllCondition(spaceStatement, spaceIds);
        }
        statement.addCondition("s.SPACESTATUS <> '" + SpaceStatus.ARCHIVED.name() + "'", new Object[0]);
        statement.addCondition("c.CONTENT_STATUS <> 'deleted'", new Object[0]);
        if (useLabel) {
            statement.addAllCondition("( l.LABELID in " + this.buildQuestionMarkString(labelIds.size()) + " )", labelIds);
        }
        if (useStatus) {
            statement.addCondition("t.TASK_STATUS = ?", status);
        }
        if (startDueDate != null || endDueDate != null) {
            if (startDueDate != null) {
                statement.addCondition("( t.DUE_DATE >= ? )", startDueDate);
            }
            if (endDueDate != null) {
                statement.addCondition("( t.DUE_DATE <= ? )", endDueDate);
            }
        }
        if (startCreateDate != null || endCreateDate != null) {
            if (startCreateDate != null) {
                statement.addCondition("( t.CREATE_DATE >= ? )", startCreateDate);
            }
            if (endCreateDate != null) {
                statement.addCondition("( t.CREATE_DATE < ? )", endCreateDate);
            }
        }
        if (useAssignee) {
            statement.addAllCondition("( t.ASSIGNEE_USER_KEY in " + this.buildQuestionMarkString(assigneeUserKeys.size()) + ")", assigneeUserKeys);
        }
        if (useCreator) {
            statement.addAllCondition("( t.CREATOR_USER_KEY in " + this.buildQuestionMarkString(creatorUserKeys.size()) + ")", creatorUserKeys);
        }
        statement.addJoin("join CONTENT c on t.CONTENT_ID = c.CONTENTID");
        if (useLabel) {
            statement.addJoin("left join CONTENT_LABEL cl on c.CONTENTID = cl.CONTENTID left join LABEL l on cl.LABELID = l.LABELID");
        }
        statement.addJoin("join SPACES s on c.SPACEID = s.SPACEID");
        if (SortColumn.ASSIGNEE == params.getSortParameters().getSortColumn()) {
            statement.addJoin("left join user_mapping um on t.ASSIGNEE_USER_KEY = um.user_key left join (select min_id_info.min_id, inner_cu.lower_user_name, inner_cu.lower_display_name from cwd_user inner_cu inner join (select min(cwd_user.id) as min_id, cwd_user.lower_user_name as min_low_user_name from cwd_user group by cwd_user.lower_user_name) min_id_info on inner_cu.id = min_id_info.min_id and inner_cu.lower_user_name = min_id_info.min_low_user_name) cu on um.lower_username = cu.lower_user_name");
        }
        statement.setFrom("from AO_BAF3AA_AOINLINE_TASK t");
        statement.setSelect("select distinct t.GLOBAL_ID , t.CONTENT_ID");
        statement.setSortParams(TaskOrderClause.orderClausesFor(params.getSortParameters().getSortColumn(), params.getSortParameters().getSortOrder()));
        String sql = this.quoteQuery(statement.asQuery());
        log.debug("Task report macro SQL query:\n{}", (Object)sql);
        ArrayList<AOInlineTask> aoInlineTasks = StringUtils.isNotBlank((CharSequence)sql) ? this.findWithSQL(sql, statement.getParams().toArray()) : new ArrayList<AOInlineTask>();
        return taskPaginationService.filter(aoInlineTasks, pageRequest, AuthenticatedUserThreadLocal.get());
    }

    private Map<String, String> getQuoteNames(String[] names) {
        if (names == null) {
            return null;
        }
        DatabaseProvider provider = this.getDBProvider();
        if (provider != null) {
            HashMap<String, String> result = new HashMap<String, String>();
            for (String name : names) {
                Object newName = name;
                int indexOfDot = name.indexOf(".");
                String prefix = "";
                if (indexOfDot >= 0) {
                    newName = name.substring(indexOfDot + 1, name.length());
                    prefix = name.substring(0, indexOfDot + 1);
                }
                newName = provider.quote((String)newName);
                newName = prefix + (String)newName;
                result.put(name, (String)newName);
            }
            return result;
        }
        return null;
    }

    private DatabaseProvider getDBProvider() {
        AOInlineTask[] aoInlineTask;
        if (DATABASE_PROVIDER == null && (aoInlineTask = (AOInlineTask[])this.ao.find(AO_TASK_TYPE, Query.select().limit(1))).length > 0) {
            DATABASE_PROVIDER = aoInlineTask[0].getEntityManager().getProvider();
        }
        return DATABASE_PROVIDER;
    }

    private Task asTask(AOInlineTask ao, String oldTitle) {
        if (ao == null) {
            return null;
        }
        return new Task.Builder().withGlobalId(ao.getGlobalId()).withId(ao.getId()).withContentId(ao.getContentId()).withStatus(ao.getTaskStatus()).withBody(ao.getBody()).withCreator(this.getUsername(ao.getCreatorUserKey())).withAssignee(this.getUsername(ao.getAssigneeUserKey())).withCreateDate(ao.getCreateDate()).withDueDate(ao.getDueDate()).withUpdateDate(ao.getUpdateDate()).withTitle(oldTitle).withCompleteDate(ao.getCompleteDate()).withCompleteUser(this.getUsername(ao.getCompleteUserKey())).build();
    }

    private Task asTask(AOInlineTask ao) {
        return this.asTask(ao, null);
    }

    private List<Task> asListTasks(Collection<AOInlineTask> aoInlineTasks) {
        ArrayList tasks = Lists.newArrayList();
        for (AOInlineTask aoInlineTask : aoInlineTasks) {
            tasks.add(this.asTask(aoInlineTask));
        }
        return tasks;
    }

    private String getUsername(String userKey) {
        if (userKey == null) {
            return null;
        }
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
        return user == null ? null : user.getName();
    }

    private String getUserKey(String userName) {
        if (userName == null) {
            return null;
        }
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        return user == null ? null : user.getKey().getStringValue();
    }

    private String quoteQuery(String sql) {
        if (this.tableFieldName == null) {
            this.tableFieldName = this.getQuoteNames(this.needed_quote_names);
        }
        if (this.tableFieldName == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : this.tableFieldName.entrySet()) {
            String unquotedTableName = entry.getKey();
            String quotedTableName = entry.getValue();
            sql = sql.replaceAll("\\b" + Pattern.quote(unquotedTableName) + "\\b", quotedTableName);
        }
        return sql;
    }

    static {
        JOINER_SPACE = Joiner.on((String)" ");
        SELECT_ID_GENERIC = input -> "union all select ?";
        SELECT_ID_HSQLDB = input -> "union all select ? from INFORMATION_SCHEMA.SYSTEM_USERS limit 1";
        SELECT_ID_ORACLE = input -> "union all select ? from DUAL";
    }

    private class Statement {
        private List<String> whereConditions = Lists.newArrayList();
        private List<String> joinConditions = Lists.newArrayList();
        private List<Object> params = Lists.newArrayList();
        private List<TaskOrderClause> sortParameters = Collections.emptyList();
        private String from = "";
        private String select = "";

        private Statement() {
        }

        public void addJoin(String statement) {
            this.joinConditions.add(statement);
        }

        public void addCondition(String statement, Object ... statementParams) {
            this.whereConditions.add(statement);
            this.params.addAll(Arrays.asList(statementParams));
        }

        public <T> void addAllCondition(String statement, List<T> ... paramsList) {
            this.whereConditions.add(statement);
            Iterable allParams = Iterables.concat(Arrays.asList(paramsList));
            this.params.addAll(Lists.newArrayList((Iterable)allParams));
        }

        public List<String> getConditions() {
            return ImmutableList.copyOf(this.whereConditions);
        }

        public String getWhere() {
            return this.whereConditions.isEmpty() ? "" : "WHERE " + Joiner.on((String)" AND ").join(this.whereConditions);
        }

        public String getJoin() {
            return this.joinConditions.isEmpty() ? "" : JOINER_SPACE.join(this.joinConditions);
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public String getSelect() {
            Collection sortParamsToAddToSelect = Collections2.filter(this.sortParameters, (Predicate)new Predicate<TaskOrderClause>(){

                public boolean apply(TaskOrderClause sortParam) {
                    Pattern columnPattern = Pattern.compile("\\b" + Pattern.quote(sortParam.getDbSortingClause()) + "\\b");
                    Matcher columnMatcher = columnPattern.matcher(Statement.this.select);
                    return !columnMatcher.find();
                }
            });
            Collection sortColumnsToAddToSelect = Collections2.transform((Collection)sortParamsToAddToSelect, (Function)new Function<TaskOrderClause, String>(){

                public String apply(TaskOrderClause sortParam) {
                    return sortParam.getDbSortingClause();
                }
            });
            return this.select + (String)(sortColumnsToAddToSelect.isEmpty() ? "" : ", " + Joiner.on((String)", ").join((Iterable)sortColumnsToAddToSelect));
        }

        public String asQuery() {
            return JOINER_SPACE.skipNulls().join(Arrays.asList(this.getSelect(), this.from, this.getJoin(), this.getWhere(), this.getOrderBy()));
        }

        public List<Object> getParams() {
            return ImmutableList.copyOf(this.params);
        }

        public void setSortParams(@Nonnull List<TaskOrderClause> sortParams) {
            this.sortParameters = sortParams;
        }

        public String getOrderBy() {
            return this.sortParameters.isEmpty() ? "" : "ORDER BY " + Joiner.on((String)", ").join(this.sortParameters);
        }
    }
}

