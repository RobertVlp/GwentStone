package main.player.cards.environment;

import main.Table;
import main.player.cards.*;

import fileio.CardInput;

public class HeartHound extends Environment {

    public HeartHound(CardInput card) {
        super(card);
    }

    @Override
    public void castSpecialAbility(int affectedRow) {
        int mirroredRow = (affectedRow - 3) * (-1);

        Card[][] cardMatrix = Table.getInstance().getCardMatrix();
        Card minion = cardMatrix[affectedRow][0];
        int maxHealth = minion.getHealth();
        int minionColumn = 0;

        for (int i = 1; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i].getHealth() > maxHealth) {
                minion = cardMatrix[affectedRow][i];
                maxHealth = cardMatrix[affectedRow][i].getHealth();
                minionColumn = i;
            }
        }

        Table.getInstance().addCardOnRow(minion, mirroredRow);
        Table.getInstance().shiftLeft(affectedRow, minionColumn);
    }
}
