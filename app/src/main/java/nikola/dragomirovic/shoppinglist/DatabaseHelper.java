package nikola.dragomirovic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {

        super(context, "shared_list_app.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS USERS (username TEXT PRIMARY KEY, email TEXT, password TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS LISTS (title TEXT PRIMARY KEY, creator TEXT, shared INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS ITEMS (id TEXT PRIMARY KEY, title TEXT, list TEXT, checked INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean registerUser(String username, String mail, String password) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query("USERS", null, "username" + " = ?", new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;

        }

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", mail);
        values.put("password", password);

        db.insert("USERS", null, values);
        db.close();
        return true;

    }

    public boolean loginUser(String username, String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("USERS", null, "username" + " = ? AND " + "password" + " = ?", new String[]{username, password}, null, null, null);

        boolean result = cursor.moveToFirst();

        cursor.close();
        db.close();

        return result;
    }

    public ArrayList<Item> loadLists(String username) {

        ArrayList<Item> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("LISTS", new String[]{"creator","title", "shared"}, "creator = ? OR shared = ?", new String[]{username, "1"}, null, null, null);

        while (cursor.moveToNext()) {

            items.add(new Item(cursor.getString(cursor.getColumnIndexOrThrow("creator")), cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("shared")) == 1)));

        }

        cursor.close();
        db.close();

        return items;
    }

    public ArrayList<Item> loadMyLists(String username) {

        ArrayList<Item> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("LISTS", new String[]{"creator","title", "shared"}, "creator = ?", new String[]{username}, null, null, null);

        while (cursor.moveToNext()) {

            items.add(new Item(cursor.getString(cursor.getColumnIndexOrThrow("creator")),cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("shared")) == 1)));

        }

        cursor.close();
        db.close();

        return items;
    }

    public boolean addList(String title, String creator, int shared) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query("LISTS", null, "title = ?", new String[]{ title }, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("creator", creator);
        values.put("shared", shared);
        db.insert("LISTS", null, values);

        cursor.close();
        db.close();
        return true;
    }

    public boolean removeList(String title) {

        SQLiteDatabase db = getWritableDatabase();

        if (db.delete("LISTS", "title = ?", new String[]{ title }) == 0) {
            return false;
        }

        db.delete("ITEMS", "list = ?", new String[]{title});

        db.close();
        return true;

    }

    public ArrayList<Task> loadTasks(String list) {
        ArrayList<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("ITEMS", new String[]{"list","id", "title", "checked"}, "list = ?", new String[]{list}, null, null, null);

        while (cursor.moveToNext()) {

            tasks.add(new Task(cursor.getString(cursor.getColumnIndexOrThrow("list")),cursor.getString(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("checked")) == 1)));
        }

        cursor.close();
        db.close();

        return tasks;
    }

    public ArrayList<Task> loadFilteredTasks(String list) {
        ArrayList<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("ITEMS", new String[]{"list","id", "title", "checked"}, "list = ? AND title != ?", new String[]{list, "zadatak1"}, null, null, null);

        while (cursor.moveToNext()) {

            tasks.add(new Task(cursor.getString(cursor.getColumnIndexOrThrow("list")),cursor.getString(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("title")), (cursor.getInt(cursor.getColumnIndexOrThrow("checked")) == 1)));
        }

        cursor.close();
        db.close();

        return tasks;
    }

    public void addTask(String title, String list, boolean shared) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        String id = generateRandomString(5);

        values.put("title", title);
        values.put("list", list);
        values.put("checked", 0);
        values.put("id", id);

        db.insert("ITEMS", null, values);

        if(shared){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String addTaskUrl = "http://192.168.0.27:3000/tasks";

                        JSONObject task = new JSONObject();

                        task.put("name", title);
                        task.put("list", list);
                        task.put("done", false);
                        task.put("taskId", id);

                        HttpHelper http_helper = new HttpHelper();
                        http_helper.postJSONObjectFromURL(addTaskUrl, task);

                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

        db.close();

    }

    public void removeTask(String id, String owner) {

        SQLiteDatabase db = getWritableDatabase();

        db.delete("ITEMS", "id = ?", new String[]{id});

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String http_id = null;

                    HttpHelper http_helper = new HttpHelper();

                    JSONArray all_tasks = http_helper.getJSONArrayFromURL("http://192.168.0.27:3000/tasks/" + owner);

                    for (int i = 0; i < all_tasks.length(); i++) {
                        JSONObject jsonObject = all_tasks.getJSONObject(i);

                        if (jsonObject.getString("taskId").equals(id)){
                            http_id = jsonObject.getString("_id");
                            break;
                        }
                    }

                    http_helper.httpDelete("http://192.168.0.27:3000/tasks/" + http_id);

                }catch (JSONException | IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        db.close();
    }

    public boolean getChecked(String id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("ITEMS", new String[] {"checked"}, "id = ?", new String[]{id}, null, null, null);

        cursor.moveToFirst();

        if (cursor.getInt(cursor.getColumnIndexOrThrow("checked")) == 1) {
            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();

        return false;
    }

    public void setChecked(String id, boolean checked) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("checked", checked ? 1 : 0);

        db.update("ITEMS", values, "id = ?", new String[]{id});

        db.close();
    }

    public static String generateRandomString(int length) {

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }

        return sb.toString();
    }

}
