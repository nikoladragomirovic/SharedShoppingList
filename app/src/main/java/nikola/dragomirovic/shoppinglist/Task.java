package nikola.dragomirovic.shoppinglist;
public class Task {
    private String title;
    private boolean check;
    private String id;
    private String owner;

    public Task(String owner, String id, String title, boolean check) {
        this.title = title;
        this.check = check;
        this.id = id;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }
    public boolean getCheck() {
        return check;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
