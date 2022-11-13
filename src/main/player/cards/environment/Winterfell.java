package main.player.cards.environment;

import fileio.CardInput;
import main.Table;

public class Winterfell extends Environment {

    public Winterfell(CardInput card) {
        super(card);
    }

    @Override
    public void castSpecialAbility(int affectedRow) {
        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (Table.getInstance().getCardMatrix()[affectedRow][i] != null)
                Table.getInstance().getCardMatrix()[affectedRow][i].setFrozen(true);
        }
    }
}
