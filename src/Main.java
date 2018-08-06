import common.Define;
import othello.O_Server;

/* Main 실행클래스 */
public class Main {
    public static void main(String[] args) {

        if(Define.isDebug) {
            // == Debug ==
            new O_Server(Define.PORT_DEBUG);
        } else {
            // == Production ==
            new O_Server(Define.PORT_PROP);
        }
    }
}
