package nikola.dragomirovic.shoppinglist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Task> tasks;
    private DatabaseHelper database_helper;

    public TaskAdapter(Context context) {

        this.context = context;
        this.tasks = new ArrayList<Task>();
        database_helper =  new DatabaseHelper(context);

    }

    public void clearTasks(){
        tasks.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    public void setCheck(int position, boolean check){

        database_helper.setChecked(tasks.get(position).getId(), check);
        new HttpHelper().setChecked(tasks.get(position).getId(), check);
        tasks.get(position).setCheck(check);
        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position){

        database_helper.removeTask(tasks.get(position).getId(), tasks.get(position).getOwner());
        new HttpHelper().removeTask(tasks.get(position).getId(), tasks.get(position).getOwner());
        tasks.remove(position);
        notifyDataSetChanged();

    }

    public void addItem(Task task){
        tasks.add(task);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.show_list_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.text_show_list_task);
            viewHolder.check = convertView.findViewById(R.id.checkbox_show_list_done);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = (Task) getItem(position);
        viewHolder.title.setText(task.getTitle());

        try{
            viewHolder.check.setChecked(database_helper.getChecked(task.getId()));
        }catch (Exception e){
            e.printStackTrace();
        }

        viewHolder.check.setOnClickListener(view -> {
            setCheck(position, viewHolder.check.isChecked());
        });

        if (task.getCheck()) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        convertView.setOnLongClickListener(view -> {
            removeItem(position);
            return false;
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        CheckBox check;
    }
}
