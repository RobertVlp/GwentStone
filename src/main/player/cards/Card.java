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

    public Card(final CardInput card) {
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

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(final boolean frozen) {
        this.isFrozen = frozen;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }


    /**
     * @return the type of the card, useful for Tanks and Environments
     */
    public String getType() {
        return switch (name) {
            case "Firestorm", "Heart Hound", "Winterfell" -> "Environment";

            case "Warden", "Goliath" -> "Tank";

            default -> "Something else";
        };

    }


    /**
     * @return the respective row for Minions or null otherwise
     */
    public String getRow() {
        return switch (name) {
            case "The Ripper", "Goliath", "Miraj", "Warden" -> "front row";

            case "Sentinel", "Berserker", "The Cursed One", "Disciple" -> "back row";

            default -> null;
        };

    }


    /**
     * @param xAttacker is coordinate x for card that uses its ability
     * @param yAttacker is coordinate y for card that uses its ability
     * @param xAttacked is coordinate x for card that is attacked
     * @param yAttacked is coordinate y for card that is attacked
     */
    public void useSpecialAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {

    }
}
