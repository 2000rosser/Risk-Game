//TEAM RPK
//RISK GAME
//By
//Patrick Smyth   - 18347566
//Kamil Michalski - 18469806
//Ross Murphy     - 20207271




package RiskGame;

enum CardType {
    INFANTRY,
    CAVALRY,
    ARTILLERY
}

public class Cards {
    public String cardTerritory;
    public CardType type;
    public Cards(String t, int i){
        cardTerritory = t;
        if(i % 3 == 0){
            type = CardType.INFANTRY;
        }
        if(i % 3 == 1){
            type = CardType.CAVALRY;
        }
        if(i % 3 == 2){
            type = CardType.ARTILLERY;
        }
    }

}
