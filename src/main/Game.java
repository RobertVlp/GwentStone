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
    private final ArrayList<Player> players;
    private final Input input;
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

            roundNumber = 1;

            players.get(0).setDeck(input.getPlayerOneDecks().getDecks().get(playerOneDeckIdx));
            players.get(1).setDeck(input.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx));
            players.get(0).setHero(games.getStartGame().getPlayerOneHero());
            players.get(1).setHero(games.getStartGame().getPlayerTwoHero());

            for (int i = 0; i < players.size(); i++) {
                players.get(i).shuffleDeck(games.getStartGame().getShuffleSeed());
                players.get(i).addCardInHand();
                players.get(i).setMana(roundNumber);
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
            String error;

            jsonObject.put("command", action.getCommand());

        switch (action.getCommand()) {
            case "getPlayerDeck" -> {
                jsonObject.put("playerIdx", action.getPlayerIdx());
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        players.get(action.getPlayerIdx() - 1).getDeck()
                                )
                        )
                );
                output.add(jsonObject);
            }
            case "getPlayerHero" -> {
                jsonObject.put("playerIdx", action.getPlayerIdx());
                jsonObject.set("output", objectMapper.readTree(
                                players.get(action.getPlayerIdx() - 1).getHero().toString()
                        )
                );
                output.add(jsonObject);
            }
            case "getPlayerTurn" -> {
                jsonObject.put("output", currentPlayer.getIdx());
                output.add(jsonObject);
            }
            case "endPlayerTurn" -> {
                currentPlayer.setHasMoved(true);
                Table.getInstance().removeDeadCards();

                if (players.get(0).getHasMoved() && players.get(1).getHasMoved()) {
                    if (roundNumber < 10) {
                        roundNumber = roundNumber + 1;
                    }

                    for (Player player : players) {
                        player.setHasMoved(false);
                        player.addCardInHand();
                        player.addMana(roundNumber);
                    }

                    currentPlayer.defrostCards();
                    Table.getInstance().resetAttackStatus();
                }
                if (currentPlayer.getIdx() == 1)
                    currentPlayer = players.get(1);
                else
                    currentPlayer = players.get(0);
            }
            case "placeCard" -> {
                error = currentPlayer.placeCardOnTable(action.getHandIdx());
                if (error != null) {
                    jsonObject.put("error", error);
                    jsonObject.put("handIdx", action.getHandIdx());
                    output.add(jsonObject);
                }
            }
            case "getCardsInHand" -> {
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        players.get(action.getPlayerIdx() - 1).getCardsInHand()
                                )
                        )
                );
                jsonObject.put("playerIdx", action.getPlayerIdx());
                output.add(jsonObject);
            }
            case "getPlayerMana" -> {
                jsonObject.put("output", players.get(action.getPlayerIdx() - 1).getMana());
                jsonObject.put("playerIdx", action.getPlayerIdx());
                output.add(jsonObject);
            }
            case "getCardsOnTable" -> {
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        Table.getInstance().getCardsOnTable()
                                )
                        )
                );
                output.add(jsonObject);
            }
            case "getEnvironmentCardsInHand" -> {
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        players.get(action.getPlayerIdx() - 1).getEnvironmentCardsInHand()
                                )
                        )
                );
                jsonObject.put("playerIdx", action.getPlayerIdx());
                output.add(jsonObject);
            }
            case "getCardAtPosition" -> {
                jsonObject.put("x", action.getX());
                jsonObject.put("y", action.getY());
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        Table.getInstance().getCardAtPosition(action.getX(), action.getY())
                                )
                        )
                );
                if (Table.getInstance().getCardAtPosition(action.getX(), action.getY()) == null) {
                    jsonObject.put("output", "No card available at that position.");
                }
                output.add(jsonObject);
            }
            case "useEnvironmentCard" -> {
                error = currentPlayer.useEnvironmentCard(action.getHandIdx(), action.getAffectedRow());
                if (error != null) {
                    jsonObject.put("handIdx", action.getHandIdx());
                    jsonObject.put("affectedRow", action.getAffectedRow());
                    jsonObject.put("error", error);
                    output.add(jsonObject);
                }
            }
            case "getFrozenCardsOnTable" -> {
                jsonObject.set(
                        "output", objectMapper.readTree(
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                        Table.getInstance().getFrozenCards()
                                )
                        )
                );
                output.add(jsonObject);
            }
            case "cardUsesAttack" -> {
                int xAttacker = action.getCardAttacker().getX();
                int yAttacker = action.getCardAttacker().getY();
                int xAttacked = action.getCardAttacked().getX();
                int yAttacked = action.getCardAttacked().getY();
                error = Table.getInstance().cardUsesAttack(xAttacker, yAttacker, xAttacked, yAttacked);
                if (error != null) {
                    jsonObject.set(
                            "cardAttacker", objectMapper.readTree(
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                            action.getCardAttacker()
                                    )
                            )
                    );
                    jsonObject.set(
                            "cardAttacked", objectMapper.readTree(
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                            action.getCardAttacked()
                                    )
                            )
                    );
                    jsonObject.put("error", error);
                    output.add(jsonObject);
                }
            }
            default -> {
            }
        }
    }
}
