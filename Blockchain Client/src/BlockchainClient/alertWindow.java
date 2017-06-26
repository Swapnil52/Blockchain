package BlockchainClient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by GCUK-SD on 22/06/2017.
 */
public class alertWindow extends Stage {

    public Label label;
    public Button ok;
    public VBox vBox;

    alertWindow(String message)
    {

        super();
        label = new Label(message);
        ok = new Button("OK");
        ok.setOnAction(e -> close());
        vBox = new VBox(20);
        vBox.getChildren().addAll(label, ok);
        vBox.setAlignment(Pos.CENTER);
        setScene(new Scene(vBox));
        setMaxHeight(300);
        setMinHeight(300);
        setMaxWidth(200);
        setMinWidth(200);

    }

    public void display()
    {

        show();

    }


}
