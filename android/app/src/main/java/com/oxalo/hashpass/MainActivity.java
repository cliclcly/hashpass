package com.oxalo.hashpass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class MainActivity
        extends     AppCompatActivity
        implements  Response.Listener<JSONArray>,
                    Response.ErrorListener
{
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected TextView res_view;
    protected ProgressBar spinner_loading;

    protected JSONArray data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res_view = (TextView ) findViewById( R.id.view_response );
        assert res_view != null;

        spinner_loading = (ProgressBar ) findViewById( R.id.spinner_loading );

        mRecyclerView = (RecyclerView) findViewById( R.id.list_sites );
        assert mRecyclerView != null;

        mRecyclerView.setHasFixedSize( true );

        mLayoutManager = new LinearLayoutManager( this );
        mRecyclerView.setLayoutManager( mLayoutManager );

        mAdapter = new ListAdapter();
        mRecyclerView.setAdapter( mAdapter );

        RequestQueue queue = Volley.newRequestQueue( this );
        String data_url = "https://raw.githubusercontent.com/cliclcly/hashpass/master/data_ex.json";

        JsonArrayRequest req = new JsonArrayRequest( Request.Method.GET, data_url, null, this, this);

        queue.add( req );
    }

    // --------------------------------
    // Volley Listeners
    // --------------------------------

    @Override
    public void onResponse(JSONArray response) {
        data = response;
        mAdapter.notifyDataSetChanged();

        spinner_loading.setVisibility( View.GONE );
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText( this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();

        spinner_loading.setVisibility( View.GONE );
    }

    // --------------------------------
    // RecyclerView Adapter
    // --------------------------------

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(TextView view) {
                super(view);

                mTextView = view;
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater  .from( parent.getContext() )
                                            .inflate( R.layout.list_item_site, parent, false );

            ViewHolder vh = new ViewHolder( (TextView) v );

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if ( null != data ) {
                try {
                    JSONObject element = (JSONObject) data.get( position );

                    holder.mTextView.setText( element.getString( "site" ) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount() {
            return (null == data) ? 0 : data.length();
        }
    }
}
