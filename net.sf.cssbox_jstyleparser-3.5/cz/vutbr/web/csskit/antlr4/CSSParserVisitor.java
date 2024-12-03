/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeVisitor
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSParser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface CSSParserVisitor<T>
extends ParseTreeVisitor<T> {
    public T visitInlinestyle(CSSParser.InlinestyleContext var1);

    public T visitStylesheet(CSSParser.StylesheetContext var1);

    public T visitStatement(CSSParser.StatementContext var1);

    public T visitAtstatement(CSSParser.AtstatementContext var1);

    public T visitImport_uri(CSSParser.Import_uriContext var1);

    public T visitPage(CSSParser.PageContext var1);

    public T visitMargin_rule(CSSParser.Margin_ruleContext var1);

    public T visitInlineset(CSSParser.InlinesetContext var1);

    public T visitMedia(CSSParser.MediaContext var1);

    public T visitMedia_query(CSSParser.Media_queryContext var1);

    public T visitMedia_term(CSSParser.Media_termContext var1);

    public T visitMedia_expression(CSSParser.Media_expressionContext var1);

    public T visitMedia_rule(CSSParser.Media_ruleContext var1);

    public T visitKeyframes_name(CSSParser.Keyframes_nameContext var1);

    public T visitKeyframe_block(CSSParser.Keyframe_blockContext var1);

    public T visitKeyframe_selector(CSSParser.Keyframe_selectorContext var1);

    public T visitUnknown_atrule(CSSParser.Unknown_atruleContext var1);

    public T visitUnknown_atrule_body(CSSParser.Unknown_atrule_bodyContext var1);

    public T visitRuleset(CSSParser.RulesetContext var1);

    public T visitDeclarations(CSSParser.DeclarationsContext var1);

    public T visitDeclaration(CSSParser.DeclarationContext var1);

    public T visitImportant(CSSParser.ImportantContext var1);

    public T visitProperty(CSSParser.PropertyContext var1);

    public T visitTerms(CSSParser.TermsContext var1);

    public T visitTermValuePart(CSSParser.TermValuePartContext var1);

    public T visitTermInvalid(CSSParser.TermInvalidContext var1);

    public T visitFunct(CSSParser.FunctContext var1);

    public T visitValuepart(CSSParser.ValuepartContext var1);

    public T visitFunct_args(CSSParser.Funct_argsContext var1);

    public T visitFunct_argument(CSSParser.Funct_argumentContext var1);

    public T visitCombined_selector(CSSParser.Combined_selectorContext var1);

    public T visitCombinator(CSSParser.CombinatorContext var1);

    public T visitSelector(CSSParser.SelectorContext var1);

    public T visitSelpart(CSSParser.SelpartContext var1);

    public T visitAttribute(CSSParser.AttributeContext var1);

    public T visitPseudo(CSSParser.PseudoContext var1);

    public T visitString(CSSParser.StringContext var1);

    public T visitBracketed_idents(CSSParser.Bracketed_identsContext var1);

    public T visitIdent_list_item(CSSParser.Ident_list_itemContext var1);

    public T visitAny(CSSParser.AnyContext var1);

    public T visitNostatement(CSSParser.NostatementContext var1);

    public T visitNoprop(CSSParser.NopropContext var1);

    public T visitNorule(CSSParser.NoruleContext var1);

    public T visitNomediaquery(CSSParser.NomediaqueryContext var1);
}

