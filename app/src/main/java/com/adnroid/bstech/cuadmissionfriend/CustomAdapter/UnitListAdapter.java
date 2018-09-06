package com.adnroid.bstech.cuadmissionfriend.CustomAdapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.adnroid.bstech.cuadmissionfriend.R;

import java.util.List;

public class UnitListAdapter extends BaseAdapter {

    List<String> name, id;
    Context context;

    public UnitListAdapter(Context context, List<String> name, List<String> id){
        this.context = context;
        this.name = name;
        this.id = id;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView.inflate(context,R.layout.layout_unit_list, null);

        CardView cardView_unit_list = v.findViewById(R.id.cardView_unit_list);
        TextView textView_unit_name = v.findViewById(R.id.textView_unit_name);

        textView_unit_name.setText(name.get(position));

        cardView_unit_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCollection.unitID = id.get(position);
                Toast.makeText(context, "ID: "+id.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
