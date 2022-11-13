package main;

import java.util.ArrayList;

import main.player.cards.Card;

public final class Table {
    private static Table instance = null;
    private Card[][] cardMatrix;
    final int numberOfRows = 4;
    final int numberOfColumns = 5;

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

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

        cardMatrix[row][numberOfColumns - 1] = null; 
    }

    public void initTable() {
        cardMatrix = new Card[numberOfRows][numberOfColumns];
    }

    public boolean isRowFull(int row) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (cardMatrix[row][i] == null) {
                return false;
            }
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

    public Card getCardAtPosition(int x, int y) {
        return cardMatrix[x][y];
    }

    public void removeDeadCards() {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (cardMatrix[i][j] != null && cardMatrix[i][j].getHealth() == 0) {
                    shiftLeft(i, j);
                    j--;
                }
            }
        }
    }

    public ArrayList<Card> getFrozenCards() {
        ArrayList<Card> frozenCards = new ArrayList<>();

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (cardMatrix[i][j] != null && cardMatrix[i][j].isFrozen()) {
                    frozenCards.add(cardMatrix[i][j]);
                }    
            }
        }

        return frozenCards;
    }

    public String cardUsesAttack(int xAttacker, int yAttacker, int xAttacked, int yAttacked) {
        if ((xAttacker <= 1 && xAttacked <= 1) || (xAttacker >= 2 && yAttacker >= 2)) {
            return "Attacked card does not belong to the enemy.";
        } else if(cardMatrix[xAttacker][yAttacker].hasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (cardMatrix[xAttacker][yAttacker].isFrozen()) {
            return "Attacker card is frozen.";
        } else if (existsTank(xAttacked) && !cardMatrix[xAttacked][yAttacked].getType().equals("Tank")) {
            return "Attacked card is not of type 'Tank'.";
        }

        int attackDamage = cardMatrix[xAttacker][yAttacker].getAttackDamage();
        int attackedCardHealth = cardMatrix[xAttacked][yAttacked].getHealth();

        cardMatrix[xAttacked][yAttacked].setHealth(attackedCardHealth - attackDamage);
        cardMatrix[xAttacker][yAttacker].setHasAttacked(true);

        return null;
    }

    private boolean existsTank(int xAttacked) {
        if (xAttacked <= 1) {
            for (int i = 0; i < numberOfColumns; i++) {
                if (cardMatrix[1][i] != null && cardMatrix[1][i].getType().equals("Tank")) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < numberOfColumns; i++) {
                if (cardMatrix[2][i] != null && cardMatrix[2][i].getType().equals("Tank")) {
                    return true;
                }
            }
        }

        return false;
    }

    public void resetAttackStatus() {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (cardMatrix[i][j] != null) {
                    cardMatrix[i][j].setHasAttacked(false);
                }
            }
        }
    }
}
