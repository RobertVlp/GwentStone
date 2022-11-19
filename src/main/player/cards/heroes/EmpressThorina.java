package main.player.cards.heroes;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class EmpressThorina extends Hero {

    public EmpressThorina(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final int affectedRow) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();
        int maxHealth = 0;
        int maxHealthCardColumn = 0;

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i] != null) {
                if (cardMatrix[affectedRow][i].getHealth() > maxHealth) {
                    maxHealth = cardMatrix[affectedRow][i].getHealth();
                    maxHealthCardColumn = i;
                }
            }
        }

        Table.getInstance().shiftLeft(affectedRow, maxHealthCardColumn);
    }
}
