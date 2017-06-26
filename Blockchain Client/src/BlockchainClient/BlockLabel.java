package BlockchainClient;

import javafx.scene.control.Label;

/**
 * Created by GCUK-SD on 22/06/2017.
 */
public class BlockLabel extends Label {


    String text;
    String style;

    BlockLabel(Block b)
    {

        text = String.format("From: %s\nTo: %s\nAmount: %s\nPrevious Hash: %s\nCurrent Hash: %s", b.from, b.to, b.amount, b.previousHash, b.currentHash);
       setText(text);

    }

}
