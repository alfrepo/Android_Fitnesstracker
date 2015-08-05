/**
 * Created by Pasin Suriyentrakorn <pasin@couchbase.com> on 2/27/14.
 */

package de.couchdbexperiment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

public class QueryAdapter extends BaseAdapter {
    private Query query;
    private QueryEnumerator enumerator;
    private Context context;

    public QueryAdapter(Context context, Query query) {
        this.context = context;
        this.query = query;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

        try {
            this.enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return enumerator != null ? enumerator.getCount() : 0;
    }

    @Override
    public Object getItem(int i) {
        return enumerator != null ? enumerator.getRow(i).getDocument() : null;
    }

    @Override
    public long getItemId(int i) {
        return enumerator.getRow(i).getSequenceNumber();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Document d = (Document) getItem(position);
        Object title = d.getProperty("title");
        Object created_at = d.getProperty("created_at");
        TextView v = new TextView(context);
        String message = String.format("%s at %s", title + "", created_at);
        v.setText(message);
        return v;
    }


}
