package main.game;

import java.util.ArrayList;

import main.player.Player;
import main.player.cards.Card;
import main.player.cards.heroes.Hero;

public final class Table {
    private static Table instance = null;
    private Card[][] cardMatrix;
    private final int numberOfRows = 4;
    private final int numberOfColumns = 5;

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * @return the instance of the game table
     */
    public static Table getInstance() {
        if (instance == null) {
            instance = new Table();
        }

        return instance;
    }

    public Card[][] getCardMatrix() {
        return cardMatrix;
    }

    /**
     * Method to init the game table
     */
    public void initTable() {
        cardMatrix = new Card[numberOfRows][numberOfColumns];
    }

    /**
     * @param row is the row to be checked
     * @return true if the row is full, otherwise false
     */
    public boolean isRowFull(final int row) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (cardMatrix[row][i] == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param row is the row where the operation is performed
     * @param column is the column where the operation is performed
     */
    public void shiftLeft(final int row, final int column) {
        int numberOfCards = getNumberOfCardsOnRow(row);

        for (int k = column + 1; k < numberOfColumns; k++) {
            cardMatrix[row][k - 1] = cardMatrix[row][k];
        }

        cardMatrix[row][numberOfCards] = null;
    }

    /**
     * @param card is the card to be added
     * @param row is the respective row
     */
    public void addCardOnRow(final Card card, final int row) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (cardMatrix[row][i] == null) {
                cardMatrix[row][i] = card;
                break;
            }
        }
    }

    /**
     * @return the cards that are currently placed on the table
     */
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

    /**
     * @param x is the x coordinate of the card
     * @param y is the y coordinate of the card
     * @return the card at this coordinates
     */
    public Card getCardAtPosition(final int x, final int y) {
        return cardMatrix[x][y];
    }

    private int getNumberOfCardsOnRow(final int row) {
        int numberOfCards = 0;

        for (int j = 0; j < numberOfColumns; j++) {
            numberOfCards = cardMatrix[row][j] != null ? numberOfCards + 1 : numberOfCards;
        }

        return numberOfCards;
    }

    /**
     * Removes all the dead cards on the game table
     */
    public void removeDeadCards() {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (cardMatrix[i][j] != null && cardMatrix[i][j].getHealth() <= 0) {
                    shiftLeft(i, j);
                    j--;
                }
            }
        }
    }

    /**
     * @return all the cards that are frozen on the game table
     */
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

    /**
     * @param xAttacker the x coordinate of the card that attacks
     * @param yAttacker the y coordinate of the card that attacks
     * @param xAttacked the x coordinate of the card that is attacked
     * @param yAttacked the y coordinate of the card that is attacked
     * @return an error message in case of failure when performing the attack
     */
    public String cardUsesAttack(final int xAttacker,
                                 final int yAttacker,
                                 final int xAttacked,
                                 final int yAttacked) {
        if ((xAttacker <= 1 && xAttacked <= 1) || (xAttacker >= 2 && xAttacked >= 2)) {
            return "Attacked card does not belong to the enemy.";
        } else if (cardMatrix[xAttacker][yAttacker].hasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (cardMatrix[xAttacker][yAttacker].isFrozen()) {
            return "Attacker card is frozen.";
        } else if (existsTank(xAttacked)
                && !cardMatrix[xAttacked][yAttacked].getType().equals("Tank")) {
            return "Attacked card is not of type 'Tank'.";
        }

        int attackDamage = cardMatrix[xAttacker][yAttacker].getAttackDamage();
        int attackedCardHealth = cardMatrix[xAttacked][yAttacked].getHealth();

        cardMatrix[xAttacked][yAttacked].setHealth(attackedCardHealth - attackDamage);
        cardMatrix[xAttacker][yAttacker].setHasAttacked(true);

        return null;
    }

    /**
     * @param xAttacker the x coordinate of the card that attacks
     * @param yAttacker the y coordinate of the card that attacks
     * @param xAttacked the x coordinate of the card that is attacked
     * @param yAttacked the y coordinate of the card that is attacked
     * @return an error message in case of failure when using the ability
     */
    public String cardUsesAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {
        if (cardMatrix[xAttacker][yAttacker].isFrozen()) {
            return "Attacker card is frozen.";
        } else if (cardMatrix[xAttacker][yAttacker].hasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else {
            if (cardMatrix[xAttacker][yAttacker].getName().equals("Disciple")) {
                if ((xAttacker <= 1 && xAttacked >= 2) || (xAttacker >= 2 && xAttacked <= 1)) {
                    return "Attacked card does not belong to the current player.";
                }

            } else {
                if ((xAttacker <= 1 && xAttacked <= 1) || (xAttacker >= 2 && xAttacked >= 2)) {
                    return "Attacked card does not belong to the enemy.";
                } else if (existsTank(xAttacked)
                        && !cardMatrix[xAttacked][yAttacked].getType().equals("Tank")) {
                    return "Attacked card is not of type 'Tank'.";
                }

            }

            cardMatrix[xAttacker][yAttacker]
                    .useSpecialAbility(xAttacker, yAttacker, xAttacked, yAttacked);
            cardMatrix[xAttacker][yAttacker].setHasAttacked(true);
        }

        return null;
    }

    /**
     * @param player is the player whose hero is attacked
     * @param xAttacker the x coordinate of the card that attacks
     * @param yAttacker the y coordinate of the card that attacks
     * @return an error message in case of failure when attacking the hero
     */
    public String useAttackHero(final Player player, final int xAttacker, final int yAttacker) {
        if (cardMatrix[xAttacker][yAttacker].isFrozen()) {
            return "Attacker card is frozen.";
        } else if (cardMatrix[xAttacker][yAttacker].hasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (existsTank(player.getFrontRow())) {
            return "Attacked card is not of type 'Tank'.";
        }

        int attackDamage = cardMatrix[xAttacker][yAttacker].getAttackDamage();
        int heroHealth = player.getHero().getHealth();

        player.getHero().setHealth(heroHealth - attackDamage);
        cardMatrix[xAttacker][yAttacker].setHasAttacked(true);

        return null;
    }

    /**
     * @param player is the player that uses his hero's ability
     * @param affectedRow is the affected row by the hero ability
     * @return an error message in case of failure when using the hero's ability
     */
    public String useHeroAbility(final Player player, final int affectedRow) {
        Card hero = player.getHero();
        int mana = player.getMana();

        if (mana < hero.getMana()) {
            return "Not enough mana to use hero's ability.";
        } else if (hero.hasAttacked()) {
            return "Hero has already attacked this turn.";
        } else {
            if ((hero.getName().equals("Empress Thorina"))
                    || (hero.getName().equals("Lord Royce"))) {
                if ((affectedRow == player.getFrontRow()) || (affectedRow == player.getBackRow())) {
                    return "Selected row does not belong to the enemy.";
                }
            } else {
                if ((affectedRow != player.getFrontRow()) && (affectedRow != player.getBackRow())) {
                    return "Selected row does not belong to the current player.";
                }
            }

            ((Hero) hero).useHeroAbility(affectedRow);
            hero.setHasAttacked(true);
            mana -= hero.getMana();
            player.setMana(mana);
        }

        return null;
    }

    private boolean existsTank(final int xAttacked) {
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

    /**
     * Resets the hasAttacked attribute for all the cards on the game table at the end of each round
     */
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
