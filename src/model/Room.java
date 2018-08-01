package model;

import java.util.UUID;

public class Room {

    /* Field */
    public String id;
    public String title;
    public int wins;
    public Client guardian;
    public Client opponent;
    public boolean play;

    /* Constructor */
    public Room(Client guardian, String title) {
        this.id = UUID.randomUUID().toString(); // == Random ==
        this.title = title;
        this.wins = 0;
        this.guardian = guardian;
        this.opponent = null;
        this.play = false;
    }

    /* Entry */
    public void entry(Client opponent) {
        this.opponent = opponent;

        // == [send] entry to guardian ==

        // == [send] room update ==


    }

    /* Leave */
    public void leave(boolean team) {
        if(team) {
            // == leave guardian ==
            if(opponent != null) {
                guardian = opponent;
                opponent = null;

                if(play) wins = 1;
                else wins = 0;
            }
        } else {
            // == leave opponent ==
            opponent = null;
            if(play) wins++;
        }

        // == [send] room update ==

    }

}
