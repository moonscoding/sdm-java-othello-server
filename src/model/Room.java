package model;

import common.Define;
import othello.O_Client;
import othello.RoomManager;
import util.Response;

public class Room {

    /* Field */
    private String id;
    private String title;
    private int wins;
    private O_Client guardian;
    private O_Client challenger;
    private boolean play;

    /* Constructor */
    public Room(O_Client guardian, String title) {
        // @Test
        // this.id = UUID.randomUUID().toString(); // == Random ==
        this.id = RoomManager.getUniqueId("fix"); // == Random ==
        this.title = title;
        this.wins = 0;
        this.guardian = guardian;
        this.challenger = null;
        this.play = false;
    }

    /* getId */
    public String getId() {
        return id;
    }

    /* getTitle */
    public String getTitle() {
        return title;
    }

    /* getWins */
    public int getWins() {
        return wins;
    }

    /* getGuardian - 방장 */
    public O_Client getGuardian() {
        return guardian;
    }

    /* getChallenger - 도전자 */
    public O_Client getChallenger() {
        return challenger;
    }

    /* getOppenent - 상대 */
    public O_Client getOppenent(O_Client client) {
        if(isFull()) {
            if(client == guardian) {
                return challenger;
            } else {
                return guardian;
            }
        } else {
            return null;
        }
    }

    /* isFull - 인원이가득찼는가? */
    public boolean isFull() {
        return challenger != null;
    }

    /* isPlay - 게임진행중인가? */
    public boolean isPlay() {
        return this.play;
    }

    /* isGuardian - 방장인가? */
    public boolean isGuardian(O_Client client) {
        return guardian == client ? true : false;
    }

    /* Entry - 게임입장 */
    public void entry(O_Client challenger) {
        this.challenger = challenger;

        // == send (URL_RES_ENTRY) ==
        this.guardian.send( Define.URL_RES_ENTRY + Response.fullBlank(this.challenger.name, Define.SIZE_USER_NAME));

        // == 브로드캐스트호출 ==
        RoomManager.getInstance().setUpdate(true);

    }

    /* Leave - 게임떠남 */
    public void leave(boolean isGuardian) {
        try {
            stop(); // == 진행불가 ==

            if(isGuardian) {
                // == leave guardian ==
                if(isFull()) {
                    // == 2인 (challenger => guardian) ==
                    guardian = challenger;
                    challenger = null;

                    if(isPlay()) wins = 1;
                    else wins = 0;

                    guardian.send(Define.URL_RES_LEAVE_GUARDIAN);

                } else {
                    // == 1인 ==
                    RoomManager.getInstance().rooms.remove(this);
                }
            } else {
                // == leave challenger ==
                challenger = null;
                if(isPlay()) wins++;

                guardian.send(Define.URL_RES_LEAVE_CHALLENGER);
            }

            // == 브로드캐스트호출 ==
            RoomManager.getInstance().setUpdate(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Start - 게임시작 */
    public void start() {
        this.play = true;
    }

    /* stop - 게임중지 */
    public void stop() {
        this.play = false;
    }

}
