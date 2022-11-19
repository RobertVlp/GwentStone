package main.player.cards.environment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fileio.CardInput;
import main.player.cards.Card;

@JsonIgnoreProperties({"attackDamage", "health", "type", "row", "frozen"})
public class Environment extends Card {

    public Environment(final CardInput card) {
        super(card);
    }


    /**
     * @param affectedRow is the affected row by the Environment card
     */
    public void castSpecialAbility(final int affectedRow) {

    }
}
