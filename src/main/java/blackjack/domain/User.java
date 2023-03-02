package blackjack.domain;

import java.util.List;

public abstract class User {

    private final Name name;
    private final CardGroup cardGroup;

    protected User(String name, CardGroup cardGroup) {
        this.name = new Name(name);
        this.cardGroup = cardGroup;
    }

    final public int getScore() {
        return cardGroup.getTotalValue();
    }

    final public String getName() {
        return name.getValue();
    }

    final public List<Card> getCards() {
        return cardGroup.getCards();
    }
}
