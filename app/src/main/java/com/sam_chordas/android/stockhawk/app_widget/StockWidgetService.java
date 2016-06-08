package com.sam_chordas.android.stockhawk.app_widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ItemFactory(getApplicationContext());
    }

    static class ItemFactory implements RemoteViewsFactory{

        private Context mContext;
        private Cursor mCursor;

        public ItemFactory(Context context) {
            mContext = context;
        }


        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            if(mCursor != null){
                mCursor.close();
            }
             mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns.SYMBOL,QuoteColumns.BIDPRICE,QuoteColumns.PERCENT_CHANGE,QuoteColumns.ISUP},
                    QuoteColumns.ISCURRENT + " = ?",
                     new String[]{"1"},null);
        }

        @Override
        public void onDestroy() {
            if(mCursor != null){
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            mCursor.moveToPosition(position);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(),R.layout.item_stock_app_widget);
            String symbol = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));
            rv.setTextViewText(R.id.symbol,symbol);
            String bidPrice = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
            rv.setTextViewText(R.id.bid_price,bidPrice);
            rv.setTextViewText(R.id.percent_changed,mCursor.getString(mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
            if(mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP)) == 1){
                rv.setInt(R.id.percent_changed,"setBackgroundColor",mContext.getResources().getColor(R.color.material_red_700));
            }else {
                rv.setInt(R.id.percent_changed,"setBackgroundColor",mContext.getResources().getColor(R.color.material_green_700));

            }
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(DetailActivity.SYMBOL,symbol);
            fillInIntent.putExtra(DetailActivity.BID_PRICE,bidPrice);

            rv.setOnClickFillInIntent(R.id.stock_item,fillInIntent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
