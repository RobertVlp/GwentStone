package main.player.cards.heroes;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class GeneralKocioraw extends Hero {

    public GeneralKocioraw(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final int affectedRow) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i] != null) {
                int attackDamage = cardMatrix[affectedRow][i].getAttackDamage();
                cardMatrix[affectedRow][i].setAttackDamage(attackDamage + 1);
            }
        }
    }
}
