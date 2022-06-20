package pavle.vukovic.memorygame;

public class JNIexample {

    static{
        System.loadLibrary("MyLibrary");
    }

    public native int points(boolean flag, int poeni);
}
