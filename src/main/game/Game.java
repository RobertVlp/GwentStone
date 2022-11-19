package main.game;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.ActionsInput;
import fileio.GameInput;
import fileio.Input;
import main.player.Player;
import main.player.cards.Card;

public final class Game {
    private final ArrayList<Player> players;
    private final Input input;
    private Player currentPlayer;
    private Player enemyPlayer;
    private Integer roundNumber;
    private int gamesPlayed;

    public Game(final Input input) {
        Player playerOne = new Player();
        Player playerTwo = new Player();

        playerOne.setFrontRow(2);
        playerOne.setBackRow(3);
        playerTwo.setFrontRow(1);
        playerTwo.setBackRow(0);
        playerOne.setNumberOfWins(0);
        playerTwo.setNumberOfWins(0);
        playerOne.setIdx(1);
        playerTwo.setIdx(2);

        players = new ArrayList<>();

        players.add(playerOne);
        players.add(playerTwo);

        gamesPlayed = 0;
        this.input = input;
    }

    private void initGame(final GameInput games) {
        Table.getInstance().initTable();

        int playerOneDeckIdx = games.getStartGame().getPlayerOneDeckIdx();
        int playerTwoDeckIdx = games.getStartGame().getPlayerTwoDeckIdx();

        for (Player player : players) {
            player.getDeck().clear();
            player.getCardsInHand().clear();
        }

        roundNumber = 1;

        players.get(0).setDeck(input.getPlayerOneDecks().getDecks().get(playerOneDeckIdx));
        players.get(1).setDeck(input.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx));
        players.get(0).setHero(games.getStartGame().getPlayerOneHero());
        players.get(1).setHero(games.getStartGame().getPlayerTwoHero());

        for (Player player : players) {
            player.shuffleDeck(games.getStartGame().getShuffleSeed());
            player.addCardInHand();
            player.setMana(roundNumber);
            player.setHasMoved(false);
        }

