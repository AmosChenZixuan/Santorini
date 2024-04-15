package edu.cmu.cs214.hw3;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.player.godCards.GodCardFactory;



public class App extends NanoHTTPD {

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    private Game game; 

    private static final int PORT = 8080;

    /**
     * Start the server at :8080 port.
     * @throws IOException
     */
    public App() throws IOException {
        super(PORT);

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Map<String, String> params = session.getParms();

        if (uri.equals("/newGame")) {
            serveNewGame(params);
        } 
        else if (uri.equals("/initWorker")) {
            serveInitWorker(params);
        } 
        else if (uri.equals("/select")) {
            serveSelect(params);
        }
        else if (uri.equals("/move")) {
            serveMove(params);
        }
        else if (uri.equals("/build")) {
            serveBuild(params);
        }
        else if (uri.equals("/endTurn")) {
            serveEndTurn(params);
        }
        else if (uri.equals("/undo")) {
            serveUndo(params);
        }
        else if (uri.equals("/ability")) {
            serveAbility(params);
        }
        else if (uri.equals("/cardList")) {            
            return newFixedLengthResponse(GodCardFactory.getCardListJSON());
        }
        return newFixedLengthResponse(game.toString());
    }

    private void serveUndo(Map<String, String> params) {
        game = this.game.undo();
    }

    private void serveNewGame(Map<String, String> params) {
        String[] godCardNames = {params.get("godCard0"), 
                                params.get("godCard1")};
        this.newGame(godCardNames);
        if (Boolean.parseBoolean(params.get("AI0"))){
            game = game.setUpAI(0);
        }
        if (Boolean.parseBoolean(params.get("AI1"))){
            game = game.setUpAI(1);
        }
    }

    private void serveInitWorker(Map<String, String> params) {
        int pos = getPosFromParams(params);
        game = game.initWorker(pos);
    }

    private void serveSelect(Map<String, String> params) {
        int pos = getPosFromParams(params);
        game = game.select(pos);
    }

    private void serveMove(Map<String, String> params) {
        int newPos = getPosFromParams(params);
        game = game.move(newPos);
    }

    private void serveBuild(Map<String, String> params) {
        int pos = getPosFromParams(params);
        game = game.build(pos);
    }

    private void serveEndTurn(Map<String, String> params) {
        game = game.newRound();
    }

    private void serveAbility(Map<String, String> params) {
        game = game.useAbility();
    }

    private int getPosFromParams(Map<String, String> params) { 
        return Integer.parseInt(params.get("field_id"));
    }


    private void newGame(String[] godCardNames) {
        this.game = new Game(godCardNames);
    }
}
