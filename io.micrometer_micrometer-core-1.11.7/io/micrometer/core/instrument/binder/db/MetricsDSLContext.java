/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jooq.AlterDatabaseStep
 *  org.jooq.AlterDomainStep
 *  org.jooq.AlterIndexOnStep
 *  org.jooq.AlterIndexStep
 *  org.jooq.AlterSchemaStep
 *  org.jooq.AlterSequenceStep
 *  org.jooq.AlterTableStep
 *  org.jooq.AlterTypeStep
 *  org.jooq.AlterViewStep
 *  org.jooq.Attachable
 *  org.jooq.Batch
 *  org.jooq.BatchBindStep
 *  org.jooq.BatchedCallable
 *  org.jooq.BatchedRunnable
 *  org.jooq.BindContext
 *  org.jooq.Block
 *  org.jooq.Catalog
 *  org.jooq.CommentOnIsStep
 *  org.jooq.CommonTableExpression
 *  org.jooq.Condition
 *  org.jooq.Configuration
 *  org.jooq.ConnectionCallable
 *  org.jooq.ConnectionRunnable
 *  org.jooq.ContextTransactionalCallable
 *  org.jooq.ContextTransactionalRunnable
 *  org.jooq.CreateDatabaseFinalStep
 *  org.jooq.CreateDomainAsStep
 *  org.jooq.CreateIndexStep
 *  org.jooq.CreateSchemaFinalStep
 *  org.jooq.CreateSequenceFlagsStep
 *  org.jooq.CreateTableColumnStep
 *  org.jooq.CreateTypeStep
 *  org.jooq.CreateViewAsStep
 *  org.jooq.Cursor
 *  org.jooq.DDLExportConfiguration
 *  org.jooq.DDLFlag
 *  org.jooq.DSLContext
 *  org.jooq.DataType
 *  org.jooq.DeleteQuery
 *  org.jooq.DeleteUsingStep
 *  org.jooq.Domain
 *  org.jooq.DropDatabaseFinalStep
 *  org.jooq.DropDomainCascadeStep
 *  org.jooq.DropIndexOnStep
 *  org.jooq.DropSchemaStep
 *  org.jooq.DropSequenceFinalStep
 *  org.jooq.DropTableStep
 *  org.jooq.DropTypeStep
 *  org.jooq.DropViewFinalStep
 *  org.jooq.ExecuteListenerProvider
 *  org.jooq.Explain
 *  org.jooq.Field
 *  org.jooq.GrantOnStep
 *  org.jooq.Index
 *  org.jooq.InsertQuery
 *  org.jooq.InsertSetStep
 *  org.jooq.InsertValuesStep1
 *  org.jooq.InsertValuesStep10
 *  org.jooq.InsertValuesStep11
 *  org.jooq.InsertValuesStep12
 *  org.jooq.InsertValuesStep13
 *  org.jooq.InsertValuesStep14
 *  org.jooq.InsertValuesStep15
 *  org.jooq.InsertValuesStep16
 *  org.jooq.InsertValuesStep17
 *  org.jooq.InsertValuesStep18
 *  org.jooq.InsertValuesStep19
 *  org.jooq.InsertValuesStep2
 *  org.jooq.InsertValuesStep20
 *  org.jooq.InsertValuesStep21
 *  org.jooq.InsertValuesStep22
 *  org.jooq.InsertValuesStep3
 *  org.jooq.InsertValuesStep4
 *  org.jooq.InsertValuesStep5
 *  org.jooq.InsertValuesStep6
 *  org.jooq.InsertValuesStep7
 *  org.jooq.InsertValuesStep8
 *  org.jooq.InsertValuesStep9
 *  org.jooq.InsertValuesStepN
 *  org.jooq.Internal
 *  org.jooq.LoaderOptionsStep
 *  org.jooq.MergeKeyStep1
 *  org.jooq.MergeKeyStep10
 *  org.jooq.MergeKeyStep11
 *  org.jooq.MergeKeyStep12
 *  org.jooq.MergeKeyStep13
 *  org.jooq.MergeKeyStep14
 *  org.jooq.MergeKeyStep15
 *  org.jooq.MergeKeyStep16
 *  org.jooq.MergeKeyStep17
 *  org.jooq.MergeKeyStep18
 *  org.jooq.MergeKeyStep19
 *  org.jooq.MergeKeyStep2
 *  org.jooq.MergeKeyStep20
 *  org.jooq.MergeKeyStep21
 *  org.jooq.MergeKeyStep22
 *  org.jooq.MergeKeyStep3
 *  org.jooq.MergeKeyStep4
 *  org.jooq.MergeKeyStep5
 *  org.jooq.MergeKeyStep6
 *  org.jooq.MergeKeyStep7
 *  org.jooq.MergeKeyStep8
 *  org.jooq.MergeKeyStep9
 *  org.jooq.MergeKeyStepN
 *  org.jooq.MergeUsingStep
 *  org.jooq.Meta
 *  org.jooq.Migration
 *  org.jooq.Name
 *  org.jooq.Param
 *  org.jooq.Parser
 *  org.jooq.Privilege
 *  org.jooq.Queries
 *  org.jooq.Query
 *  org.jooq.QueryPart
 *  org.jooq.Record
 *  org.jooq.Record1
 *  org.jooq.Record10
 *  org.jooq.Record11
 *  org.jooq.Record12
 *  org.jooq.Record13
 *  org.jooq.Record14
 *  org.jooq.Record15
 *  org.jooq.Record16
 *  org.jooq.Record17
 *  org.jooq.Record18
 *  org.jooq.Record19
 *  org.jooq.Record2
 *  org.jooq.Record20
 *  org.jooq.Record21
 *  org.jooq.Record22
 *  org.jooq.Record3
 *  org.jooq.Record4
 *  org.jooq.Record5
 *  org.jooq.Record6
 *  org.jooq.Record7
 *  org.jooq.Record8
 *  org.jooq.Record9
 *  org.jooq.RenderContext
 *  org.jooq.Result
 *  org.jooq.ResultQuery
 *  org.jooq.Results
 *  org.jooq.RevokeOnStep
 *  org.jooq.RowCountQuery
 *  org.jooq.SQL
 *  org.jooq.SQLDialect
 *  org.jooq.Schema
 *  org.jooq.Select
 *  org.jooq.SelectField
 *  org.jooq.SelectFieldOrAsterisk
 *  org.jooq.SelectQuery
 *  org.jooq.SelectSelectStep
 *  org.jooq.SelectWhereStep
 *  org.jooq.Sequence
 *  org.jooq.Source
 *  org.jooq.Statement
 *  org.jooq.Table
 *  org.jooq.TableField
 *  org.jooq.TableLike
 *  org.jooq.TableRecord
 *  org.jooq.TransactionalCallable
 *  org.jooq.TransactionalRunnable
 *  org.jooq.TruncateIdentityStep
 *  org.jooq.UDT
 *  org.jooq.UDTRecord
 *  org.jooq.UpdatableRecord
 *  org.jooq.UpdateQuery
 *  org.jooq.UpdateSetFirstStep
 *  org.jooq.Version
 *  org.jooq.WithAsStep
 *  org.jooq.WithAsStep1
 *  org.jooq.WithAsStep10
 *  org.jooq.WithAsStep11
 *  org.jooq.WithAsStep12
 *  org.jooq.WithAsStep13
 *  org.jooq.WithAsStep14
 *  org.jooq.WithAsStep15
 *  org.jooq.WithAsStep16
 *  org.jooq.WithAsStep17
 *  org.jooq.WithAsStep18
 *  org.jooq.WithAsStep19
 *  org.jooq.WithAsStep2
 *  org.jooq.WithAsStep20
 *  org.jooq.WithAsStep21
 *  org.jooq.WithAsStep22
 *  org.jooq.WithAsStep3
 *  org.jooq.WithAsStep4
 *  org.jooq.WithAsStep5
 *  org.jooq.WithAsStep6
 *  org.jooq.WithAsStep7
 *  org.jooq.WithAsStep8
 *  org.jooq.WithAsStep9
 *  org.jooq.WithStep
 *  org.jooq.conf.Settings
 *  org.jooq.exception.ConfigurationException
 *  org.jooq.exception.DataAccessException
 *  org.jooq.exception.InvalidResultException
 *  org.jooq.exception.NoDataFoundException
 *  org.jooq.exception.TooManyRowsException
 *  org.jooq.impl.DSL
 *  org.jooq.tools.jdbc.MockCallable
 *  org.jooq.tools.jdbc.MockDataProvider
 *  org.jooq.tools.jdbc.MockRunnable
 *  org.jooq.util.xml.jaxb.InformationSchema
 */
package io.micrometer.core.instrument.binder.db;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.db.JooqExecuteListener;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.jooq.AlterDatabaseStep;
import org.jooq.AlterDomainStep;
import org.jooq.AlterIndexOnStep;
import org.jooq.AlterIndexStep;
import org.jooq.AlterSchemaStep;
import org.jooq.AlterSequenceStep;
import org.jooq.AlterTableStep;
import org.jooq.AlterTypeStep;
import org.jooq.AlterViewStep;
import org.jooq.Attachable;
import org.jooq.Batch;
import org.jooq.BatchBindStep;
import org.jooq.BatchedCallable;
import org.jooq.BatchedRunnable;
import org.jooq.BindContext;
import org.jooq.Block;
import org.jooq.Catalog;
import org.jooq.CommentOnIsStep;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ConnectionCallable;
import org.jooq.ConnectionRunnable;
import org.jooq.ContextTransactionalCallable;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.CreateDatabaseFinalStep;
import org.jooq.CreateDomainAsStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateSchemaFinalStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.CreateTableColumnStep;
import org.jooq.CreateTypeStep;
import org.jooq.CreateViewAsStep;
import org.jooq.Cursor;
import org.jooq.DDLExportConfiguration;
import org.jooq.DDLFlag;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DeleteQuery;
import org.jooq.DeleteUsingStep;
import org.jooq.Domain;
import org.jooq.DropDatabaseFinalStep;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropIndexOnStep;
import org.jooq.DropSchemaStep;
import org.jooq.DropSequenceFinalStep;
import org.jooq.DropTableStep;
import org.jooq.DropTypeStep;
import org.jooq.DropViewFinalStep;
import org.jooq.ExecuteListenerProvider;
import org.jooq.Explain;
import org.jooq.Field;
import org.jooq.GrantOnStep;
import org.jooq.Index;
import org.jooq.InsertQuery;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep11;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep16;
import org.jooq.InsertValuesStep17;
import org.jooq.InsertValuesStep18;
import org.jooq.InsertValuesStep19;
import org.jooq.InsertValuesStep2;
import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep22;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.InsertValuesStep6;
import org.jooq.InsertValuesStep7;
import org.jooq.InsertValuesStep8;
import org.jooq.InsertValuesStep9;
import org.jooq.InsertValuesStepN;
import org.jooq.Internal;
import org.jooq.LoaderOptionsStep;
import org.jooq.MergeKeyStep1;
import org.jooq.MergeKeyStep10;
import org.jooq.MergeKeyStep11;
import org.jooq.MergeKeyStep12;
import org.jooq.MergeKeyStep13;
import org.jooq.MergeKeyStep14;
import org.jooq.MergeKeyStep15;
import org.jooq.MergeKeyStep16;
import org.jooq.MergeKeyStep17;
import org.jooq.MergeKeyStep18;
import org.jooq.MergeKeyStep19;
import org.jooq.MergeKeyStep2;
import org.jooq.MergeKeyStep20;
import org.jooq.MergeKeyStep21;
import org.jooq.MergeKeyStep22;
import org.jooq.MergeKeyStep3;
import org.jooq.MergeKeyStep4;
import org.jooq.MergeKeyStep5;
import org.jooq.MergeKeyStep6;
import org.jooq.MergeKeyStep7;
import org.jooq.MergeKeyStep8;
import org.jooq.MergeKeyStep9;
import org.jooq.MergeKeyStepN;
import org.jooq.MergeUsingStep;
import org.jooq.Meta;
import org.jooq.Migration;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.Parser;
import org.jooq.Privilege;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.RevokeOnStep;
import org.jooq.RowCountQuery;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectQuery;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWhereStep;
import org.jooq.Sequence;
import org.jooq.Source;
import org.jooq.Statement;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.TransactionalCallable;
import org.jooq.TransactionalRunnable;
import org.jooq.TruncateIdentityStep;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.UpdateSetFirstStep;
import org.jooq.Version;
import org.jooq.WithAsStep;
import org.jooq.WithAsStep1;
import org.jooq.WithAsStep10;
import org.jooq.WithAsStep11;
import org.jooq.WithAsStep12;
import org.jooq.WithAsStep13;
import org.jooq.WithAsStep14;
import org.jooq.WithAsStep15;
import org.jooq.WithAsStep16;
import org.jooq.WithAsStep17;
import org.jooq.WithAsStep18;
import org.jooq.WithAsStep19;
import org.jooq.WithAsStep2;
import org.jooq.WithAsStep20;
import org.jooq.WithAsStep21;
import org.jooq.WithAsStep22;
import org.jooq.WithAsStep3;
import org.jooq.WithAsStep4;
import org.jooq.WithAsStep5;
import org.jooq.WithAsStep6;
import org.jooq.WithAsStep7;
import org.jooq.WithAsStep8;
import org.jooq.WithAsStep9;
import org.jooq.WithStep;
import org.jooq.conf.Settings;
import org.jooq.exception.ConfigurationException;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockCallable;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockRunnable;
import org.jooq.util.xml.jaxb.InformationSchema;

