package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import checker.CheckerConstants;
import fileio.Input;
import main.player.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        //TODO add here the entry point to your implementation
        Player playerOne = new Player();
        Player playerTwo = new Player();
        ArrayList<Player> players = new ArrayList<>();
        // Table table = new Table();

        for (var games : inputData.getGames()) {
            int playerOneDeckIdx = games.getStartGame().getPlayerOneDeckIdx();
            int playerTwoDeckIdx = games.getStartGame().getPlayerTwoDeckIdx();

            playerOne.setDeck(inputData.getPlayerOneDecks().getDecks().get(playerOneDeckIdx));
            playerTwo.setDeck(inputData.getPlayerTwoDecks().getDecks().get(playerTwoDeckIdx));
            playerOne.setHero(games.getStartGame().getPlayerOneHero());
            playerTwo.setHero(games.getStartGame().getPlayerTwoHero());
            playerOne.shuffleDeck(games.getStartGame().getShuffleSeed());
            playerTwo.shuffleDeck(games.getStartGame().getShuffleSeed());
            playerOne.addCardInHand();
            playerTwo.addCardInHand();
            players.add(playerOne);
            players.add(playerTwo);
            
            for (var action : games.getActions()) {
                ObjectNode jsonObject = objectMapper.createObjectNode();

                jsonObject.put("command", action.getCommand());

                switch (action.getCommand()) {
                    case "getPlayerDeck":
                        jsonObject.put("playerIdx", action.getPlayerIdx());
                        jsonObject.set("output", objectMapper.readTree(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(players.get(action.getPlayerIdx() - 1).getDeck())));
                        output.add(jsonObject);
                        break;
                
                    case "getPlayerHero":
                        jsonObject.put("playerIdx", action.getPlayerIdx());
                        jsonObject.set("output", objectMapper.readTree(players.get(action.getPlayerIdx() - 1).getHero().toString()));
                        output.add(jsonObject);
                        break;

                    case "getPlayerTurn":
                        jsonObject.put("output", 2);
                        output.add(jsonObject);
                        break;

                    default:
                        break;
                }
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }
}
