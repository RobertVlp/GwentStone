package main.player.cards.heroes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fileio.CardInput;
import main.player.cards.Card;

public class Hero extends Card {

    public Hero(final CardInput card) {
        super(card);
        this.setHealth(30);
    }

    /**
     * @param affectedRow is affected row by Hero ability
     */
    public void useHeroAbility(final int affectedRow) {

    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return "{\"mana\":" + this.getMana() + ",\"description\":\"" + this.getDescription()
                    + "\",\"colors\":" + objectMapper.writeValueAsString(this.getColors())
                    + ",\"name\":\"" + this.getName() + "\",\"health\":" + this.getHealth() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
