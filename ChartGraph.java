package view;

import cellularData.Country;
import cellularData.DataModel;
import cellularData.LinkedList;
import cellularData.SubscriptionYear;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Instantiates an JavaFX application which creates a model of the data.
 * Uses the model to instantiate an object of type  javafx.scene.chart.LineChart
 * via the GraphView class. Then sets up the scene with basic modification to
 * the stage.
 *
 * @author Foothill College, Evan R, Matt W, Paul H
 */
public class ChartGraph extends Application
{
    final int  BTM_SPACING = 10;
	private GraphView graphView;
	private DataModel model;
    Label cursorCoord;
    HBox btmBox = new HBox(BTM_SPACING);
    BorderPane graphBox = new BorderPane();
    final Text toolTip = new Text("Tool Tip: Hover over a point to get data:");

	/**
	 * Called by launch method of Application
	 * @param stage: Stage
	 */
	@Override
	public void start(Stage stage) 
	{
        final int BTM_BOX_X = 60, BTM_BOX_MARGIN = 4;
		final int TOP_X = 62, TOP_Y = 8, TOP_SPACING = 10;

        final Text textArea = new Text("Select File:  ");
//        final Text toolTip = new Text("Tool Tip: Hover over a point to get data:");
        final Text spacerText = new Text(" | ");
        ComboBox<String> cbo = new ComboBox<>();
        HBox topBox = new HBox(TOP_SPACING);
//        HBox btmBox = new HBox(BTM_SPACING);
        HBox cboBox = new HBox();

//		BorderPane graphBox = new BorderPane();
		cbo.getItems().addAll("cellular.csv", "collegeEnrollment.csv");
		cbo.setValue("cellular.csv");
		cbo.setVisibleRowCount(2);
		cbo.setOnAction(e -> {
			model = new DataModel(cbo.getValue());
			graphView = new GraphView(model);
			graphView.update();                  // a lot of this is common code --> put into a method
			graphBox.setCenter(graphView);
            cursorCoord = hoverLabel(graphView);
            btmBox.getChildren().clear();
            btmBox.getChildren().addAll(toolTip, cursorCoord);
		});

//		// Displays graph of subscription rate by country.
//		model = new DataModel();
//		graphView = new GraphView(model);
//        graphView.update();
//        graphBox.setCenter(graphView);
//        cursorCoord = hoverLabel(graphView);
//        btmBox.getChildren().clear();
//        btmBox.getChildren().addAll(toolTip, cursorCoord);


        // Build topBox
        topBox.setTranslateX(TOP_X);
        topBox.setTranslateY(TOP_Y);
        cboBox.getChildren().addAll(textArea, cbo);
        spacerText.setFont(Font.font(null, FontWeight.BOLD, 16));
        topBox.getChildren().addAll(cboBox, spacerText); // ADD OTHER SELECTS HERE

        // Build bottom box
//        cursorCoord = hoverLabel(graphView);
        toolTip.setFont(Font.font(null, FontWeight.BOLD, 12));
//        btmBox.getChildren().addAll(toolTip, cursorCoord);
        btmBox.setSpacing(BTM_BOX_MARGIN);
//        btmBox.getChildren().addAll(toolTip);

		// Build graphBox
		graphBox.setTop(topBox);
//		graphBox.setCenter(graphView);
		btmBox.setTranslateX(BTM_BOX_X);
        graphBox.setBottom(btmBox);
        graphBox.setMargin(btmBox, new Insets(BTM_BOX_MARGIN));

//		graphView.update();
        // Builds model and displays graph
        model = new DataModel();
        graphView = new GraphView(model);
        graphView.update();
        graphBox.setCenter(graphView);
        cursorCoord = hoverLabel(graphView);
        btmBox.getChildren().clear();
        btmBox.getChildren().addAll(toolTip, cursorCoord);

        // Creates a scene and adds the graph to the scene.
        Scene scene = new Scene(graphBox);
		
		// Places the scene in the stage
		stage.setScene(scene);
		
		// Set the stage title
		stage.setTitle("GraphView Test");
		
		// Display the stage
		stage.show();
	}

