package nikola.dragomirovic.shoppinglist;

public class List {
    private String title;
    private boolean shared;
    private String owner;

    public List(String owner, String title, boolean shared) {
        this.owner = owner;
        this.title = title;
        this.shared = shared;
    }

    public String getTitle() {
        return title;
    }

    public boolean getShared() {
        return shared;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}