package com.sam_chordas.android.stockhawk.data;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.net.Uri;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.TheApplication;
import com.sam_chordas.android.stockhawk.app_widget.StockAppWidgetProvider;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by sam_chordas on 10/5/15.
 */
@ContentProvider(authority = QuoteProvider.AUTHORITY, database = QuoteDatabase.class)
public class QuoteProvider {
  public static final String AUTHORITY = "com.sam_chordas.android.stockhawk.data.QuoteProvider";

  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  interface Path{
    String QUOTES = "quotes";
    String STOCKHISTORY = "stockhistory";
  }

  private static Uri buildUri(String... paths){
    Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
    for (String path:paths){
      builder.appendPath(path);
    }
    return builder.build();
  }

  @TableEndpoint(table = QuoteDatabase.QUOTES)
  public static class Quotes{
    @ContentUri(
        path = Path.QUOTES,
        type = "vnd.android.cursor.dir/quote"
    )
    public static final Uri CONTENT_URI = buildUri(Path.QUOTES);

    @InexactContentUri(
        name = "QUOTE_ID",
        path = Path.QUOTES + "/*",
        type = "vnd.android.cursor.item/quote",
        whereColumn = QuoteColumns.SYMBOL,
        pathSegment = 1
    )
    public static Uri withSymbol(String symbol){
      return buildUri(Path.QUOTES, symbol);
    }

    @NotifyInsert(paths = Path.QUOTES)
    public static Uri[] notifyWhenInsert(){
      updateAppWidgetData();
      return new Uri[]{CONTENT_URI};
    }
    @NotifyDelete(paths = Path.QUOTES + "/*")
    public static Uri[] notifyWhenDelete(){
updateAppWidgetData();
      return new Uri[]{withSymbol("*")};
    }

    private static void updateAppWidgetData(){
      ComponentName cm = new ComponentName(TheApplication.getContext(), StockAppWidgetProvider.class);
      AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(TheApplication.getContext());
      int[] appWidgetIds =appWidgetManager.getAppWidgetIds(cm);
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection);

    }
  }

  @TableEndpoint(table = QuoteDatabase.STOCKHISTORY)
  public static class StockHistory{
    @ContentUri(path = Path.STOCKHISTORY,type = "")
    public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().path(Path.STOCKHISTORY).build();
  }
}
