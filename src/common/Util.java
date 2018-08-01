package common;

public class Util {

    /* log */
    public static void log(String log) {
        if(!Config.dist) System.out.println(log);
    }


}
