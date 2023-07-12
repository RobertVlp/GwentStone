package main.player.cards.heroes;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class KingMudface extends Hero {

    public KingMudface(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final int affectedRow) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i] != null) {
                int health = cardMatrix[affectedRow][i].getHealth();
                cardMatrix[affectedRow][i].setHealth(health + 1);
            }
        }
    }
}
