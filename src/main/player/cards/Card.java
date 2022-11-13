package main.player.cards;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fileio.CardInput;

@JsonIgnoreProperties({"type", "row", "frozen", "attacked"})
public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private boolean isFrozen;
    private boolean hasAttacked;

    public Card(CardInput card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public String getType() {
        return switch (name) {
            case "Firestorm", "Heart Hound", "Winterfell" -> "Environment";
            case "Warden", "Goliath" -> "Tank";
            default -> "Something else";
        };

    }

    public String getRow() {
        return switch (name) {
            case "The Ripper", "Goliath", "Miraj", "Warden" -> "front row";
            case "Sentinel", "Berserker", "The Cursed One", "Disciple" -> "back row";
            default -> null;
        };

    }
}