@Incubating(since="1.4.0")
public class MetricsDSLContext
implements DSLContext {
    private final DSLContext context;
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final ThreadLocal<Iterable<Tag>> contextTags = new ThreadLocal();
    private final ExecuteListenerProvider defaultExecuteListenerProvider;

    public static MetricsDSLContext withMetrics(DSLContext jooq, MeterRegistry registry, Iterable<Tag> tags) {
        return new MetricsDSLContext(jooq, registry, tags);
    }

    MetricsDSLContext(DSLContext context, MeterRegistry registry, Iterable<Tag> tags) {
        this.registry = registry;
        this.tags = tags;
        this.defaultExecuteListenerProvider = () -> new JooqExecuteListener(registry, tags, () -> {
            Iterable<Tag> queryTags = this.contextTags.get();
            this.contextTags.remove();
            return queryTags;
        });
        Configuration configuration = context.configuration().derive();
        Configuration derivedConfiguration = this.derive(configuration, this.defaultExecuteListenerProvider);
        this.context = DSL.using((Configuration)derivedConfiguration);
    }

    public <Q extends Query> Q time(Q q) {
        q.attach(this.time(q.configuration()));
        return q;
    }

    public Configuration time(Configuration c) {
        Iterable<Tag> queryTags = this.contextTags.get();
        this.contextTags.remove();
        return this.derive(c, () -> new JooqExecuteListener(this.registry, this.tags, () -> queryTags));
    }

    private Configuration derive(Configuration configuration, ExecuteListenerProvider executeListenerProvider) {
        ExecuteListenerProvider[] providers = configuration.executeListenerProviders();
        for (int i = 0; i < providers.length; ++i) {
            if (providers[i] != this.defaultExecuteListenerProvider) continue;
            ExecuteListenerProvider[] newProviders = Arrays.copyOf(providers, providers.length);
            newProviders[i] = executeListenerProvider;
            return configuration.derive(newProviders);
        }
        ExecuteListenerProvider[] newProviders = Arrays.copyOf(providers, providers.length + 1);
        newProviders[providers.length] = executeListenerProvider;
        return configuration.derive(newProviders);
    }

    public <O> O timeCoercable(Object o) {
        return (O)this.time((Query)o);
    }

    public DSLContext tag(String key, String name) {
        return this.tags(Tags.of(key, name));
    }

    public DSLContext tag(Tag tag) {
        return this.tags(Tags.of(tag));
    }

    public DSLContext tags(Iterable<Tag> tags) {
        this.contextTags.set(tags);
        return this;
    }

    public Schema map(Schema schema) {
        return this.context.map(schema);
    }

    public <R extends Record> Table<R> map(Table<R> table) {
        return this.context.map(table);
    }

    public Parser parser() {
        return this.context.parser();
    }

    public Connection parsingConnection() {
        return this.context.parsingConnection();
    }

    public DataSource parsingDataSource() {
        return this.context.parsingDataSource();
    }

    public Connection diagnosticsConnection() {
        return this.context.diagnosticsConnection();
    }

    public DataSource diagnosticsDataSource() {
        return this.context.diagnosticsDataSource();
    }

    public Version version(String id) {
        return this.context.version(id);
    }

    public Migration migrateTo(Version to) {
        return this.context.migrateTo(to);
    }

    public Meta meta() {
        return this.context.meta();
    }

    public Meta meta(DatabaseMetaData meta) {
        return this.context.meta(meta);
    }

    public Meta meta(Catalog ... catalogs) {
        return this.context.meta(catalogs);
    }

    public Meta meta(Schema ... schemas) {
        return this.context.meta(schemas);
    }

    public Meta meta(Table<?> ... tables) {
        return this.context.meta(tables);
    }

    public Meta meta(InformationSchema schema) {
        return this.context.meta(schema);
    }

    public Meta meta(String ... sources) {
        return this.context.meta(sources);
    }

    @Internal
    public Meta meta(Source ... scripts) {
        return this.context.meta(scripts);
    }

    public Meta meta(Query ... queries) {
        return this.context.meta(queries);
    }

    public InformationSchema informationSchema(Catalog catalog) {
        return this.context.informationSchema(catalog);
    }

    public InformationSchema informationSchema(Catalog ... catalogs) {
        return this.context.informationSchema(catalogs);
    }

    public InformationSchema informationSchema(Schema schema) {
        return this.context.informationSchema(schema);
    }

    public InformationSchema informationSchema(Schema ... schemas) {
        return this.context.informationSchema(schemas);
    }

    public InformationSchema informationSchema(Table<?> table) {
        return this.context.informationSchema(table);
    }

    public InformationSchema informationSchema(Table<?> ... table) {
        return this.context.informationSchema(table);
    }

    public Explain explain(Query query) {
        return this.context.explain(query);
    }

    public <T> T transactionResult(TransactionalCallable<T> transactional) {
        return (T)this.context.transactionResult(transactional);
    }

    public <T> T transactionResult(ContextTransactionalCallable<T> transactional) throws ConfigurationException {
        return (T)this.context.transactionResult(transactional);
    }

    public void transaction(TransactionalRunnable transactional) {
        this.context.transaction(transactional);
    }

    public void transaction(ContextTransactionalRunnable transactional) throws ConfigurationException {
        this.context.transaction(transactional);
    }

    public <T> CompletionStage<T> transactionResultAsync(TransactionalCallable<T> transactional) throws ConfigurationException {
        return this.context.transactionResultAsync(transactional);
    }

    public CompletionStage<Void> transactionAsync(TransactionalRunnable transactional) throws ConfigurationException {
        return this.context.transactionAsync(transactional);
    }

    public <T> CompletionStage<T> transactionResultAsync(Executor executor, TransactionalCallable<T> transactional) throws ConfigurationException {
        return this.context.transactionResultAsync(executor, transactional);
    }

    public CompletionStage<Void> transactionAsync(Executor executor, TransactionalRunnable transactional) throws ConfigurationException {
        return this.context.transactionAsync(executor, transactional);
    }

    public <T> T connectionResult(ConnectionCallable<T> callable) {
        return (T)this.context.connectionResult(callable);
    }

    public void connection(ConnectionRunnable runnable) {
        this.context.connection(runnable);
    }

    public <T> T mockResult(MockDataProvider provider, MockCallable<T> mockable) {
        return (T)this.context.mockResult(provider, mockable);
    }

    public void mock(MockDataProvider provider, MockRunnable mockable) {
        this.context.mock(provider, mockable);
    }

    @Deprecated
    @Internal
    public RenderContext renderContext() {
        return this.context.renderContext();
    }

    public String render(QueryPart part) {
        return this.context.render(part);
    }

    public String renderNamedParams(QueryPart part) {
        return this.context.renderNamedParams(part);
    }

    public String renderNamedOrInlinedParams(QueryPart part) {
        return this.context.renderNamedOrInlinedParams(part);
    }

    public String renderInlined(QueryPart part) {
        return this.context.renderInlined(part);
    }

    public List<Object> extractBindValues(QueryPart part) {
        return this.context.extractBindValues(part);
    }

    public Map<String, Param<?>> extractParams(QueryPart part) {
        return this.context.extractParams(part);
    }

    public Param<?> extractParam(QueryPart part, String name) {
        return this.context.extractParam(part, name);
    }

    @Deprecated
    @Internal
    public BindContext bindContext(PreparedStatement stmt) {
        return this.context.bindContext(stmt);
    }

    @Deprecated
    public int bind(QueryPart part, PreparedStatement stmt) {
        return this.context.bind(part, stmt);
    }

    public void attach(Attachable ... attachables) {
        this.context.attach(attachables);
    }

    public void attach(Collection<? extends Attachable> attachables) {
        this.context.attach(attachables);
    }

    public <R extends Record> LoaderOptionsStep<R> loadInto(Table<R> table) {
        return this.context.loadInto(table);
    }

    public Queries queries(Query ... queries) {
        return this.context.queries(queries);
    }

    public Queries queries(Collection<? extends Query> queries) {
        return this.context.queries(queries);
    }

    public Block begin(Statement ... statements) {
        return this.context.begin(statements);
    }

    public Block begin(Collection<? extends Statement> statements) {
        return this.context.begin(statements);
    }

    public RowCountQuery query(SQL sql) {
        return this.context.query(sql);
    }

    public RowCountQuery query(String sql) {
        return this.context.query(sql);
    }

    public RowCountQuery query(String sql, Object ... bindings) {
        return this.context.query(sql, bindings);
    }

    public RowCountQuery query(String sql, QueryPart ... parts) {
        return this.context.query(sql, parts);
    }

    public Result<Record> fetch(SQL sql) throws DataAccessException {
        return this.context.fetch(sql);
    }

    public Result<Record> fetch(String sql) throws DataAccessException {
        return this.context.fetch(sql);
    }

    public Result<Record> fetch(String sql, Object ... bindings) throws DataAccessException {
        return this.context.fetch(sql, bindings);
    }

    public Result<Record> fetch(String sql, QueryPart ... parts) throws DataAccessException {
        return this.context.fetch(sql, parts);
    }

    public Cursor<Record> fetchLazy(SQL sql) throws DataAccessException {
        return this.context.fetchLazy(sql);
    }

    public Cursor<Record> fetchLazy(String sql) throws DataAccessException {
        return this.context.fetchLazy(sql);
    }

    public Cursor<Record> fetchLazy(String sql, Object ... bindings) throws DataAccessException {
        return this.context.fetchLazy(sql, bindings);
    }

    public Cursor<Record> fetchLazy(String sql, QueryPart ... parts) throws DataAccessException {
        return this.context.fetchLazy(sql, parts);
    }

    public CompletionStage<Result<Record>> fetchAsync(SQL sql) {
        return this.context.fetchAsync(sql);
    }

    public CompletionStage<Result<Record>> fetchAsync(String sql) {
        return this.context.fetchAsync(sql);
    }

    public CompletionStage<Result<Record>> fetchAsync(String sql, Object ... bindings) {
        return this.context.fetchAsync(sql, bindings);
    }

    public CompletionStage<Result<Record>> fetchAsync(String sql, QueryPart ... parts) {
        return this.context.fetchAsync(sql, parts);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, SQL sql) {
        return this.context.fetchAsync(executor, sql);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql) {
        return this.context.fetchAsync(executor, sql);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql, Object ... bindings) {
        return this.context.fetchAsync(executor, sql, bindings);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, String sql, QueryPart ... parts) {
        return this.context.fetchAsync(executor, sql, parts);
    }

    public Stream<Record> fetchStream(SQL sql) throws DataAccessException {
        return this.context.fetchStream(sql);
    }

    public Stream<Record> fetchStream(String sql) throws DataAccessException {
        return this.context.fetchStream(sql);
    }

    public Stream<Record> fetchStream(String sql, Object ... bindings) throws DataAccessException {
        return this.context.fetchStream(sql, bindings);
    }

    public Stream<Record> fetchStream(String sql, QueryPart ... parts) throws DataAccessException {
        return this.context.fetchStream(sql, parts);
    }

    public Results fetchMany(SQL sql) throws DataAccessException {
        return this.context.fetchMany(sql);
    }

    public Results fetchMany(String sql) throws DataAccessException {
        return this.context.fetchMany(sql);
    }

    public Results fetchMany(String sql, Object ... bindings) throws DataAccessException {
        return this.context.fetchMany(sql, bindings);
    }

    public Results fetchMany(String sql, QueryPart ... parts) throws DataAccessException {
        return this.context.fetchMany(sql, parts);
    }

    public Record fetchOne(SQL sql) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(sql);
    }

    public Record fetchOne(String sql) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(sql);
    }

    public Record fetchOne(String sql, Object ... bindings) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(sql, bindings);
    }

    public Record fetchOne(String sql, QueryPart ... parts) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(sql, parts);
    }

    public Record fetchSingle(SQL sql) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(sql);
    }

    public Record fetchSingle(String sql) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(sql);
    }

    public Record fetchSingle(String sql, Object ... bindings) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(sql, bindings);
    }

    public Record fetchSingle(String sql, QueryPart ... parts) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(sql, parts);
    }

    public Optional<Record> fetchOptional(SQL sql) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(sql);
    }

    public Optional<Record> fetchOptional(String sql) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(sql);
    }

    public Optional<Record> fetchOptional(String sql, Object ... bindings) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(sql, bindings);
    }

    public Optional<Record> fetchOptional(String sql, QueryPart ... parts) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(sql, parts);
    }

    public Object fetchValue(SQL sql) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchValue(sql);
    }

    public Object fetchValue(String sql) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchValue(sql);
    }

    public Object fetchValue(String sql, Object ... bindings) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchValue(sql, bindings);
    }

    public Object fetchValue(String sql, QueryPart ... parts) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchValue(sql, parts);
    }

    public Optional<?> fetchOptionalValue(SQL sql) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(sql);
    }

    public Optional<?> fetchOptionalValue(String sql) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(sql);
    }

    public Optional<?> fetchOptionalValue(String sql, Object ... bindings) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(sql, bindings);
    }

    public Optional<?> fetchOptionalValue(String sql, QueryPart ... parts) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(sql, parts);
    }

    public List<?> fetchValues(SQL sql) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(sql);
    }

    public List<?> fetchValues(String sql) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(sql);
    }

    public List<?> fetchValues(String sql, Object ... bindings) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(sql, bindings);
    }

    public List<?> fetchValues(String sql, QueryPart ... parts) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(sql, parts);
    }

    public int execute(SQL sql) throws DataAccessException {
        return this.context.execute(sql);
    }

    public int execute(String sql) throws DataAccessException {
        return this.context.execute(sql);
    }

    public int execute(String sql, Object ... bindings) throws DataAccessException {
        return this.context.execute(sql, bindings);
    }

    public int execute(String sql, QueryPart ... parts) throws DataAccessException {
        return this.context.execute(sql, parts);
    }

    public ResultQuery<Record> resultQuery(SQL sql) {
        return this.context.resultQuery(sql);
    }

    public ResultQuery<Record> resultQuery(String sql) {
        return this.context.resultQuery(sql);
    }

    public ResultQuery<Record> resultQuery(String sql, Object ... bindings) {
        return this.context.resultQuery(sql, bindings);
    }

    public ResultQuery<Record> resultQuery(String sql, QueryPart ... parts) {
        return this.context.resultQuery(sql, parts);
    }

    public Result<Record> fetch(ResultSet rs) throws DataAccessException {
        return this.context.fetch(rs);
    }

    public Result<Record> fetch(ResultSet rs, Field<?> ... fields) throws DataAccessException {
        return this.context.fetch(rs, fields);
    }

    public Result<Record> fetch(ResultSet rs, DataType<?> ... types) throws DataAccessException {
        return this.context.fetch(rs, types);
    }

    public Result<Record> fetch(ResultSet rs, Class<?> ... types) throws DataAccessException {
        return this.context.fetch(rs, (Class[])types);
    }

    public Record fetchOne(ResultSet rs) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(rs);
    }

    public Record fetchOne(ResultSet rs, Field<?> ... fields) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(rs, fields);
    }

    public Record fetchOne(ResultSet rs, DataType<?> ... types) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(rs, types);
    }

    public Record fetchOne(ResultSet rs, Class<?> ... types) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOne(rs, (Class[])types);
    }

    public Record fetchSingle(ResultSet rs) throws DataAccessException, TooManyRowsException {
        return this.context.fetchSingle(rs);
    }

    public Record fetchSingle(ResultSet rs, Field<?> ... fields) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(rs, fields);
    }

    public Record fetchSingle(ResultSet rs, DataType<?> ... types) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(rs, types);
    }

    public Record fetchSingle(ResultSet rs, Class<?> ... types) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchSingle(rs, (Class[])types);
    }

    public Optional<Record> fetchOptional(ResultSet rs) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return this.context.fetchOptional(rs);
    }

    public Optional<Record> fetchOptional(ResultSet rs, Field<?> ... fields) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(rs, fields);
    }

    public Optional<Record> fetchOptional(ResultSet rs, DataType<?> ... types) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(rs, types);
    }

    public Optional<Record> fetchOptional(ResultSet rs, Class<?> ... types) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(rs, (Class[])types);
    }

    public Object fetchValue(ResultSet rs) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchValue(rs);
    }

    public <T> T fetchValue(ResultSet rs, Field<T> field) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return (T)this.context.fetchValue(rs, field);
    }

    public <T> T fetchValue(ResultSet rs, DataType<T> type) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return (T)this.context.fetchValue(rs, type);
    }

    public <T> T fetchValue(ResultSet rs, Class<T> type) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return (T)this.context.fetchValue(rs, type);
    }

    public Optional<?> fetchOptionalValue(ResultSet rs) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(rs);
    }

    public <T> Optional<T> fetchOptionalValue(ResultSet rs, Field<T> field) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(rs, field);
    }

    public <T> Optional<T> fetchOptionalValue(ResultSet rs, DataType<T> type) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(rs, type);
    }

    public <T> Optional<T> fetchOptionalValue(ResultSet rs, Class<T> type) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(rs, type);
    }

    public List<?> fetchValues(ResultSet rs) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(rs);
    }

    public <T> List<T> fetchValues(ResultSet rs, Field<T> field) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(rs, field);
    }

    public <T> List<T> fetchValues(ResultSet rs, DataType<T> type) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(rs, type);
    }

    public <T> List<T> fetchValues(ResultSet rs, Class<T> type) throws DataAccessException, InvalidResultException {
        return this.context.fetchValues(rs, type);
    }

    public Cursor<Record> fetchLazy(ResultSet rs) throws DataAccessException {
        return this.context.fetchLazy(rs);
    }

    public Cursor<Record> fetchLazy(ResultSet rs, Field<?> ... fields) throws DataAccessException {
        return this.context.fetchLazy(rs, fields);
    }

    public Cursor<Record> fetchLazy(ResultSet rs, DataType<?> ... types) throws DataAccessException {
        return this.context.fetchLazy(rs, types);
    }

    public Cursor<Record> fetchLazy(ResultSet rs, Class<?> ... types) throws DataAccessException {
        return this.context.fetchLazy(rs, (Class[])types);
    }

    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs) {
        return this.context.fetchAsync(rs);
    }

    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, Field<?> ... fields) {
        return this.context.fetchAsync(rs, fields);
    }

    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, DataType<?> ... types) {
        return this.context.fetchAsync(rs, types);
    }

    public CompletionStage<Result<Record>> fetchAsync(ResultSet rs, Class<?> ... types) {
        return this.context.fetchAsync(rs, (Class[])types);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs) {
        return this.context.fetchAsync(executor, rs);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, Field<?> ... fields) {
        return this.context.fetchAsync(executor, rs, fields);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, DataType<?> ... types) {
        return this.context.fetchAsync(executor, rs, types);
    }

    public CompletionStage<Result<Record>> fetchAsync(Executor executor, ResultSet rs, Class<?> ... types) {
        return this.context.fetchAsync(executor, rs, (Class[])types);
    }

    public Stream<Record> fetchStream(ResultSet rs) throws DataAccessException {
        return this.context.fetchStream(rs);
    }

    public Stream<Record> fetchStream(ResultSet rs, Field<?> ... fields) throws DataAccessException {
        return this.context.fetchStream(rs, fields);
    }

    public Stream<Record> fetchStream(ResultSet rs, DataType<?> ... types) throws DataAccessException {
        return this.context.fetchStream(rs, types);
    }

    public Stream<Record> fetchStream(ResultSet rs, Class<?> ... types) throws DataAccessException {
        return this.context.fetchStream(rs, (Class[])types);
    }

    public Result<Record> fetchFromTXT(String string) throws DataAccessException {
        return this.context.fetchFromTXT(string);
    }

    public Result<Record> fetchFromTXT(String string, String nullLiteral) throws DataAccessException {
        return this.context.fetchFromTXT(string, nullLiteral);
    }

    public Result<Record> fetchFromHTML(String string) throws DataAccessException {
        return this.context.fetchFromHTML(string);
    }

    public Result<Record> fetchFromCSV(String string) throws DataAccessException {
        return this.context.fetchFromCSV(string);
    }

    public Result<Record> fetchFromCSV(String string, char delimiter) throws DataAccessException {
        return this.context.fetchFromCSV(string, delimiter);
    }

    public Result<Record> fetchFromCSV(String string, boolean header) throws DataAccessException {
        return this.context.fetchFromCSV(string, header);
    }

    public Result<Record> fetchFromCSV(String string, boolean header, char delimiter) throws DataAccessException {
        return this.context.fetchFromCSV(string, header, delimiter);
    }

    public Result<Record> fetchFromJSON(String string) {
        return this.context.fetchFromJSON(string);
    }

    public Result<Record> fetchFromXML(String string) {
        return this.context.fetchFromXML(string);
    }

    public Result<Record> fetchFromStringData(String[] ... data) {
        return this.context.fetchFromStringData(data);
    }

    public Result<Record> fetchFromStringData(List<String[]> data) {
        return this.context.fetchFromStringData(data);
    }

    public Result<Record> fetchFromStringData(List<String[]> data, boolean header) {
        return this.context.fetchFromStringData(data, header);
    }

    public WithAsStep with(String alias) {
        return this.context.with(alias);
    }

    public WithAsStep with(String alias, String ... fieldAliases) {
        return this.context.with(alias, fieldAliases);
    }

    public WithAsStep with(String alias, Collection<String> fieldAliases) {
        return this.context.with(alias, fieldAliases);
    }

    public WithAsStep with(Name alias) {
        return this.context.with(alias);
    }

    public WithAsStep with(Name alias, Name ... fieldAliases) {
        return this.context.with(alias, fieldAliases);
    }

    public WithAsStep with(Name alias, Collection<? extends Name> fieldAliases) {
        return this.context.with(alias, fieldAliases);
    }

    public WithAsStep with(String alias, Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return this.context.with(alias, fieldNameFunction);
    }

    public WithAsStep with(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return this.context.with(alias, fieldNameFunction);
    }

    public WithAsStep1 with(String alias, String fieldAlias1) {
        return this.context.with(alias, fieldAlias1);
    }

    public WithAsStep2 with(String alias, String fieldAlias1, String fieldAlias2) {
        return this.context.with(alias, fieldAlias1, fieldAlias2);
    }

    public WithAsStep3 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    public WithAsStep4 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    public WithAsStep5 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    public WithAsStep6 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    public WithAsStep7 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    public WithAsStep8 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    public WithAsStep9 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    public WithAsStep10 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    public WithAsStep11 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    public WithAsStep12 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    public WithAsStep13 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    public WithAsStep14 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    public WithAsStep15 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    public WithAsStep16 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    public WithAsStep17 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    public WithAsStep18 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    public WithAsStep19 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    public WithAsStep20 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    public WithAsStep21 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    public WithAsStep22 with(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    public WithAsStep1 with(Name alias, Name fieldAlias1) {
        return this.context.with(alias, fieldAlias1);
    }

    public WithAsStep2 with(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return this.context.with(alias, fieldAlias1, fieldAlias2);
    }

    public WithAsStep3 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    public WithAsStep4 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    public WithAsStep5 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    public WithAsStep6 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    public WithAsStep7 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    public WithAsStep8 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    public WithAsStep9 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    public WithAsStep10 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    public WithAsStep11 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    public WithAsStep12 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    public WithAsStep13 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    public WithAsStep14 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    public WithAsStep15 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    public WithAsStep16 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    public WithAsStep17 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    public WithAsStep18 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    public WithAsStep19 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    public WithAsStep20 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    public WithAsStep21 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    public WithAsStep22 with(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return this.context.with(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    public WithStep with(CommonTableExpression<?> ... tables) {
        return this.context.with(tables);
    }

    public WithStep with(Collection<? extends CommonTableExpression<?>> tables) {
        return this.context.with(tables);
    }

    public WithAsStep withRecursive(String alias) {
        return this.context.withRecursive(alias);
    }

    public WithAsStep withRecursive(String alias, String ... fieldAliases) {
        return this.context.withRecursive(alias, fieldAliases);
    }

    public WithAsStep withRecursive(String alias, Collection<String> fieldAliases) {
        return this.context.withRecursive(alias, fieldAliases);
    }

    public WithAsStep withRecursive(Name alias) {
        return this.context.withRecursive(alias);
    }

    public WithAsStep withRecursive(Name alias, Name ... fieldAliases) {
        return this.context.withRecursive(alias, fieldAliases);
    }

    public WithAsStep withRecursive(Name alias, Collection<? extends Name> fieldAliases) {
        return this.context.withRecursive(alias, fieldAliases);
    }

    public WithAsStep withRecursive(String alias, Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return this.context.withRecursive(alias, fieldNameFunction);
    }

    public WithAsStep withRecursive(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return this.context.withRecursive(alias, fieldNameFunction);
    }

    public WithAsStep1 withRecursive(String alias, String fieldAlias1) {
        return this.context.withRecursive(alias, fieldAlias1);
    }

    public WithAsStep2 withRecursive(String alias, String fieldAlias1, String fieldAlias2) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2);
    }

    public WithAsStep3 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    public WithAsStep4 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    public WithAsStep5 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    public WithAsStep6 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    public WithAsStep7 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    public WithAsStep8 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    public WithAsStep9 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    public WithAsStep10 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    public WithAsStep11 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    public WithAsStep12 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    public WithAsStep13 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    public WithAsStep14 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    public WithAsStep15 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    public WithAsStep16 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    public WithAsStep17 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    public WithAsStep18 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    public WithAsStep19 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    public WithAsStep20 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    public WithAsStep21 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    public WithAsStep22 withRecursive(String alias, String fieldAlias1, String fieldAlias2, String fieldAlias3, String fieldAlias4, String fieldAlias5, String fieldAlias6, String fieldAlias7, String fieldAlias8, String fieldAlias9, String fieldAlias10, String fieldAlias11, String fieldAlias12, String fieldAlias13, String fieldAlias14, String fieldAlias15, String fieldAlias16, String fieldAlias17, String fieldAlias18, String fieldAlias19, String fieldAlias20, String fieldAlias21, String fieldAlias22) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    public WithAsStep1 withRecursive(Name alias, Name fieldAlias1) {
        return this.context.withRecursive(alias, fieldAlias1);
    }

    public WithAsStep2 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2);
    }

    public WithAsStep3 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3);
    }

    public WithAsStep4 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4);
    }

    public WithAsStep5 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5);
    }

    public WithAsStep6 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6);
    }

    public WithAsStep7 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7);
    }

    public WithAsStep8 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8);
    }

    public WithAsStep9 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9);
    }

    public WithAsStep10 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10);
    }

    public WithAsStep11 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11);
    }

    public WithAsStep12 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12);
    }

    public WithAsStep13 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13);
    }

    public WithAsStep14 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14);
    }

    public WithAsStep15 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15);
    }

    public WithAsStep16 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16);
    }

    public WithAsStep17 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17);
    }

    public WithAsStep18 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18);
    }

    public WithAsStep19 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19);
    }

    public WithAsStep20 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20);
    }

    public WithAsStep21 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21);
    }

    public WithAsStep22 withRecursive(Name alias, Name fieldAlias1, Name fieldAlias2, Name fieldAlias3, Name fieldAlias4, Name fieldAlias5, Name fieldAlias6, Name fieldAlias7, Name fieldAlias8, Name fieldAlias9, Name fieldAlias10, Name fieldAlias11, Name fieldAlias12, Name fieldAlias13, Name fieldAlias14, Name fieldAlias15, Name fieldAlias16, Name fieldAlias17, Name fieldAlias18, Name fieldAlias19, Name fieldAlias20, Name fieldAlias21, Name fieldAlias22) {
        return this.context.withRecursive(alias, fieldAlias1, fieldAlias2, fieldAlias3, fieldAlias4, fieldAlias5, fieldAlias6, fieldAlias7, fieldAlias8, fieldAlias9, fieldAlias10, fieldAlias11, fieldAlias12, fieldAlias13, fieldAlias14, fieldAlias15, fieldAlias16, fieldAlias17, fieldAlias18, fieldAlias19, fieldAlias20, fieldAlias21, fieldAlias22);
    }

    public WithStep withRecursive(CommonTableExpression<?> ... tables) {
        return this.context.withRecursive(tables);
    }

    public WithStep withRecursive(Collection<? extends CommonTableExpression<?>> tables) {
        return this.context.withRecursive(tables);
    }

    public <R extends Record> SelectWhereStep<R> selectFrom(Table<R> table) {
        return this.time(this.context.selectFrom(table));
    }

    public SelectWhereStep<Record> selectFrom(Name table) {
        return this.time(this.context.selectFrom(table));
    }

    public SelectWhereStep<Record> selectFrom(SQL sql) {
        return this.time(this.context.selectFrom(sql));
    }

    public SelectWhereStep<Record> selectFrom(String sql) {
        return this.time(this.context.selectFrom(sql));
    }

    public SelectWhereStep<Record> selectFrom(String sql, Object ... bindings) {
        return this.time(this.context.selectFrom(sql, bindings));
    }

    public SelectWhereStep<Record> selectFrom(String sql, QueryPart ... parts) {
        return this.time(this.context.selectFrom(sql, parts));
    }

    public SelectSelectStep<Record> select(Collection<? extends SelectFieldOrAsterisk> fields) {
        return this.time(this.context.select(fields));
    }

    public SelectSelectStep<Record> select(SelectFieldOrAsterisk ... fields) {
        return this.time(this.context.select(fields));
    }

    public <T1> SelectSelectStep<Record1<T1>> select(SelectField<T1> field1) {
        return this.time(this.context.select(field1));
    }

    public <T1, T2> SelectSelectStep<Record2<T1, T2>> select(SelectField<T1> field1, SelectField<T2> field2) {
        return this.time(this.context.select(field1, field2));
    }

    public <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return this.time(this.context.select(field1, field2, field3));
    }

    public <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return this.time(this.context.select(field1, field2, field3, field4));
    }

    public <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return this.time(this.context.select(field1, field2, field3, field4, field5));
    }

    public <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6));
    }

    public <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return this.time(this.context.select(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    public SelectSelectStep<Record> selectDistinct(Collection<? extends SelectFieldOrAsterisk> fields) {
        return this.time(this.context.selectDistinct(fields));
    }

    public SelectSelectStep<Record> selectDistinct(SelectFieldOrAsterisk ... fields) {
        return this.time(this.context.selectDistinct(fields));
    }

    public <T1> SelectSelectStep<Record1<T1>> selectDistinct(SelectField<T1> field1) {
        return this.time(this.context.selectDistinct(field1));
    }

    public <T1, T2> SelectSelectStep<Record2<T1, T2>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2) {
        return this.time(this.context.selectDistinct(field1, field2));
    }

    public <T1, T2, T3> SelectSelectStep<Record3<T1, T2, T3>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) {
        return this.time(this.context.selectDistinct(field1, field2, field3));
    }

    public <T1, T2, T3, T4> SelectSelectStep<Record4<T1, T2, T3, T4>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4));
    }

    public <T1, T2, T3, T4, T5> SelectSelectStep<Record5<T1, T2, T3, T4, T5>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5));
    }

    public <T1, T2, T3, T4, T5, T6> SelectSelectStep<Record6<T1, T2, T3, T4, T5, T6>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6));
    }

    public <T1, T2, T3, T4, T5, T6, T7> SelectSelectStep<Record7<T1, T2, T3, T4, T5, T6, T7>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> SelectSelectStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> SelectSelectStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SelectSelectStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> SelectSelectStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> SelectSelectStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> SelectSelectStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> SelectSelectStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> SelectSelectStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> SelectSelectStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> SelectSelectStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> SelectSelectStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> SelectSelectStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> SelectSelectStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> SelectSelectStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> SelectSelectStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> selectDistinct(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) {
        return this.time(this.context.selectDistinct(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    public SelectSelectStep<Record1<Integer>> selectZero() {
        return this.time(this.context.selectZero());
    }

    public SelectSelectStep<Record1<Integer>> selectOne() {
        return this.time(this.context.selectOne());
    }

    public SelectSelectStep<Record1<Integer>> selectCount() {
        return this.time(this.context.selectCount());
    }

    public SelectQuery<Record> selectQuery() {
        return this.time(this.context.selectQuery());
    }

    public <R extends Record> SelectQuery<R> selectQuery(TableLike<R> table) {
        return this.time(this.context.selectQuery(table));
    }

    public <R extends Record> InsertQuery<R> insertQuery(Table<R> into) {
        return this.time(this.context.insertQuery(into));
    }

    public <R extends Record> InsertSetStep<R> insertInto(Table<R> into) {
        return (InsertSetStep)this.timeCoercable(this.context.insertInto(into));
    }

    public <R extends Record, T1> InsertValuesStep1<R, T1> insertInto(Table<R> into, Field<T1> field1) {
        return this.time(this.context.insertInto(into, field1));
    }

    public <R extends Record, T1, T2> InsertValuesStep2<R, T1, T2> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2) {
        return this.time(this.context.insertInto(into, field1, field2));
    }

    public <R extends Record, T1, T2, T3> InsertValuesStep3<R, T1, T2, T3> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return this.time(this.context.insertInto(into, field1, field2, field3));
    }

    public <R extends Record, T1, T2, T3, T4> InsertValuesStep4<R, T1, T2, T3, T4> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4));
    }

    public <R extends Record, T1, T2, T3, T4, T5> InsertValuesStep5<R, T1, T2, T3, T4, T5> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6> InsertValuesStep6<R, T1, T2, T3, T4, T5, T6> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7> InsertValuesStep7<R, T1, T2, T3, T4, T5, T6, T7> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> InsertValuesStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> InsertValuesStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> InsertValuesStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> InsertValuesStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> InsertValuesStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> InsertValuesStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> InsertValuesStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> InsertValuesStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> InsertValuesStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> InsertValuesStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> InsertValuesStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> InsertValuesStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> InsertValuesStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> InsertValuesStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21));
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> InsertValuesStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> insertInto(Table<R> into, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return this.time(this.context.insertInto(into, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22));
    }

    public <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Field<?> ... fields) {
        return this.time(this.context.insertInto(into, fields));
    }

    public <R extends Record> InsertValuesStepN<R> insertInto(Table<R> into, Collection<? extends Field<?>> fields) {
        return this.time(this.context.insertInto(into, fields));
    }

    public <R extends Record> UpdateQuery<R> updateQuery(Table<R> table) {
        return this.time(this.context.updateQuery(table));
    }

    public <R extends Record> UpdateSetFirstStep<R> update(Table<R> table) {
        return (UpdateSetFirstStep)this.timeCoercable(this.context.update(table));
    }

    public <R extends Record> MergeUsingStep<R> mergeInto(Table<R> table) {
        return this.context.mergeInto(table);
    }

    public <R extends Record, T1> MergeKeyStep1<R, T1> mergeInto(Table<R> table, Field<T1> field1) {
        return this.context.mergeInto(table, field1);
    }

    public <R extends Record, T1, T2> MergeKeyStep2<R, T1, T2> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2) {
        return this.context.mergeInto(table, field1, field2);
    }

    public <R extends Record, T1, T2, T3> MergeKeyStep3<R, T1, T2, T3> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return this.context.mergeInto(table, field1, field2, field3);
    }

    public <R extends Record, T1, T2, T3, T4> MergeKeyStep4<R, T1, T2, T3, T4> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return this.context.mergeInto(table, field1, field2, field3, field4);
    }

    public <R extends Record, T1, T2, T3, T4, T5> MergeKeyStep5<R, T1, T2, T3, T4, T5> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6> MergeKeyStep6<R, T1, T2, T3, T4, T5, T6> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7> MergeKeyStep7<R, T1, T2, T3, T4, T5, T6, T7> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8> MergeKeyStep8<R, T1, T2, T3, T4, T5, T6, T7, T8> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9> MergeKeyStep9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MergeKeyStep10<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> MergeKeyStep11<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> MergeKeyStep12<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> MergeKeyStep13<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> MergeKeyStep14<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> MergeKeyStep15<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> MergeKeyStep16<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> MergeKeyStep17<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> MergeKeyStep18<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> MergeKeyStep19<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> MergeKeyStep20<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> MergeKeyStep21<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public <R extends Record, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> MergeKeyStep22<R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> mergeInto(Table<R> table, Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return this.context.mergeInto(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Field<?> ... fields) {
        return this.context.mergeInto(table, fields);
    }

    public <R extends Record> MergeKeyStepN<R> mergeInto(Table<R> table, Collection<? extends Field<?>> fields) {
        return this.context.mergeInto(table, fields);
    }

    public <R extends Record> DeleteQuery<R> deleteQuery(Table<R> table) {
        return this.context.deleteQuery(table);
    }

    public <R extends Record> DeleteUsingStep<R> deleteFrom(Table<R> table) {
        return this.context.deleteFrom(table);
    }

    public <R extends Record> DeleteUsingStep<R> delete(Table<R> table) {
        return this.context.delete(table);
    }

    public void batched(BatchedRunnable runnable) {
        this.context.batched(runnable);
    }

    public <T> T batchedResult(BatchedCallable<T> callable) {
        return (T)this.context.batchedResult(callable);
    }

    public Batch batch(Query ... queries) {
        return this.context.batch(queries);
    }

    public Batch batch(Queries queries) {
        return this.context.batch(queries);
    }

    public Batch batch(String ... queries) {
        return this.context.batch(queries);
    }

    public Batch batch(Collection<? extends Query> queries) {
        return this.context.batch(queries);
    }

    public BatchBindStep batch(Query query) {
        return this.context.batch(query);
    }

    public BatchBindStep batch(String sql) {
        return this.context.batch(sql);
    }

    public Batch batch(Query query, Object[] ... bindings) {
        return this.context.batch(query, bindings);
    }

    public Batch batch(String sql, Object[] ... bindings) {
        return this.context.batch(sql, bindings);
    }

    public Batch batchStore(UpdatableRecord<?> ... records) {
        return this.context.batchStore(records);
    }

    public Batch batchStore(Collection<? extends UpdatableRecord<?>> records) {
        return this.context.batchStore(records);
    }

    public Batch batchInsert(TableRecord<?> ... records) {
        return this.context.batchInsert(records);
    }

    public Batch batchInsert(Collection<? extends TableRecord<?>> records) {
        return this.context.batchInsert(records);
    }

    public Batch batchUpdate(UpdatableRecord<?> ... records) {
        return this.context.batchUpdate(records);
    }

    public Batch batchUpdate(Collection<? extends UpdatableRecord<?>> records) {
        return this.context.batchUpdate(records);
    }

    public Batch batchMerge(UpdatableRecord<?> ... records) {
        return this.context.batchMerge(records);
    }

    public Batch batchMerge(Collection<? extends UpdatableRecord<?>> records) {
        return this.context.batchMerge(records);
    }

    public Batch batchDelete(UpdatableRecord<?> ... records) {
        return this.context.batchDelete(records);
    }

    public Batch batchDelete(Collection<? extends UpdatableRecord<?>> records) {
        return this.context.batchDelete(records);
    }

    public Queries ddl(Catalog catalog) {
        return this.context.ddl(catalog);
    }

    public Queries ddl(Catalog schema, DDLExportConfiguration configuration) {
        return this.context.ddl(schema, configuration);
    }

    public Queries ddl(Catalog schema, DDLFlag ... flags) {
        return this.context.ddl(schema, flags);
    }

    public Queries ddl(Schema schema) {
        return this.context.ddl(schema);
    }

    public Queries ddl(Schema schema, DDLExportConfiguration configuration) {
        return this.context.ddl(schema, configuration);
    }

    public Queries ddl(Schema schema, DDLFlag ... flags) {
        return this.context.ddl(schema, flags);
    }

    public Queries ddl(Table<?> table) {
        return this.context.ddl(table);
    }

    public Queries ddl(Table<?> table, DDLExportConfiguration configuration) {
        return this.context.ddl(table, configuration);
    }

    public Queries ddl(Table<?> table, DDLFlag ... flags) {
        return this.context.ddl(table, flags);
    }

    public Queries ddl(Table<?> ... tables) {
        return this.context.ddl(tables);
    }

    public Queries ddl(Table<?>[] tables, DDLExportConfiguration configuration) {
        return this.context.ddl(tables, configuration);
    }

    public Queries ddl(Table<?>[] tables, DDLFlag ... flags) {
        return this.context.ddl(tables, flags);
    }

    public Queries ddl(Collection<? extends Table<?>> tables) {
        return this.context.ddl(tables);
    }

    public Queries ddl(Collection<? extends Table<?>> tables, DDLFlag ... flags) {
        return this.context.ddl(tables, flags);
    }

    public Queries ddl(Collection<? extends Table<?>> tables, DDLExportConfiguration configuration) {
        return this.context.ddl(tables, configuration);
    }

    public RowCountQuery setCatalog(String catalog) {
        return this.context.setCatalog(catalog);
    }

    public RowCountQuery setCatalog(Name catalog) {
        return this.context.setCatalog(catalog);
    }

    public RowCountQuery setCatalog(Catalog catalog) {
        return this.context.setCatalog(catalog);
    }

    public RowCountQuery setSchema(String schema) {
        return this.context.setSchema(schema);
    }

    public RowCountQuery setSchema(Name schema) {
        return this.context.setSchema(schema);
    }

    public RowCountQuery setSchema(Schema schema) {
        return this.context.setSchema(schema);
    }

    public RowCountQuery set(Name name, Param<?> param) {
        return this.context.set(name, param);
    }

    public CreateDatabaseFinalStep createDatabase(String database) {
        return this.context.createDatabase(database);
    }

    public CreateDatabaseFinalStep createDatabase(Name database) {
        return this.context.createDatabase(database);
    }

    public CreateDatabaseFinalStep createDatabase(Catalog database) {
        return this.context.createDatabase(database);
    }

    public CreateDatabaseFinalStep createDatabaseIfNotExists(String database) {
        return this.context.createDatabaseIfNotExists(database);
    }

    public CreateDatabaseFinalStep createDatabaseIfNotExists(Name database) {
        return this.context.createDatabaseIfNotExists(database);
    }

    public CreateDatabaseFinalStep createDatabaseIfNotExists(Catalog database) {
        return this.context.createDatabaseIfNotExists(database);
    }

    public CreateDomainAsStep createDomain(String domain) {
        return this.context.createDomain(domain);
    }

    public CreateDomainAsStep createDomain(Name domain) {
        return this.context.createDomain(domain);
    }

    public CreateDomainAsStep createDomain(Domain<?> domain) {
        return this.context.createDomain(domain);
    }

    public CreateDomainAsStep createDomainIfNotExists(String domain) {
        return this.context.createDomainIfNotExists(domain);
    }

    public CreateDomainAsStep createDomainIfNotExists(Name domain) {
        return this.context.createDomainIfNotExists(domain);
    }

    public CreateDomainAsStep createDomainIfNotExists(Domain<?> domain) {
        return this.context.createDomainIfNotExists(domain);
    }

    public CommentOnIsStep commentOnTable(String tableName) {
        return this.context.commentOnTable(tableName);
    }

    public CommentOnIsStep commentOnTable(Name tableName) {
        return this.context.commentOnTable(tableName);
    }

    public CommentOnIsStep commentOnTable(Table<?> table) {
        return this.context.commentOnTable(table);
    }

    public CommentOnIsStep commentOnView(String viewName) {
        return this.context.commentOnView(viewName);
    }

    public CommentOnIsStep commentOnView(Name viewName) {
        return this.context.commentOnView(viewName);
    }

    public CommentOnIsStep commentOnView(Table<?> view) {
        return this.context.commentOnView(view);
    }

    public CommentOnIsStep commentOnColumn(Name columnName) {
        return this.context.commentOnColumn(columnName);
    }

    public CommentOnIsStep commentOnColumn(Field<?> field) {
        return this.context.commentOnColumn(field);
    }

    public CreateSchemaFinalStep createSchema(String schema) {
        return this.context.createSchema(schema);
    }

    public CreateSchemaFinalStep createSchema(Name schema) {
        return this.context.createSchema(schema);
    }

    public CreateSchemaFinalStep createSchema(Schema schema) {
        return this.context.createSchema(schema);
    }

    public CreateSchemaFinalStep createSchemaIfNotExists(String schema) {
        return this.context.createSchemaIfNotExists(schema);
    }

    public CreateSchemaFinalStep createSchemaIfNotExists(Name schema) {
        return this.context.createSchemaIfNotExists(schema);
    }

    public CreateSchemaFinalStep createSchemaIfNotExists(Schema schema) {
        return this.context.createSchemaIfNotExists(schema);
    }

    public CreateTableColumnStep createTable(String table) {
        return this.context.createTable(table);
    }

    public CreateTableColumnStep createTable(Name table) {
        return this.context.createTable(table);
    }

    public CreateTableColumnStep createTable(Table<?> table) {
        return this.context.createTable(table);
    }

    public CreateTableColumnStep createTableIfNotExists(String table) {
        return this.context.createTableIfNotExists(table);
    }

    public CreateTableColumnStep createTableIfNotExists(Name table) {
        return this.context.createTableIfNotExists(table);
    }

    public CreateTableColumnStep createTableIfNotExists(Table<?> table) {
        return this.context.createTableIfNotExists(table);
    }

    public CreateTableColumnStep createTemporaryTable(String table) {
        return this.context.createTemporaryTable(table);
    }

    public CreateTableColumnStep createTemporaryTable(Name table) {
        return this.context.createTemporaryTable(table);
    }

    public CreateTableColumnStep createTemporaryTable(Table<?> table) {
        return this.context.createTemporaryTable(table);
    }

    public CreateTableColumnStep createTemporaryTableIfNotExists(String table) {
        return this.context.createTemporaryTableIfNotExists(table);
    }

    public CreateTableColumnStep createTemporaryTableIfNotExists(Name table) {
        return this.context.createTemporaryTableIfNotExists(table);
    }

    public CreateTableColumnStep createTemporaryTableIfNotExists(Table<?> table) {
        return this.context.createTemporaryTableIfNotExists(table);
    }

    public CreateTableColumnStep createGlobalTemporaryTable(String table) {
        return this.context.createGlobalTemporaryTable(table);
    }

    public CreateTableColumnStep createGlobalTemporaryTable(Name table) {
        return this.context.createGlobalTemporaryTable(table);
    }

    public CreateTableColumnStep createGlobalTemporaryTable(Table<?> table) {
        return this.context.createGlobalTemporaryTable(table);
    }

    public CreateViewAsStep<Record> createView(String view, String ... fields) {
        return this.context.createView(view, fields);
    }

    public CreateViewAsStep<Record> createView(Name view, Name ... fields) {
        return this.context.createView(view, fields);
    }

    public CreateViewAsStep<Record> createView(Table<?> view, Field<?> ... fields) {
        return this.context.createView(view, fields);
    }

    public CreateViewAsStep<Record> createView(String view, Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createView(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createView(Name view, Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createView(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createView(Table<?> view, Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createView(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return this.context.createView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(String view, String ... fields) {
        return this.context.createOrReplaceView(view, fields);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Name view, Name ... fields) {
        return this.context.createOrReplaceView(view, fields);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Field<?> ... fields) {
        return this.context.createOrReplaceView(view, fields);
    }

    public CreateViewAsStep<Record> createOrReplaceView(String view, Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Name view, Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createOrReplaceView(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return this.context.createOrReplaceView(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(String view, String ... fields) {
        return this.context.createViewIfNotExists(view, fields);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Name view, Name ... fields) {
        return this.context.createViewIfNotExists(view, fields);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Field<?> ... fields) {
        return this.context.createViewIfNotExists(view, fields);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(String view, Function<? super Field<?>, ? extends String> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(String view, BiFunction<? super Field<?>, ? super Integer, ? extends String> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Name view, Function<? super Field<?>, ? extends Name> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Name view, BiFunction<? super Field<?>, ? super Integer, ? extends Name> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, Function<? super Field<?>, ? extends Field<?>> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateViewAsStep<Record> createViewIfNotExists(Table<?> view, BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> fieldNameFunction) {
        return this.context.createViewIfNotExists(view, fieldNameFunction);
    }

    public CreateTypeStep createType(String type) {
        return this.context.createType(type);
    }

    public CreateTypeStep createType(Name type) {
        return this.context.createType(type);
    }

    public AlterTypeStep alterType(String type) {
        return this.context.alterType(type);
    }

    public AlterTypeStep alterType(Name type) {
        return this.context.alterType(type);
    }

    public DropTypeStep dropType(String type) {
        return this.context.dropType(type);
    }

    public DropTypeStep dropType(Name type) {
        return this.context.dropType(type);
    }

    public DropTypeStep dropType(String ... type) {
        return this.context.dropType(type);
    }

    public DropTypeStep dropType(Name ... type) {
        return this.context.dropType(type);
    }

    public DropTypeStep dropType(Collection<?> type) {
        return this.context.dropType(type);
    }

    public DropTypeStep dropTypeIfExists(String type) {
        return this.context.dropTypeIfExists(type);
    }

    public DropTypeStep dropTypeIfExists(Name type) {
        return this.context.dropTypeIfExists(type);
    }

    public DropTypeStep dropTypeIfExists(String ... type) {
        return this.context.dropTypeIfExists(type);
    }

    public DropTypeStep dropTypeIfExists(Name ... type) {
        return this.context.dropTypeIfExists(type);
    }

    public DropTypeStep dropTypeIfExists(Collection<?> type) {
        return this.context.dropTypeIfExists(type);
    }

    public CreateIndexStep createIndex() {
        return this.context.createIndex();
    }

    public CreateIndexStep createIndex(String index) {
        return this.context.createIndex(index);
    }

    public CreateIndexStep createIndex(Name index) {
        return this.context.createIndex(index);
    }

    public CreateIndexStep createIndex(Index index) {
        return this.context.createIndex(index);
    }

    public CreateIndexStep createIndexIfNotExists(String index) {
        return this.context.createIndexIfNotExists(index);
    }

    public CreateIndexStep createIndexIfNotExists(Name index) {
        return this.context.createIndexIfNotExists(index);
    }

    public CreateIndexStep createIndexIfNotExists(Index index) {
        return this.context.createIndexIfNotExists(index);
    }

    public CreateIndexStep createUniqueIndex() {
        return this.context.createUniqueIndex();
    }

    public CreateIndexStep createUniqueIndex(String index) {
        return this.context.createUniqueIndex(index);
    }

    public CreateIndexStep createUniqueIndex(Name index) {
        return this.context.createUniqueIndex(index);
    }

    public CreateIndexStep createUniqueIndex(Index index) {
        return this.context.createUniqueIndex(index);
    }

    public CreateIndexStep createUniqueIndexIfNotExists(String index) {
        return this.context.createUniqueIndexIfNotExists(index);
    }

    public CreateIndexStep createUniqueIndexIfNotExists(Name index) {
        return this.context.createUniqueIndexIfNotExists(index);
    }

    public CreateIndexStep createUniqueIndexIfNotExists(Index index) {
        return this.context.createUniqueIndexIfNotExists(index);
    }

    public CreateSequenceFlagsStep createSequence(String sequence) {
        return this.context.createSequence(sequence);
    }

    public CreateSequenceFlagsStep createSequence(Name sequence) {
        return this.context.createSequence(sequence);
    }

    public CreateSequenceFlagsStep createSequence(Sequence<?> sequence) {
        return this.context.createSequence(sequence);
    }

    public CreateSequenceFlagsStep createSequenceIfNotExists(String sequence) {
        return this.context.createSequenceIfNotExists(sequence);
    }

    public CreateSequenceFlagsStep createSequenceIfNotExists(Name sequence) {
        return this.context.createSequenceIfNotExists(sequence);
    }

    public CreateSequenceFlagsStep createSequenceIfNotExists(Sequence<?> sequence) {
        return this.context.createSequenceIfNotExists(sequence);
    }

    public AlterDatabaseStep alterDatabase(String database) {
        return this.context.alterDatabase(database);
    }

    public AlterDatabaseStep alterDatabase(Name database) {
        return this.context.alterDatabase(database);
    }

    public AlterDatabaseStep alterDatabase(Catalog database) {
        return this.context.alterDatabase(database);
    }

    public AlterDatabaseStep alterDatabaseIfExists(String database) {
        return this.context.alterDatabaseIfExists(database);
    }

    public AlterDatabaseStep alterDatabaseIfExists(Name database) {
        return this.context.alterDatabaseIfExists(database);
    }

    public AlterDatabaseStep alterDatabaseIfExists(Catalog database) {
        return this.context.alterDatabaseIfExists(database);
    }

    public <T> AlterDomainStep<T> alterDomain(String domain) {
        return this.context.alterDomain(domain);
    }

    public <T> AlterDomainStep<T> alterDomain(Name domain) {
        return this.context.alterDomain(domain);
    }

    public <T> AlterDomainStep<T> alterDomain(Domain<T> domain) {
        return this.context.alterDomain(domain);
    }

    public <T> AlterDomainStep<T> alterDomainIfExists(String domain) {
        return this.context.alterDomainIfExists(domain);
    }

    public <T> AlterDomainStep<T> alterDomainIfExists(Name domain) {
        return this.context.alterDomainIfExists(domain);
    }

    public <T> AlterDomainStep<T> alterDomainIfExists(Domain<T> domain) {
        return this.context.alterDomainIfExists(domain);
    }

    public AlterSequenceStep<BigInteger> alterSequence(String sequence) {
        return this.context.alterSequence(sequence);
    }

    public AlterSequenceStep<BigInteger> alterSequence(Name sequence) {
        return this.context.alterSequence(sequence);
    }

    public <T extends Number> AlterSequenceStep<T> alterSequence(Sequence<T> sequence) {
        return this.context.alterSequence(sequence);
    }

    public AlterSequenceStep<BigInteger> alterSequenceIfExists(String sequence) {
        return this.context.alterSequenceIfExists(sequence);
    }

    public AlterSequenceStep<BigInteger> alterSequenceIfExists(Name sequence) {
        return this.context.alterSequenceIfExists(sequence);
    }

    public <T extends Number> AlterSequenceStep<T> alterSequenceIfExists(Sequence<T> sequence) {
        return this.context.alterSequenceIfExists(sequence);
    }

    public AlterTableStep alterTable(String table) {
        return this.context.alterTable(table);
    }

    public AlterTableStep alterTable(Name table) {
        return this.context.alterTable(table);
    }

    public AlterTableStep alterTable(Table<?> table) {
        return this.context.alterTable(table);
    }

    public AlterTableStep alterTableIfExists(String table) {
        return this.context.alterTableIfExists(table);
    }

    public AlterTableStep alterTableIfExists(Name table) {
        return this.context.alterTableIfExists(table);
    }

    public AlterTableStep alterTableIfExists(Table<?> table) {
        return this.context.alterTableIfExists(table);
    }

    public AlterSchemaStep alterSchema(String schema) {
        return this.context.alterSchema(schema);
    }

    public AlterSchemaStep alterSchema(Name schema) {
        return this.context.alterSchema(schema);
    }

    public AlterSchemaStep alterSchema(Schema schema) {
        return this.context.alterSchema(schema);
    }

    public AlterSchemaStep alterSchemaIfExists(String schema) {
        return this.context.alterSchemaIfExists(schema);
    }

    public AlterSchemaStep alterSchemaIfExists(Name schema) {
        return this.context.alterSchemaIfExists(schema);
    }

    public AlterSchemaStep alterSchemaIfExists(Schema schema) {
        return this.context.alterSchemaIfExists(schema);
    }

    public DropDatabaseFinalStep dropDatabase(String database) {
        return this.context.dropDatabase(database);
    }

    public DropDatabaseFinalStep dropDatabase(Name database) {
        return this.context.dropDatabase(database);
    }

    public DropDatabaseFinalStep dropDatabase(Catalog database) {
        return this.context.dropDatabase(database);
    }

    public DropDatabaseFinalStep dropDatabaseIfExists(String database) {
        return this.context.dropDatabaseIfExists(database);
    }

    public DropDatabaseFinalStep dropDatabaseIfExists(Name database) {
        return this.context.dropDatabaseIfExists(database);
    }

    public DropDatabaseFinalStep dropDatabaseIfExists(Catalog database) {
        return this.context.dropDatabaseIfExists(database);
    }

    public DropDomainCascadeStep dropDomain(String domain) {
        return this.context.dropDomain(domain);
    }

    public DropDomainCascadeStep dropDomain(Name domain) {
        return this.context.dropDomain(domain);
    }

    public DropDomainCascadeStep dropDomain(Domain<?> domain) {
        return this.context.dropDomain(domain);
    }

    public DropDomainCascadeStep dropDomainIfExists(String domain) {
        return this.context.dropDomainIfExists(domain);
    }

    public DropDomainCascadeStep dropDomainIfExists(Name domain) {
        return this.context.dropDomainIfExists(domain);
    }

    public DropDomainCascadeStep dropDomainIfExists(Domain<?> domain) {
        return this.context.dropDomainIfExists(domain);
    }

    public AlterViewStep alterView(String view) {
        return this.context.alterView(view);
    }

    public AlterViewStep alterView(Name view) {
        return this.context.alterView(view);
    }

    public AlterViewStep alterView(Table<?> view) {
        return this.context.alterView(view);
    }

    public AlterViewStep alterViewIfExists(String view) {
        return this.context.alterViewIfExists(view);
    }

    public AlterViewStep alterViewIfExists(Name view) {
        return this.context.alterViewIfExists(view);
    }

    public AlterViewStep alterViewIfExists(Table<?> view) {
        return this.context.alterViewIfExists(view);
    }

    public AlterIndexOnStep alterIndex(String index) {
        return this.context.alterIndex(index);
    }

    public AlterIndexOnStep alterIndex(Name index) {
        return this.context.alterIndex(index);
    }

    public AlterIndexOnStep alterIndex(Index index) {
        return this.context.alterIndex(index);
    }

    public AlterIndexStep alterIndexIfExists(String index) {
        return this.context.alterIndexIfExists(index);
    }

    public AlterIndexStep alterIndexIfExists(Name index) {
        return this.context.alterIndexIfExists(index);
    }

    public AlterIndexStep alterIndexIfExists(Index index) {
        return this.context.alterIndexIfExists(index);
    }

    public DropSchemaStep dropSchema(String schema) {
        return this.context.dropSchema(schema);
    }

    public DropSchemaStep dropSchema(Name schema) {
        return this.context.dropSchema(schema);
    }

    public DropSchemaStep dropSchema(Schema schema) {
        return this.context.dropSchema(schema);
    }

    public DropSchemaStep dropSchemaIfExists(String schema) {
        return this.context.dropSchemaIfExists(schema);
    }

    public DropSchemaStep dropSchemaIfExists(Name schema) {
        return this.context.dropSchemaIfExists(schema);
    }

    public DropSchemaStep dropSchemaIfExists(Schema schema) {
        return this.context.dropSchemaIfExists(schema);
    }

    public DropViewFinalStep dropView(String view) {
        return this.context.dropView(view);
    }

    public DropViewFinalStep dropView(Name view) {
        return this.context.dropView(view);
    }

    public DropViewFinalStep dropView(Table<?> view) {
        return this.context.dropView(view);
    }

    public DropViewFinalStep dropViewIfExists(String view) {
        return this.context.dropViewIfExists(view);
    }

    public DropViewFinalStep dropViewIfExists(Name view) {
        return this.context.dropViewIfExists(view);
    }

    public DropViewFinalStep dropViewIfExists(Table<?> view) {
        return this.context.dropViewIfExists(view);
    }

    public DropTableStep dropTable(String table) {
        return this.context.dropTable(table);
    }

    public DropTableStep dropTable(Name table) {
        return this.context.dropTable(table);
    }

    public DropTableStep dropTable(Table<?> table) {
        return this.context.dropTable(table);
    }

    public DropTableStep dropTableIfExists(String table) {
        return this.context.dropTableIfExists(table);
    }

    public DropTableStep dropTableIfExists(Name table) {
        return this.context.dropTableIfExists(table);
    }

    public DropTableStep dropTableIfExists(Table<?> table) {
        return this.context.dropTableIfExists(table);
    }

    public DropTableStep dropTemporaryTable(String table) {
        return this.context.dropTemporaryTable(table);
    }

    public DropTableStep dropTemporaryTable(Name table) {
        return this.context.dropTemporaryTable(table);
    }

    public DropTableStep dropTemporaryTable(Table<?> table) {
        return this.context.dropTemporaryTable(table);
    }

    public DropTableStep dropTemporaryTableIfExists(String table) {
        return this.context.dropTemporaryTableIfExists(table);
    }

    public DropTableStep dropTemporaryTableIfExists(Name table) {
        return this.context.dropTemporaryTableIfExists(table);
    }

    public DropTableStep dropTemporaryTableIfExists(Table<?> table) {
        return this.context.dropTemporaryTableIfExists(table);
    }

    public DropIndexOnStep dropIndex(String index) {
        return this.context.dropIndex(index);
    }

    public DropIndexOnStep dropIndex(Name index) {
        return this.context.dropIndex(index);
    }

    public DropIndexOnStep dropIndex(Index index) {
        return this.context.dropIndex(index);
    }

    public DropIndexOnStep dropIndexIfExists(String index) {
        return this.context.dropIndexIfExists(index);
    }

    public DropIndexOnStep dropIndexIfExists(Name index) {
        return this.context.dropIndexIfExists(index);
    }

    public DropIndexOnStep dropIndexIfExists(Index index) {
        return this.context.dropIndexIfExists(index);
    }

    public DropSequenceFinalStep dropSequence(String sequence) {
        return this.context.dropSequence(sequence);
    }

    public DropSequenceFinalStep dropSequence(Name sequence) {
        return this.context.dropSequence(sequence);
    }

    public DropSequenceFinalStep dropSequence(Sequence<?> sequence) {
        return this.context.dropSequence(sequence);
    }

    public DropSequenceFinalStep dropSequenceIfExists(String sequence) {
        return this.context.dropSequenceIfExists(sequence);
    }

    public DropSequenceFinalStep dropSequenceIfExists(Name sequence) {
        return this.context.dropSequenceIfExists(sequence);
    }

    public DropSequenceFinalStep dropSequenceIfExists(Sequence<?> sequence) {
        return this.context.dropSequenceIfExists(sequence);
    }

    public TruncateIdentityStep<Record> truncate(String table) {
        return this.context.truncate(table);
    }

    public TruncateIdentityStep<Record> truncate(Name table) {
        return this.context.truncate(table);
    }

    public <R extends Record> TruncateIdentityStep<R> truncate(Table<R> table) {
        return this.context.truncate(table);
    }

    public TruncateIdentityStep<Record> truncateTable(String table) {
        return this.context.truncateTable(table);
    }

    public TruncateIdentityStep<Record> truncateTable(Name table) {
        return this.context.truncateTable(table);
    }

    public <R extends Record> TruncateIdentityStep<R> truncateTable(Table<R> table) {
        return this.context.truncateTable(table);
    }

    public GrantOnStep grant(Privilege privilege) {
        return this.context.grant(privilege);
    }

    public GrantOnStep grant(Privilege ... privileges) {
        return this.context.grant(privileges);
    }

    public GrantOnStep grant(Collection<? extends Privilege> privileges) {
        return this.context.grant(privileges);
    }

    public RevokeOnStep revoke(Privilege privilege) {
        return this.context.revoke(privilege);
    }

    public RevokeOnStep revoke(Privilege ... privileges) {
        return this.context.revoke(privileges);
    }

    public RevokeOnStep revoke(Collection<? extends Privilege> privileges) {
        return this.context.revoke(privileges);
    }

    public RevokeOnStep revokeGrantOptionFor(Privilege privilege) {
        return this.context.revokeGrantOptionFor(privilege);
    }

    public RevokeOnStep revokeGrantOptionFor(Privilege ... privileges) {
        return this.context.revokeGrantOptionFor(privileges);
    }

    public RevokeOnStep revokeGrantOptionFor(Collection<? extends Privilege> privileges) {
        return this.context.revokeGrantOptionFor(privileges);
    }

    public BigInteger lastID() throws DataAccessException {
        return this.context.lastID();
    }

    public BigInteger nextval(String sequence) throws DataAccessException {
        return this.context.nextval(sequence);
    }

    public BigInteger nextval(Name sequence) throws DataAccessException {
        return this.context.nextval(sequence);
    }

    public <T extends Number> T nextval(Sequence<T> sequence) throws DataAccessException {
        return (T)this.context.nextval(sequence);
    }

    public <T extends Number> List<T> nextvals(Sequence<T> sequence, int size) throws DataAccessException {
        return this.context.nextvals(sequence, size);
    }

    public BigInteger currval(String sequence) throws DataAccessException {
        return this.context.currval(sequence);
    }

    public BigInteger currval(Name sequence) throws DataAccessException {
        return this.context.currval(sequence);
    }

    public <T extends Number> T currval(Sequence<T> sequence) throws DataAccessException {
        return (T)this.context.currval(sequence);
    }

    public <R extends UDTRecord<R>> R newRecord(UDT<R> type) {
        return (R)this.context.newRecord(type);
    }

    public <R extends Record> R newRecord(Table<R> table) {
        return (R)this.context.newRecord(table);
    }

    public <R extends Record> R newRecord(Table<R> table, Object source) {
        return (R)this.context.newRecord(table, source);
    }

    public Record newRecord(Field<?> ... fields) {
        return this.context.newRecord(fields);
    }

    public Record newRecord(Collection<? extends Field<?>> fields) {
        return this.context.newRecord(fields);
    }

    public <T1> Record1<T1> newRecord(Field<T1> field1) {
        return this.context.newRecord(field1);
    }

    public <T1, T2> Record2<T1, T2> newRecord(Field<T1> field1, Field<T2> field2) {
        return this.context.newRecord(field1, field2);
    }

    public <T1, T2, T3> Record3<T1, T2, T3> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return this.context.newRecord(field1, field2, field3);
    }

    public <T1, T2, T3, T4> Record4<T1, T2, T3, T4> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return this.context.newRecord(field1, field2, field3, field4);
    }

    public <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return this.context.newRecord(field1, field2, field3, field4, field5);
    }

    public <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> newRecord(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return this.context.newRecord(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public <R extends Record> Result<R> newResult(Table<R> table) {
        return this.context.newResult(table);
    }

    public Result<Record> newResult(Field<?> ... fields) {
        return this.context.newResult(fields);
    }

    public Result<Record> newResult(Collection<? extends Field<?>> fields) {
        return this.context.newResult(fields);
    }

    public <T1> Result<Record1<T1>> newResult(Field<T1> field1) {
        return this.context.newResult(field1);
    }

    public <T1, T2> Result<Record2<T1, T2>> newResult(Field<T1> field1, Field<T2> field2) {
        return this.context.newResult(field1, field2);
    }

    public <T1, T2, T3> Result<Record3<T1, T2, T3>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return this.context.newResult(field1, field2, field3);
    }

    public <T1, T2, T3, T4> Result<Record4<T1, T2, T3, T4>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return this.context.newResult(field1, field2, field3, field4);
    }

    public <T1, T2, T3, T4, T5> Result<Record5<T1, T2, T3, T4, T5>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return this.context.newResult(field1, field2, field3, field4, field5);
    }

    public <T1, T2, T3, T4, T5, T6> Result<Record6<T1, T2, T3, T4, T5, T6>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> Result<Record7<T1, T2, T3, T4, T5, T6, T7>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> Result<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Result<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Result<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Result<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Result<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Result<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Result<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Result<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Result<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Result<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Result<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Result<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Result<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Result<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Result<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> newResult(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return this.context.newResult(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public <R extends Record> Result<R> fetch(ResultQuery<R> query) throws DataAccessException {
        return this.context.fetch(query);
    }

    public <R extends Record> Cursor<R> fetchLazy(ResultQuery<R> query) throws DataAccessException {
        return this.context.fetchLazy(query);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(ResultQuery<R> query) {
        return this.context.fetchAsync(query);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, ResultQuery<R> query) {
        return this.context.fetchAsync(executor, query);
    }

    public <R extends Record> Stream<R> fetchStream(ResultQuery<R> query) throws DataAccessException {
        return this.context.fetchStream(query);
    }

    public <R extends Record> Results fetchMany(ResultQuery<R> query) throws DataAccessException {
        return this.context.fetchMany(query);
    }

    public <R extends Record> R fetchOne(ResultQuery<R> query) throws DataAccessException, TooManyRowsException {
        return (R)this.context.fetchOne(query);
    }

    public <R extends Record> R fetchSingle(ResultQuery<R> query) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return (R)this.context.fetchSingle(query);
    }

    public <R extends Record> Optional<R> fetchOptional(ResultQuery<R> query) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(query);
    }

    public <T> T fetchValue(Table<? extends Record1<T>> table) throws DataAccessException, TooManyRowsException {
        return (T)this.context.fetchValue(table);
    }

    public <T, R extends Record1<T>> T fetchValue(ResultQuery<R> query) throws DataAccessException, TooManyRowsException {
        return (T)this.context.fetchValue(query);
    }

    public <T> T fetchValue(TableField<?, T> field) throws DataAccessException, TooManyRowsException {
        return (T)this.context.fetchValue(field);
    }

    public <T> T fetchValue(Field<T> field) throws DataAccessException {
        return (T)this.context.fetchValue(field);
    }

    public <T, R extends Record1<T>> Optional<T> fetchOptionalValue(ResultQuery<R> query) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(query);
    }

    public <T> Optional<T> fetchOptionalValue(TableField<?, T> field) throws DataAccessException, TooManyRowsException, InvalidResultException {
        return this.context.fetchOptionalValue(field);
    }

    public <T> List<T> fetchValues(Table<? extends Record1<T>> table) throws DataAccessException {
        return this.context.fetchValues(table);
    }

    public <T, R extends Record1<T>> List<T> fetchValues(ResultQuery<R> query) throws DataAccessException {
        return this.context.fetchValues(query);
    }

    public <T> List<T> fetchValues(TableField<?, T> field) throws DataAccessException {
        return this.context.fetchValues(field);
    }

    public <R extends TableRecord<R>> Result<R> fetchByExample(R example) throws DataAccessException {
        return this.context.fetchByExample(example);
    }

    public int fetchCount(Select<?> query) throws DataAccessException {
        return this.context.fetchCount(query);
    }

    public int fetchCount(Table<?> table) throws DataAccessException {
        return this.context.fetchCount(table);
    }

    public int fetchCount(Table<?> table, Condition condition) throws DataAccessException {
        return this.context.fetchCount(table, condition);
    }

    public int fetchCount(Table<?> table, Condition ... conditions) throws DataAccessException {
        return this.context.fetchCount(table, conditions);
    }

    public int fetchCount(Table<?> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return this.context.fetchCount(table, conditions);
    }

    public boolean fetchExists(Select<?> query) throws DataAccessException {
        return this.context.fetchExists(query);
    }

    public boolean fetchExists(Table<?> table) throws DataAccessException {
        return this.context.fetchExists(table);
    }

    public boolean fetchExists(Table<?> table, Condition condition) throws DataAccessException {
        return this.context.fetchExists(table, condition);
    }

    public boolean fetchExists(Table<?> table, Condition ... conditions) throws DataAccessException {
        return this.context.fetchExists(table, conditions);
    }

    public boolean fetchExists(Table<?> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return this.context.fetchExists(table, conditions);
    }

    public int execute(Query query) throws DataAccessException {
        return this.context.execute(query);
    }

    public <R extends Record> Result<R> fetch(Table<R> table) throws DataAccessException {
        return this.context.fetch(table);
    }

    public <R extends Record> Result<R> fetch(Table<R> table, Condition condition) throws DataAccessException {
        return this.context.fetch(table, condition);
    }

    public <R extends Record> Result<R> fetch(Table<R> table, Condition ... conditions) throws DataAccessException {
        return this.context.fetch(table, conditions);
    }

    public <R extends Record> Result<R> fetch(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return this.context.fetch(table, conditions);
    }

    public <R extends Record> R fetchOne(Table<R> table) throws DataAccessException, TooManyRowsException {
        return (R)this.context.fetchOne(table);
    }

    public <R extends Record> R fetchOne(Table<R> table, Condition condition) throws DataAccessException, TooManyRowsException {
        return (R)this.context.fetchOne(table, condition);
    }

    public <R extends Record> R fetchOne(Table<R> table, Condition ... conditions) throws DataAccessException, TooManyRowsException {
        return (R)this.context.fetchOne(table, conditions);
    }

    public <R extends Record> R fetchOne(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException, TooManyRowsException {
        return (R)this.context.fetchOne(table, conditions);
    }

    public <R extends Record> R fetchSingle(Table<R> table) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return (R)this.context.fetchSingle(table);
    }

    public <R extends Record> R fetchSingle(Table<R> table, Condition condition) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return (R)this.context.fetchSingle(table, condition);
    }

    public <R extends Record> R fetchSingle(Table<R> table, Condition ... conditions) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return (R)this.context.fetchSingle(table, conditions);
    }

    public <R extends Record> R fetchSingle(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException, NoDataFoundException, TooManyRowsException {
        return (R)this.context.fetchSingle(table, conditions);
    }

    public Record fetchSingle(SelectField<?> ... fields) throws DataAccessException {
        return this.context.fetchSingle(fields);
    }

    public Record fetchSingle(Collection<? extends SelectField<?>> fields) throws DataAccessException {
        return this.context.fetchSingle(fields);
    }

    public <T1> Record1<T1> fetchSingle(SelectField<T1> field1) throws DataAccessException {
        return this.context.fetchSingle(field1);
    }

    public <T1, T2> Record2<T1, T2> fetchSingle(SelectField<T1> field1, SelectField<T2> field2) throws DataAccessException {
        return this.context.fetchSingle(field1, field2);
    }

    public <T1, T2, T3> Record3<T1, T2, T3> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3);
    }

    public <T1, T2, T3, T4> Record4<T1, T2, T3, T4> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4);
    }

    public <T1, T2, T3, T4, T5> Record5<T1, T2, T3, T4, T5> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5);
    }

    public <T1, T2, T3, T4, T5, T6> Record6<T1, T2, T3, T4, T5, T6> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6);
    }

    public <T1, T2, T3, T4, T5, T6, T7> Record7<T1, T2, T3, T4, T5, T6, T7> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8> Record8<T1, T2, T3, T4, T5, T6, T7, T8> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9> Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> fetchSingle(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11, SelectField<T12> field12, SelectField<T13> field13, SelectField<T14> field14, SelectField<T15> field15, SelectField<T16> field16, SelectField<T17> field17, SelectField<T18> field18, SelectField<T19> field19, SelectField<T20> field20, SelectField<T21> field21, SelectField<T22> field22) throws DataAccessException {
        return this.context.fetchSingle(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    public <R extends Record> Optional<R> fetchOptional(Table<R> table) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(table);
    }

    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition condition) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(table, condition);
    }

    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Condition ... conditions) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(table, conditions);
    }

    public <R extends Record> Optional<R> fetchOptional(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException, TooManyRowsException {
        return this.context.fetchOptional(table, conditions);
    }

    public <R extends Record> R fetchAny(Table<R> table) throws DataAccessException {
        return (R)this.context.fetchAny(table);
    }

    public <R extends Record> R fetchAny(Table<R> table, Condition condition) throws DataAccessException {
        return (R)this.context.fetchAny(table, condition);
    }

    public <R extends Record> R fetchAny(Table<R> table, Condition ... conditions) throws DataAccessException {
        return (R)this.context.fetchAny(table, conditions);
    }

    public <R extends Record> R fetchAny(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return (R)this.context.fetchAny(table, conditions);
    }

    public <R extends Record> Cursor<R> fetchLazy(Table<R> table) throws DataAccessException {
        return this.context.fetchLazy(table);
    }

    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition condition) throws DataAccessException {
        return this.context.fetchLazy(table, condition);
    }

    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Condition ... conditions) throws DataAccessException {
        return this.context.fetchLazy(table, conditions);
    }

    public <R extends Record> Cursor<R> fetchLazy(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return this.context.fetchLazy(table, conditions);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table) {
        return this.context.fetchAsync(table);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition condition) {
        return this.context.fetchAsync(table, condition);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Condition ... condition) {
        return this.context.fetchAsync(table, condition);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Table<R> table, Collection<? extends Condition> condition) {
        return this.context.fetchAsync(table, condition);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table) {
        return this.context.fetchAsync(executor, table);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition condition) {
        return this.context.fetchAsync(executor, table, condition);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Condition ... conditions) {
        return this.context.fetchAsync(executor, table, conditions);
    }

    public <R extends Record> CompletionStage<Result<R>> fetchAsync(Executor executor, Table<R> table, Collection<? extends Condition> conditions) {
        return this.context.fetchAsync(executor, table, conditions);
    }

    public <R extends Record> Stream<R> fetchStream(Table<R> table) throws DataAccessException {
        return this.context.fetchStream(table);
    }

    public <R extends Record> Stream<R> fetchStream(Table<R> table, Condition condition) throws DataAccessException {
        return this.context.fetchStream(table, condition);
    }

    public <R extends Record> Stream<R> fetchStream(Table<R> table, Condition ... conditions) throws DataAccessException {
        return this.context.fetchStream(table, conditions);
    }

    public <R extends Record> Stream<R> fetchStream(Table<R> table, Collection<? extends Condition> conditions) throws DataAccessException {
        return this.context.fetchStream(table, conditions);
    }

    public int executeInsert(TableRecord<?> record) throws DataAccessException {
        return this.context.executeInsert(record);
    }

    public int executeUpdate(UpdatableRecord<?> record) throws DataAccessException {
        return this.context.executeUpdate(record);
    }

    public int executeUpdate(TableRecord<?> record, Condition condition) throws DataAccessException {
        return this.context.executeUpdate(record, condition);
    }

    public int executeDelete(UpdatableRecord<?> record) throws DataAccessException {
        return this.context.executeDelete(record);
    }

    public int executeDelete(TableRecord<?> record, Condition condition) throws DataAccessException {
        return this.context.executeDelete(record, condition);
    }

    public Configuration configuration() {
        return this.context.configuration();
    }

    public DSLContext dsl() {
        return this.context.dsl();
    }

    public Settings settings() {
        return this.context.settings();
    }

    public SQLDialect dialect() {
        return this.context.dialect();
    }

    public SQLDialect family() {
        return this.context.family();
    }

    public Map<Object, Object> data() {
        return this.context.data();
    }

    public Object data(Object key) {
        return this.context.data(key);
    }

    public Object data(Object key, Object value) {
        return this.context.data(key, value);
    }
}

