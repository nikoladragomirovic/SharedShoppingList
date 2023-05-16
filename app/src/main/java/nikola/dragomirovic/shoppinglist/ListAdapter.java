package nikola.dragomirovic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<List> lists;
    DatabaseHelper database_helper;
    HttpHelper http_helper;
    String username;

    public ListAdapter(Context context, String username) {
        this.context = context;
        this.lists = new ArrayList<List>();
        this.username = username;
        database_helper = new DatabaseHelper(context);
        http_helper = new HttpHelper();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public List getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(List list) {
        lists.add(list);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if (lists.get(position).getShared()){
        http_helper.removeList(lists.get(position).getOwner(), lists.get(position).getTitle());
        }
        database_helper.removeList(lists.get(position).getTitle());
        lists.remove(position);
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.welcome_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.text_welcome_list_title);
            viewHolder.shared = convertView.findViewById(R.id.text_welcome_shared_status);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        viewHolder.title.setText(getItem(position).getTitle());

        if (getItem(position).getShared()) {

            viewHolder.shared.setText("True");

        } else {

            viewHolder.shared.setText("False");

        }

        convertView.setOnClickListener(view -> {

            Intent intent = new Intent(context, ShowListActivity.class);
            intent.putExtra("title", lists.get(position).getTitle());
            intent.putExtra("shared", lists.get(position).getShared());
            context.startActivity(intent);

        });

        convertView.setOnLongClickListener(view -> {
                removeItem(position);
                return true; });

        return convertView;

    }

    private static class ViewHolder {
        TextView title;
        TextView shared;
    }
}
