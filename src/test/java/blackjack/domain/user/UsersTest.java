package blackjack.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import blackjack.domain.card.Card;
import blackjack.domain.card.CardGroup;
import blackjack.domain.card.CardNumber;
import blackjack.domain.card.CardShape;
import blackjack.domain.card.Deck;
import blackjack.domain.card.RandomDeckGenerator;
import blackjack.domain.card.TestNonShuffledDeckGenerator;
import blackjack.domain.result.CardResult;
import blackjack.domain.result.WinningStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UsersTest {

    final List<Card> testCards = List.of(new Card(CardShape.SPADE, CardNumber.ACE),
            new Card(CardShape.CLOVER, CardNumber.TEN),
            new Card(CardShape.CLOVER, CardNumber.NINE),
            new Card(CardShape.HEART, CardNumber.JACK),
            new Card(CardShape.HEART, CardNumber.NINE),
            new Card(CardShape.DIAMOND, CardNumber.FOUR));

    @Nested
    class validation {

        @Test
        @DisplayName("플레이어의 이름이 6개 이상 입력되는 경우 예외처리")
        void throwExceptionIfPlayerNamesLengthOver5() {
            final List<String> playerNames = List.of("홍실", "제이미", "네오", "솔라", "필립", "다니");

            final Runnable initialUsersByPlayerNamesLengthOver5 =
                    () -> new Users(playerNames, new Deck(new RandomDeckGenerator()));

            assertThatThrownBy(initialUsersByPlayerNamesLengthOver5::run)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("플레이어의 이름은 5개까지만 입력해야 합니다.");
        }

        @Test
        @DisplayName("조건을 만족하는 경우 정상적으로 Users가 생성된다.")
        void initialUsersSuccess() {
            final List<String> playerNames = List.of("홍실", "제이미", "필립");

            final Runnable initialUsers =
                    () -> new Users(playerNames, new Deck(new RandomDeckGenerator()));

            assertDoesNotThrow(initialUsers::run);
        }
    }

    @Test
    @DisplayName("유저들의 이름을 받고 CardGroup을 반환하는 기능 테스트")
    void getUsersStatus() {
        final Users users = new Users(List.of("필립", "홍실")
                , new Deck(new TestNonShuffledDeckGenerator(testCards)));

        final CardGroup philipCardGroup = users.getCardGroupBy("필립");

        assertThat(philipCardGroup.getCards())
                .containsExactlyInAnyOrderElementsOf(testCards.subList(2, 4));
    }

    @Test
    @DisplayName("유저들의 첫 패를 반환하는 기능 테스트")
    void getUsersInitialStatus() {
        final Users users = new Users(List.of("필립", "홍실")
                , new Deck(new TestNonShuffledDeckGenerator(testCards)));

        final Map<String, CardGroup> firstOpenCardGroups = users.getFirstOpenCardGroups();

        assertSoftly(softly -> {
            softly.assertThat(firstOpenCardGroups.get("필립").getCards())
                    .containsExactlyInAnyOrderElementsOf(testCards.subList(2, 4));
            softly.assertThat(firstOpenCardGroups.get("홍실").getCards())
                    .containsExactlyInAnyOrderElementsOf(testCards.subList(4, 6));
            softly.assertThat(firstOpenCardGroups.get("딜러").getCards())
                    .containsExactlyInAnyOrderElementsOf(testCards.subList(0, 1));
        });
    }

    @Test
    @DisplayName("딜러의 스코어가 16이 넘는지 확인하는 기능 테스트")
    void isDealerOverDrawLimitTest() {
        final Users users = new Users(List.of("필립"), new Deck(new TestNonShuffledDeckGenerator(testCards)));

        boolean dealerOverDrawLimit = users.isDealerUnderDrawLimit();

        assertThat(dealerOverDrawLimit).isFalse();
    }

    @Test
    @DisplayName("딜러의 카드를 하나 추가하는 기능 테스트")
    void drawDealerTest() {
        final Deck deck = new Deck(new TestNonShuffledDeckGenerator(testCards));
        final Users users = new Users(List.of("필립"), deck);

        users.drawDealer(deck);
        final CardResult cardResult = users.getUserNameAndCardResults().get("딜러");

        assertThat(cardResult.getCards().getCards())
                .containsExactly(new Card(CardShape.SPADE, CardNumber.ACE)
                        , new Card(CardShape.CLOVER, CardNumber.TEN)
                        , new Card(CardShape.HEART, CardNumber.NINE));
    }

    @Test
    @DisplayName("플레이어 리스트를 반환하는 기능 테스트")
    void getPlayersTest() {
        final Users users = new Users(List.of("필립"), new Deck(new RandomDeckGenerator()));

        final List<String> players = users.getPlayerNames();

        assertThat(players).containsExactly("필립");
    }

    /*
    딜러: 21
    필립: 19
    홍실: 13
     */
    @Test
    @DisplayName("플레이어들의 승리 여부 반환 테스트")
    void getWinningResultTest() {
        final Users users = new Users(List.of("필립", "홍실"), new Deck(new TestNonShuffledDeckGenerator(testCards)));

        Map<String, WinningStatus> winningResult = users.getPlayersWinningResults();

        assertSoftly(softly -> {
            softly.assertThat(winningResult.get("필립")).isEqualTo(WinningStatus.LOSE);
            softly.assertThat(winningResult.get("홍실")).isEqualTo(WinningStatus.LOSE);
        });
    }

    @Test
    @DisplayName("유저(플레이어+딜러)의 이름과 카드목록 점수를 반환하는 기능 테스트")
    void getUserNamesAndResultsTest() {
        final Users users = new Users(List.of("필립"), new Deck(new TestNonShuffledDeckGenerator(testCards)));

        final Map<String, CardResult> userNameAndCardResults = users.getUserNameAndCardResults();

        assertSoftly(softly -> {
            softly.assertThat(userNameAndCardResults.get(Dealer.DEALER_NAME).getCards().getCards())
                    .containsExactlyInAnyOrderElementsOf(testCards.subList(0, 2));
            softly.assertThat(userNameAndCardResults.get(Dealer.DEALER_NAME).getScore().getValue())
                    .isEqualTo(21);
            softly.assertThat(userNameAndCardResults.get("필립").getCards().getCards())
                    .containsExactlyInAnyOrderElementsOf(testCards.subList(2, 4));
            softly.assertThat(userNameAndCardResults.get("필립").getScore().getValue())
                    .isEqualTo(19);
        });
    }

    @Test
    @DisplayName("딜러의 승무패 결과를 반환하는 기능 테스트")
    void getDealerWinningResultsTest() {
        final Users users = new Users(List.of("제이미", "홍실"), new Deck(new TestNonShuffledDeckGenerator(testCards)));
        final Map<WinningStatus, Long> dealerWinningResults = users.getDealerWinningResults();

        assertThat(dealerWinningResults.get(WinningStatus.WIN))
                .isEqualTo(2);
    }
}
