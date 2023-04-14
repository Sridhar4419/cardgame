import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

enum Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
}

enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES
}

class Card {
    private Rank rank;
    private Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

public class CardGame {
    private static final int MAX_PLAYERS = 4;
    private static final int INITIAL_HAND_SIZE = 5;
    private ArrayList<Card> deck;
    private ArrayList<Card> discardPile;
    private ArrayList<Card>[] playerHands;
    private int currentPlayer;
    private boolean reversed;
    private boolean skipped;
    private boolean drawTwo;
    private boolean drawFour;

    public CardGame(int numPlayers) {
        if (numPlayers < 2 || numPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between 2 and 4.");
        }

        deck = new ArrayList<>();
        discardPile = new ArrayList<>();
        playerHands = new ArrayList[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            playerHands[i] = new ArrayList<>();
        }
        currentPlayer = 0;
        reversed = false;
        skipped = false;
        drawTwo = false;
        drawFour = false;
    }

    public void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
    }

    public void dealInitialHands() {
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            for (int j = 0; j < playerHands.length; j++) {
                playerHands[j].add(deck.remove(0));
            }
        }
    }

    public void playGame() {
        initializeDeck();
        dealInitialHands();
        discardPile.add(deck.remove(0));

        while (true) {
            System.out.println("Current Player: Player " + (currentPlayer + 1));
            System.out.println("Top Card: " + discardPile.get(discardPile.size() - 1));
            System.out.println("Your Hand: " + playerHands[currentPlayer]);
            System.out.println("Enter card index to play (or -1 to draw a card): ");
            Scanner scanner = new Scanner(System.in);
            int cardIndex = scanner.nextInt();

            if (cardIndex == -1) {
                drawCard();
            } else if (isValidMove(playerHands[currentPlayer].get(cardIndex))) {
                playCard(cardIndex);
            } else {
                System.out.println("Invalid move. Please try again.");
            }

            if (playerHands[currentPlayer].isEmpty()) {
                System.out.println("Player " + (currentPlayer + 1) + " wins!");
                break;
            }

            if (!skipped) {
                updatePlayer();
            } else {
                skipped = false;
            }
        }
    }

    public boolean isValidMove(Card card) {
        Card topCard = discardPile.get(discardPile.size() - 1);
        if (topCard.getRank() == Rank.ACE && !skipped) {
            return true;
        } else if (topCard.getRank() == Rank.KING && !reversed) {
            return true;
        } else if (topCard.getRank() == Rank.QUEEN && !drawTwo) {
            return true;
        } else if (topCard.getRank() == Rank.JACK && !drawFour) {
            return true;
        } else if (card.getRank() == topCard.getRank() || card.getSuit() == topCard.getSuit()) {
            return true;
        }
        return false;
    }

    public void playCard(int cardIndex) {
        Card card = playerHands[currentPlayer].remove(cardIndex);
        discardPile.add(card);

        switch (card.getRank()) {
            case ACE:
                skipped = true;
                break;
            case KING:
                reversed = !reversed;
                break;
            case QUEEN:
                drawTwo = true;
                break;
            case JACK:
                drawFour = true;
                break;
            default:
                break;
        }

        if (card.getRank() != Rank.JACK) {
            drawTwo = false;
            drawFour = false;
        }
    }

    public void drawCard() {
        if (deck.isEmpty()) {
            System.out.println("Draw pile is empty. Game ends in a draw.");
            System.exit(0);
        }

        Card card = deck.remove(0);
        playerHands[currentPlayer].add(card);

        if (isValidMove(card)) {
            System.out.println("Drawn card can be played. Playing card: " + card);
            playCard(playerHands[currentPlayer].size() - 1);
        } else {
            System.out.println("Drawn card cannot be played. Skipping turn.");
            updatePlayer();
        }
    }

    public void updatePlayer() {
        if (reversed) {
            currentPlayer = (currentPlayer - 1 + playerHands.length) % playerHands.length;
        } else {
            currentPlayer = (currentPlayer + 1) % playerHands.length;
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the Card Game!");
        System.out.println("Enter number of players (2-4): ");
        Scanner scanner = new Scanner(System.in);
        int numPlayers = scanner.nextInt();
        CardGame cardGame = new CardGame(numPlayers);
        cardGame.playGame();
    }
}