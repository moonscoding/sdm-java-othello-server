package util;

public class Response {

    /* fullBlank */
    public static String fullBlank(String string, int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size - string.length(); i++) {
            sb.append(" ");
        }
        return sb.append(string).toString();
    }
}
