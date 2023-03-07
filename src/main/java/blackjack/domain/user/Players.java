package blackjack.domain.user;

import blackjack.domain.card.CardGroup;
import blackjack.domain.card.Deck;
import blackjack.domain.result.WinningStatus;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Players {

    private static final String PLAYER_NAMES_IS_EMPTY = "쉼표만 입력할 수 없습니다.";
    private static final String NUMBER_OF_PLAYER_OVER_LIMIT = "플레이어의 이름은 5개까지만 입력해야 합니다.";
    private static final int NUMBER_OF_PLAYER_LIMIT = 5;

    private final List<Player> players;

    public Players(final List<String> playerNames, final Deck deck) {
        validatePlayerNames(playerNames);
        this.players = playerNames.stream()
                .map(name -> new Player(name, deck.drawFirstCardGroup()))
                .collect(Collectors.toUnmodifiableList());
    }

    private void validatePlayerNames(final List<String> playerNames) {
        if (playerNames.isEmpty()) {
            throw new IllegalArgumentException(PLAYER_NAMES_IS_EMPTY);
        }
        if (playerNames.size() > NUMBER_OF_PLAYER_LIMIT) {
            throw new IllegalArgumentException(NUMBER_OF_PLAYER_OVER_LIMIT);
        }
    }

    public Map<String, CardGroup> getFirstOpenCardGroup() {
        return players.stream()
                .collect(Collectors.toUnmodifiableMap(Player::getName, Player::getFirstOpenCardGroup));
    }

    public Map<String, CardGroup> getStatus() {
        return players.stream()
                .collect(Collectors.toUnmodifiableMap(Player::getName, Player::getCardGroups));
    }

    public List<String> getPlayerNames() {
        return players.stream()
                .map(Player::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<String, WinningStatus> getWinningResult(final Dealer dealer) {
        return players.stream()
                .collect(Collectors.toUnmodifiableMap(Player::getName, dealer::comparePlayer));
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }
}
