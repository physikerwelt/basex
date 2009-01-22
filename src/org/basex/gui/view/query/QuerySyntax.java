package org.basex.gui.view.query;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.HashSet;
import org.basex.gui.GUIConstants;
import org.basex.gui.layout.BaseXSyntax;
import org.basex.query.QueryTokens;
import org.basex.query.func.FunDef;
import org.basex.util.XMLToken;

/**
 * This abstract class defines syntax highlighting of text panels.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class QuerySyntax extends BaseXSyntax {
  /** Error color. */
  public static HashSet<String> keys = new HashSet<String>();
  /** Error color. */
  public static HashSet<String> funs = new HashSet<String>();
  /** Variable color. */
  private static final Color VAR = new Color(0, 160, 0);
  /** Keyword. */
  private static final Color KEY = new Color(0, 144, 144);
  /** Keyword. */
  private static final Color FUNS = new Color(160, 0, 160);

  /** Last quote. */
  private int quote;
  /** Variable flag. */
  private boolean var;

  // initialize xquery keys
  static {
    try {
      for(final Field f : QueryTokens.class.getFields()) {
        if(f.getName().equals("IGNORE")) break;
        final String s = (String) f.get(null);
        for(String ss : s.split("-")) keys.add(ss);
      }
      for(final FunDef f : FunDef.values()) {
        final String s = f.toString();
        for(String ss : s.substring(0, s.indexOf("(")).split("-")) funs.add(ss);
      }
    } catch(final Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void init() {
    quote = 0;
    var = false;
  }

  @Override
  public Color getColor(final String word) {
    final char ch = word.charAt(0);

    // quotes
    if(quote == 0 && (ch == '"' || ch == '\'')) {
      quote = ch;
      return GUIConstants.COLORERROR;
    }
    if(quote != 0) {
      if(ch == quote) quote = 0;
      return GUIConstants.COLORERROR;
    }

    // variables
    if(ch == '$') {
      var = true;
      return VAR;
    }
    if(var) {
      var = XMLToken.isLetterOrDigit(ch);
      return VAR;
    }

    // special characters
    if(keys.contains(word)) return GUIConstants.COLORS[16];
    // special characters
    if(funs.contains(word)) return FUNS;

    // special characters
    if(!XMLToken.isXMLLetterOrDigit(ch)) return KEY;

    // letters and numbers
    return Color.black;
  }
}
