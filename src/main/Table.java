package main;

import java.util.ArrayList;

import main.player.cards.Card;

public class Table {
    private static Table instance = null;
    private Card[][] cardMatrix;
    final int numberOfRows = 4;
    final int numberOfColumns = 5;

    public static Table getInstance() {
        if (instance == null) {
            instance = new Table();
        }

        return instance;
    }

    public Card[][] getCardMatrix() {
        return cardMatrix;
    }

    public void shiftLeft(int row, int column) {
        for (int i = column + 1; i < numberOfColumns; i++) {
            cardMatrix[row][i - 1] = cardMatrix[row][i];
        }
    }

    public void initTable() {
        cardMatrix = new Card[numberOfRows][numberOfColumns];
    }

    public boolean isRowFull(int row) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (cardMatrix[row][i] == null)
                return false;
        }

        return true;
    }

    public void addCardOnRow(Card card, int row) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (cardMatrix[row][i] == null) {
                cardMatrix[row][i] = card;
                break;
            } 
        }
    }

    public ArrayList<ArrayList<Card>> getCardsOnTable() {
        ArrayList<ArrayList<Card>> cardsOnTable = new ArrayList<>();

        for (int i = 0; i < numberOfRows; i++) {
            ArrayList<Card> currentRow = new ArrayList<>();

            for (int j = 0; j < numberOfColumns; j++) {
                if (cardMatrix[i][j] != null) {
                    currentRow.add(cardMatrix[i][j]);
                }
            }

            cardsOnTable.add(currentRow);
        }

        return cardsOnTable;
    }
}
