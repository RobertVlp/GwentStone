package main.player.cards.environment;

import fileio.CardInput;
import main.game.Table;

public final class Winterfell extends Environment {

    public Winterfell(final CardInput card) {
        super(card);
    }

    @Override
    public void castSpecialAbility(final int affectedRow) {
        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (Table.getInstance().getCardMatrix()[affectedRow][i] != null) {
                Table.getInstance().getCardMatrix()[affectedRow][i].setFrozen(true);
            }
        }
    }
}
