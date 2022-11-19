package main.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;
import main.player.cards.environment.Environment;
import main.player.cards.environment.Firestorm;
import main.player.cards.environment.HeartHound;
import main.player.cards.environment.Winterfell;
import main.player.cards.heroes.EmpressThorina;
import main.player.cards.heroes.GeneralKocioraw;
import main.player.cards.heroes.KingMudface;
import main.player.cards.heroes.LordRoyce;
import main.player.cards.minions.Berserker;
import main.player.cards.minions.Warden;
import main.player.cards.minions.Sentinel;
import main.player.cards.minions.Goliath;
import main.player.cards.minions.TheRipper;
import main.player.cards.minions.Miraj;
import main.player.cards.minions.TheCursedOne;
import main.player.cards.minions.Disciple;


public final class Player {
    private final ArrayList<Card> deck;
    private final ArrayList<Card> cardsInHand;
    private Card hero;
    private int mana;
    private int numberOfWins;
    private int frontRow;
    private int backRow;
    private int idx;
    private boolean hasMoved;

    public Player() {
        deck = new ArrayList<>();
        cardsInHand = new ArrayList<>();
    }

    /**
     * @param shuffleSeed is the seed at which the deck is shuffled
     */
    public void shuffleDeck(final int shuffleSeed) {
        Collections.shuffle(deck, new Random(shuffleSeed));
    }

    /** Deep copy the deck from the input
     * @param deck is the deck of the player form the input
     */
    public void setDeck(final ArrayList<CardInput> deck) {
        for (CardInput card : deck) {
            switch (card.getName()) {
                case "Sentinel" -> this.deck.add(new Sentinel(card));

                case "Goliath" -> this.deck.add(new Goliath(card));

                case "Berserker" -> this.deck.add(new Berserker(card));

                case "Warden" -> this.deck.add(new Warden(card));

                case "The Ripper" -> this.deck.add(new TheRipper(card));

                case "Miraj" -> this.deck.add(new Miraj(card));

                case "The Cursed One" -> this.deck.add(new TheCursedOne(card));

                case "Disciple" -> this.deck.add(new Disciple(card));

                case "Firestorm" -> this.deck.add(new Firestorm(card));

                case "Winterfell" -> this.deck.add(new Winterfell(card));

                case "Heart Hound" -> this.deck.add(new HeartHound(card));

                default -> {
                }
            }
        }
    }

    /**
     * Adds one card in the hand of the player
     */
    public void addCardInHand() {
        if (deck.isEmpty()) {
            return;
        }

        cardsInHand.add(deck.get(0));
        deck.remove(deck.get(0));
    }

    /**
     * @return the Environment cards that are in hand
     */
    public ArrayList<Card> getEnvironmentCardsInHand() {
        ArrayList<Card> environmentCards = new ArrayList<>();

        for (Card card : cardsInHand) {
            if (card.getType().equals("Environment")) {
                environmentCards.add(card);
            }
        }

        return environmentCards;
    }

    /**
     * @param hero is the Hero of the current player
     */
    public void setHero(final CardInput hero) {
        switch (hero.getName()) {
            case "Lord Royce" -> this.hero = new LordRoyce(hero);

            case "Empress Thorina" -> this.hero = new EmpressThorina(hero);

            case "King Mudface" -> this.hero = new KingMudface(hero);

            case "General Kocioraw" -> this.hero = new GeneralKocioraw(hero);

            default -> {
            }
        }
    }

    /**
     * @param handIdx is the index from the cards in hand
     * @return error message in case of failure when placing a card
     */
    public String placeCardOnTable(final int handIdx) {
        Card selectedCard = cardsInHand.get(handIdx);

        if (selectedCard.getMana() > mana) {
            return "Not enough mana to place card on table.";
        } else if (selectedCard.getType().equals("Environment")) {
            return "Cannot place environment card on table.";
        } else if (
            (selectedCard.getRow().equals("front row") && Table.getInstance().isRowFull(frontRow))
            || selectedCard.getRow().equals("back row") && Table.getInstance().isRowFull(backRow)
        ) {
            return "Cannot place card on table since row is full.";
        }

        mana -= selectedCard.getMana();

        switch (selectedCard.getName()) {
            case "The Ripper", "Miraj", "Goliath",
                    "Warden" -> Table.getInstance().addCardOnRow(selectedCard, frontRow);
            case "Sentinel", "Berserker", "The Cursed One", "Disciple" ->
                    Table.getInstance().addCardOnRow(selectedCard, backRow);
            default -> {
            }
        }

        cardsInHand.remove(selectedCard);
        return null;
    }

    /**
     * @param handIdx is the index from the cards in hand
     * @param affectedRow is the affected row by the Environment card
     * @return error message in case of failure when using the card
     */
    public String useEnvironmentCard(final int handIdx, final int affectedRow) {
        Card selectCard = cardsInHand.get(handIdx);
        int mirroredRow = (affectedRow - 3) * (-1);

        if (!selectCard.getType().equals("Environment")) {
            return "Chosen card is not of type environment.";
        } else if (selectCard.getMana() > mana) {
            return "Not enough mana to use environment card.";
        } else if (affectedRow == frontRow || affectedRow == backRow) {
            return "Chosen row does not belong to the enemy.";
        } else if (
            selectCard.getName().equals("Heart Hound") && Table.getInstance().isRowFull(mirroredRow)
        ) {
            return "Cannot steal enemy card since the player's row is full.";
        }

        mana -= selectCard.getMana();
        ((Environment) selectCard).castSpecialAbility(affectedRow);

        cardsInHand.remove(selectCard);
        return null;
    }

    /**
     * Defrosts all the cards on the game table
     */
    public void defrostCards() {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[frontRow][i] != null && cardMatrix[frontRow][i].isFrozen()) {
                cardMatrix[frontRow][i].setFrozen(false);
            }

            if (cardMatrix[backRow][i] != null && cardMatrix[backRow][i].isFrozen()) {
                cardMatrix[backRow][i].setFrozen(false);
            }
        }
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(final int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public void increaseNumberOfWins() {
        numberOfWins++;
    }

    public void setFrontRow(final int frontRow) {
        this.frontRow = frontRow;
    }

    public void setBackRow(final int backRow) {
        this.backRow = backRow;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(final int idx) {
        this.idx = idx;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(final boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    public int getFrontRow() {
        return frontRow;
    }

    public int getBackRow() {
        return backRow;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void addMana(final int roundNumber) {
        mana += roundNumber;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public Card getHero() {
        return hero;
    }
}
