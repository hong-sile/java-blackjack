package blackjack.controller;

import blackjack.domain.BlackJackGame;
import blackjack.domain.Card;
import blackjack.domain.RandomDeckGenerator;
import blackjack.domain.WinningStatus;
import blackjack.dto.CardResult;
import blackjack.view.InputView;
import blackjack.view.OutputView;
import blackjack.view.ViewRenderer;

import java.util.List;
import java.util.Map;

public class BackJackController {

    private final InputView inputView = new InputView();
    private final OutputView outputView = new OutputView();

    public void run() {
        final BlackJackGame blackJackGame = initBlackJackGame();
        printInitialStatus(blackJackGame);
        playPlayerTurn(blackJackGame);
        playDealerTurn(blackJackGame);
        printCardResult(blackJackGame);
        printWinningResult(blackJackGame);
    }

    private BlackJackGame initBlackJackGame() {
        outputView.printPlayerNameRequestMessage();
        final List<String> names = inputView.readPlayerNames();
        outputView.printLineBreak();
        return new BlackJackGame(names, new RandomDeckGenerator());
    }

    private void printInitialStatus(final BlackJackGame blackJackGame) {
        outputView.printInitialStatus(ViewRenderer.renderStatus(blackJackGame.getInitialStatus()));
    }

    private void playPlayerTurn(final BlackJackGame blackJackGame) {
        final List<String> playerNames = blackJackGame.getPlayerNames();
        for (final String name : playerNames) {
            playFor(blackJackGame, name);
            outputView.printLineBreak();
        }
    }

    private void playFor(final BlackJackGame blackJackGame, final String name) {
        while (isContinuous(name, blackJackGame)) {
            blackJackGame.playPlayer(name);
            List<Card> userCards = blackJackGame.getStatus().get(name);
            outputView.printCards(name, ViewRenderer.renderCardsToString(userCards));
        }
    }

    private boolean isContinuous(final String name, BlackJackGame blackJackGame) {
        if (blackJackGame.isBlackJackScore(name) || blackJackGame.isBust(name)) {
            return false;
        }
        outputView.printDrawCardRequestMessage(name);
        return DrawInput.from(inputView.readDrawOrStay()).isDraw();
    }

    private void playDealerTurn(BlackJackGame blackJackGame) {
        int dealerDrawCount = blackJackGame.playDealerTurn();
        while (dealerDrawCount-- > 0) {
            outputView.printDealerDrawInfoMessage();
        }
        outputView.printLineBreak();
    }

    private void printCardResult(BlackJackGame blackJackGame) {
        final Map<String, CardResult> cardResult = blackJackGame.getCardResult();
        for (final String name : cardResult.keySet()) {
            final CardResult cardDto = cardResult.get(name);
            outputView.printCardResult(name, ViewRenderer.renderCardsToString(cardDto.getCards()), cardDto.getScore());
        }
        outputView.printLineBreak();
    }

    private void printWinningResult(BlackJackGame blackJackGame) {
        Map<String, WinningStatus> winningResult = blackJackGame.getWinningResult();
        outputView.printWinningResult(ViewRenderer.renderWinningResult(winningResult));
    }
}
