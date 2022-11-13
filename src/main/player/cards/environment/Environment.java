package main.player.cards.environment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fileio.CardInput;
import main.player.cards.Card;

@JsonIgnoreProperties({"attackDamage", "health", "type", "row", "frozen"})
public class Environment extends Card {

    public Environment(CardInput card) {
        super(card);
    }
    
    public void castSpecialAbility(int affectedRow) {

    }
}
