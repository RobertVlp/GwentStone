package main;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.ActionsInput;
import fileio.Input;
import main.player.*;

public final class Game {
    private ArrayList<Player> players;
    private Input input;
    private Player currentPlayer;
    private Integer roundNumber;

    public Game(Input input) {
        Player playerOne = new Player();
        Player playerTwo = new Player();

        playerOne.setFrontRow(2);
        playerOne.setBackRow(3);
        playerTwo.setFrontRow(1);
        playerTwo.setBackRow(0);

        players = new ArrayList<>();

        players.add(playerOne);
        players.add(playerTwo);

        this.input = input;
    }

    public void startPlaying(ObjectMapper objectMapper, ArrayNode output) throws IOException {
        for (var games : input.getGames()) {
            Table.getInstance().initTable();

            int playerOneDeckIdx = games.getStartGame().getPlayerOneDeckIdx();
            int playerTwoDeckIdx = games.getStartGame().getPlayerTwoDeckIdx();

            roundNumber = Integer.valueOf(1);

            players.get(0).setDeck(input.getPlayerOneDecks().getDecks().get(playerOneDeckIdx));
            players.get(1).setDeck(input.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx));
            players.get(0).setHero(games.getStartGame().getPlayerOneHero());
            players.get(1).setHero(games.getStartGame().getPlayerTwoHero());

            for (int i = 0; i < players.size(); i++) {
                players.get(i).shuffleDeck(games.getStartGame().getShuffleSeed());
                players.get(i).addCardInHand();
                players.get(i).setMana(roundNumber.intValue());
                players.get(i).setHasMoved(false);
                players.get(i).setIdx(i + 1);
            }

            currentPlayer = players.get(games.getStartGame().getStartingPlayer() - 1);

            for (var action : games.getActions()) {
                performAction(players, action, objectMapper, output);
            }
        }
    }

    private void performAction(ArrayList<Player> players,
        ActionsInput action, ObjectMapper objectMapper, ArrayNode output) throws IOException {
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("command", action.getCommand());

            switch (action.getCommand()) {
                case "getPlayerDeck":
                    jsonObject.put("playerIdx", action.getPlayerIdx());
                    jsonObject.set(
                        "output", objectMapper.readTree(
                            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                players.get(action.getPlayerIdx() - 1).getDeck()
                            )
                        )
                    );
                    output.add(jsonObject);
                break;
        
                case "getPlayerHero":
                    jsonObject.put("playerIdx", action.getPlayerIdx());
                    jsonObject.set("output", objectMapper.readTree(
                            players.get(action.getPlayerIdx() - 1).getHero().toString()
                        )
                    );
                    output.add(jsonObject);
                break;

                case "getPlayerTurn":
                        jsonObject.put("output", currentPlayer.getIdx());
                        output.add(jsonObject);
                    break;

                case "endPlayerTurn":
                    currentPlayer.setHasMoved(true);

                    if (currentPlayer.getIdx() == 1)
                        currentPlayer = players.get(1);
                    else
                        currentPlayer = players.get(0);

                    if (players.get(0).getHasMoved() && players.get(1).getHasMoved()) {
                        if (roundNumber.intValue() < 10)
                            roundNumber = Integer.valueOf(roundNumber.intValue() + 1);

                        for (int i = 0; i < players.size(); i++) {
                            players.get(i).setHasMoved(false);
                            players.get(i).addCardInHand();
                            players.get(i).addMana(roundNumber.intValue());
                        }
                    }

                    break;

                case "placeCard":
                    String error = currentPlayer.placeCardOnTable(action.getHandIdx());

                    if (error != null) {
                        jsonObject.put("error", error);
                        jsonObject.put("handIdx", action.getHandIdx());
                        output.add(jsonObject);
                    }

                    break;

                case "getCardsInHand":
                    jsonObject.set(
                        "output", objectMapper.readTree(
                            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                players.get(action.getPlayerIdx() - 1).getCardsInHand()
                            )
                        )
                    );
                    jsonObject.put("playerIdx", action.getPlayerIdx());
                    output.add(jsonObject);
                    break;
            
                case "getPlayerMana":
                    jsonObject.put("output", players.get(action.getPlayerIdx() - 1).getMana());
                    jsonObject.put("playerIdx", action.getPlayerIdx());
                    output.add(jsonObject);
                    break;

                case "getCardsOnTable":
                    output.add(jsonObject);
                    jsonObject.set(
                        "output", objectMapper.readTree(
                            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                Table.getInstance().getCardsOnTable()
                            )
                        )
                    );
                    break;

                default:
                    break;
            }
    }
}
