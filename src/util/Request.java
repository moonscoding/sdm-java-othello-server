package util;

import common.Util;

import java.util.LinkedList;
import java.util.List;

public class Request {

    /* cutHeader - packet method 추출 */
    public static String cutHeader(String request) {
        try {
            return request.substring(0, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* cutBody */
    public static List<String> cutBody(String request, int[] cutPoint)  {
        try {
            // @Test
            System.out.println("[서버] 전송데이터 : " + request);
            System.out.println();

            List<String> body = new LinkedList<>();
            int pointer = 0;
            for (int i = 0; i < cutPoint.length; i++) {
                String item = null;
                try {
                    item = request.substring(pointer, pointer + cutPoint[i]).trim();
                    pointer += cutPoint[i];
                    body.add(item);
                } catch (Exception e) {
                    body.add(null);
                }
            }
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