	// this method adapted and modified from:
    // https://stackoverflow.com/questions/16473078/javafx-2-x-translate-mouse-
    // click-coordinate-into-xychart-axis-value
	private Label hoverLabel(LineChart<Number, Number> lineChart) {
		final Axis<Number> xAxis = lineChart.getXAxis();
		final Axis<Number> yAxis = lineChart.getYAxis();
		final Label cursorCoords = new Label();

		final Node chartBackground = lineChart.lookup(".chart-plot-background");

		// the following keeps the cursorCoords visible when cursor right on graph
		for (Node n: chartBackground.getParent().getChildrenUnmodifiable()) {
			if (n != chartBackground && n != xAxis && n != yAxis) {
				n.setMouseTransparent(true);
			}
		}

		chartBackground.setOnMouseEntered(e -> cursorCoords.setVisible(true));

		chartBackground.setOnMouseMoved(e -> {
            final double x = (double) xAxis.getValueForDisplay(e.getX());
            final double y = (double) yAxis.getValueForDisplay(e.getY());
            final ValueAxis yValueAxis = (ValueAxis) yAxis, xValueAxis = (ValueAxis) xAxis;
            final double xScale = xAxis.getWidth() /
                    (xValueAxis.getUpperBound() - xValueAxis.getLowerBound());
            final double yScale = yAxis.getHeight() /
                    (yValueAxis.getUpperBound() - yValueAxis.getLowerBound());

            String closest = getClosestCoords(x, y, xScale, yScale, true);
            if (!closest.equals("")) {
                cursorCoords.setText(closest);
//                cursorCoords.setText(String.format("(%.0f, %.2f)",x, y));
            }
            else
                cursorCoords.setText("");
		});

		chartBackground.setOnMouseExited(e -> cursorCoords.setVisible(false));

		xAxis.setOnMouseEntered(e -> cursorCoords.setVisible(true));

		xAxis.setOnMouseMoved(e -> cursorCoords.setVisible(false));

		xAxis.setOnMouseExited(e -> cursorCoords.setVisible(false));

		yAxis.setOnMouseEntered(e -> cursorCoords.setVisible(true));


		yAxis.setOnMouseMoved(e -> cursorCoords.setVisible(false));

		yAxis.setOnMouseExited(e -> cursorCoords.setVisible(false));

		return cursorCoords;
	}

    /**
     * Returns string holding data of point closest to cursor along with country
     * name.
     * @param x current cursor X-graph coordinate value
     * @param y current cursor Y-graph coordinate value
     * @param xScale scale factor of pixels to span of X-axis
     * @param yScale scale factor of pixels to span of Y-axis
     * @param censor boolean true causes return of empty String if (x,y) point
     *        is further than CENSOR_LIMIT from any data point in graph.
     * @return specified String.
     */
	private String getClosestCoords(double x, double y, double xScale,
                                    double yScale, boolean censor) {
	    final double CENSOR_LIMIT = 15;

        String countryName = "";
	    LinkedList<Country> countries = graphView.getSelectedCountryList();
        SubscriptionYear firstSubYear = countries.getIndex(0).getSubscriptions().getIndex(0);
        int bestYear = firstSubYear.getYear();
        double bestSub = firstSubYear.getSubscriptions();
        double closestDistance = Double.MAX_VALUE, distance;

        for (Country country : countries) {
            for (SubscriptionYear subYear : country.getSubscriptions()) {
                double deltaYear = (x - subYear.getYear()) * xScale;
                double deltaSub = (y - subYear.getSubscriptions()) * yScale;
                distance = Math.sqrt(deltaYear * deltaYear + deltaSub * deltaSub);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    bestSub = subYear.getSubscriptions();
                    bestYear = subYear.getYear();
                    countryName = country.getName();
                } // end if
            } // end SubscriptionYear loop
        } // end Country loop

//        System.out.printf("%d %.2f and %.2f %.2f distance %.2f", bestYear, x, bestSub, y, closestDistance);

        if (!censor || closestDistance < CENSOR_LIMIT)
            return String.format("%s: (%d, %.2f)", countryName, bestYear, bestSub);
        return "";
    }


	/**
	 * Launches a standalone JavaFx App
	 */
	public static void main(String[] args) 
	{
		launch();
	}
}