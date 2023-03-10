package blackjack.domain.user;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import blackjack.domain.card.CardGroup;
import blackjack.domain.card.Deck;
import blackjack.domain.result.CardResult;
import blackjack.domain.result.WinningStatus;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Users {

    private final Dealer dealer;
    private final Players players;

    public Users(final List<String> playerNames, final Deck deck) {
        dealer = new Dealer(new CardGroup(deck));
        this.players = new Players(playerNames, deck);
    }

    public Map<Name, CardGroup> getUserNameAndFirstOpenCardGroups() {
        final Map<Name, CardGroup> userNameAndFirstOpenCardGroups = new LinkedHashMap<>();
        userNameAndFirstOpenCardGroups.put(dealer.getName(), dealer.getFirstOpenCardGroup());
        final Map<Name, CardGroup> playerFirstOpenCardGroups = players.getFirstOpenCardGroup();
        userNameAndFirstOpenCardGroups.putAll(playerFirstOpenCardGroups);
        return Collections.unmodifiableMap(userNameAndFirstOpenCardGroups);
    }

    public CardGroup getCardGroupBy(final Name name) {
        return players.getCardGroupBy(name);
    }

    public boolean isDealerUnderDrawLimit() {
        return dealer.isUnderDrawLimit();
    }

    public List<Name> getPlayerNames() {
        return players.getPlayerNames();
    }

    public void drawDealer(final Deck deck) {
        dealer.drawCard(deck);
    }

    public Map<Name, WinningStatus> getPlayersWinningResults() {
        return players.getWinningResult(dealer);
    }

    public Map<WinningStatus, Long> getDealerWinningResults() {
        return getPlayersWinningResults()
                .values()
                .stream()
                .collect(collectingAndThen(groupingBy(WinningStatus::opposite, counting()),
                        Collections::unmodifiableMap));
    }

    public boolean isPlayerBust(final Name name) {
        return players.isPlayerBust(name);
    }

    public void drawCard(final Name userName, final Deck deck) {
        players.drawCard(userName, deck);
    }

    public boolean isBlackJackScore(final Name name) {
        return players.isBlackJackScore(name);
    }

    public Map<Name, CardResult> getUserNameAndCardResults() {
        final Map<Name, CardResult> userNameAndResults = new LinkedHashMap<>();
        userNameAndResults.put(dealer.getName(), new CardResult(dealer.getCardGroups(), dealer.getScore()));
        final Map<Name, CardResult> playerNameAndResults = players.getPlayerNameAndCardResults();
        userNameAndResults.putAll(playerNameAndResults);
        return Collections.unmodifiableMap(userNameAndResults);
    }
}
