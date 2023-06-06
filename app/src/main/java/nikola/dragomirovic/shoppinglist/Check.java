package nikola.dragomirovic.shoppinglist;

public class Check {
    static {
        System.loadLibrary("Check");
    }

    public native int check(String s);

}
