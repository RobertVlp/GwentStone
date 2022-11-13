package main.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import fileio.*;
import main.Table;
import main.player.cards.*;
import main.player.cards.environment.Environment;
import main.player.cards.environment.Firestorm;
import main.player.cards.environment.HeartHound;
import main.player.cards.environment.Winterfell;
import main.player.cards.heros.EmpressThorina;
import main.player.cards.heros.GeneralKocioraw;
import main.player.cards.heros.KingMudface;
import main.player.cards.heros.LordRoyce;
import main.player.cards.minions.*;


public class Player {
    private ArrayList<Card> deck;
    private ArrayList<Card> cardsInHand;
    private Card hero;
    private int mana;
    private int numberOfWins;
    private int numberofGames;
    private int frontRow;
    private int backRow;
    private int idx;
    private boolean hasMoved;

    public Player() {
        deck = new ArrayList<>();
        cardsInHand = new ArrayList<>();
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<CardInput> deck) {
        for (var card : deck) {
            switch (card.getName()) {
                case "Sentinel":
                    this.deck.add(new Sentinel(card));
                    break;
                
                case "Goliath":
                    this.deck.add(new Goliath(card));
                    break;

                case "Berserker":
                    this.deck.add(new Berserker(card));
                    break;

                case "Warden":
                    this.deck.add(new Warden(card));
                    break;

                case "The Ripper":
                    this.deck.add(new TheRipper(card));
                    break;

                case "Miraj":
                    this.deck.add(new Miraj(card));
                    break;

                case "The Cursed One":
                    this.deck.add(new TheCursedOne(card));
                    break;

                case "Disciple":
                    this.deck.add(new Disciple(card));
                    break;
                
                case "Firestorm":
                    this.deck.add(new Firestorm(card));
                    break;

                case "Winterfell":
                    this.deck.add(new Winterfell(card));
                    break;

                case "Heart Hound":
                    this.deck.add(new HeartHound(card));
                    break;

                default:
                    break;
            }
        }
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void addCardInHand() {
        if (deck.isEmpty())
            return;

        cardsInHand.add(deck.get(0));
        deck.remove(deck.get(0));
    }

    public void addMana(int roundNumber) {
        mana += roundNumber;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void shuffleDeck(int shuffleSeed) {
        Collections.shuffle(deck, new Random(shuffleSeed));
    }

    public ArrayList<Card> getEnvironmentCardsInHand() {
        ArrayList<Card> environmentCards = new ArrayList<>();

        for (int i = 0; i < cardsInHand.size(); i++) {
            if (cardsInHand.get(i).getType().equals("Environment"))
                environmentCards.add(cardsInHand.get(i));
        }

        return environmentCards;
    }

    public Card getHero() {
        return hero;
    }

    public void setHero(CardInput hero) {
        switch (hero.getName()) {
            case "Lord Royce":
                this.hero = new LordRoyce(hero);
                break;
            
            case "Empress Thorina":
                this.hero = new EmpressThorina(hero);
                break;

            case "King Mudface":
                this.hero = new KingMudface(hero);
                break;

            case "General Kocioraw":
                this.hero = new GeneralKocioraw(hero);
                break;

            default:
                break;
        }
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public int getNumberofGames() {
        return numberofGames;
    }

    public void setNumberOfGames(int numberOfGames) {
        this.numberofGames = numberOfGames;
    }

    public int getFrontRow() {
        return frontRow;
    }

    public void setFrontRow(int frontRow) {
        this.frontRow = frontRow;
    }

    public int getBackRow() {
        return backRow;
    }

    public void setBackRow(int backRow) {
        this.backRow = backRow;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public String placeCardOnTable(int handIdx) {
        Card selectedCard = cardsInHand.get(handIdx);

        if (selectedCard.getMana() > mana)
            return "Not enough mana to place card on table.";
        else if (selectedCard.getType().equals("Environment"))
            return "Cannot place environment card on table.";
        else if (
            (selectedCard.getRow().equals("front row") && Table.getInstance().isRowFull(frontRow)) || 
            selectedCard.getRow().equals("back row") && Table.getInstance().isRowFull(backRow)
        )
            return "Cannot place card on table since row is full.";

        mana -= selectedCard.getMana();

        switch (selectedCard.getName()) {
            case "The Ripper":
            case "Miraj":
            case "Goliath":
            case "Warden":
                Table.getInstance().addCardOnRow(selectedCard, frontRow);
                break;

            case "Sentinel":
            case "Berserker":
            case "The Cursed One":
            case "Disciple":
                Table.getInstance().addCardOnRow(selectedCard, backRow);
                break;
        }

        cardsInHand.remove(selectedCard);
        return null;
    }

    public String useEnvironmentCard(int handIdx, int affectedRow) {
        Card selectCard = cardsInHand.get(handIdx);
        int mirroredRow = (affectedRow - 3) * (-1);

        if (!selectCard.getType().equals("Environment"))
            return "Chosen card is not of type environment.";
        else if (selectCard.getMana() > mana)
            return "Not enough mana to use environment card.";
        else if (affectedRow == frontRow || affectedRow == backRow)
            return "Chosen row does not belong to the enemy.";
        else if (selectCard.getName().equals("Heart Hound") &&
            Table.getInstance().isRowFull(mirroredRow)
        )
            return "Cannot steal enemy card since the player's row is full.";

        mana -= selectCard.getMana();
        ((Environment)selectCard).castSpecialAbility(affectedRow);

        cardsInHand.remove(selectCard);
        return null;
    }

    public void defrostCards() {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[frontRow][i] != null && cardMatrix[frontRow][i].isFrozen())
                cardMatrix[frontRow][i].setFrozen(false);

            if (cardMatrix[backRow][i] != null && cardMatrix[backRow][i].isFrozen())
                cardMatrix[backRow][i].setFrozen(false);
        }
    }
}
