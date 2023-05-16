package nikola.dragomirovic.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> items;
    DatabaseHelper database_helper;

    public ListAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<Item>();
        database_helper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Item item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        if (items.get(position).getShared()){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String deleteUrl = "http://192.168.0.27:3000/lists";
                        String url = deleteUrl + "/" + items.get(position).getOwner() + "/" + items.get(position).getTitle();

                        HttpHelper http_helper = new HttpHelper();
                        http_helper.httpDelete(url);

                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        database_helper.removeList(items.get(position).getTitle());
        items.remove(position);
        notifyDataSetChanged();
    }

    public void clearAllItems(){
        items.clear();
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
            intent.putExtra("title", items.get(position).getTitle());
            intent.putExtra("shared", items.get(position).getShared());
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
