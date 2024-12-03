/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.tree.ParseTreeListener
 */
package cz.vutbr.web.csskit.antlr4;

import cz.vutbr.web.csskit.antlr4.CSSParser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface CSSParserListener
extends ParseTreeListener {
    public void enterInlinestyle(CSSParser.InlinestyleContext var1);

    public void exitInlinestyle(CSSParser.InlinestyleContext var1);

    public void enterStylesheet(CSSParser.StylesheetContext var1);

    public void exitStylesheet(CSSParser.StylesheetContext var1);

    public void enterStatement(CSSParser.StatementContext var1);

    public void exitStatement(CSSParser.StatementContext var1);

    public void enterAtstatement(CSSParser.AtstatementContext var1);

    public void exitAtstatement(CSSParser.AtstatementContext var1);

    public void enterImport_uri(CSSParser.Import_uriContext var1);

    public void exitImport_uri(CSSParser.Import_uriContext var1);

    public void enterPage(CSSParser.PageContext var1);

    public void exitPage(CSSParser.PageContext var1);

    public void enterMargin_rule(CSSParser.Margin_ruleContext var1);

    public void exitMargin_rule(CSSParser.Margin_ruleContext var1);

    public void enterInlineset(CSSParser.InlinesetContext var1);

    public void exitInlineset(CSSParser.InlinesetContext var1);

    public void enterMedia(CSSParser.MediaContext var1);

    public void exitMedia(CSSParser.MediaContext var1);

    public void enterMedia_query(CSSParser.Media_queryContext var1);

    public void exitMedia_query(CSSParser.Media_queryContext var1);

    public void enterMedia_term(CSSParser.Media_termContext var1);

    public void exitMedia_term(CSSParser.Media_termContext var1);

    public void enterMedia_expression(CSSParser.Media_expressionContext var1);

    public void exitMedia_expression(CSSParser.Media_expressionContext var1);

    public void enterMedia_rule(CSSParser.Media_ruleContext var1);

    public void exitMedia_rule(CSSParser.Media_ruleContext var1);

    public void enterKeyframes_name(CSSParser.Keyframes_nameContext var1);

    public void exitKeyframes_name(CSSParser.Keyframes_nameContext var1);

    public void enterKeyframe_block(CSSParser.Keyframe_blockContext var1);

    public void exitKeyframe_block(CSSParser.Keyframe_blockContext var1);

    public void enterKeyframe_selector(CSSParser.Keyframe_selectorContext var1);

    public void exitKeyframe_selector(CSSParser.Keyframe_selectorContext var1);

    public void enterUnknown_atrule(CSSParser.Unknown_atruleContext var1);

    public void exitUnknown_atrule(CSSParser.Unknown_atruleContext var1);

    public void enterUnknown_atrule_body(CSSParser.Unknown_atrule_bodyContext var1);

    public void exitUnknown_atrule_body(CSSParser.Unknown_atrule_bodyContext var1);

    public void enterRuleset(CSSParser.RulesetContext var1);

    public void exitRuleset(CSSParser.RulesetContext var1);

    public void enterDeclarations(CSSParser.DeclarationsContext var1);

    public void exitDeclarations(CSSParser.DeclarationsContext var1);

    public void enterDeclaration(CSSParser.DeclarationContext var1);

    public void exitDeclaration(CSSParser.DeclarationContext var1);

    public void enterImportant(CSSParser.ImportantContext var1);

    public void exitImportant(CSSParser.ImportantContext var1);

    public void enterProperty(CSSParser.PropertyContext var1);

    public void exitProperty(CSSParser.PropertyContext var1);

    public void enterTerms(CSSParser.TermsContext var1);

    public void exitTerms(CSSParser.TermsContext var1);

    public void enterTermValuePart(CSSParser.TermValuePartContext var1);

    public void exitTermValuePart(CSSParser.TermValuePartContext var1);

    public void enterTermInvalid(CSSParser.TermInvalidContext var1);

    public void exitTermInvalid(CSSParser.TermInvalidContext var1);

    public void enterFunct(CSSParser.FunctContext var1);

    public void exitFunct(CSSParser.FunctContext var1);

    public void enterValuepart(CSSParser.ValuepartContext var1);

    public void exitValuepart(CSSParser.ValuepartContext var1);

    public void enterFunct_args(CSSParser.Funct_argsContext var1);

    public void exitFunct_args(CSSParser.Funct_argsContext var1);

    public void enterFunct_argument(CSSParser.Funct_argumentContext var1);

    public void exitFunct_argument(CSSParser.Funct_argumentContext var1);

    public void enterCombined_selector(CSSParser.Combined_selectorContext var1);

    public void exitCombined_selector(CSSParser.Combined_selectorContext var1);

    public void enterCombinator(CSSParser.CombinatorContext var1);

    public void exitCombinator(CSSParser.CombinatorContext var1);

    public void enterSelector(CSSParser.SelectorContext var1);

    public void exitSelector(CSSParser.SelectorContext var1);

    public void enterSelpart(CSSParser.SelpartContext var1);

    public void exitSelpart(CSSParser.SelpartContext var1);

    public void enterAttribute(CSSParser.AttributeContext var1);

    public void exitAttribute(CSSParser.AttributeContext var1);

    public void enterPseudo(CSSParser.PseudoContext var1);

    public void exitPseudo(CSSParser.PseudoContext var1);

    public void enterString(CSSParser.StringContext var1);

    public void exitString(CSSParser.StringContext var1);

    public void enterBracketed_idents(CSSParser.Bracketed_identsContext var1);

    public void exitBracketed_idents(CSSParser.Bracketed_identsContext var1);

    public void enterIdent_list_item(CSSParser.Ident_list_itemContext var1);

    public void exitIdent_list_item(CSSParser.Ident_list_itemContext var1);

    public void enterAny(CSSParser.AnyContext var1);

    public void exitAny(CSSParser.AnyContext var1);

    public void enterNostatement(CSSParser.NostatementContext var1);

    public void exitNostatement(CSSParser.NostatementContext var1);

    public void enterNoprop(CSSParser.NopropContext var1);

    public void exitNoprop(CSSParser.NopropContext var1);

    public void enterNorule(CSSParser.NoruleContext var1);

    public void exitNorule(CSSParser.NoruleContext var1);

    public void enterNomediaquery(CSSParser.NomediaqueryContext var1);

    public void exitNomediaquery(CSSParser.NomediaqueryContext var1);
}