        currentPlayer = players.get(games.getStartGame().getStartingPlayer() - 1);
        enemyPlayer = currentPlayer != players.get(0) ? players.get(0) : players.get(1);
    }

    /**
     *
     * @param objectMapper for objectMapper
     * @param output for output
     * @throws IOException in case of exceptions to reading / writing
     */
    public void startPlaying(final ObjectMapper objectMapper,
                             final ArrayNode output) throws IOException {
        for (GameInput games : input.getGames()) {
            initGame(games);

            for (ActionsInput action : games.getActions()) {
                performAction(action, objectMapper, output);
            }
        }
    }

    private void performAction(final ActionsInput action,
                               final ObjectMapper objectMapper,
                               final ArrayNode output) throws IOException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        String error;
        int xAttacker, yAttacker, xAttacked, yAttacked;

        jsonObject.put("command", action.getCommand());

        switch (action.getCommand()) {
            case "getPlayerDeck" -> {
                jsonObject.put("playerIdx", action.getPlayerIdx());

                parseArrayListOutput(players.get(action.getPlayerIdx() - 1).getDeck(),
                        objectMapper, jsonObject);

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

            case "endPlayerTurn" -> endPlayerTurn();

            case "placeCard" -> {
                error = currentPlayer.placeCardOnTable(action.getHandIdx());

                if (error != null) {
                    jsonObject.put("error", error);
                    jsonObject.put("handIdx", action.getHandIdx());
                    output.add(jsonObject);
                }
            }

            case "getCardsInHand" -> {
                parseArrayListOutput(players.get(action.getPlayerIdx() - 1).getCardsInHand(),
                        objectMapper, jsonObject);

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
                parseArrayListOutput(players.get(action.getPlayerIdx() - 1)
                        .getEnvironmentCardsInHand(), objectMapper, jsonObject);

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
                error = currentPlayer.useEnvironmentCard(action.getHandIdx(),
                        action.getAffectedRow());

                if (error != null) {
                    jsonObject.put("handIdx", action.getHandIdx());
                    jsonObject.put("affectedRow", action.getAffectedRow());
                    jsonObject.put("error", error);
                    output.add(jsonObject);
                }

                Table.getInstance().removeDeadCards();
            }

            case "getFrozenCardsOnTable" -> {
                parseArrayListOutput(Table.getInstance().getFrozenCards(),
                        objectMapper, jsonObject);

                output.add(jsonObject);
            }

            case "cardUsesAttack" -> {
                xAttacker = action.getCardAttacker().getX();
                yAttacker = action.getCardAttacker().getY();
                xAttacked = action.getCardAttacked().getX();
                yAttacked = action.getCardAttacked().getY();

                error = Table.getInstance().cardUsesAttack(xAttacker, yAttacker,
                        xAttacked, yAttacked);

                parseAttackOutput(action, objectMapper, output, jsonObject, error);
                Table.getInstance().removeDeadCards();
            }

            case "cardUsesAbility" -> {
                xAttacker = action.getCardAttacker().getX();
                yAttacker = action.getCardAttacker().getY();
                xAttacked = action.getCardAttacked().getX();
                yAttacked = action.getCardAttacked().getY();

                error = Table.getInstance().cardUsesAbility(xAttacker, yAttacker,
                        xAttacked, yAttacked);

                parseAttackOutput(action, objectMapper, output, jsonObject, error);
                Table.getInstance().removeDeadCards();
            }

            case "useAttackHero" -> {
                xAttacker = action.getCardAttacker().getX();
                yAttacker = action.getCardAttacker().getY();

                error = Table.getInstance().useAttackHero(enemyPlayer, xAttacker, yAttacker);

                parseAttackOutput(action, objectMapper, output, jsonObject, error);
                jsonObject.remove("cardAttacked");

                if (enemyPlayer.getHero().getHealth() <= 0) {
                    String winner = currentPlayer.getIdx() == 1 ? "one" : "two";
                    jsonObject.put("gameEnded", "Player " + winner + " killed the enemy hero.");
                    jsonObject.remove("command");
                    output.add(jsonObject);
                    gamesPlayed++;
                    currentPlayer.increaseNumberOfWins();
                }
            }

            case "useHeroAbility" -> {
                error = Table.getInstance().useHeroAbility(currentPlayer, action.getAffectedRow());

                if (error != null) {
                    jsonObject.put("affectedRow", action.getAffectedRow());
                    jsonObject.put("error", error);
                    output.add(jsonObject);
                }

                Table.getInstance().removeDeadCards();
            }

            case "getTotalGamesPlayed" -> {
                jsonObject.put("output", gamesPlayed);
                output.add(jsonObject);
            }

            case "getPlayerOneWins" -> {
                jsonObject.put("output", players.get(0).getNumberOfWins());
                output.add(jsonObject);
            }

            case "getPlayerTwoWins" -> {
                jsonObject.put("output", players.get(1).getNumberOfWins());
                output.add(jsonObject);
            }

            default -> {

            }
        }
    }

    private void endPlayerTurn() {
        currentPlayer.setHasMoved(true);
        currentPlayer.defrostCards();

        if (players.get(0).getHasMoved() && players.get(1).getHasMoved()) {
            roundNumber = roundNumber < 10 ? roundNumber + 1 : roundNumber;

            for (Player player : players) {
                player.setHasMoved(false);
                player.addCardInHand();
                player.addMana(roundNumber);
                player.getHero().setHasAttacked(false);
            }

            Table.getInstance().resetAttackStatus();
        }

        currentPlayer = currentPlayer.getIdx() == 1 ? players.get(1) : players.get(0);
        enemyPlayer = enemyPlayer.getIdx() == 1 ? players.get(1) : players.get(0);
    }

    private void parseArrayListOutput(final ArrayList<Card> cards,
                                      final ObjectMapper objectMapper,
                                      final ObjectNode jsonObject) throws JsonProcessingException {
        jsonObject.set(
            "output", objectMapper.readTree(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cards)
            )
        );
    }

    private void parseAttackOutput(final ActionsInput action,
                                   final ObjectMapper objectMapper,
                                   final ArrayNode output,
                                   final ObjectNode jsonObject,
                                   final String error) throws JsonProcessingException {
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
}
